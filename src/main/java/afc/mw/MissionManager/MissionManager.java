/* Copyright 2018-2021 Universidad Politécnica de Madrid (UPM).
 *
 * Authors:
 *    Néstor Lucas Martínez
 *    José-Fernán Martínez Ortega
 *    Vicente Hernández Díaz
 * 
 * This software is distributed under a dual-license scheme:
 *
 * - For academic uses: Licensed under GNU Affero General Public License as
 *                      published by the Free Software Foundation, either
 *                      version 3 of the License, or (at your option) any
 *                      later version.
 * 
 * - For any other use: Licensed under the Apache License, Version 2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * You can get a copy of the license terms in licenses/LICENSE.
 * 
 */

package afc.mw.MissionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyNamingStrategy;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Region;
import com.afarcloud.thrift.Vehicle;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;

import afc.mw.MissionManager.types.MissionReport;
import afc.mw.MissionManager.types.VehiclePlan;
import afc.mw.MissionManager.types.VehiclePlanException;
import afc.mw.MissionManager.types.isobus.PrescriptionMap;
import afc.mw.MissionManager.utils.MissionParser;
import afc.mw.MissionManager.utils.MissionValidator;
import afc.mw.MissionManager.utils.MmtClient;
import afc.mw.MissionManager.utils.MqttClientMission;
import afc.mw.MissionManager.utils.RestClient;
import afc.mw.MissionManager.utils.ValidationResult;

/**
 * Mission Manager (AFarCloud)
 *
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionManager implements IManager {
	public static final int NOT_STARTED = 0;
	public static final int RUNNING = 1;
	public static final int FINISHED = 2;
	public static final int STOPPED = 3;

	public static final int ABORT_HARD = 1;
	public static final int ABORT_SOFT = 2;
	public static final int DRONE_SPEED = 3;

	private static final Jsonb jsonb = JsonbBuilder
			.create(new JsonbConfig().withNullValues(false).withFormatting(true));

	private static final Jsonb jsonbPrescriptionMap = JsonbBuilder.create(new JsonbConfig().withNullValues(false)
			.withFormatting(true).withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE));

	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	private Logger sciLog = context.sciLog;

	private long tic1;
	private long tic2;
	private long toc1;
	private long toc2;

	private Mission currentMission = null;
	private static MissionManager instance = null;
	private MissionParser parser = null;

	private JWSObject jwsObject;
	private boolean vehiclePlanSigned = false;
	private boolean missionActive = false;

	/**
	 * Mission Manager Constructor
	 */
	private MissionManager() {
		parser = new MissionParser();
	}

	public static MissionManager getInstance() {
		if (instance == null)
			instance = new MissionManager();

		return instance;
	}

	/**
	 * A call to this method starts a new mission sent by the MMT through the Apache
	 * Thrift interface.
	 *
	 * @param requestId
	 * @param missionPlan
	 * @return The missionID assigned to the mission
	 * @throws InterruptedException
	 */
	public void startMission(int requestId, Mission missionPlan) throws InterruptedException {
		tic1 = System.currentTimeMillis();
		mmLog.log(Level.INFO, "Received new mission from the MMT. Mission ID: " + missionPlan.missionId
				+ " (request ID: " + requestId + ")");

		// STEP 1.1: Check missionPlan integrity
		mmLog.log(Level.INFO, "Checking mission " + missionPlan.missionId + " validity.");
		ValidationResult result = MissionValidator.getInstance().validate(missionPlan);
		MmtClient.getInstance().sendValidationResult(result);

		while (result != ValidationResult.VALID) {
			switch (result) {
			// UNSOLVABLE ERRORS
			case MISSION_NULL:
			case MISSION_EMPTY:
			case NO_MISSION_ID:
			case NO_NAVIGATION:
			case NO_VEHICLES:
			case NO_TASKS:
			case NO_COMMANDS:
			case NO_PRESCRIPTION_MAP:
				mmLog.log(Level.SEVERE, "Mission " + missionPlan.getMissionId() + " is INVALID");
				return;
			// SOLVABLE ERRORS
			case NO_MISSION_NAME:
				mmLog.log(Level.WARNING, "Mission with no name -> Name changed to \"unnamedMission\".");
				missionPlan.name = "unnamedMission";
				break;
			case NO_HOME_LOCATION:
				mmLog.log(Level.WARNING, "Mission with no home location -> Adding an empty one.");
				missionPlan.homeLocation = new ArrayList<Position>();
				break;
			case NO_FORBIDDEN_AREA:
				mmLog.log(Level.WARNING, "Mission with no forbidden area -> Adding an empty one.");
				missionPlan.forbiddenArea = new ArrayList<Region>();
				break;
			// NO ERRORS, BUT WARNINGS
			case NO_COMMANDS_WARN:
				mmLog.log(Level.INFO, "Mission has unmanned vehicles with no commands assigned.");
				result = ValidationResult.VALID;
				break;
			case NO_PRESCRIPTION_MAP_WARN:
				mmLog.log(Level.INFO, "Mission has tractors with no prescription maps assigned.");
				result = ValidationResult.VALID;
				break;
			case NO_COMMANDS_PM_WARN:
				mmLog.log(Level.INFO,
						"Mission has both unmanned vehicles with no commands and tractors with no prescription maps.");
				break;
			// NO ERRORS
			case VALID:
				mmLog.log(Level.INFO, "Mission is valid. Processing for dispatch...");
				break;
			default:
				mmLog.log(Level.SEVERE, "Unknown validation result. Please contact for more information.");
			}

			if (result != ValidationResult.VALID) {
				result = MissionValidator.getInstance().validate(missionPlan);
			}
		}

		currentMission = missionPlan;
		missionActive = true;
		mmLog.log(Level.INFO, "Mission " + missionPlan.getMissionId() + " is valid. Continue processing...");

		// STEP 2: Store mission data through the Data Query
		(new Thread() {
			public void run() {
				RestClient.getInstance().storeMission(missionPlan);
			}
		}).start();

		// STEP 3: For each vehicle, retrieve and dispatch vehicle plan
		for (Vehicle vehicle : missionPlan.vehicles) {
			vehiclePlanSigned = false;
			switch (vehicle.type) {
			case AUAV:
			case RUAV:
			case UAV:
			case AGV:
			case UGV:
				try {
					// STEP 3.A.0: Set variables for the vehicle plan processing
					String datetime = (new SimpleDateFormat("yyyyMMdd-HHmmss"))
							.format(Calendar.getInstance().getTime());
					String filename = context.current_mission_dir + File.separator + "AFC-VP-" + datetime + "-"
							+ requestId + "-" + missionPlan.missionId + "-" + vehicle.id + ".json";

					// STEP 3.A.1: Extract the vehicle plan from the mission plan
					tic2 = System.currentTimeMillis();
					VehiclePlan vehiclePlan = parser.parseMission(vehicle, missionPlan);
					toc2 = System.currentTimeMillis();
					sciLog.log(Level.INFO, "parse_auav_plan," + tic2 + "," + toc2 + "," + (toc2 - tic2));

					// STEP 3.A.2: Save the vehicle plan as a JSON file
					tic2 = System.currentTimeMillis();
					try {
						jsonb.toJson(vehiclePlan, new FileWriter(filename));
					} catch (JsonbException e) {
						mmLog.log(Level.WARNING, "Error parsing vehicle plan as JSON (save as file).");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_vehicle_plan,jsonb_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} catch (IOException e) {
						mmLog.log(Level.WARNING, "I/O error saving the vehicle plan.");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_vehicle_plan,io_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} catch (Exception e) {
						mmLog.log(Level.WARNING, "Unexpected error saving the vehicle plan");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_vehicle_plan,unknown_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} finally {
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO, "save_vehicle_plan,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					}

					mmLog.log(Level.INFO, "Parsed vehicle plan for vehicle " + vehicle.name + "(" + vehicle.id
							+ ") and locally saved as " + filename);

					// STEP 3.A.3-pre1: If enabled, generate signed vehicle plan
					if (context.mm2ddsEnabled) {
						tic2 = System.currentTimeMillis();

						try {
							JWSSigner signer = new MACSigner(context.mm2ddsSecret);
							jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
									new Payload(jsonb.toJson(vehiclePlan)));

							jwsObject.sign(signer);
							vehiclePlanSigned = true;
						} catch (KeyLengthException e) {
							mmLog.log(Level.SEVERE, "Error with shared secret between the MM and the DDS.");
							if (MissionManagerContext.getInstance().debug) {
								mmLog.log(Level.SEVERE, e.getMessage(), e);
							}
							toc2 = System.currentTimeMillis();
							sciLog.log(Level.INFO, "sign_vehicle_plan,shared_secret_problem," + tic2 + "," + toc2 + ","
									+ (toc2 - tic2));
						} catch (JOSEException e) {
							mmLog.log(Level.SEVERE, "Error signing the vehicle plan!");
							if (MissionManagerContext.getInstance().debug) {
								mmLog.log(Level.SEVERE, e.getMessage(), e);
							}
							toc2 = System.currentTimeMillis();
							sciLog.log(Level.INFO,
									"sign_vehicle_plan,signing_problem," + tic2 + "," + toc2 + "," + (toc2 - tic2));
						}

						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO, "sign_vehicle_plan,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));

						if (vehiclePlanSigned) {

							// STEP 3.A.3-pre2: Save signed vehicle plan
							tic2 = System.currentTimeMillis();

							try {
								Files.write(Paths.get(filename + ".signed"), jwsObject.serialize().getBytes());
							} catch (IOException e) {
								mmLog.log(Level.WARNING, "Error saving the signed vehicle plan!");
								if (MissionManagerContext.getInstance().debug) {
									mmLog.log(Level.WARNING, e.getMessage(), e);
								}
								toc2 = System.currentTimeMillis();
								sciLog.log(Level.INFO, "save_signed_vehicle_plan,io_exception," + tic2 + "," + toc2
										+ "," + (toc2 - tic2));
							}

							toc2 = System.currentTimeMillis();
							sciLog.log(Level.INFO,
									"save_signed_vehicle_plan,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));
						}
					}

					// STEP 3.A.3: Publish the vehicle plan (signed)
					try {
						tic2 = System.currentTimeMillis();
						if (context.mm2ddsEnabled) {
							MqttClientMission.getInstance().publishMission(vehicle,
									missionPlan.isSetName() ? missionPlan.name : "", jwsObject.serialize());
						} else {
							MqttClientMission.getInstance().publishMission(vehicle,
									missionPlan.isSetName() ? missionPlan.name : "", jsonb.toJson(vehiclePlan));
						}
					} catch (MqttException e) {
						mmLog.log(Level.SEVERE, "There was an error trying to publish the vehicle " + vehicle.id
								+ " plan to the MQTT Broker");
						if (context.debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"publish_vehicle_plan,mqtt_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} finally {
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"publish_vehicle_plan,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					}
				} catch (VehiclePlanException e) {
					mmLog.log(Level.SEVERE, "Vehicle " + vehicle.name + "(" + vehicle.id
							+ ") has bad formed commands or no commands assigned.");
					if (context.debug) {
						mmLog.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				break;
			case RGV:
			case Tractor:
				try {
					// STEP 3.B.0: Generate the filename variables
					String datetime = (new SimpleDateFormat("yyyyMMdd-HHmmss"))
							.format(Calendar.getInstance().getTime());
					String filename = context.current_mission_dir + File.separator + "AFC-PM-" + datetime + "-"
							+ requestId + "-" + missionPlan.missionId + "-" + vehicle.id + ".json";

					// STEP 3.B.1: Parse the mission to generate the prescription map
					tic2 = System.currentTimeMillis();
					PrescriptionMap prescriptionMap = parser.parsePrescriptionMap(vehicle, missionPlan);
					toc2 = System.currentTimeMillis();
					sciLog.log(Level.INFO, "parse_auav_plan," + tic2 + "," + toc2 + "," + (toc2 - tic2));

					// STEP 3.B.2: Store locally the generated prescription map in JSON format
					tic2 = System.currentTimeMillis();
					try {
						jsonbPrescriptionMap.toJson(prescriptionMap, new FileWriter(filename));
					} catch (JsonbException e) {
						mmLog.log(Level.WARNING, "Error parsing prescription map as JSON (save as file).");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_prescription_map,jsonb_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} catch (IOException e) {
						mmLog.log(Level.WARNING, "I/O error saving the prescription map.");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_prescription_map,io_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} catch (Exception e) {
						mmLog.log(Level.WARNING, "Unexpected error saving the prescription map.");
						if (MissionManagerContext.getInstance().debug) {
							mmLog.log(Level.SEVERE, e.getMessage(), e);
						}
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_prescription_map,unknown_exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					} finally {
						toc2 = System.currentTimeMillis();
						sciLog.log(Level.INFO,
								"save_prescription_map,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));
					}

					mmLog.log(Level.INFO, "Parsed prescription map for vehicle " + vehicle.name + "(" + vehicle.id
							+ ") and locally saved as " + filename);

					// STEP 3.B.3: Send the prescription map to the ISOBUS Converter
					(new Thread() {
						public void run() {
							try {
								tic2 = System.currentTimeMillis();
								RestClient.getInstance().sendPrescriptionMap(Integer.toString(missionPlan.missionId),
										prescriptionMap);
							} catch (Exception e) {
								mmLog.log(Level.SEVERE, "There was an error trying to send the prescription map for "
										+ vehicle.id + ".");
								if (context.debug) {
									mmLog.log(Level.SEVERE, e.getMessage(), e);
								}
								toc2 = System.currentTimeMillis();
								sciLog.log(Level.INFO,
										"send_prescription_map,exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
							} finally {
								toc2 = System.currentTimeMillis();
								sciLog.log(Level.INFO,
										"send_prescription_map,success," + tic2 + "," + toc2 + "," + (toc2 - tic2));
							}
						}
					}).start();
				} catch (VehiclePlanException e) {
					mmLog.log(Level.SEVERE,
							"Vehicle " + vehicle.id + " has bad formed prescription map associated info.");
					if (context.debug) {
						mmLog.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				break;
			default:
				mmLog.log(Level.WARNING, "Unknown vehicle type for vehicle id " + vehicle.id + ".");
				break;
			}
		}
		toc1 = System.currentTimeMillis() - tic1;
		sciLog.log(Level.INFO, "exit,ok,," + toc1);
	}

	/**
	 * Aborts the vehicle plan for the given vehicleId.
	 * 
	 * @param vehicleId The id of the vehicle that has to abort the mission.
	 * @return "OK" if the abort message was correctly published to the vehicle, "NOK" otherwise.
	 */
	public String abortVehiclePlan(int vehicleId) {
		tic1 = System.currentTimeMillis();
		
		if (currentMission == null) {
			mmLog.log(Level.WARNING, "Abort vehicle plan (soft) ignored, as there is no active mission");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_soft_exit,nok,no_active_mission," + toc1);
			return "NOK: No active mission";
		}

		try {
			for (Vehicle vehicle : currentMission.vehicles) {
				if (vehicle.id == vehicleId) {
					mmLog.log(Level.INFO, "Sending soft abort event to vehicle " + vehicleId);
					MqttClientMission.getInstance().publishAbort(vehicle, currentMission.name);
					toc1 = System.currentTimeMillis() - tic1;
					sciLog.log(Level.INFO, "abort_vehicle_soft_exit,ok,," + toc1);
					return "OK";
				}
			}
			mmLog.log(Level.WARNING, "Requested soft abort for vehicle " + vehicleId + " not in current mission.");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_soft_exit,nok,no_vehicle," + toc1);
			return "NOK";
		} catch (MqttException e) {
			mmLog.log(Level.SEVERE, "There was an error trying soft abort mission for vehicle " + vehicleId);
			if (context.debug) {
				mmLog.log(Level.SEVERE, e.getMessage(), e);
			}
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_soft_exit,nok,mqtt_error," + toc1);
			return "NOK";
		}
	}

	/**
	 * Aborts the mission plan for the given missionId.
	 * 
	 * @param missionId The id of the mission that has to be aborted.
	 * @return "OK" if the abort message was correctly published, "NOK" otherwise.
	 */
	public String abortMissionPlan(int missionId) {
		tic1 = System.currentTimeMillis();
		
		if (currentMission == null) {
			mmLog.log(Level.WARNING, "Abort mission (soft) ignored, as there is no active mission");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_soft_exit,nok,no_active_mission," + toc1);
			return "NOK: No active mission";
		}

		mmLog.log(Level.INFO, "Processing abort mission (soft) request.");
		boolean error = false;
		mmLog.log(Level.INFO, "Current mission to be aborted (soft) is missionId " + currentMission.missionId
				+ " and has " + currentMission.vehicles.size() + " vehicles.");
		for (Vehicle vehicle : currentMission.vehicles) {
			switch (vehicle.type) {
			case AUAV:
			case RUAV:
			case UAV:
			case AGV:
			case UGV:
				mmLog.log(Level.INFO, "Sending abort mission (soft) to " + vehicle.type + " " + vehicle.name + " ("
						+ vehicle.id + ").");
				if (!abortVehiclePlanHard(vehicle.id).equals("OK")) {
					error = true;
				}
				break;
			default:
				mmLog.log(Level.INFO, "Ignoring abort mission (soft) for " + vehicle.type + " " + vehicle.name + " ("
						+ vehicle.id + ").");
				break;
			}
		}

		if (!error) {
			currentMission = null;
			missionActive = false;
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_soft_exit,ok,," + toc1);
			return "OK";
		} else
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_soft_exit,nok,misc," + toc1);
			return "NOK";
	}

	/**
	 * Force aborts the vehicle plan for the given vehicleId.
	 * 
	 * @param vehicleId The id of the vehicle that has to abort the mission.
	 * @return "OK" if the abort message was correctly published to the vehicle, "NOK" otherwise.
	 */
	public String abortVehiclePlanHard(int vehicleId) {
		tic1 = System.currentTimeMillis();
		
		if (currentMission == null) {
			mmLog.log(Level.WARNING, "Abort vehicle plan (hard) ignored, as there is no active mission");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_hard_exit,nok,no_active_mission," + toc1);
			return "NOK: No active mission";
		}

		try {
			for (Vehicle vehicle : currentMission.vehicles) {
				if (vehicle.id == vehicleId) {
					mmLog.log(Level.INFO, "Sending hard abort event to vehicle " + vehicleId);
					MqttClientMission.getInstance().publishAbortHard(vehicle,
							currentMission.isSetName() ? currentMission.name : "");
					toc1 = System.currentTimeMillis() - tic1;
					sciLog.log(Level.INFO, "abort_vehicle_hard_exit,ok,," + toc1);
					return "OK";
				}
			}
			mmLog.log(Level.WARNING, "Requested hard abort for vehicle " + vehicleId + " not in current mission.");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_hard_exit,nok,no_vehicle," + toc1);			return "NOK";
		} catch (MqttException e) {
			mmLog.log(Level.SEVERE, "There was an error trying hard abort mission for vehicle " + vehicleId);
			if (context.debug) {
				mmLog.log(Level.SEVERE, e.getMessage(), e);
			}
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_vehicle_hard_exit,nok,mqtt_error," + toc1);
			return "NOK";
		}
	}


	/**
	 * Force aborts the mission plan for the given missionId.
	 * 
	 * @param missionId The id of the mission that has to be aborted.
	 * @return "OK" if the abort message was correctly published, "NOK" otherwise.
	 */
	public String abortMissionPlanHard(int missionId) {
		tic1 = System.currentTimeMillis();

		if (currentMission == null) {
			mmLog.log(Level.WARNING, "Abort mission (hard) ignored, as there is no active mission");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_hard_exit,nok,no_active_mission," + toc1);
			return "NOK: No active mission";
		}

		mmLog.log(Level.INFO, "Processing abort mission (hard) request.");
		boolean error = false;
		mmLog.log(Level.INFO, "Current mission to be aborted (hard) is missionId " + currentMission.missionId
				+ " and has " + currentMission.vehicles.size() + " vehicles.");
		for (Vehicle vehicle : currentMission.vehicles) {
			switch (vehicle.type) {
			case AUAV:
			case RUAV:
			case UAV:
			case AGV:
			case UGV:
				mmLog.log(Level.INFO, "Sending abort mission (hard) to " + vehicle.type + " " + vehicle.name + " ("
						+ vehicle.id + ").");
				if (!abortVehiclePlanHard(vehicle.id).equals("OK")) {
					error = true;
				}
				break;
			default:
				mmLog.log(Level.INFO, "Ignoring abort mission (hard) for " + vehicle.type + " " + vehicle.name + " ("
						+ vehicle.id + ").");
				break;
			}
		}

		if (!error) {
			currentMission = null;
			missionActive = false;
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_hard_exit,ok,," + toc1);
			return "OK";
		} else
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "abort_mission_hard_exit,nok,misc," + toc1);
			return "NOK";
	}
	
	/**
	 * Gets the current mission.
	 * 
	 * @return Mission The current mission,
	 */
	public Mission getCurrentMission() {
		return currentMission;
	}

	/**
	 * Checks if the mission is active
	 * 
	 * @return {@code true} if the mission is active; {@code false} otherwise.
	 */
	public boolean isMissionActive() {
		return missionActive;
	}

	/**
	 * Validates a mission report against the current mission.
	 * 
	 * @param report The report to be validated.
	 * @return {@code MISSION_REPORT_VALID} if the mission report is valid, or an error code otherwise. 
	 */
	public byte validateReport(MissionReport report) {
		tic1 = System.currentTimeMillis();

		mmLog.log(Level.INFO, "Validating mission report for mission id: " + report.mission_id);
		if (currentMission == null) {
			mmLog.log(Level.INFO, "Mission report is NOT VALID: No mission active");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "validate_report_exit,nok,no_mission," + toc1);
			return MissionReport.MISSION_REPORT_INVALID_MISSION_NO_MISSION;
		}

		if (report.mission_id != currentMission.missionId) {
			mmLog.log(Level.INFO, "Mission report is NOT VALID: Mission ID mismatch");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "validate_report_exit,nok,mission_id_mismatch," + toc1);
			return MissionReport.MISSION_REPORT_INVALID_MISSION_ID;
		}

		if (!missionActive) {
			mmLog.log(Level.INFO, "Mission report is NOT VALID: Inactive mission");
			toc1 = System.currentTimeMillis() - tic1;
			sciLog.log(Level.INFO, "validate_report_exit,nok,mission_not_active," + toc1);
			return MissionReport.MISSION_REPORT_INVALID_MISSION_NOT_ACTIVE;
		}

		for (Vehicle vehicle : currentMission.vehicles) {
			if (report.vehicle_id == vehicle.id) {
				mmLog.log(Level.INFO, "Mission report is VALID");
				toc1 = System.currentTimeMillis() - tic1;
				sciLog.log(Level.INFO, "validate_report_exit,ok,," + toc1);
				return MissionReport.MISSION_REPORT_VALID;
			}
		}
		
		mmLog.log(Level.INFO, "Mission report is NOT VALID: Vehicle reporting is not in mission");
		toc1 = System.currentTimeMillis() - tic1;
		sciLog.log(Level.INFO, "validate_report_exit,nok,invalid_vehicle," + toc1);
		return MissionReport.MISSION_REPORT_INVALID_VEHICLE_ID;
	}
}
