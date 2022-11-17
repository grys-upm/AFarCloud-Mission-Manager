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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import afc.mw.MissionManager.MissionManagerContext;

/**
 * This class provides a singleton MQTT client for the AFC System Configuration
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MqttClientSystemConfiguration {
	
	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	private Logger sciLog = context.sciLog;
	
	private long tic;
	private long toc;
	
	private MqttClient client;
	
	private static MqttClientSystemConfiguration instance = null;
	
	public MqttClientSystemConfiguration() { }
	
	/**
	 * Gets the instance of the MQTT client
	 * 
	 * @return The MQTT client for the AFD System Configuration
	 */
	public static MqttClientSystemConfiguration getInstance() {
        if(instance == null)
            instance = new MqttClientSystemConfiguration();
        
        return instance;	
	}

	/**
	 * Publish a System Configuration request
	 * 
	 * @param requestID			Request ID
	 * @param maxCount			Max number of available vehicles
	 * @param timeOut			Timeout for the request
	 * @throws MqttException	If occurs a MQTT error
	 */
	public void publishSCRequest(int requestID, int maxCount, long timeOut) throws MqttException {
		String broker   = context.mqttProtocol + "://" + context.mqttServer + ":" + context.mqttPort;
		String userName = context.mqttUser;
		String password = context.mqttPass;
		String pubTopic = context.mqttTopicSystemConfiguration;
		String subTopic = context.mqttTopicSystemConfigurationReport;

		MemoryPersistence persistence = new MemoryPersistence();

		client = new MqttClient(broker, context.mqttClientID, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();

		Properties sslProperties = new Properties();
		sslProperties.setProperty("com.ibm.ssl.protocol", "TLS");
		sslProperties.setProperty("com.ibm.ssl.trustStore", "mqttTrustStore.jks");
		sslProperties.setProperty("com.ibm.ssl.trustStorePassword", "qwerty");
		connOpts.setSSLProperties(sslProperties);

		connOpts.setUserName(userName);
		connOpts.setPassword(password.toCharArray());

		mmLog.log(Level.INFO, "Requesting vehicle status update - Connecting to MQTT server...");
		tic = System.currentTimeMillis();
		client.connect(connOpts);
		toc = System.currentTimeMillis();
		sciLog.log(Level.INFO, "publishSCRequest,connect," + tic + "," + toc + "," + (toc-tic));
		mmLog.log(Level.INFO, "Requesting vehicle status update - Connected!");

		String request = Json.createObjectBuilder()
			     .add("sequence_number", requestID).build().toString();
        MqttMessage message = new MqttMessage(request.getBytes());
        message.setQos(1);
        message.setRetained(context.mqttRetainedSystemConfiguration);
        
        mmLog.log(Level.INFO, "Requesting vehicle status update - Publishing to topic " + pubTopic);
        tic = System.currentTimeMillis();
        client.publish(pubTopic, message);
        toc = System.currentTimeMillis();
        sciLog.log(Level.INFO, "publishSCRequest,publish," + pubTopic + "," + request.length() + ","  + tic + "," + toc + "," + (toc-tic));
        mmLog.log(Level.INFO, "Requesting vehicle status update - Published!");

        (new Thread() {
        	public void run() {
        		CountDownLatch receivedSignal = new CountDownLatch(maxCount);
        		try {
    				mmLog.log(Level.INFO, this.getClass().getSimpleName() + ": Subscribing to " + subTopic);
        			client.subscribe(subTopic, (receivedTopic, msg) -> {
        				mmLog.log(Level.INFO, this.getClass().getSimpleName() + ": Received MQTT message for topic " + receivedTopic + ":\n" + new String(msg.getPayload()));
        				receivedSignal.countDown();
        			});
        			receivedSignal.await(timeOut, TimeUnit.SECONDS);
        			
        			if (receivedSignal.getCount() > 0) {        				
        				mmLog.log(Level.INFO, "Timeout awaiting for vehicles resposes.");
        				SCRequestManager.getInstance().setTimedout();
        			}
        			else {
        				mmLog.log(Level.INFO, "Received responses from all vehicles.");
        				SCRequestManager.getInstance().setCompleted();
        			}
        		} catch (MqttException e) {
        			mmLog.log(Level.WARNING, "Unhandled MqttException while awaiting for publications.");
        			if (context.debug) {
    					mmLog.log(Level.WARNING, e.getMessage(), e);				
        			}
        		} catch (InterruptedException e) {
        			mmLog.log(Level.WARNING, "Unhandled InterruptedException while awaiting for publications.");
        			if (context.debug) {
    					mmLog.log(Level.WARNING, e.getMessage(), e);				
        			}
        		} 
        	}
        }).start();
	}
}