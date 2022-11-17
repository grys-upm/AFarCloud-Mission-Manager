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

import org.eclipse.paho.client.mqttv3.MqttException;

import afc.mw.MissionManager.MissionManagerContext;

/**
 * The SC Request Manager is responsible for managing the responses received for a SC request
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class SCRequestManager {
	public static final int ACCEPTED = 201;
	public static final int IN_PROGRESS = 304; // NOT MODIFIED
	public static final int COMPLETED = 200;
	public static final int MQTT_ERROR = 503;  // SEVICE UNAVAILABLE
	public static final int INTERNAL_ERROR = 500;
	public static final int TIMEOUT = 504; // TIMEOUT
	public static final int CONFLICT = 409;
	public static final int READY = 0;
	
    private static SCRequestManager instance = null;  
    
    MissionManagerContext context = MissionManagerContext.getInstance();
    private Logger mmLog = context.mmLog;
    private int status = READY;
    private int currentRequestID;
    private long requestTimestamp;
    
    private SCRequestManager() { }

    /**
     * Gets the singleton instance of the SC Request Manager
     * 
     * @return The singleton instance of the SC Request Manager
     */
    public static SCRequestManager getInstance() {
            if(instance == null)
                instance = new SCRequestManager();
            
            return instance;	
    }

    /**
     * Returns the current status for the request ID
     * 
     * @param requestID	The ID of the request
     * @return			The current status
     */
	public synchronized int requestVehiclesStatus(int requestID) {
		if ((status == COMPLETED) || (status == TIMEOUT)){
			if (requestID == currentRequestID) {
				mmLog.log(Level.INFO, "Received request (requestID:" + requestID + ") for current request with status " + status + ".");
				return status;
			}
			else {
				mmLog.log(Level.INFO, "Received request (requestID:" + requestID + ") for a new request.");
				status = READY;
			}
			
		}
		else if (status == IN_PROGRESS) {
			if (requestID == currentRequestID) {
				mmLog.log(Level.INFO, "Received request (requestID:" + requestID + ") for current request with status IN PROGRESS ("
									+ ((System.currentTimeMillis() - requestTimestamp)/1000) + " seconds since the start of the request).");
				return status;
			}
			else {
				mmLog.log(Level.INFO, "Received request (requestID:" + requestID + ") while another request (currentID:" + currentRequestID + ") is still IN PROGRESS.");
				return CONFLICT;
			}
		}
		
		if (status == READY) {
			mmLog.log(Level.INFO, "Processing new request (requestID:" + requestID + ") for vehicles status update.");
			currentRequestID = requestID;

			try {
				RestClient.getInstance().getTotalVehiclesFromDB();
				requestTimestamp = System.currentTimeMillis();
				MqttClientSystemConfiguration.getInstance().publishSCRequest(requestID, 1, context.SCRequestTimeout);
			} catch (MqttException e) {
				mmLog.log(Level.WARNING, "An error has occurred while requesting the vehicles status update.");
				if (context.debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);				
				}
				return MQTT_ERROR;
			}
			
			status = IN_PROGRESS;

			return ACCEPTED;
		}
		
		return INTERNAL_ERROR;
	}

	/**
	 * Sets the current status
	 * 
	 * @param status The current status
	 */
	public synchronized void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * Sets the current status to completed
	 */
	public synchronized void setCompleted() {
		this.status = COMPLETED;
		mmLog.log(Level.INFO, "Pre-Mission request " + currentRequestID + " finished: COMPLETED");
	}
	
	/**
	 * Sets the current status to timeout
	 */
	public synchronized void setTimedout() {
		this.status = TIMEOUT;
		mmLog.log(Level.INFO, "Pre-Mission request " + currentRequestID + " finished: TIMEOUT");
	}
}
