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
import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Common context for the Mission Manager.
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionManagerContext {	
    private String version = "2.0.0";

    // Configuration file names
	private static final String INTERNAL_CONFIG_FILE_NAME = "config.properties";
	private static final String LOCAL_CONFIG_FILE_NAME = "local.properties";
	private static final String STORED_CONFIG_FILE_NAME = "stored.properties";
	
	// Default properties values
	private static final int DEFAULT_THRIFT_PORT = 9225;
	private static final String DEFAULT_THRIFT_STYLE = "multiplex";
	private static final int DEFAULT_REST_PORT = 9226;
	private static final String DEFAULT_REST_BASE_URI = "afarcloud";
	private static final String DEFAULT_MMT_IP = "192.168.1.1";
	private static final int DEFAULT_MMT_PORT = 9096;
	private static final boolean DEFAULT_MMT_ENABLED_NOTIFICATIONS = false;
	private static final boolean DEFAULT_DEBUG = true;
	private static final String DEFAULT_MQTT_PROTOCOL = "ssl";
	private static final String DEFAULT_MQTT_SERVER = "mqtt.afarcloud.smartarch.cz";
	private static final int DEFAULT_MQTT_PORT = 1883;
	private static final String DEFAULT_MQTT_USERNAME = "username";
	private static final String DEFAULT_MQTT_PASSWORD = "password";
	private static final String DEFAULT_MQTT_CLIENT_ID = "AFC Mission Manager";
	private static final String DEFAULT_MQTT_TOPIC_MISSION = "mission/mission";
	private static final String DEFAULT_MQTT_TOPIC_SC = "mission/system_configuration";
	private static final String DEFAULT_MQTT_TOPIC_SC_REPORT = "mission/system_configuration/report";
	private static final boolean DEFAULT_MQTT_RETAINED_MISSION = false;
	private static final boolean DEFAULT_MQTT_RETAINED_SC = false;
	private static final int DEFAULT_SC_REQUEST_TIMEOUT = 60;
	private static final int DEFAULT_SC_MAX_VEHICLES= 10;
	private static final String DEFAULT_DQ_SERVER = "dq.server";
	private static final int DEFAULT_DQ_PORT = 8080;
	private static final String DEFAULT_ISOBUSCONVERTER_SERVER = "isobusconverter.server";
	private static final int DEFAULT_ISOBUSCONVERTER_PORT = 8080;
    private static final int DEFAULT_LAST_SEQUENCE_NUMBER = 0;
    private static final String DEFAULT_MM2DDS_HMAC_SECRET = "hmac_secret";
    private static final boolean DEFAULT_MM2DDS_HMAC_ENABLED = true;
    private static final boolean DEFAULT_HTTPS_ENABLED = true;
	
	// Properties names
	public static final String PROP_THRIFT_PORT = "thrift.port";
	public static final String PROP_THRIFT_STYLE = "thrift.style";
	public static final String PROP_REST_PORT = "rest.port";
	public static final String PROP_REST_BASE_URI = "rest.base_uri";
	public static final String PROP_MMT_IP = "mmt.ip";
	public static final String PROP_MMT_PORT = "mmt.port";
	public static final String PROP_MMT_ENABLED_NOTIFICATIONS = "mmt.enabled_notifications";
	public static final String PROP_DEBUG = "debug";
	public static final String PROP_MQTT_PROTOCOL = "mqtt.protocol";
	public static final String PROP_MQTT_SERVER = "mqtt.server";
	public static final String PROP_MQTT_PORT = "mqtt.port";
	public static final String PROP_MQTT_USERNAME = "mqtt.user";
	public static final String PROP_MQTT_PASSWORD = "mqtt.pass";
	public static final String PROP_MQTT_CLIENT_ID = "mqtt.client_id";
	public static final String PROP_MQTT_TOPIC_MISSION = "mqtt.topic.mission";
	public static final String PROP_MQTT_TOPIC_SC = "mqtt.topic.system_configuration";
	public static final String PROP_MQTT_TOPIC_SC_REPORT = "mqtt.topic.system_configuration_report";
	public static final String PROP_MQTT_RETAINED_MISSION = "mqtt.retained.mission";
	public static final String PROP_MQTT_RETAINED_SC = "mqtt.retained.system_configuration";	
	public static final String PROP_SC_REQUEST_TIMEOUT = "sc.request.timeout";
	public static final String PROP_SC_MAX_VEHICLES = "sc.max_vehicles";
	public static final String PROP_DQ_SERVER = "dq.server";
	public static final String PROP_DQ_PORT = "dq.port";
	public static final String PROP_ISOBUSCONVERTER_SERVER = "isobusconverter.server";
	public static final String PROP_ISOBUSCONVERTER_PORT = "isobusconverter.port";
	public static final String PROP_LAST_SEQUENCE_NUMBER = "last_sequence_number";
    public static final String PROP_MM2DDS_HMAC_SECRET = "mm2dds.hmac.secret";
    public static final String PROP_MM2DDS_HMAC_ENABLED = "mm2dds.hmac.enabled";
    public static final String PROP_HTTPS_ENABLED = "https.enabled";

	// Context private attributes
	private FileHandler fh;
	private FileHandler fhsci;
    private static MissionManagerContext instance = null;    

    // Context public fields    
    public Logger mmLog = Logger.getLogger("MissionManagerLog");
    public Logger sciLog = Logger.getLogger("SciTechTrackingLog");
    public int thriftPort = DEFAULT_THRIFT_PORT;
    public String thriftStyle = DEFAULT_THRIFT_STYLE;
    public int restPort = DEFAULT_REST_PORT;
    public String restBaseURI = DEFAULT_REST_BASE_URI;
    public String mmtIP = DEFAULT_MMT_IP;
    public int mmtPort = DEFAULT_MMT_PORT;
    public boolean mmtEnabled = DEFAULT_MMT_ENABLED_NOTIFICATIONS;
    public boolean debug = DEFAULT_DEBUG;
    public String mqttProtocol = DEFAULT_MQTT_PROTOCOL;
    public String mqttServer = DEFAULT_MQTT_SERVER;
    public int mqttPort = DEFAULT_MQTT_PORT;
    public String mqttUser = DEFAULT_MQTT_USERNAME;
    public String mqttPass = DEFAULT_MQTT_PASSWORD;
    public String mqttClientID = DEFAULT_MQTT_CLIENT_ID;
    public String mqttTopicMission = DEFAULT_MQTT_TOPIC_MISSION;
    public String mqttTopicSystemConfiguration = DEFAULT_MQTT_TOPIC_SC;
    public String mqttTopicSystemConfigurationReport = DEFAULT_MQTT_TOPIC_SC_REPORT;
    public boolean mqttRetainedMission = DEFAULT_MQTT_RETAINED_MISSION;
    public boolean mqttRetainedSystemConfiguration = DEFAULT_MQTT_RETAINED_SC;
    public int SCRequestTimeout = DEFAULT_SC_REQUEST_TIMEOUT;
    public int SCMaxVehicles = DEFAULT_SC_MAX_VEHICLES;
    public String dqServer = DEFAULT_DQ_SERVER;
    public int dqPort = DEFAULT_DQ_PORT;
    public String isobusConverterServer = DEFAULT_ISOBUSCONVERTER_SERVER;
    public int isobusConverterPort = DEFAULT_ISOBUSCONVERTER_PORT;
    public int sequenceNumber = DEFAULT_LAST_SEQUENCE_NUMBER;
    public String mm2ddsSecret = DEFAULT_MM2DDS_HMAC_SECRET;
    public boolean mm2ddsEnabled = DEFAULT_MM2DDS_HMAC_ENABLED;
    public boolean httpsEnabled = DEFAULT_HTTPS_ENABLED;
    
    // Context public MM contextual variables
    public String logdir = "log";
    public String mission_base_dir = "missions";
    public String current_mission_dir;
    
    private boolean storedConfigAvailable = false;

    // INTERNAL CONFIG - DEFAULT AND PRIVATE VALUES
    Configurations internalConfigs = new Configurations();
    FileBasedConfigurationBuilder<PropertiesConfiguration> internalBuilder = internalConfigs.propertiesBuilder(new File(INTERNAL_CONFIG_FILE_NAME));
    Configuration internalConfig;

    // LOCAL CONFIG - LOCAL VALUES
    Configurations localConfigs = new Configurations();
    FileBasedConfigurationBuilder<PropertiesConfiguration> localBuilder = internalConfigs.propertiesBuilder(new File(LOCAL_CONFIG_FILE_NAME));
    Configuration localConfig;

    // STORED CONFIG - STORED VALUES
    Configurations storedConfigs = new Configurations();
    FileBasedConfigurationBuilder<PropertiesConfiguration> storedBuilder = internalConfigs.propertiesBuilder(new File(STORED_CONFIG_FILE_NAME));
    Configuration storedConfig;
    
    private MissionManagerContext() {
        try {        	
        	// Check if logdir exists, and if not, create it
            File directory = new File(logdir);
            if (! directory.exists()){
                directory.mkdir();
            }
        	
        	// Create MissionManager logger   	
            fh = new FileHandler(logdir + File.separator + "MissionManager.log", true);
            mmLog.setUseParentHandlers(false);
            mmLog.addHandler(fh);
            fh.setFormatter(new SimpleFormatter() {
                private static final String formatNormal = "[%1$tF %1$tT] [%2$-7s] [%3$s:%4$s (%5$s)] %6$s %n";
                private static final String formatException = "[%1$tF %1$tT] [%2$-7s] [%3$s:%4$s (%5$s)] %6$s %7$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                	if (lr.getThrown() != null ) {
                		return String.format(formatException,
                				new Date(lr.getMillis()),
                				lr.getLevel().getLocalizedName(),
                				lr.getSourceClassName().substring(lr.getSourceClassName().lastIndexOf('.') + 1),
                				lr.getSourceMethodName(),
                				lr.getThreadID(),
                				lr.getMessage(),
                				lr.getThrown());
                	}
                	else {
                		return String.format(formatNormal,
                				new Date(lr.getMillis()),
                				lr.getLevel().getLocalizedName(),
                				lr.getSourceClassName().substring(lr.getSourceClassName().lastIndexOf('.') + 1),
                				lr.getSourceMethodName(),
                				lr.getThreadID(),
                				lr.getMessage());
                	}
                }
            });
            
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter() {
                private static final String formatNormal = "[%1$tF %1$tT] [%2$-7s] [%3$s:%4$s (%5$s)] %6$s %n";
                private static final String formatException = "[%1$tF %1$tT] [%2$-7s] [%3$s:%4$s (%5$s)] %6$s %7$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                	if (lr.getThrown() != null ) {
                		return String.format(formatException,
                				new Date(lr.getMillis()),
                				lr.getLevel().getLocalizedName(),
                				lr.getSourceClassName().substring(lr.getSourceClassName().lastIndexOf('.') + 1),
                				lr.getSourceMethodName(),
                				lr.getThreadID(),
                				lr.getMessage(),
                				lr.getThrown());
                	}
                	else {
                		return String.format(formatNormal,
                				new Date(lr.getMillis()),
                				lr.getLevel().getLocalizedName(),
                				lr.getSourceClassName().substring(lr.getSourceClassName().lastIndexOf('.') + 1),
                				lr.getSourceMethodName(),
                				lr.getThreadID(),
                				lr.getMessage());
                	}
                }
            });

            mmLog.addHandler(ch);

            mmLog.log(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            mmLog.log(Level.INFO, "STARTING Mission Manager ----------------------------");
            
            // Create logger for technical/scientific characterization and tracking
            fhsci = new FileHandler(logdir + File.separator + "SciTrack.log", true);
            sciLog.addHandler(fhsci);
            sciLog.setUseParentHandlers(false);
            fhsci.setFormatter(new SimpleFormatter() {
                private static final String format = "%1$tF,%1$tT,%2$-7s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getSourceClassName().substring(lr.getSourceClassName().lastIndexOf('.') + 1),
                            lr.getSourceMethodName(),
                            lr.getThreadID(),
                            Runtime.getRuntime().maxMemory(),
                            Runtime.getRuntime().totalMemory(),
                            Runtime.getRuntime().freeMemory(),
                            lr.getMessage()
                    );
                }
            });            
            
            internalConfig = internalBuilder.getConfiguration();

            // Load configuration
        	mmLog.log(Level.INFO, this.getClass().getName() + ": Reading configuration from " + INTERNAL_CONFIG_FILE_NAME);
            loadConfiguration();

        }
        catch (SecurityException e) {
        	mmLog.log(Level.SEVERE, "Security exception while creating the MMContext instance.");
        	if (debug) {
        		mmLog.log(Level.SEVERE, e.getMessage(), e);
        	}
        }
        catch (IOException e) {
        	mmLog.log(Level.SEVERE, "I/O exception while creating the MMContext instance.");
        	if (debug) {
        		mmLog.log(Level.SEVERE, e.getMessage(), e);
        	}
        }
        catch (ConfigurationException e) {
        	mmLog.log(Level.SEVERE, "Configuration exception while creating the MMContext instance.");
        	if (debug) {
        		mmLog.log(Level.SEVERE, e.getMessage(), e);
        	}
        }
        
        try {
			localConfig = localBuilder.getConfiguration();
	    	mmLog.log(Level.INFO, this.getClass().getName() + ": Reading configuration from " + LOCAL_CONFIG_FILE_NAME);
            updateConfiguration(localConfig);
		}
        catch (ConfigurationException e) {
        	mmLog.log(Level.WARNING, "No " + LOCAL_CONFIG_FILE_NAME + " found.");
		}

        try {
			storedConfig = storedBuilder.getConfiguration();
			storedConfigAvailable = true;
	    	mmLog.log(Level.INFO, this.getClass().getName() + ": Reading configuration from " + STORED_CONFIG_FILE_NAME);
	    	updateConfiguration(storedConfig);
		}
        catch (ConfigurationException e) {
        	mmLog.log(Level.WARNING, "No " + STORED_CONFIG_FILE_NAME + " found. Creating it for stored configurations.");
        	
    		File file = new File(STORED_CONFIG_FILE_NAME);
            try {
        		file.createNewFile();
            }
            catch (IOException e1) {
            	mmLog.log(Level.WARNING, "Unable to create " + STORED_CONFIG_FILE_NAME + ". Updated configuration will not be stored.");
            }
            finally {
    			try {
    				storedConfig = storedBuilder.getConfiguration();
    				storedConfigAvailable = true;
    				mmLog.log(Level.INFO, this.getClass().getName() + ": Reading configuration from " + STORED_CONFIG_FILE_NAME);
    				updateConfiguration(storedConfig); 
    			}
    	        catch (ConfigurationException e2) {
    	        	mmLog.log(Level.SEVERE, "Impossible to access to " + STORED_CONFIG_FILE_NAME + ". Updated configuation will not be stored.");
    	        }
            }
		}    

		mmLog.log(Level.INFO, "Configuration read as follows:\n" + getConfigurationAsString());
    }

    /**
     * Provides the singleton instance of the MissionManagerContext
     * 
     * @return The singleton instance of the MissionManagerContect
     */
    public static MissionManagerContext getInstance() {
            if(instance == null)
                instance = new MissionManagerContext();
            
            return instance;	
    }

    private void loadConfiguration() {			
		thriftPort = internalConfig.getInt(PROP_THRIFT_PORT, DEFAULT_THRIFT_PORT);
		thriftStyle = internalConfig.getString(PROP_THRIFT_STYLE, DEFAULT_THRIFT_STYLE);
		restPort = internalConfig.getInt(PROP_REST_PORT, DEFAULT_REST_PORT);
		restBaseURI = internalConfig.getString(PROP_REST_BASE_URI, DEFAULT_REST_BASE_URI);
		mmtIP = internalConfig.getString(PROP_MMT_IP, DEFAULT_MMT_IP);
		mmtPort = internalConfig.getInt(PROP_MMT_PORT, DEFAULT_MMT_PORT);
		mmtEnabled = internalConfig.getBoolean(PROP_MMT_ENABLED_NOTIFICATIONS, DEFAULT_MMT_ENABLED_NOTIFICATIONS);
		mqttProtocol = internalConfig.getString(PROP_MQTT_PROTOCOL, DEFAULT_MQTT_PROTOCOL);
		mqttServer = internalConfig.getString(PROP_MQTT_SERVER, DEFAULT_MQTT_SERVER);
		mqttPort = internalConfig.getInt(PROP_MQTT_PORT, DEFAULT_MQTT_PORT);
		mqttUser = internalConfig.getString(PROP_MQTT_USERNAME, DEFAULT_MQTT_USERNAME);
		mqttPass = internalConfig.getString(PROP_MQTT_PASSWORD, DEFAULT_MQTT_PASSWORD);
		mqttClientID = internalConfig.getString(PROP_MQTT_CLIENT_ID, DEFAULT_MQTT_CLIENT_ID);
		mqttTopicMission = internalConfig.getString(PROP_MQTT_TOPIC_MISSION, DEFAULT_MQTT_TOPIC_MISSION);
		mqttTopicSystemConfiguration = internalConfig.getString(PROP_MQTT_TOPIC_SC, DEFAULT_MQTT_TOPIC_SC);
		mqttTopicSystemConfigurationReport = internalConfig.getString(PROP_MQTT_TOPIC_SC_REPORT, DEFAULT_MQTT_TOPIC_SC_REPORT);
		mqttRetainedMission = internalConfig.getBoolean(PROP_MQTT_RETAINED_MISSION, DEFAULT_MQTT_RETAINED_MISSION);
		mqttRetainedSystemConfiguration = internalConfig.getBoolean(PROP_MQTT_RETAINED_SC, DEFAULT_MQTT_RETAINED_SC);
		SCRequestTimeout = internalConfig.getInt(PROP_SC_REQUEST_TIMEOUT, DEFAULT_SC_REQUEST_TIMEOUT);
		SCMaxVehicles = internalConfig.getInt(PROP_SC_MAX_VEHICLES, DEFAULT_SC_MAX_VEHICLES);
	    dqServer = internalConfig.getString(PROP_DQ_SERVER, DEFAULT_DQ_SERVER);
	    dqPort = internalConfig.getInt(PROP_DQ_PORT, DEFAULT_DQ_PORT);
	    isobusConverterServer = internalConfig.getString(PROP_ISOBUSCONVERTER_SERVER, DEFAULT_ISOBUSCONVERTER_SERVER);
	    isobusConverterPort = internalConfig.getInt(PROP_ISOBUSCONVERTER_PORT, DEFAULT_ISOBUSCONVERTER_PORT);
	    sequenceNumber = internalConfig.getInt(PROP_LAST_SEQUENCE_NUMBER, DEFAULT_LAST_SEQUENCE_NUMBER);
	    mm2ddsSecret = internalConfig.getString(PROP_MM2DDS_HMAC_SECRET, DEFAULT_MM2DDS_HMAC_SECRET);
		mm2ddsEnabled = internalConfig.getBoolean(PROP_MM2DDS_HMAC_ENABLED, DEFAULT_MM2DDS_HMAC_ENABLED);
		httpsEnabled = internalConfig.getBoolean(PROP_HTTPS_ENABLED, DEFAULT_HTTPS_ENABLED);
	    
		debug = internalConfig.getBoolean(PROP_DEBUG, DEFAULT_DEBUG);
    }

    private void updateConfiguration(Configuration config) {			
		thriftPort = config.getInt(PROP_THRIFT_PORT, thriftPort);
		thriftStyle = config.getString(PROP_THRIFT_STYLE, thriftStyle);
		restPort = config.getInt(PROP_REST_PORT, restPort);
		restBaseURI = config.getString(PROP_REST_BASE_URI, restBaseURI);
		mmtIP = config.getString(PROP_MMT_IP, mmtIP);
		mmtPort = config.getInt(PROP_MMT_PORT, mmtPort);
		mmtEnabled = config.getBoolean(PROP_MMT_ENABLED_NOTIFICATIONS, mmtEnabled);
		mqttProtocol = config.getString(PROP_MQTT_PROTOCOL, mqttProtocol);
		mqttServer = config.getString(PROP_MQTT_SERVER, mqttServer);
		mqttPort = config.getInt(PROP_MQTT_PORT, mqttPort);
		mqttUser = config.getString(PROP_MQTT_USERNAME, mqttUser);
		mqttPass = config.getString(PROP_MQTT_PASSWORD, mqttPass);
		mqttClientID = config.getString(PROP_MQTT_CLIENT_ID, mqttClientID);
		mqttTopicMission = config.getString(PROP_MQTT_TOPIC_MISSION, mqttTopicMission);
		mqttTopicSystemConfiguration = config.getString(PROP_MQTT_TOPIC_SC, mqttTopicSystemConfiguration);
		mqttTopicSystemConfigurationReport = config.getString(PROP_MQTT_TOPIC_SC_REPORT, mqttTopicSystemConfigurationReport);
		mqttRetainedMission = config.getBoolean(PROP_MQTT_RETAINED_MISSION, mqttRetainedMission);
		mqttRetainedSystemConfiguration = config.getBoolean(PROP_MQTT_RETAINED_SC, mqttRetainedSystemConfiguration);
		SCRequestTimeout = config.getInt(PROP_SC_REQUEST_TIMEOUT, SCRequestTimeout);
		SCMaxVehicles = config.getInt(PROP_SC_MAX_VEHICLES, SCMaxVehicles);
	    dqServer = config.getString(PROP_DQ_SERVER, dqServer);
	    dqPort = config.getInt(PROP_DQ_PORT, dqPort);
	    isobusConverterServer = config.getString(PROP_ISOBUSCONVERTER_SERVER, isobusConverterServer);
	    isobusConverterPort = config.getInt(PROP_ISOBUSCONVERTER_PORT, isobusConverterPort);
	    sequenceNumber = config.getInt(PROP_LAST_SEQUENCE_NUMBER, sequenceNumber);
	    mm2ddsSecret = config.getString(PROP_MM2DDS_HMAC_SECRET, mm2ddsSecret);
		mm2ddsEnabled = config.getBoolean(PROP_MM2DDS_HMAC_ENABLED, mm2ddsEnabled);
		httpsEnabled = config.getBoolean(PROP_HTTPS_ENABLED, DEFAULT_HTTPS_ENABLED);

		debug = config.getBoolean(PROP_DEBUG, debug);
    }
    
    /**
     * Provides a string representation of the current configuration
     * 
     * @return The current configuration as a string.
     */
    public String getConfigurationAsString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PROP_THRIFT_PORT + " = " + thriftPort + System.getProperty("line.separator"));
    	sb.append(PROP_THRIFT_STYLE + " = " + thriftStyle + System.getProperty("line.separator"));
    	sb.append(PROP_REST_PORT + " = " + restPort + System.getProperty("line.separator"));
    	sb.append(PROP_REST_BASE_URI + " = " + restBaseURI + System.getProperty("line.separator"));
    	sb.append(PROP_MMT_IP + " = " + mmtIP + System.getProperty("line.separator"));
    	sb.append(PROP_MMT_PORT + " = " + mmtPort + System.getProperty("line.separator"));
    	sb.append(PROP_MMT_ENABLED_NOTIFICATIONS + " = " + mmtEnabled + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_PROTOCOL + " = " + mqttProtocol + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_SERVER + " = " + mqttServer + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_PORT + " = " + mqttPort + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_USERNAME + " = " + mqttUser + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_CLIENT_ID + " = " + mqttClientID + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_TOPIC_MISSION + " = " + mqttTopicMission + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_TOPIC_SC + " = " + mqttTopicSystemConfiguration + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_TOPIC_SC_REPORT + " = " + mqttTopicSystemConfigurationReport + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_RETAINED_MISSION + " = " + mqttRetainedMission + System.getProperty("line.separator"));
    	sb.append(PROP_MQTT_RETAINED_SC + " = " + mqttRetainedSystemConfiguration + System.getProperty("line.separator"));
    	sb.append(PROP_SC_REQUEST_TIMEOUT + " = " + SCRequestTimeout + System.getProperty("line.separator"));
    	sb.append(PROP_SC_MAX_VEHICLES + " = " + SCMaxVehicles + System.getProperty("line.separator"));
    	sb.append(PROP_DQ_SERVER + " = " + dqServer + System.getProperty("line.separator"));
    	sb.append(PROP_DQ_PORT + " = " + dqPort + System.getProperty("line.separator"));
    	sb.append(PROP_ISOBUSCONVERTER_SERVER + " = " + isobusConverterServer + System.getProperty("line.separator"));
    	sb.append(PROP_ISOBUSCONVERTER_PORT + " = " + isobusConverterPort + System.getProperty("line.separator"));
    	sb.append(PROP_LAST_SEQUENCE_NUMBER + " = " + sequenceNumber + System.getProperty("line.separator"));
    	sb.append(PROP_MM2DDS_HMAC_ENABLED + " = " + mm2ddsEnabled + System.getProperty("line.separator"));
    	sb.append(PROP_HTTPS_ENABLED + " = " + httpsEnabled + System.getProperty("line.separator"));
    	sb.append(PROP_DEBUG + " = " + debug + System.getProperty("line.separator"));
    	
    	return sb.toString();
    }

    /**
     * Gets the current port used by the Mission Manager Thrift server.
     * 
     * @return The port used by the Thrift server.
     */
	public int getThriftPort() {
		return thriftPort;
	}

	/**
	 * Sets the value for the port to be used by the Mission Manager Thrfit server.
	 * 
	 * @param thriftPort The value for the port.
	 */
	public void setThriftPort(int thriftPort) {
		this.thriftPort = thriftPort;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_THRIFT_PORT, thriftPort);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the current port to be used by the Mission Manager REST service.
	 * 
	 * @return The port used by the REST service.
	 */
	public int getRestPort() {
		return restPort;
	}

	/**
	 * Sets the value for the port to be used by the Mission Manager REST service.
	 * 
	 * @param restPort The value for the port.
	 */
	public void setRestPort(int restPort) {
		this.restPort = restPort;
	}

	/**
	 * Gets the current base URI used by the Mission Manager REST service.
	 * 
	 * @return The base URE used by the REST service.
	 */
	public String getRestBaseURI() {
		return restBaseURI;
	}

	/**
	 * Sets the value for the base URI to be used by the Mission Manager REST service.
	 * 
	 * @param restBaseURI The value for the base URI.
	 */
	public void setRestBaseURI(String restBaseURI) {
		this.restBaseURI = restBaseURI;
	}

	/**
	 * Gets the IP used to connect to the MMT.
	 * 
	 * @return The IP used to connect to the MMT.
	 */
	public String getMmtIP() {
		return mmtIP;
	}

	/**
	 * Sets the IP to connect to the MMT.
	 * 
	 * @param mmtIP The IP to connect to the MMT.
	 */
	public void setMmtIP(String mmtIP) {
		this.mmtIP = mmtIP;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MMT_IP, mmtIP);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Gets the port used to connect to the MMT Thrift server.
	 * 
	 * @return The port used to connect to the MMT Thrift server.
	 */
	public int getMmtPort() {
		return mmtPort;
	}

	/**
	 * Sets the port to be used to connect to the MMT Thrift server.
	 * 
	 * @param mmtPort The port to be used to connect to the MMT Thrift server.
	 */
	public void setMmtPort(int mmtPort) {
		this.mmtPort = mmtPort;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MMT_PORT, mmtPort);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Checks whether if the notifications to the MMT are enabled or not.
	 * 
	 * @return {@code true} if the notifications to the MMT are enabled, {@code false} otherwise.
	 */
	public boolean isMmtEnabled() {
		return mmtEnabled;
	}

	/**
	 * Enables or disables the notifications to the MMT.
	 * 
	 * @param mmtEnabled {@code true} to enable the notifications to the MMT, {@code false} to disable them.
	 */
	public void setMmtEnabled(boolean mmtEnabled) {
		this.mmtEnabled = mmtEnabled;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MMT_ENABLED_NOTIFICATIONS, mmtEnabled);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Gets the Thrift server style used by the MM Thrift server.
	 * 
	 * @return A descriptive text for the Thrift server style being used (simplex or multiplex).
	 */
	public String getThriftStyle() {
		return thriftStyle;
	}

	/**
	 * Sets the Thrift server style to be used by the MM Thrift server.
	 * 
	 * @param thriftStyle {@value simplex} for a simple server, {@value multiplex} for a multiplexed server.
	 */
	public void setThriftStyle(String thriftStyle) {
		this.thriftStyle = thriftStyle;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_THRIFT_STYLE, thriftStyle);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}

	}

	/**
	 * Checks whether the debug messages are enabled or not
	 * 
	 * @return {@code true} if debug messages are enabled, {@code false} otherwise.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enables or disables the use of debug messages.
	 * 
	 * @param debug {@code true} to enable the debug messages, {@code false} to disable them.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_DEBUG, debug);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT protocol being used.
	 * 
	 * @return The MQTT protocol being used.
	 */
	public String getMqttProtocol() {
		return mqttProtocol;
	}

	/**
	 * Sets the MQTT protocol to be used to connect to the MQTT Broker.
	 * 
	 * @param mqttProtocol The MQTT protocol to be used.
	 */
	public void setMqttProtocol(String mqttProtocol) {
		this.mqttProtocol = mqttProtocol;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_PROTOCOL, mqttProtocol);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT server address being used to connect to the MQTT broker.
	 * 
	 * @return The MQTT server address being used.
	 */
	public String getMqttServer() {
		return mqttServer;
	}

	/**
	 * Sets the MQTT server address to be used to connect to the MQTT broker.
	 * 
	 * @param mqttServer The MQTT server address to be used.
	 */
	public void setMqttServer(String mqttServer) {
		this.mqttServer = mqttServer;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_SERVER, mqttServer);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT port being used to connect to the MQTT broker.
	 * 
	 * @return The MQTT port being used.
	 */
	public int getMqttPort() {
		return mqttPort;
	}

	/**
	 * Sets the MQTT port to be used to connect to the MQTT broker.
	 * 
	 * @param mqttPort The MQTT port to be used.
	 */
	public void setMqttPort(int mqttPort) {
		this.mqttPort = mqttPort;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_PORT, mqttPort);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT user name being used to connect to the MQTT broker.
	 * 
	 * @return The MQTT user name being used.
	 */
	public String getMqttUser() {
		return mqttUser;
	}

	/**
	 * Sets the MQTT user name to be used to connect to the MQTT broker.
	 * 
	 * @param mqttUser The MQTT user name to be used.
	 */
	public void setMqttUser(String mqttUser) {
		this.mqttUser = mqttUser;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_USERNAME, mqttUser);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT password being used to connect to the MQTT broker.
	 * 
	 * @return The MQTT password being used to connect to the MQTT broker.
	 */
	public String getMqttPass() {
		return mqttPass;
	}

	/**
	 * Sets the MQTT password to be used to connect to the MQTT broker.
	 * 
	 * @param mqttPass The MQTT password to be used to connect to the MQTT broker.
	 */
	public void setMqttPass(String mqttPass) {
		this.mqttPass = mqttPass;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_PASSWORD, mqttPass);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT client ID being used to connect to the MQTT broker.
	 * 
	 * @return The MQTT client ID being used.
	 */
	public String getMqttClientID() {
		return mqttClientID;
	}

	/**
	 * Sets the MQTT client ID to be used to connect to the MQTT broker.
	 * 
	 * @param mqttClientID The MQTT client ID to be used.
	 */
	public void setMqttClientID(String mqttClientID) {
		this.mqttClientID = mqttClientID;
	
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_CLIENT_ID, mqttClientID);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT topic being used to publish a new mission.
	 * 
	 * @return The MQTT topic being used to publish a new mission.
	 */
	public String getMqttTopicNewMission() {
		return mqttTopicMission;
	}

	/**
	 * Sets the MQTT topic to be used to publish a new mission.
	 * 
	 * @param mqttTopicNewMission The MQTT topic to be used to publish a new mission.
	 */
	public void setMqttTopicNewMission(String mqttTopicNewMission) {
		this.mqttTopicMission = mqttTopicNewMission;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_TOPIC_MISSION, mqttTopicNewMission);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT topic being used to publish a system configuration request.
	 * 
	 * @return The MQTT topic being used to publish a system configuration request.
	 */
	public String getMqttTopicSystemConfiguration() {
		return mqttTopicSystemConfiguration;
	}

	/**
	 * Sets the MQTT topic to be used to publish a system configuration request.
	 * 
	 * @param mqttTopicSystemConfiguration The MQTT topic to be used to publish a system configuration request.
	 */
	public void setMqttTopicSystemConfiguration(String mqttTopicSystemConfiguration) {
		this.mqttTopicSystemConfiguration = mqttTopicSystemConfiguration;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_TOPIC_SC, mqttTopicSystemConfiguration);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the MQTT topic being used to subscribe to the system configuration report.
	 * 
	 * @return The MQTT topic being used to subscribe to the system configuration report.
	 */
	public String getMqttTopicSystemConfigurationReport() {
		return mqttTopicSystemConfigurationReport;
	}

	/**
	 * Sets the MQTT topic being used to subscribe to the system configuration report.
	 * 
	 * @param mqttTopicSystemConfigurationReport The MQTT topic being used to subscribe to the system configuration report.
	 */
	public void setMqttTopicSystemConfigurationReport(String mqttTopicSystemConfigurationReport) {
		this.mqttTopicSystemConfigurationReport = mqttTopicSystemConfigurationReport;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_TOPIC_SC_REPORT, mqttTopicSystemConfigurationReport);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the time limit for waiting for a system configuration report once requested.
	 * 
	 * @return The time limit, in seconds.
	 */
	public int getSCRequestTimeLimit() {
		return SCRequestTimeout;
	}

	/**
	 * Sets the time limit for waiting for a system configuration report once requested.
	 * 
	 * @param SCRequestTimeLimit The time limit, in seconds.
	 */
	public void setSCRequestTimeLimit(int SCRequestTimeLimit) {
		this.SCRequestTimeout = SCRequestTimeLimit;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_SC_REQUEST_TIMEOUT, SCRequestTimeLimit);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Sets the maximum number of vehicles that can respond to a system configuration request.
	 * 
	 * @deprecated As of release 1.3.0.
	 * 
	 * @param SCMaxVehicles The maximum number of vehicles.
	 */
	@Deprecated
	public void setSCMaxVehicles(int SCMaxVehicles) {
		this.SCMaxVehicles = SCMaxVehicles;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_SC_MAX_VEHICLES, SCMaxVehicles);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Gets the address being used to connect to the Data Query (DQ).
	 * 
	 * @return The DQ server address being used.
	 */
	public String getDqServer() {
		return dqServer;
	}

	/**
	 * Sets the address to be used to connect to the Data Query (DQ).
	 * 
	 * @param dqServer The DQ server address to be used.
	 */
	public void setDqServer(String dqServer) {
		this.dqServer = dqServer;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_DQ_SERVER, dqServer);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the port being used to connect to the Data Query (DQ).
	 * 
	 * @return The DQ port being used.
	 */
	public int getDqPort() {
		return dqPort;
	}

	/**
	 * Sets the port to be used to connect to the Data Query (DQ).
	 * 
	 * @param dqPort The DQ port to be used.
	 */
	public void setDqPort(int dqPort) {
		this.dqPort = dqPort;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_DQ_PORT, dqPort);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the server address being used to connect to the ISOBUS Converter.
	 * 
	 * @return The server address being used.
	 */
	public String getIsobusConverterServer() {
		return isobusConverterServer;
	}

	/**
	 * Sets the server address to be used to connect to the ISOBUS Converter.
	 * 
	 * @param isobusConverterServer The server address to be used.
	 */
	public void setIsobusConverterServer(String isobusConverterServer) {
		this.isobusConverterServer = isobusConverterServer;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_ISOBUSCONVERTER_SERVER, isobusConverterServer);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the server port being used to connect to the ISOBUS Converter.
	 * 
	 * @return The server port being used.
	 */
	public int getIsobusConverterPort() {
		return isobusConverterPort;
	}

	/**
	 * Sets the server port to be used to connect to the ISOBUS Converter.
	 * 
	 * @param isobusConverterPort The server port to be used.
	 */
	public void setIsobusConverterPort(int isobusConverterPort) {
		this.isobusConverterPort = isobusConverterPort;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_ISOBUSCONVERTER_PORT, isobusConverterPort);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the current sequence number being used for messages publication.
	 * 
	 * @return The current sequence number.
	 */
	synchronized public int getNewSequenceNumber() {
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_LAST_SEQUENCE_NUMBER, ++sequenceNumber);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
		
		return sequenceNumber;
	}
	
	/**
	 * Sets the current sequence number to be used for messages publication.
	 * 
	 * @param sequenceNumber The sequence number.
	 */
	synchronized public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_LAST_SEQUENCE_NUMBER, sequenceNumber);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Gets the current Mission Manager version.
	 * 
	 * @return The current Mission Manager version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Checks if the MQTT works in retained mode for mission publishing.
	 * 
	 * @return {@code true} if the MQTT is used in retained mode, {@code false} otherwise.
	 */
	public boolean isMqttRetainedMission() {
		return mqttRetainedMission;
	}

	/**
	 * Sets the use of MQTT in retained mode for mission publishing.
	 * 
	 * @param mqttRetainedMission {@code true} for using retained mode, {@code false} otherwise.
	 */
	public void setMqttRetainedMission(boolean mqttRetainedMission) {
		this.mqttRetainedMission = mqttRetainedMission;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_RETAINED_MISSION, mqttRetainedMission);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Checks if the MQTT works in retained mode for system configuration.
	 * 
	 * @return {@code true} if the MQTT is used in retained mode, {@code false} otherwise.
	 */
	public boolean isMqttRetainedSystemConfiguration() {
		return mqttRetainedSystemConfiguration;
	}

	/**
	 * Sets the use of MQTT in retained mode for system configuration.
	 * 
	 * @param mqttRetainedMission {@code true} for using retained mode, {@code false} otherwise.
	 */
	public void setMqttRetainedSystemConfiguration(boolean mqttRetainedSystemConfiguration) {
		this.mqttRetainedSystemConfiguration = mqttRetainedSystemConfiguration;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MQTT_RETAINED_SC, mqttRetainedSystemConfiguration);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Gets the shared secret used between the MM and the DDS.
	 * 
	 * @return The shared secret.
	 */
	public String getMm2ddsSecret() {
		return mm2ddsSecret;
	}
	
	/**
	 * Sets the shared secret used between the MM and the DSS.
	 * 
	 * @param mm2ddsSecret The shared secret.
	 */
	public void setMm2ddsSecret(String mm2ddsSecret) {
		this.mm2ddsSecret = mm2ddsSecret;
		
		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MM2DDS_HMAC_SECRET, mm2ddsSecret);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Checks if the secure channel between the MM and the DDS is enabled.
	 * 
	 * @return {@code true} if it is enabled, {@code false} otherwise.
	 */
	public boolean isMm2ddsEnabled() {
		return mm2ddsEnabled;
	}

	/**
	 * Enables or disables the use of a secure channel between the MM and the DDS.
	 * 
	 * @param mm2ddsEnabled {@code true} to enable the secure channel, {@code false} to disable it.
	 */
	public void setMm2ddsEnabled(boolean mm2ddsEnabled) {
		this.mm2ddsEnabled = mm2ddsEnabled;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_MM2DDS_HMAC_ENABLED, mm2ddsEnabled);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Checks if the use of HTTPS is enabled.
	 *  
	 * @return {@code true} if it is enabled, {@code false} otherwise.
	 */
	public boolean isHttpsEnabled() {
		return httpsEnabled;
	}

	/**
	 * Enables or disables the use of HTTPS.
	 * 
	 * @param httpsEnabled {@code true} to enable, {@code false} otherwise.
	 */
	public void setHttpsEnabled(boolean httpsEnabled) {
		this.httpsEnabled = httpsEnabled;

		if (storedConfigAvailable) {
			storedConfig.setProperty(PROP_HTTPS_ENABLED, httpsEnabled);
			try {
				storedBuilder.save();
			} catch (ConfigurationException e) {
				mmLog.log(Level.WARNING, "Error saving updated configuration file.");
				if (debug) {
					mmLog.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
}
