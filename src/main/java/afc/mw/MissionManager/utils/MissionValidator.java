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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.afarcloud.thrift.Mission;
import com.afarcloud.thrift.Vehicle;

import afc.mw.MissionManager.MissionManagerContext;

/**
 * This class provides the methods to validate a mission plan.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionValidator {
	
	private Logger mmLog = MissionManagerContext.getInstance().mmLog;
	private Logger sciLog = MissionManagerContext.getInstance().sciLog;
	private long tic;
	private long toc;
	private static MissionValidator instance = null;
	
	private MissionValidator() {
		super();
	}
	
	/**
	 * Gets an instance of the Mission Validator.
	 * 
	 * @return	The singleton instance of the Mission Validator
	 */
    public static MissionValidator getInstance() {
        if(instance == null)
            instance = new MissionValidator();
        
        return instance;	
    }
    
    /**
     * Validates a mission
     * 
     * @param mission The mission to be validated
     * @return		  The validation result
     */
    public ValidationResult validate(Mission mission) {
		tic = System.currentTimeMillis();

		// SIMPLE SEVERE VALIDATION ERRORS
		
    	// Mission unassigned (null)
    	if (mission == null) {
    		mmLog.log(Level.SEVERE, ValidationResult.MISSION_NULL.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,mission_null," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.MISSION_NULL;
    	}

    	// Mission ID
    	if (!mission.isSetMissionId()) {
    		mmLog.log(Level.SEVERE, ValidationResult.NO_MISSION_ID.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_mission_id," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_MISSION_ID;
    	}
    	
    	// Navigation Area
    	if (!mission.isSetNavigationArea()) {
    		mmLog.log(Level.SEVERE, ValidationResult.NO_NAVIGATION.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_navigation_area," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_NAVIGATION;
    	}
    	
    	// Vehicles
    	if (!mission.isSetVehicles()) {
    		mmLog.log(Level.SEVERE, ValidationResult.NO_VEHICLES.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_vehicles," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_VEHICLES;
    	}
    	
    	// Tasks
    	if (!mission.isSetTasks()) {
    		mmLog.log(Level.SEVERE, ValidationResult.NO_TASKS.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_tasks," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_TASKS;    		
    	}
    	
    	// SIMPLE REPARABLE ERRORS
    	
    	// Mission name
    	if (!mission.isSetName()) {
    		mmLog.log(Level.WARNING, ValidationResult.NO_MISSION_NAME.getDescription());
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_mission_name," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_MISSION_NAME;    		
    	}

    	// SIMPLE IGNORABLE ERRORS
    	
    	// Home location
    	if (!mission.isSetHomeLocation()) {
    		mmLog.log(Level.WARNING, ValidationResult.NO_HOME_LOCATION.getDescription());   		
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_home_location," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_HOME_LOCATION; 
    	}
    	
    	// Forbidden area (MANDATORY FIELD FOR Y2)
    	if (!mission.isSetForbiddenArea()) {
    		mmLog.log(Level.WARNING, ValidationResult.NO_FORBIDDEN_AREA.getDescription());   		
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,invalid,no_forbidden_area," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_FORBIDDEN_AREA; 
    	}    	
    	
    	// COMPLEX ERRORS
    	
    	boolean hasUVs = false;
    	boolean hasTractors = false;
    	
    	for (Vehicle vehicle : mission.vehicles) {
    		switch (vehicle.type) {
        	case AUAV:
        	case RUAV:
        	case UAV:
        	case AGV:
        	case UGV:
        		hasUVs = true;
        		break;
        	case RGV:
        	case Tractor:
        		hasTractors = true;
        		break;
    		}
    	}
    	
    	// COMPLEX SEVERE ERRORS
    	
    	// Mission with only UVs and no commands
    	if (hasUVs && !hasTractors && !mission.isSetCommands()) {
    		mmLog.log(Level.WARNING, ValidationResult.NO_COMMANDS.getDescription());   		
    		toc = System.currentTimeMillis() - tic;
    		sciLog.log(Level.INFO, "exit,invalid,no_commands," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_COMMANDS; 
    	}

    	// Mission with only tractors and no prescription maps
    	if (!hasUVs && hasTractors && !mission.tasks.stream().anyMatch(t -> t.isSetTreatmentGrids())) {    		
    		mmLog.log(Level.WARNING, ValidationResult.NO_PRESCRIPTION_MAP.getDescription());   		
    		toc = System.currentTimeMillis() - tic;
    		sciLog.log(Level.INFO, "exit,invalid,no_prescription_map," + tic + "," + toc + "," + (toc-tic));
    		return ValidationResult.NO_PRESCRIPTION_MAP;    		
    	}
    	
    	// COMPLEX WARNINGS (Just logging)
    	
    	// Mission with unmanned vehicles without commands
    	boolean failUV = false;
    	boolean failTractor = false;
    	
    	for (Vehicle vehicle : mission.vehicles) {
    		switch(vehicle.type) {
    		case AUAV:
    		case RUAV:
        	case UAV:
        	case AGV:
        	case UGV:
        		if (!mission.commands.stream().anyMatch(c -> c.relatedTask.assignedVehicleId == vehicle.id)) {
            		mmLog.log(Level.WARNING, ValidationResult.NO_COMMANDS_WARN.getDescription() + " Vehicle ID: " + vehicle.id);   		
            		toc = System.currentTimeMillis() - tic;
            		sciLog.log(Level.INFO, "continue,warning,no_commands_warn," + tic + "," + toc + "," + (toc-tic));
            		failUV = true;
        		}
        		break;
        	case RGV:
        	case Tractor:  
        		if (!mission.tasks.stream().anyMatch(t -> (t.assignedVehicleId == vehicle.id && t.isSetTreatmentGrids()))) {
            		mmLog.log(Level.WARNING, ValidationResult.NO_PRESCRIPTION_MAP_WARN.getDescription() + " Vehicle ID: " + vehicle.id);   		
            		toc = System.currentTimeMillis() - tic;
            		sciLog.log(Level.INFO, "continue,warning,no_prescription_map_warn," + tic + "," + toc + "," + (toc-tic));        			
            		failTractor = true;
        		}
        		break;
        	default:
        		break;
    		}
    	}
    	
    	if (failUV && failTractor) {
    			mmLog.log(Level.WARNING, ValidationResult.NO_COMMANDS_PM_WARN.getDescription());   		
    			toc = System.currentTimeMillis() - tic;
    			sciLog.log(Level.INFO, "exit,warning,no_commands_prescription_map_warn," + tic + "," + toc + "," + (toc-tic));        			
    			return ValidationResult.NO_COMMANDS_PM_WARN;
    	}

    	if (failUV && !failTractor) {
			mmLog.log(Level.WARNING, ValidationResult.NO_COMMANDS_WARN.getDescription());   		
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,warning,no_commands_warn," + tic + "," + toc + "," + (toc-tic));        			
			return ValidationResult.NO_COMMANDS_WARN;
    	}
    	
    	if (!failUV && failTractor) {
			mmLog.log(Level.WARNING, ValidationResult.NO_PRESCRIPTION_MAP_WARN.getDescription());   		
			toc = System.currentTimeMillis() - tic;
			sciLog.log(Level.INFO, "exit,warning,no_prescription_map_warn," + tic + "," + toc + "," + (toc-tic));        			
			return ValidationResult.NO_PRESCRIPTION_MAP_WARN;
    	}
    	  	
		mmLog.log(Level.WARNING, ValidationResult.VALID.getDescription());   		
		toc = System.currentTimeMillis() - tic;
		sciLog.log(Level.INFO, "exit,valid,valid," + tic + "," + toc + "," + (toc-tic));        			
    	return ValidationResult.VALID;
    }
}
