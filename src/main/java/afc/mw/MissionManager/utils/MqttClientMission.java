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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.afarcloud.thrift.Vehicle;

import afc.mw.MissionManager.MissionManager;
import afc.mw.MissionManager.MissionManagerContext;

/**
 * This class provides a singleton MQTT client for mission dispatching.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MqttClientMission {
	
	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	private Logger sciLog = context.sciLog;
	
	private long tic;
	private long toc;
	
	private static MqttClientMission instance = null;
	
	public MqttClientMission() { }
	
	/**
	 * Gets the singleton instance of the MQTT Client
	 * 
	 * @return
	 */
	public static MqttClientMission getInstance() {
        if(instance == null)
            instance = new MqttClientMission();
        
        return instance;	
	}

	/**
	 * Publish a vehicle mission to the MQTT proxy
	 * 
	 * @param vehicle			Associated vehicle
	 * @param missionName		Mission name
	 * @param jsonMission		Vehicle mission plan
	 * @throws MqttException	If occurs a MQTT error
	 */
	public void publishMission(Vehicle vehicle, String missionName, String jsonMission) throws MqttException {
		String broker   = context.mqttProtocol + "://" + context.mqttServer + ":" + context.mqttPort;
		String userName = context.mqttUser;
		String password = context.mqttPass;
		String[] topicLevels = context.mqttTopicMission.split("/");

		String topic = topicLevels[0] + "/" +
		               topicLevels[1] + "/" +
				       missionName + "/" +
		               vehicle.type + "/" +
				       vehicle.name + "/" +
		               topicLevels[5];		               
		
		MemoryPersistence persistence = new MemoryPersistence();

		MqttClient client = new MqttClient(broker, context.mqttClientID, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();

		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", "TLS");
		sslProperties.setProperty("com.ibm.ssl.trustStore", "mqttTrustStore.jks");
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", "qwerty");
		connOpts.setSSLProperties(sslProperties);

		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		mmLog.log(Level.INFO, "Publish new mission - Connecting to MQTT server...");
		tic = System.currentTimeMillis();
		client.connect(connOpts);
		toc = System.currentTimeMillis();
		sciLog.log(Level.INFO, ",publishMission,connect," + tic + "," + toc + "," + (toc-tic));
		mmLog.log(Level.INFO, "Publish new mission - Connected!");

        MqttMessage message = new MqttMessage(jsonMission.getBytes());
        message.setQos(1);
        message.setRetained(context.mqttRetainedMission);
        
        mmLog.log(Level.INFO, "Publish new mission - Publishing mission to topic " + topic);
        tic = System.currentTimeMillis();
        client.publish(topic, message);
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishMission,publish," + topic + "," + jsonMission.length() + "," + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish new mission - New mission published!");

        mmLog.log(Level.INFO, "Publish new mission - Disconnecting from MQTT server...");
        tic = System.currentTimeMillis();
        client.disconnect();
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishMission,disconnect,"  + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish new mission - Disconnected!");
	}

	/**
	 * Publish an soft abort plan request for the selected vehicle
	 * 
	 * @param vehicle			The vehicle
	 * @param missionName		The mission name
	 * @throws MqttException	If occurs a MQTT error
	 */
	public void publishAbort(Vehicle vehicle, String missionName) throws MqttException {
		String broker   = context.mqttProtocol + "://" + context.mqttServer + ":" + context.mqttPort;
		String userName = context.mqttUser;
		String password = context.mqttPass;
		String[] topicLevels = context.mqttTopicMission.split("/");

		String topic = topicLevels[0] + "/" +
		               topicLevels[1] + "/" +
				       missionName + "/" +
		               vehicle.type + "/" +
				       vehicle.name + "/" +
		               "event";
		
		MemoryPersistence persistence = new MemoryPersistence();

		MqttClient client = new MqttClient(broker, context.mqttClientID, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();

		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", "TLS");
		sslProperties.setProperty("com.ibm.ssl.trustStore", "mqttTrustStore.jks");
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", "qwerty");
		connOpts.setSSLProperties(sslProperties);

		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		mmLog.log(Level.INFO, "Publish soft abort vehicle mission for vehicle " + vehicle.id + " - Connecting to MQTT server...");
		tic = System.currentTimeMillis();
		client.connect(connOpts);
		toc = System.currentTimeMillis();
		sciLog.log(Level.INFO, "publishAbort," + vehicle.id + ",connect," + tic + "," + toc + "," + (toc-tic));
		mmLog.log(Level.INFO, "Publish soft abort vehicle mission (vehicleID:" + vehicle.id+ ") - Connected!");

		String request = Json.createObjectBuilder()
			     .add("sequence_number", context.getNewSequenceNumber())
			     .add("vehicle_id", vehicle.id)
			     .add("event_type_id", MissionManager.ABORT_SOFT)
			     .add("param_array", Json.createArrayBuilder()
			    		 .add(0)
			    		 .add(0)
			    		 .add(0))
			     .build().toString();
		MqttMessage message = new MqttMessage(request.getBytes());
		    
        mmLog.log(Level.INFO, "Publish soft abort vehicle mission - Publishing to topic " + topic);
        tic = System.currentTimeMillis();
        client.publish(topic, message);
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishAbort,publish," + topic + "," + vehicle.id + ","  + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish soft abort vehicle mission - Published!");

        mmLog.log(Level.INFO, "Publish soft abort vehicle mission - Disconnecting from MQTT server...");
        tic = System.currentTimeMillis();
        client.disconnect();
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishAbort,disconnect," + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish soft abort vehicle mission - Disconnected!");		
	}

	/**
	 * Publish a hard abort plan request for the selected vehicle
	 * 
	 * @param vehicle			The vehicle
	 * @param missionName		The mission name
	 * @throws MqttException	If occurs a MQTT error
	 */
	public void publishAbortHard(Vehicle vehicle, String missionName) throws MqttException {
		String broker   = context.mqttProtocol + "://" + context.mqttServer + ":" + context.mqttPort;
		String userName = context.mqttUser;
		String password = context.mqttPass;
		String[] topicLevels = context.mqttTopicMission.split("/");

		String topic = topicLevels[0] + "/" +
		               topicLevels[1] + "/" +
				       missionName + "/" +
		               vehicle.type + "/" +
				       vehicle.name + "/" +
		               "event";
		MemoryPersistence persistence = new MemoryPersistence();

		MqttClient client = new MqttClient(broker, context.mqttClientID, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();

		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", "TLS");
		sslProperties.setProperty("com.ibm.ssl.trustStore", "mqttTrustStore.jks");
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", "qwerty");
		connOpts.setSSLProperties(sslProperties);

		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		mmLog.log(Level.INFO, "Publish hard abort vehicle mission for vehicle " + vehicle.id + " - Connecting to MQTT server...");
		tic = System.currentTimeMillis();
		client.connect(connOpts);
		toc = System.currentTimeMillis();
		sciLog.log(Level.INFO, "publishAbortHard," + vehicle.id + ",connect," + tic + "," + toc + "," + (toc-tic));
		mmLog.log(Level.INFO, "Publish hard abort vehicle mission (vehicleID:" + vehicle.id + ") - Connected!");

		String request = Json.createObjectBuilder()
			     .add("sequence_number", context.getNewSequenceNumber())
			     .add("vehicle_id", vehicle.id)
			     .add("event_type_id", MissionManager.ABORT_HARD)
			     .add("param_array", Json.createArrayBuilder()
			    		 .add(0)
			    		 .add(0)
			    		 .add(0))
			     .build().toString();
		MqttMessage message = new MqttMessage(request.getBytes());
		    
        mmLog.log(Level.INFO, "Publish hard abort vehicle mission - Publishing to topic " + topic);
        tic = System.currentTimeMillis();
        client.publish(topic, message);
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishAbortHard,publish," + topic + "," + vehicle.id + "," + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish hard abort vehicle mission - Published!");

        mmLog.log(Level.INFO, "Publish hard abort vehicle mission - Disconnecting from MQTT server...");
        tic = System.currentTimeMillis();
        client.disconnect();
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishAbortHard,disconnect," + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Publish hard abort vehicle mission - Disconnected!");		
	}
}