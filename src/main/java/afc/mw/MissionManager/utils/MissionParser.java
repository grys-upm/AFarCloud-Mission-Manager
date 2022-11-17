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

package afc.mw.MissionManager.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.afarcloud.thrift.Command;
import com.afarcloud.thrift.Equipment;
import com.afarcloud.thrift.EquipmentType;
import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Position;
import com.afarcloud.thrift.Region;
import com.afarcloud.thrift.Task;
import com.afarcloud.thrift.TaskType;
import com.afarcloud.thrift.Vehicle;

import afc.mw.MissionManager.MissionManagerContext;
import afc.mw.MissionManager.types.ParsedCommand;
import afc.mw.MissionManager.types.VehiclePlan;
import afc.mw.MissionManager.types.VehiclePlanException;
import afc.mw.MissionManager.types.isobus.CropType;
import afc.mw.MissionManager.types.isobus.CulturalPractice;
import afc.mw.MissionManager.types.isobus.Grid;
import afc.mw.MissionManager.types.isobus.LineString;
import afc.mw.MissionManager.types.isobus.OperTechPractice;
import afc.mw.MissionManager.types.isobus.OperationTechnique;
import afc.mw.MissionManager.types.isobus.OperationTechniqueReference;
import afc.mw.MissionManager.types.isobus.Point;
import afc.mw.MissionManager.types.isobus.Polygon;
import afc.mw.MissionManager.types.isobus.PrescriptionMap;
import afc.mw.MissionManager.types.isobus.ProcessDataVariable;
import afc.mw.MissionManager.types.isobus.Product;
import afc.mw.MissionManager.types.isobus.TreatmentZone;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.PolygonArea;
import net.sf.geographiclib.PolygonResult;

/**
 * This class provides the methods to parse the global mission plan, both to extract a specific
 * vehicle low-level command sequence plan, and the prescription map for ISOBUS tractors.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionParser {
	    
    private int parsedSequence = 0;
    private long tic;
    private long toc;
    private MissionManagerContext context = MissionManagerContext.getInstance();
    private Logger mmLog = context.mmLog;
    private Logger sciLog = context.sciLog;
        
    /**
     * If some command has a time assigned as NaN, it is replaced with Double.MAX_VALUE to
     * avoid problems in further operations with the mission plan.
     * 
     * @param mission
     * @return
     */
    public Mission replaceNaN(Mission mission) {    	
		tic = System.currentTimeMillis();
		
		if (mission.isSetCommands()) {
			for (Command command : mission.commands) {
				
				if (Double.isNaN(command.relatedTask.speed)) {
					mmLog.log(Level.WARNING, "Command " + command.id + " has related task speed set as NaN. Changed to MAX_VALUE");
					command.relatedTask.speed = Double.MAX_VALUE;
				}
				if (Double.isNaN(command.relatedTask.altitude)) {
					mmLog.log(Level.WARNING, "Command " + command.id + " has related task altitude set as NaN. Changed to MAX_VALUE");
					command.relatedTask.altitude = Double.MAX_VALUE;
				}
				if (Double.isNaN(command.relatedTask.range)) {
					mmLog.log(Level.WARNING, "Command " + command.id + " has related task range set as NaN. Changed to MAX_VALUE");
					command.relatedTask.range = Double.MAX_VALUE;
				}
				if (Double.isNaN(command.relatedTask.taskTemplate.maxSpeed)) {
					mmLog.log(Level.WARNING, "Command " + command.id + " has related task template max speed set as NaN. Changed to MAX_VALUE");
					command.relatedTask.taskTemplate.maxSpeed = Double.MAX_VALUE;
				}					
					
				for (int i = 0; i < command.getParamsSize(); i++) {
					if (Double.isNaN(command.params.get(i))) {
						mmLog.log(Level.WARNING, "Command " + command.id + " has max param #" + i + " set as NaN. Changed to MAX_VALUE");
						command.params.set(i, Double.MAX_VALUE);
					}
				}
			}
		}
		toc = System.currentTimeMillis() - tic;
		sciLog.log(Level.INFO, this.getClass().getSimpleName() + ",removeNaN," + toc);
    	return mission;
    }
    
    /**
     *  Parses the global mission plan to extract the vehicle plan for the requested vehicle.
     *  
     * @param vehicle					Vehicle of interest
     * @param missionPlan				Global mission plan
     * @return							Vehicle plan
     * @throws VehiclePlanException		If an error occurs parsing the global mission plan for vehicle
     */
	public VehiclePlan parseMission(Vehicle vehicle, Mission missionPlan) throws VehiclePlanException {
		tic = System.currentTimeMillis();

		VehiclePlan vehiclePlan = new VehiclePlan();
		
		ArrayList<Command> vehicleCommands = new ArrayList<Command>();
		
		for (Command command : missionPlan.commands) {
			if (command.relatedTask.assignedVehicleId == vehicle.id) {
				vehicleCommands.add(command);
			}
		}
		
		Collections.sort(vehicleCommands, new CommandStartTimeComparator());
		
		vehiclePlan.sequence_number = ++parsedSequence;
		vehiclePlan.mission_id = missionPlan.missionId;
		vehiclePlan.vehicle_id = vehicle.id;
		vehiclePlan.maximum_linear_speed = vehicle.maxSpeed;
		if (missionPlan.isSetCommands()) {
			for (Command command : vehicleCommands) {
				ParsedCommand pCommand = new ParsedCommand();
				pCommand.command_id = command.id;
				pCommand.command_type_id = command.commandType.getValue();
				for (Double param : command.params) {
					pCommand.param_array.add(param);
				}
				if (pCommand.param_array.size() == 0) {
					toc = System.currentTimeMillis() - tic;
					sciLog.log(Level.INFO, "exit,exception,no_params," + toc);
					throw new VehiclePlanException("Mission " + missionPlan.missionId + ": Vehicle " + vehicle.id + " (" + vehicle.type + ") command " + command.id + " has no params assigned.");
				}
				vehiclePlan.command_array.add(pCommand);
			}

			if (vehiclePlan.command_array.size() == 0) {
				toc = System.currentTimeMillis() - tic;
				sciLog.log(Level.INFO, "exit,exception,no_commands_vehicle," + toc);
				throw new VehiclePlanException("Mission " + missionPlan.missionId + ": Vehicle " + vehicle.id + " (" + vehicle.type + ") has no commands assigned.");
			}

			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,ok,," + toc);
			mmLog.log(Level.INFO, "MissionParser: Parsed plan for vehicle " + vehicle.id + " with " + vehiclePlan.command_array.size() + " commands");
			return vehiclePlan;
		}
		else {
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,exception,no_commands_mission" + toc);
			throw new VehiclePlanException("Mission " + missionPlan.missionId + ": has no commands assigned.");
		}
	}
	
	/**
	 * Parses the global mission plan for extracting the prescription map associated to a given vehicle.
	 * 
	 * @param vehicle				The requested vehicle
	 * @param missionPlan			The global mission plan
	 * @return						The associated prescription map
	 * @throws VehiclePlanException	If it was not possible to extract the prescription map
	 */
	public PrescriptionMap parsePrescriptionMap(Vehicle vehicle, Mission missionPlan) throws VehiclePlanException {
		PrescriptionMap prescriptionMap = new PrescriptionMap();		
		// STEP 1: Extract the tasks assigned to the vehicle in the mission plan
		ArrayList<Task> missionTasks = new ArrayList<Task>();
		for (Task task : missionPlan.tasks) {
			if (task.assignedVehicleId == vehicle.id) {
				if (task.isSetTreatmentGrids()) {
					missionTasks.add(task);
				}
			}
		}
		
		// STEP 2: Validate the list of tasks
		if (missionTasks.isEmpty()) {
			mmLog.log(Level.SEVERE, "Vehicle " + vehicle.name + "(" + vehicle.id + ") has no tasks assigned for prescription map!");
			throw new VehiclePlanException("Parsing prescription map with no tasks assigned.");
		}

		if (missionTasks.size() > 1) {
			mmLog.log(Level.WARNING, "Vehicle " + vehicle.name + "(" + vehicle.id + ") has more than one task. Prescription maps for Y2 should have only one.");
		}
		
		// STEP 3: Assign values to prescription map
		// NOTE: Most values are fixed for Y2

		// Customer
		prescriptionMap.customer.customerId = "CTR1";
		prescriptionMap.customer.customerDesignator = "Customer Designator #1";
		
		// Farm
		prescriptionMap.farm.farmId = "FRM1";
		prescriptionMap.farm.farmDesignator = "Farm Designator #1";
		prescriptionMap.farm.customerIdRef = "CTR1";
		
		// Operation Technique
		prescriptionMap.operationTechnique.add(new OperationTechnique("OTQ7", "TOFERTILIZE"));

		// Partfield (For Y2 only one partfield is included in the prescription map)
		prescriptionMap.partfield.partfieldId = "PFD1";
		prescriptionMap.partfield.partfieldDesignator = "Partfield Designator #1";
		prescriptionMap.partfield.farmIdRef = "FRM1";
		prescriptionMap.partfield.cropTypeIdRef = "CTP1";
		
		Region perimeter = missionTasks.get(0).area;
		
		if (missionTasks.get(0).isSetPartfields()) {
			if (missionTasks.get(0).partfields.get(0).isSetIsoId()) {
				prescriptionMap.partfield.partfieldId = missionTasks.get(0).partfields.get(0).isoId;				
			}

			if (missionTasks.get(0).partfields.get(0).isSetName()) {
				prescriptionMap.partfield.partfieldDesignator = missionTasks.get(0).partfields.get(0).name;				
			}
			
			if (missionTasks.get(0).partfields.get(0).isSetBorderPoints()) {
				perimeter = missionTasks.get(0).partfields.get(0).borderPoints;
			}
		}
		
		prescriptionMap.partfield.partfieldArea = calculatePartfieldArea(perimeter);
		
		prescriptionMap.partfield.polygon = new Polygon();
		prescriptionMap.partfield.polygon.polygonType = 1;
		prescriptionMap.partfield.polygon.lineString = new LineString();
		prescriptionMap.partfield.polygon.lineString.lineStringType = LineString.POLYGON_EXTERIOR;
		
		double minLongitude = Double.MAX_VALUE;
		double maxLongitude = -Double.MIN_VALUE;
		double minLatitude = Double.MAX_VALUE;
		double maxLatitude = -Double.MAX_VALUE;
		
		for (Position position : perimeter.area) {
			prescriptionMap.partfield.polygon.lineString.point.add(new Point(Point.OTHER, position.latitude, position.longitude));
			// Assuming a rectangular perimeter for Y2.
			// Complex perimeters would require to search for the closest position to the minimum coords
			minLongitude = Double.min(minLongitude, position.longitude);
			maxLongitude = Double.max(maxLongitude, position.longitude);
			minLatitude = Double.min(minLatitude, position.latitude);
			maxLatitude= Double.max(maxLatitude, position.latitude);
		}
		
		// For further use in the task parameter of the prescription map
		// Again, assuming a rectangular perimeter for Y2
		Position minimumPosition = new Position(minLongitude, minLatitude, 0);
		double gridWidth = maxLongitude - minLongitude;
		double gridHeight = maxLatitude - minLatitude;
		
		// Product
		prescriptionMap.product.add(new Product("PDT1", "Wasser"));
		
		// Crop Type
		prescriptionMap.cropType.add(new CropType("CTP1", "Crop Type Designator #1"));
		
		// Cultural Practice
		CulturalPractice culturalPractice = new CulturalPractice();
		culturalPractice.culturalPracticeId = "CPC1";
		culturalPractice.culturalPracticeDesignator = "Cultural Practice Designator #1";
		culturalPractice.operationTechniqueReference.add(new OperationTechniqueReference("OTQ7"));
		prescriptionMap.culturalPractice.add(culturalPractice);
		
		// Worker
		prescriptionMap.worker.workerId = "WKR1";
		prescriptionMap.worker.workerDesignator = "Worker Designator #1";
		
		// Task
		// NOTE: For Y2 only one task is considered per prescription map.
		afc.mw.MissionManager.types.isobus.Task task = new afc.mw.MissionManager.types.isobus.Task();
		task.taskId = "TSK1";
		task.taskDesignator = "Task Designator #1";
		task.customerIdRef = "CTR1";
		task.farmIdRef = "FRM1";
		task.partfieldIdRef = "PFD1";
		task.responsibleWorkerIdRef = "WKR1";
		task.defaultTreatmentZoneCode = 1;
		task.taskStatus = 1;
		task.positionLostTreatmentZoneCode = 2;
		task.outOfFieldTreatmentZoneCode = 3;
		
		TreatmentZone treatmentZone = new TreatmentZone();
		treatmentZone.treatmentZoneCode = 0;
		treatmentZone.treatmentZoneDesginator = "SiteSpecific";
		treatmentZone.processDataVariable.add(new ProcessDataVariable(0, "mm3/m2", "PDT1"));
		task.treatmentZone.add(treatmentZone);
		
		task.operTechPractice = new OperTechPractice("CPC1");
		
		task.grid = new Grid();
		task.grid.gridMinimumNorthPosition = minimumPosition.latitude;
		task.grid.gridMinimumEastPosition = minimumPosition.longitude;
		task.grid.gridCellNorthSize = gridHeight / missionTasks.get(0).treatmentGrids.get(0).numRows;
		task.grid.gridCellEastSize = gridWidth / missionTasks.get(0).treatmentGrids.get(0).numCols;
		task.grid.gridMaximumColumn = missionTasks.get(0).treatmentGrids.get(0).numCols;
		task.grid.gridMaximumRow = missionTasks.get(0).treatmentGrids.get(0).numRows;
		task.grid.gridType = 2;
		task.grid.treatmentZoneCode = 0;
		
		for (double value : missionTasks.get(0).treatmentGrids.get(0).treatmentValue) {
			task.grid.gridCell.add(0); // Fixed in Y2 to TreatmentZone 0
		}
		
		prescriptionMap.task.add(task);
		
		return prescriptionMap;
	}

	/**
	 * Exports the global mission plan as a CSV
	 * 
	 * @param requestId		Request ID
	 * @param mission		Mission plan
	 */
	public void exportToCSV (int requestId, Mission mission) {		
    	String datetime = (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(Calendar.getInstance().getTime());
    	String basename = "AFC-CSV-" + datetime + "-" + requestId + "-" + mission.missionId + "-";
    	String navigationAreaFileName = context.current_mission_dir + File.separator + basename + "navigationArea.csv";
		String forbiddenAreaFileName = context.current_mission_dir + File.separator + basename + "forbidenArea.csv";
		String homeLocationFileName = context.current_mission_dir + File.separator + basename + "homeLocation.csv";
		String tasksFileName = context.current_mission_dir + File.separator + basename + "tasks.csv";
		String vehiclesFileName = context.current_mission_dir + File.separator + basename + "vehicles.csv";
		String commandsFileName = context.current_mission_dir + File.separator + basename + "commands.csv";
		
		PrintWriter pw = null;
		int i = 0;
		
		// NAVIGATION AREA
		tic = System.currentTimeMillis();
        try {
            pw = new PrintWriter(navigationAreaFileName);
            pw.println("latitude;longitude;altitude");
            for (Position position : mission.navigationArea.area) {
            	pw.println(position.latitude + ";"
                       + position.longitude + ";"
            		   + position.altitude);
            }
        }
        catch (FileNotFoundException e) {
        	mmLog.log(Level.WARNING, "Unable to store " + navigationAreaFileName);
        }
        catch (Exception e) {
        	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission navigationArea");
        	if (context.debug) {
        		mmLog.log(Level.WARNING, e.getMessage(), e);
        	}
        }
        finally {
            if ( pw != null ) {
                pw.close();
            }
        }
		toc = System.currentTimeMillis();
		sciLog.log(Level.INFO, "exportToCSV,navArea,manual," + tic + "," + toc + "," + (toc-tic));
		
		// FORBIDDEN AREA
        pw = null;
        try {
            pw = new PrintWriter(forbiddenAreaFileName);
            pw.println("area;latitude;longitude;altitude");
            i = 0;
            for (Region region : mission.forbiddenArea) {
                for (Position position : region.area) {
                	pw.println(i + ";"
                           + position.latitude + ";"
                           + position.longitude + ";"
                		   + position.altitude);
                }
                i++;
            }
        }
        catch (FileNotFoundException e) {
        	mmLog.log(Level.WARNING, "Unable to store " + forbiddenAreaFileName);
        }
        catch (Exception e) {
        	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission forbiddenArea");
        	if (context.debug) {
        		mmLog.log(Level.WARNING, e.getMessage(), e);
        	}
        }
        finally {
            if ( pw != null ) {
                pw.close();
            }
        }		
        
		// HOME LOCATION
        pw = null;
        try {
            pw = new PrintWriter(homeLocationFileName);
            pw.println("latitude;longitude;altitude");
            for (Position position : mission.homeLocation) {
            	pw.println(position.latitude + ";"
            			+ position.longitude + ";"
            			+ position.altitude);
            }
        }
        catch (FileNotFoundException e) {
        	mmLog.log(Level.WARNING, "Unable to store " + homeLocationFileName);
        }
        catch (Exception e) {
        	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission homeLocation");
        	if (context.debug) {
        		mmLog.log(Level.WARNING, e.getMessage(), e);
        	}
        }
        finally {
            if ( pw != null ) {
                pw.close();
            }
        }	
        
		// TASKS        
        pw = null;
        try {
            pw = new PrintWriter(tasksFileName);
            pw.println("task_type;description;region_type;equipment (list);max_speed;task_id;mission_id;area;speed;altitude;range (coords);timelapse;bearing (roll,pitch,yaw);start_time;end_time;status;assigned_vehicle_id;parent_task_id");

            for (Task task : mission.tasks) {
            	StringJoiner tuple = new StringJoiner(";");
            	tuple.add(task.taskTemplate.taskType.name());
            	tuple.add(task.taskTemplate.description);
            	tuple.add(task.taskTemplate.regionType.name());

            	StringBuffer equipment = new StringBuffer();
            	equipment.append("(");
            	i = 1;
            	for (EquipmentType equipmentType : task.taskTemplate.requiredTypes) {
            		equipment.append(equipmentType.name());
            		if (i < task.taskTemplate.requiredTypes.size()) {
            			equipment.append(",");
            		}            		
            		i++;
            	}
            	equipment.append(")");
            	
            	tuple.add(equipment.toString());
            	tuple.add(Double.toString(task.taskTemplate.maxSpeed));
            	
            	tuple.add(Integer.toString(task.id));
            	tuple.add(Integer.toString(task.missionId));
            	
            	StringBuffer area = new StringBuffer();
            	area.append("(");
            	i = 1;
            	for (Position position : task.area.area) {
                	area.append("(").append(position.latitude).append(",")
                	    .append(position.longitude).append(",")
                	    .append(position.altitude).append(")");
                	if (i < task.area.area.size()) {
                		area.append(",");
                	}
                	i++;
            	}
            	area.append(")");  

            	tuple.add(area.toString());
            	tuple.add(Double.toString(task.speed));
            	tuple.add(Double.toString(task.altitude));
            	tuple.add(Double.toString(task.range));
            	tuple.add(Integer.toString(task.timeLapse));
            	
            	tuple.add("(" + task.bearing.roll + "," + task.bearing.pitch + "," + task.bearing.yaw + ")");
            	tuple.add(Long.toString(task.startTime));
            	tuple.add(Long.toString(task.endTime));
            	if (task.isSetTaskStatus()) {
            		tuple.add(task.taskStatus.name());
            	}
            	else {
            		tuple.add(null);
            	}
            	tuple.add(Integer.toString(task.assignedVehicleId));
            	tuple.add(Integer.toString(task.parentTaskId));

            	pw.println(tuple.toString());
            }
        }
        catch (FileNotFoundException e) {
        	mmLog.log(Level.WARNING, "Unable to store " + tasksFileName);
        }
        catch (Exception e) {
        	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission tasks list");
        	if (context.debug) {
        		mmLog.log(Level.WARNING, e.getMessage(), e);
        	}
        }
        finally {
            if ( pw != null ) {
                pw.close();
            }
        }
        
		// VEHICLES
        pw = null;
        try {
            pw = new PrintWriter(vehiclesFileName);
            pw.println("id;name;vehicle_type;max_speed;max_running_time;equipments (list);capabilities (list);position;orientation (roll,pitch,yaw);gimbal_pitch;battery_capacty;battery_percentage;linear_speed;last_update");

            for (Vehicle vehicle : mission.vehicles) {
            	StringJoiner tuple = new StringJoiner(";");
            	tuple.add(Integer.toString(vehicle.id));
            	tuple.add(vehicle.name);
            	tuple.add(vehicle.type.name());
            	tuple.add(Double.toString(vehicle.maxSpeed));
            	tuple.add(Integer.toString(vehicle.maxRunningTime));
            	

            	StringBuffer equipmentList = new StringBuffer();
            	equipmentList.append("(");
            	
            	if (vehicle.isSetEquipments()) {
            		i = 1;
            		for (Equipment equipment : vehicle.equipments) {
            			equipmentList.append(equipment.name)
            			.append(" (");
            			
            			if (equipment.isSetType()) {
            				equipmentList.append(equipment.type.name()).append(")");
            			}

            			if (i < vehicle.equipments.size()) {
            				equipmentList.append(",");
            			}
            			i++;
            		}
            	}
            	equipmentList.append(")");
            	tuple.add(equipmentList).toString();

            	
            	StringBuffer capabilitiesList = new StringBuffer();
            	capabilitiesList.append("(");
            	i = 1;
            	if (vehicle.isSetCapabilities()) {
            		for (TaskType taskType : vehicle.capabilities) {
            			capabilitiesList.append(taskType.name());

            			if (i < vehicle.capabilities.size()) {
            				equipmentList.append(",");
            			}
            			i++;
            		}
            	}
            	equipmentList.append(")");
            	tuple.add(capabilitiesList.toString());
            	
            	tuple.add("(" + vehicle.stateVector.position.latitude + ","
            			      + vehicle.stateVector.position.longitude + ","
            			      + vehicle.stateVector.position.altitude + ")");
            	
            	if (vehicle.stateVector.isSetOrientation()) {
            		tuple.add("(" + vehicle.stateVector.orientation.roll + ","
            				+ vehicle.stateVector.orientation.pitch + ","
            				+ vehicle.stateVector.orientation.yaw + ")");
            	}
            	else {
            		tuple.add("()");
            	}
            	
            	tuple.add(Double.toString(vehicle.stateVector.gimbalPitch));
            	tuple.add(Double.toString(vehicle.stateVector.battery.batteryCapacity));
            	tuple.add(Double.toString(vehicle.stateVector.battery.batteryPercentage));
            	tuple.add(Double.toString(vehicle.stateVector.linearSpeed));
            	tuple.add(Long.toString(vehicle.stateVector.lastUpdate));
            	tuple.add(Double.toString(vehicle.safetyDistance));
                
            	pw.println(tuple.toString());
            }
        }
        catch (FileNotFoundException e) {
        	mmLog.log(Level.WARNING, "Unable to store " + vehiclesFileName);
        }
        catch (Exception e) {
        	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission vehicles list");
        	if (context.debug) {
        		mmLog.log(Level.WARNING, e.getMessage(), e);
        	}
        }
        finally {
            if ( pw != null ) {
                pw.close();
            }
        }
        
		// COMMANDS
        if (mission.isSetCommands()) {
        	pw = null;
        	try {
        		pw = new PrintWriter(commandsFileName);
        		pw.println("id;type;start_time;end_time;status;params (list);related_task_type;description;region_type;equipment (list);max_speed;related_task_id;mission_id;area;speed;altitude;range (coords);timelapse;bearing (roll,pitch,yaw);related_task_start_time;related_task_end_time;related_task_status;assigned_vehicle_id;related_task_parent_task_id");

        		for (Command command: mission.commands) {
        			StringJoiner tuple = new StringJoiner(";");
        			tuple.add(Integer.toString(command.id));
        			tuple.add(command.commandType.name());
        			tuple.add(Long.toString(command.startTime));
        			tuple.add(Long.toString(command.endTime));
        			tuple.add(command.commandStatus.name());

        			i = 1;
        			StringBuffer paramList = new StringBuffer();
        			paramList.append("(");
        			for (Double param : command.params) {
        				paramList.append(param);
        				if (i < command.params.size()) {
        					paramList.append(",");
        				}
        				i++;
        			}
        			paramList.append(")");
        			tuple.add(paramList.toString());

        			Task task = command.relatedTask;

        			tuple.add(task.taskTemplate.taskType.name());
        			tuple.add(task.taskTemplate.description);
        			tuple.add(task.taskTemplate.regionType.name());

        			StringBuffer equipment = new StringBuffer();
        			equipment.append("(");
        			i = 1;
        			for (EquipmentType equipmentType : task.taskTemplate.requiredTypes) {
        				equipment.append(equipmentType.name());
        				if (i < task.taskTemplate.requiredTypes.size()) {
        					equipment.append(",");
        				}            		
        				i++;
        			}
        			equipment.append(")");

        			tuple.add(equipment.toString());
        			tuple.add(Double.toString(task.taskTemplate.maxSpeed));

        			tuple.add(Integer.toString(task.id));
        			tuple.add(Integer.toString(task.missionId));

        			StringBuffer area = new StringBuffer();
        			area.append("(");
        			i = 1;
        			for (Position position : task.area.area) {
        				area.append("(").append(position.latitude).append(",")
        				.append(position.longitude).append(",")
        				.append(position.altitude).append(")");
        				if (i < task.area.area.size()) {
        					area.append(",");
        				}
        				i++;
        			}
        			area.append(")");  

        			tuple.add(area.toString());
        			tuple.add(Double.toString(task.speed));
        			tuple.add(Double.toString(task.altitude));
        			tuple.add(Double.toString(task.range));
        			tuple.add(Integer.toString(task.timeLapse));

        			tuple.add("(" + task.bearing.roll + "," + task.bearing.pitch + "," + task.bearing.yaw + ")");
        			tuple.add(Long.toString(task.startTime));
        			tuple.add(Long.toString(task.endTime));
        			if (task.isSetTaskStatus()) {
        				tuple.add(task.taskStatus.name());
        			}
        			else {
        				tuple.add(null);
        			}
        			tuple.add(Integer.toString(task.assignedVehicleId));
        			tuple.add(Integer.toString(task.parentTaskId));

        			pw.println(tuple.toString());
        		}
        	}
        	catch (FileNotFoundException e) {
        		mmLog.log(Level.WARNING, "Unable to store " + commandsFileName);
        	}
            catch (Exception e) {
            	mmLog.log(Level.WARNING, "Unexpected exception accessing to the mission commands list");
            	if (context.debug) {
            		mmLog.log(Level.WARNING, e.getMessage(), e);
            	}
            }
        	finally {
        		if ( pw != null ) {
        			pw.close();
        		}
        	}
        }
	}
	
	/**
	 * Calculates the partfield area for a prescription map given a perimeter
	 * 
	 * @param perimeter	Perimeter
	 * @return			Partfield area
	 */
	private long calculatePartfieldArea(Region perimeter) {
	    PolygonArea polygon = new PolygonArea(Geodesic.WGS84, false);
		
		for (Position position : perimeter.area) {
			polygon.AddPoint(position.latitude, position.longitude);
		}

		PolygonResult r = polygon.Compute();

		return (long) Math.abs(Math.round(r.area));
	}
}
