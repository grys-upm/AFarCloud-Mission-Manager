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

package afc.mw.MissionManager.services.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import afc.mw.MissionManager.MissionManagerContext;

/**
 * REST Service for the Mission Manager configuration.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 * 
 */
@Path("MissionManager/conf")
public class MissionManagerConfRESTService {
	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	private Logger sciLog = context.sciLog;

	public MissionManagerConfRESTService() {
	}

	/**
	 * Gets the current Mission Manager Configuration.
	 * 
	 * @return The Mission Manager current configuration.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMissionManagerConf() {
		mmLog.log(Level.INFO, "Requested Mission Manager configuration");
		return Response.ok(context.getConfigurationAsString()).build();
	}

	/**
	 * Sets or updates one or more configuration parameters.
	 * 
	 * @param params A map of parameters and values to be set.
	 * @return The updated Mission Manager configuration.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response setMissionManagerConf(MultivaluedMap<String, String> params) {
		long tic;
		long toc;
		StringBuilder updatedParams = new StringBuilder();
		
		tic = System.currentTimeMillis();
		mmLog.log(Level.INFO, "Requested change of configuration");
		StringBuilder response = new StringBuilder();
		int nValue;
		boolean bValue;

		for (String param : params.keySet()) {
			switch (param) {
			case MissionManagerContext.PROP_THRIFT_STYLE:
				updatedParams.append("thrift_server_style,");
				context.setThriftStyle(params.getFirst(MissionManagerContext.PROP_THRIFT_STYLE));
				mmLog.log(Level.INFO, "Thrift server style changed to " + context.thriftStyle);
				response.append("Thrift server changed updated to: " + context.thriftStyle + "\n");
				break;
			case MissionManagerContext.PROP_THRIFT_PORT:
				updatedParams.append("thrift_server_port,");
				nValue = context.thriftPort;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_THRIFT_PORT)));
					updatedParams.append("valid,");
					context.setThriftPort(nValue);
					mmLog.log(Level.INFO, "Thrift server port changed to " + context.thriftPort);
					response.append("Thrift server port changed to: " + context.thriftPort + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request of thrift port to "
							+ params.getFirst(MissionManagerContext.PROP_THRIFT_PORT));
					response.append("Thrift server port UNCHANGED. Bad port number: "
							+ params.getFirst(MissionManagerContext.PROP_THRIFT_PORT) + "\n");
				}
				break;
			case MissionManagerContext.PROP_REST_PORT:
				updatedParams.append("rest_server_port,");
				response.append("Change request for REST PORT is NOT ALLOWED");
				break;
			case MissionManagerContext.PROP_REST_BASE_URI:
				updatedParams.append("rest_server_base_uri,");
				response.append("Change request for REST BASE URI is NOT ALLOWED");
				break;
			case MissionManagerContext.PROP_MMT_IP:
				updatedParams.append("mmt_address,");
				context.setMmtIP(params.getFirst(MissionManagerContext.PROP_MMT_IP));
				mmLog.log(Level.INFO, "MMT address changed to " + context.mmtIP);
				response.append("MMT address changed updated to: " + context.mmtIP + "\n");
				break;
			case MissionManagerContext.PROP_MMT_PORT:
				updatedParams.append("mmt_port,");
				nValue = context.mmtPort;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_MMT_PORT)));
					updatedParams.append("valid,");
					context.setMmtPort(nValue);
					mmLog.log(Level.INFO, "MMT port changed to " + context.mmtPort);
					response.append("MMT port changed to: " + context.mmtPort + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request of MMT port to "
							+ params.getFirst(MissionManagerContext.PROP_MMT_PORT));
					response.append("MMT port UNCHANGED. Bad port number: "
							+ params.getFirst(MissionManagerContext.PROP_MMT_PORT) + "\n");
				}
				break;
			case MissionManagerContext.PROP_MMT_ENABLED_NOTIFICATIONS:
				updatedParams.append("mmt_enable_notifications,");
				bValue = context.mmtEnabled;
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_MMT_ENABLED_NOTIFICATIONS)));
				context.setMmtEnabled(bValue);
				mmLog.log(Level.INFO, "MMT notifications updated to: " + context.mmtEnabled + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_PROTOCOL:
				updatedParams.append("mqtt_protocol,");
				context.setMqttProtocol(params.getFirst(MissionManagerContext.PROP_MQTT_PROTOCOL));
				mmLog.log(Level.INFO, "MQTT protocol changed to " + context.mqttProtocol);
				response.append("MQTT protocol changed updated to: " + context.mqttProtocol + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_SERVER:
				updatedParams.append("mqtt_server_address,");
				context.setMqttServer(params.getFirst(MissionManagerContext.PROP_MQTT_SERVER));
				mmLog.log(Level.INFO, "MQTT server address changed to " + context.mqttServer);
				response.append("MQTT server address changed updated to: " + context.mqttServer + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_PORT:
				updatedParams.append("mqtt_server_port,");
				nValue = context.mqttPort;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_MQTT_PORT)));
					updatedParams.append("valid,");
					context.setMqttPort(nValue);
					mmLog.log(Level.INFO, "MQTT Broker port changed to " + context.mqttPort);
					response.append("MQTT Broker port changed to: " + context.mqttPort + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request of MQTT Broker port to "
							+ params.getFirst(MissionManagerContext.PROP_MQTT_PORT));
					response.append("MQTT broker port UNCHANGED. Bad port number: "
							+ params.getFirst(MissionManagerContext.PROP_MQTT_PORT) + "\n");
				}
				break;
			case MissionManagerContext.PROP_MQTT_USERNAME:
				updatedParams.append("mqtt_username,");
				context.setMqttUser(params.getFirst(MissionManagerContext.PROP_MQTT_USERNAME));
				mmLog.log(Level.INFO, "MQTT user name changed to " + context.mqttUser);
				response.append("MQTT user name changed updated to: " + context.mqttUser + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_PASSWORD:
				updatedParams.append("mqtt_password,");
				context.setMqttPass(params.getFirst(MissionManagerContext.PROP_MQTT_PASSWORD));
				mmLog.log(Level.INFO, "MQTT user password changed to " + context.mqttPass);
				response.append("MQTT user password changed updated to: " + context.mqttPass + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_CLIENT_ID:
				updatedParams.append("mqtt_client_id,");
				context.setMqttClientID(params.getFirst(MissionManagerContext.PROP_MQTT_CLIENT_ID));
				mmLog.log(Level.INFO, "MQTT client ID changed to " + context.mqttClientID);
				response.append("MQTT client ID changed updated to: " + context.mqttClientID + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_TOPIC_MISSION:
				updatedParams.append("mqtt_topic_mission,");
				context.setMqttTopicNewMission(params.getFirst(MissionManagerContext.PROP_MQTT_TOPIC_MISSION));
				mmLog.log(Level.INFO, "MQTT mission topic changed to " + context.mqttTopicMission);
				response.append("MQTT topic for new missions updated to: " + context.mqttTopicMission + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_TOPIC_SC:
				updatedParams.append("mqtt_topic_sc,");
				context.setMqttTopicSystemConfiguration(params.getFirst(MissionManagerContext.PROP_MQTT_TOPIC_SC));
				mmLog.log(Level.INFO,
						"MQTT system configuration topic changed to " + context.mqttTopicSystemConfiguration);
				response.append("MQTT topic for system configuration updated to: "
						+ context.mqttTopicSystemConfiguration + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_TOPIC_SC_REPORT:
				updatedParams.append("mqtt_topic_sc_report,");
				context.setMqttTopicSystemConfigurationReport(
						params.getFirst(MissionManagerContext.PROP_MQTT_TOPIC_SC_REPORT));
				mmLog.log(Level.INFO, "MQTT system configuration report topic changed to "
						+ context.mqttTopicSystemConfigurationReport);
				response.append("MQTT topic for system configuration report updated to: "
						+ context.mqttTopicSystemConfigurationReport + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_RETAINED_MISSION:
				updatedParams.append("mqtt_retained_mission,");
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_MQTT_RETAINED_MISSION)));
				context.setMqttRetainedMission(bValue);
				mmLog.log(Level.INFO, "MQTT retained value for missions changed to " + context.mqttRetainedMission);
				response.append("MQTT retained value for missions updated to: " + context.mqttRetainedMission + "\n");
				break;
			case MissionManagerContext.PROP_MQTT_RETAINED_SC:
				updatedParams.append("mqtt_retained_sc,");
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_MQTT_RETAINED_SC)));
				context.setMqttRetainedSystemConfiguration(bValue);
				mmLog.log(Level.INFO,
						"MQTT retained value for sc changed to " + context.mqttRetainedSystemConfiguration);
				response.append(
						"MQTT retained value for sc updated to: " + context.mqttRetainedSystemConfiguration + "\n");
				break;
			case MissionManagerContext.PROP_SC_REQUEST_TIMEOUT:
				updatedParams.append("sc_request_timeout,");				
				nValue = context.SCRequestTimeout;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_SC_REQUEST_TIMEOUT)));
					updatedParams.append("valid,");
					context.setSCRequestTimeLimit(nValue);
					mmLog.log(Level.INFO, "SC request timeout changed to " + context.SCRequestTimeout);
					response.append("SC request timeout changed to: " + context.SCRequestTimeout + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.WARNING, "Invalid change request of SC request timeout to "
							+ params.getFirst(MissionManagerContext.PROP_SC_REQUEST_TIMEOUT));
					response.append("SC request timeout UNCHANGED. Bad number: "
							+ params.getFirst(MissionManagerContext.PROP_SC_REQUEST_TIMEOUT) + "\n");
				}
				break;
			case MissionManagerContext.PROP_SC_MAX_VEHICLES:
				updatedParams.append("sc_max_vehicles,");				
				nValue = context.SCMaxVehicles;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_SC_MAX_VEHICLES)));
					updatedParams.append("valid,");
					context.setSCMaxVehicles(nValue);
					mmLog.log(Level.INFO, "SC max vehicles changed to " + context.SCMaxVehicles);
					response.append("SC max vehicles changed to: " + context.SCMaxVehicles + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.WARNING, "Invalid change request of SC request timeout to "
							+ params.getFirst(MissionManagerContext.PROP_SC_MAX_VEHICLES));
					response.append("SC max vehicles UNCHANGED. Bad number: "
							+ params.getFirst(MissionManagerContext.PROP_SC_MAX_VEHICLES) + "\n");
				}
				break;
			case MissionManagerContext.PROP_DQ_SERVER:
				updatedParams.append("dq_server_address,");				
				context.setDqServer(params.getFirst(MissionManagerContext.PROP_DQ_SERVER));
				mmLog.log(Level.INFO, "DQ server changed to " + context.dqServer);
				response.append("DQ server address changed to: " + context.dqServer + "\n");
				break;
			case MissionManagerContext.PROP_DQ_PORT:
				updatedParams.append("dq_server_port,");
				nValue = context.dqPort;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_DQ_PORT)));
					updatedParams.append("valid,");
					context.setDqPort(nValue);
					mmLog.log(Level.INFO, "DQ server port changed to " + context.dqPort);
					response.append("DQ server port chaged to: " + context.dqPort + "\n");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request of DQ server port to "
							+ params.getFirst(MissionManagerContext.PROP_DQ_PORT));
					response.append("DQ server port UNCHANGED. Bad port number: "
							+ params.getFirst(MissionManagerContext.PROP_DQ_PORT) + "\n");
				}
				break;
			case MissionManagerContext.PROP_ISOBUSCONVERTER_SERVER:
				updatedParams.append("isobusconverter_server_address,");
				context.setIsobusConverterServer(params.getFirst(MissionManagerContext.PROP_ISOBUSCONVERTER_SERVER));
				mmLog.log(Level.INFO, "ISOBUS Converter server changed to " + context.isobusConverterServer);
				response.append("ISOBUS Converter server address updated to: " + context.isobusConverterServer + "\n");
				break;
			case MissionManagerContext.PROP_ISOBUSCONVERTER_PORT:
				updatedParams.append("isobusconverter_server_port,");				
				nValue = context.isobusConverterPort;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_ISOBUSCONVERTER_PORT)));
					updatedParams.append("valid,");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request of ISOBUS Converter server port to "
							+ params.getFirst(MissionManagerContext.PROP_ISOBUSCONVERTER_PORT));
					response.append("ISOBUS Converter server port UNCHANGED. Bad port number: "
							+ params.getFirst(MissionManagerContext.PROP_ISOBUSCONVERTER_PORT) + "\n");
				} finally {
					context.setIsobusConverterPort(nValue);
					mmLog.log(Level.INFO, "ISOBUS Converter server port changed to " + context.isobusConverterPort);
					response.append("ISOBUS Converter server port updated to: " + context.isobusConverterPort + "\n");
				}
				break;
			case MissionManagerContext.PROP_LAST_SEQUENCE_NUMBER:
				updatedParams.append("last_sequence_number,");
				nValue = context.sequenceNumber;
				try {
					nValue = (Integer.parseInt(params.getFirst(MissionManagerContext.PROP_LAST_SEQUENCE_NUMBER)));
					updatedParams.append("valid,");
				} catch (NumberFormatException e) {
					updatedParams.append("invalid,");
					mmLog.log(Level.INFO, "Invalid change request last sequence number to "
							+ params.getFirst(MissionManagerContext.PROP_LAST_SEQUENCE_NUMBER));
					response.append("Last sequence number UNCHANGED. Bad number: "
							+ params.getFirst(MissionManagerContext.PROP_LAST_SEQUENCE_NUMBER) + "\n");
				} finally {
					context.setSequenceNumber(nValue);
					mmLog.log(Level.INFO, "Last sequence number changed to " + context.sequenceNumber);
					response.append("Last sequence number updated to: " + context.sequenceNumber + "\n");
				}
				break;
			case MissionManagerContext.PROP_DEBUG:
				updatedParams.append("debug,");
				bValue = context.debug;
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_DEBUG)));
				context.setDebug(bValue);
				mmLog.log(Level.INFO, "Debug control changed to " + context.debug);
				response.append("Debug control updated to: " + context.debug + "\n");
				break;
			case MissionManagerContext.PROP_MM2DDS_HMAC_SECRET:
				updatedParams.append("mm2dds_hmac_secret,");
				context.setMm2ddsSecret(params.getFirst(MissionManagerContext.PROP_MM2DDS_HMAC_SECRET));
				mmLog.log(Level.INFO, "Mission Manager/DDS Manager shared secret changed to " + context.mm2ddsSecret);
				response.append(
						"Mission Manager/DDS Manager shared secret changed updated to: " + context.mm2ddsSecret + "\n");
				break;
			case MissionManagerContext.PROP_MM2DDS_HMAC_ENABLED:
				updatedParams.append("mm2dds_hmac_enabled,");
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_MM2DDS_HMAC_ENABLED)));
				context.setMm2ddsEnabled(bValue);
				mmLog.log(Level.INFO,
						"MM2DDS HMAC usage changed to " + context.mm2ddsEnabled);
				response.append(
						"MM2DDS HMAC usage updated to: " + context.mm2ddsEnabled + "\n");
				break;
			case MissionManagerContext.PROP_HTTPS_ENABLED:
				updatedParams.append("https_enabled,");
				bValue = (Boolean.parseBoolean(params.getFirst(MissionManagerContext.PROP_HTTPS_ENABLED)));
				context.setHttpsEnabled(bValue);
				mmLog.log(Level.INFO,
						"Use of HTTPS changed to " + context.httpsEnabled);
				response.append(
						"Use of HTTPS updated to: " + context.httpsEnabled + "\n");
				break;			}
		}

		toc = System.currentTimeMillis() - tic;
		sciLog.log(Level.INFO, "rest_conf_put," + updatedParams.toString() + toc);
		return Response.ok(response.toString()).build();
	}
}
