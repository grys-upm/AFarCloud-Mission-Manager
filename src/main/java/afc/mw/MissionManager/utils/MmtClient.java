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

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.afarcloud.thrift.MmtService;

import afc.mw.MissionManager.MissionManagerContext;

/**
 * Thrfit client (singleton) to access to the MMT services
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MmtClient {
	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	
	private static MmtClient instance = null;
	
	private MmtClient() {}
	
	/**
	 * Gets an instance of the MMT Thrift client
	 * 
	 * @return The singleton instance of the MMT client
	 */
	public static MmtClient getInstance() {
		if (instance == null) {
			instance = new MmtClient();
		}
		
		return instance;
	}
	
	/**
	 * Sends the results of the mission validation to the MMT
	 * 
	 * @param result The results of the mission validation
	 */
	public void sendValidationResult(ValidationResult result) {
		mmLog.log(Level.INFO, "Sending validation result to MMT: (" + result.getCode() + ") " + result.getDescription());

		if (context.mmtEnabled) {
			sendPing();
			
			switch(result) {
        	// UNSOLVABLE ERRORS
        	case MISSION_NULL:
        	case MISSION_EMPTY:    	
        	case NO_MISSION_ID:
        	case NO_NAVIGATION:
        	case NO_VEHICLES:
        	case NO_TASKS:
        	case NO_COMMANDS:
        	case NO_PRESCRIPTION_MAP:
        		sendError(result);
        		break;
        	// SOLVABLE ERRORS
        	case NO_MISSION_NAME:
        	case NO_HOME_LOCATION:
        	case NO_FORBIDDEN_AREA:
        	// NO ERRORS, BUT WARNINGS
        	case NO_COMMANDS_WARN:
        	case NO_PRESCRIPTION_MAP_WARN:
        	case NO_COMMANDS_PM_WARN:
        	// NO ERRORS
        	case VALID:
        		break;
        	default:
        		mmLog.log(Level.SEVERE, "Unknown validation result notificarion. Please contact for more information");
			}
		}
		else {
			mmLog.log(Level.INFO, "Notifications to the MMT are disabled.");
		}
	}
	
	/**
	 * Sends a ping to the MMT
	 * 
	 * @return A salutation message
	 */
	public String sendPing() {
		String response = "";
		try {
			mmLog.log(Level.INFO, "Connecting to " + context.mmtIP + " on port " + context.mmtPort);

			TTransport transport = new TSocket(context.mmtIP, context.mmtPort);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);

			MmtService.Client client = new MmtService.Client(protocol);

			mmLog.log(Level.INFO, "Sending ping to " + context.mmtIP + " on port " + context.mmtPort);
			client.send_ping();

			mmLog.log(Level.INFO, "Notification of validation error sent to MMT");
			transport.close();
		}
		catch (Exception e) {
			mmLog.log(Level.WARNING, "Error trying to communicate with the MMT.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}			
		}

		return response;
	}
	    
	/**
	 * Sends an error notification to the MMT, currently limited to the mission plan validation result.
	 * 
	 * @param The validation result to be sent to the MMT
	 */
	public void sendError(ValidationResult result) {		
		try {
			mmLog.log(Level.INFO, "Connecting to " + context.mmtIP + " on port " + context.mmtPort);

			TTransport transport = new TSocket(context.mmtIP, context.mmtPort);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);

			MmtService.Client client = new MmtService.Client(protocol);

			mmLog.log(Level.INFO, "Sending ping to " + context.mmtIP + " on port " + context.mmtPort);
			client.send_ping();
			mmLog.log(Level.INFO, "Sending error " + result.getCode() + " \"" + result.getDescription() + "\" to " + context.mmtIP + " on port " + context.mmtPort);
			client.sendError(context.getNewSequenceNumber(), result.getCode(), result.getDescription());

			mmLog.log(Level.INFO, "Notification of validation error sent to MMT");
			transport.close();
		}
		catch (Exception e) {
			mmLog.log(Level.WARNING, "Error trying to communicate with the MMT.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}			
		}
	}	
}
