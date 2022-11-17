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

package afc.mw.MissionManager.services.thrift;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import org.apache.thrift.TException;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.MissionManagerService;
import com.afarcloud.thrift.Region;

import afc.mw.MissionManager.MissionManager;
import afc.mw.MissionManager.MissionManagerContext;
import afc.mw.MissionManager.utils.MissionParser;

/**
 * Handler for the Mission Manager associated thrift service.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionManagerThriftServiceHandler implements MissionManagerService.Iface {

    private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
    private Logger sciLog = context.sciLog;
    private long tic1;
    private long tic2;
    private long toc1;
    private long toc2;
    
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(false).withFormatting(true));

    /**
     * Thrift handler for the reception of a mission plan from the MMT.
     * 
     */
    @Override
	public void sendPlan(int requestId, Mission plan) {
    	
    	(new Thread() {
    		public void run() {

    			tic1 = System.currentTimeMillis();

    			mmLog.log(Level.INFO, "Received new mission plan with requestId: " + requestId + ", and missionId: " + plan.missionId);

    			// STEP -1: Workaround for bad formed missions with no forbidden areas and/or no mission names.
    			if (!plan.isSetForbiddenArea()) {
    				mmLog.log(Level.WARNING, "Received mission has not set a forbidden area. Setting an empty one.");
    				plan.forbiddenArea = new ArrayList<Region>();
    			}
    			
    			if (!plan.isSetName()) {
    				mmLog.log(Level.WARNING, "Received mission has not set a mission name");
    				plan.name = "unnamedMission";
    			} 			

    			String date  = (new SimpleDateFormat("yyyyMMdd")).format(Calendar.getInstance().getTime());
    			String time = (new SimpleDateFormat("HHmmss")).format(Calendar.getInstance().getTime());
    			context.current_mission_dir = context.mission_base_dir + File.separator + date;
    			String filenamebase = context.current_mission_dir + File.separator
    					+ "AFC-Mission-" + date + "-" + time + "-" + plan.missionId + "-" + requestId;

    			// STEP 0: Check if missions directory exists, and create it if not
    			File dirMissions = new File(context.mission_base_dir);
    			if (! dirMissions.exists()){
    				dirMissions.mkdir();
    			}

    			File dirCurrentMission = new File(context.current_mission_dir);
    			if (! dirCurrentMission.exists()){
    				dirCurrentMission.mkdir();
    			}
    			// STEP 1: Store the received mission as a serialized object
    			tic2 = System.currentTimeMillis();
    			try {
    				FileOutputStream fos = new FileOutputStream(filenamebase + ".ser");
    				ObjectOutputStream oos = new ObjectOutputStream(fos);

    				oos.writeObject(plan);
    				oos.close();

    			} 
    			catch (IOException e) {
    				mmLog.log(Level.WARNING, "Error saving the mission received from the MMT as serialized data." );
    				if (MissionManagerContext.getInstance().debug) {
    					mmLog.log(Level.SEVERE, e.getMessage(), e);
    				}
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,serialized,exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}
    			finally {
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,serialized,normal," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}

    			// STEP 2: Store the UNMODIFIED received mission as separate CSV files (one per attribute list in the mission)
    			tic2 = System.currentTimeMillis();
    			MissionParser parser = new MissionParser();    	
    			parser.exportToCSV(requestId, plan);
    			toc2 = System.currentTimeMillis();
    			sciLog.log(Level.INFO, "sendPlan,exportToCSV," + tic2 + "," + toc2 + "," + (toc2 - tic2));    	

    			// STEP 3: Replace NaN values in the mission plan to allow conversion to JSON
    			tic2 = System.currentTimeMillis();
    			Mission mission = parser.replaceNaN(plan);
    			toc2 = System.currentTimeMillis();
    			sciLog.log(Level.INFO, "sendPlan,replaceNaN," + tic2 + "," + toc2 + "," + (toc2 - tic2));

    			// STEP 4: Store the received mission with the replaced NaN as a JSON file
    			tic2 = System.currentTimeMillis();
    			try {    		    		
    				jsonb.toJson(mission, new FileWriter(filenamebase + ".json"));
    			}
    			catch (JsonbException e) {
    				mmLog.log(Level.WARNING, "Error parsing the mission received from the MMT as JSON");
    				if (MissionManagerContext.getInstance().debug) {
    					mmLog.log(Level.SEVERE, e.getMessage(), e);
    				}
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,json,exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}
    			catch (IOException e) {
    				mmLog.log(Level.WARNING, "Error saving the mission received from the MMT as JSON.");
    				if (MissionManagerContext.getInstance().debug) {
    					mmLog.log(Level.SEVERE, e.getMessage(), e);
    				}
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,json,exception,"  + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}
    			catch (Exception e) {
    				mmLog.log(Level.WARNING, "Unexpected error parsing the mission as JSON.");
    				if (MissionManagerContext.getInstance().debug) {
    					mmLog.log(Level.SEVERE, e.getMessage(), e);
    				}
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,json,exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}
    			finally {
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,storePlan,json,normal," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}

    			// STEP 5: Call the startMission method from the Mission Manager to handle the request
    			tic2 = System.currentTimeMillis();
    			try {
    				MissionManager.getInstance().startMission(requestId, mission);
    			} 
    			catch (InterruptedException e) {
    				mmLog.log(Level.WARNING, "Error passing the plan to the MissionManager.");
    				if (MissionManagerContext.getInstance().debug) {
    					mmLog.log(Level.SEVERE, e.getMessage(), e);
    				}
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,startMission,exception," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}
    			finally {
    				toc2 = System.currentTimeMillis();
    				sciLog.log(Level.INFO, "sendPlan,startMission,normal," + tic2 + "," + toc2 + "," + (toc2 - tic2));
    			}    	
    			toc1 = System.currentTimeMillis();
    			sciLog.log(Level.INFO, "sendPlan,full,normal," + tic1 + "," + toc1 + "," + (toc1 - tic1));
		
    		}
    	}).start();
    }

    /**
     * Thrift handler for soft abort a vehicle plan
     */
    @Override
    public String abortVehiclePlan(int requestId, int vehicleId) {
        mmLog.log(Level.INFO, "Received ABORT VEHICLE PLAN for vehicleId " + vehicleId);        
    	return MissionManager.getInstance().abortVehiclePlan(vehicleId);
    }
    
    /**
     * Thrift handler for soft abort a mission plan
     */
    @Override
    public String abortMissionPlan(int requestId, int missionId) {
        mmLog.log(Level.INFO, "Received ABORT MISSION PLAN for missionId " + missionId);        
    	return MissionManager.getInstance().abortMissionPlan(missionId);
    }
    
    /**
     * Thrift handler for hard abort a mission plan
     */
    @Override
    public String abortVehiclePlanHard(int requestId, int vehicleId) {
        mmLog.log(Level.INFO, "Received ABORT VEHICLE PLAN (HARD) for vehicleId " + vehicleId);        
    	return MissionManager.getInstance().abortVehiclePlanHard(vehicleId);
    }
    
    /**
     * Thrift handler for hard abort a mission plan
     */
    @Override
    public String abortMissionPlanHard(int requestId, int missionId) {
        mmLog.log(Level.INFO, "Received ABORT MISSION PLAN (HARD) for missionId " + missionId);        
    	return MissionManager.getInstance().abortMissionPlanHard(missionId);
    }
    
    /**
     * The almighty ping!
     */
    @Override
    public String ping() throws TException {
        mmLog.log(Level.INFO, "Ping requested!");        
        return "Pong from MissionManagerService";
    }

}
