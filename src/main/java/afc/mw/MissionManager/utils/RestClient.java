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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.util.Header;

import com.afarcloud.thrift.Mission;

import afc.mw.MissionManager.MissionManagerContext;
import afc.mw.MissionManager.types.RegisteredVehicle;
import afc.mw.MissionManager.types.isobus.PrescriptionMap;

/**
 * This class provides a REST client for accessing multiple REST services in the AFC Platform
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class RestClient {
	
	private MissionManagerContext context = MissionManagerContext.getInstance();
	private Logger mmLog = context.mmLog;
	
    private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(false).withFormatting(true));
	
	private static RestClient instance = null;

	public RestClient () { }
	
	/**
	 * Gets the singleton instance of the REST client
	 * 
	 * @return The singleton instance of the REST client
	 */
    public static RestClient getInstance() {
        if(instance == null)
            instance = new RestClient();
        
        return instance;	
    }	
	
    /**
     * Gets the total number of vehicles listed in the AFC repository.
     * 
     * @return The total number of vehicles listed in the AFC repository.
     */
	public int getTotalVehiclesFromDB() {
		int totalVehicles = 0;

		String requesturi = "https://"
                + context.dqServer + ":" + context.dqPort + "/"
                + "storage/rest/dq/getAllVehicleTypes";
		
		mmLog.log(Level.INFO, "Request registered vehicles to DQ at " + requesturi);

		TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

		    @Override
		    public X509Certificate[] getAcceptedIssuers() {
		        return null;
		    }

		    @Override
		    public void checkClientTrusted(X509Certificate[] certs, String authType)  throws CertificateException {

		    }

		    @Override
		    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		    
			}

		}};
				
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, null);		
		
			Client client = ClientBuilder.newBuilder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.readTimeout(12, TimeUnit.SECONDS)
					.sslContext(sslContext).build();
			WebTarget target = client.target(requesturi);

			Response response = target.request().get();

			String jsonResponse = response.readEntity(String.class);
			
			mmLog.log(Level.INFO, "Response from registered vehicles to DQ: " + response.getStatus());
			mmLog.log(Level.INFO, "Response body from DQ: \n" + jsonResponse);

			ArrayList<RegisteredVehicle> registeredVehicles = new ArrayList<RegisteredVehicle>();
			registeredVehicles = jsonb.fromJson(jsonResponse, ArrayList.class);
			
			totalVehicles = registeredVehicles.size();
		
		mmLog.log(Level.INFO, "Total number of registered vehicles: " + totalVehicles);
		
		} catch (NoSuchAlgorithmException e) {
			mmLog.log(Level.INFO, "Unable to request the number of vehicles. SSL is not available locally.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (KeyManagementException e) {
			mmLog.log(Level.INFO, "Unable to request the number of vehicles. Error with key management.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (ProcessingException e) {
			mmLog.log(Level.INFO, "Unable to request the number of vehicles. Error processing the request.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		}
		
		if (totalVehicles == 0) {
			mmLog.log(Level.WARNING, "Total number of vehicles retrieved from DQ is 0. Using test value of " + context.SCMaxVehicles);
			totalVehicles = context.SCMaxVehicles;
		}
		return totalVehicles;
	}
	
	/**
	 * Sends a global mission plan in JSON format to the DQ
	 * 
	 * @param mission The global mission plan to be sent
	 */
	public void storeMission(Mission mission) {
		String requesturi = "https://"
                + context.dqServer + ":" + context.dqPort + "/"
                + "storage/rest/dq/addMission";
		
		mmLog.log(Level.INFO, "Request storing mission to DQ at " + requesturi);

		TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

		    @Override
		    public X509Certificate[] getAcceptedIssuers() {
		        return null;
		    }

		    @Override
		    public void checkClientTrusted(X509Certificate[] certs, String authType)  throws CertificateException {

		    }

		    @Override
		    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		    
			}

		}};
				
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, null);
			
			Client client = ClientBuilder.newBuilder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.readTimeout(12, TimeUnit.SECONDS)
					.sslContext(sslContext).build();
			WebTarget target = client.target(requesturi);
			
			Response response = target.request()
					.header(Header.ContentType.toString(), MediaType.APPLICATION_JSON)
					.post(Entity.json(jsonb.toJson(mission)));
			
			mmLog.log(Level.INFO, "DQ Mission storing response: " + response.getStatus() + ": " + response.readEntity(String.class));
			
		} catch (NoSuchAlgorithmException e) {
			mmLog.log(Level.INFO, "Unable to store the mission through the DQ. SSL is not available locally.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (KeyManagementException e) {
			mmLog.log(Level.INFO, "Unable to store the mission through the DQ. Error with key management.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (ProcessingException e) {
			mmLog.log(Level.INFO, "Unable to store the mission through the DQ. Error processing the request.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	/**
	 * Sends the Pre-Prescription Map (JSON) to the ISOBUS Converter)
	 * 
	 * @param missionIdHash		Mission ID hash
	 * @param prescriptionMap	Pre-Prescription Map to be sent
	 */
	public void sendPrescriptionMap(String missionIdHash, PrescriptionMap prescriptionMap) {
		String requesturi = "https://"
                + context.isobusConverterServer + ":" + context.isobusConverterPort + "/"
                + "convert";
		
		TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

		    @Override
		    public X509Certificate[] getAcceptedIssuers() {
		        return null;
		    }

		    @Override
		    public void checkClientTrusted(X509Certificate[] certs, String authType)  throws CertificateException {

		    }

		    @Override
		    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		    
			}

		}};
				
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManager, null);
			
			Client client = ClientBuilder.newBuilder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.readTimeout(12, TimeUnit.SECONDS)
					.sslContext(sslContext).build();
			WebTarget target = client.target(requesturi).queryParam("missionID", missionIdHash);
			
			// Added exception for self-signed certificate at the ISOBUS Converter
			mmLog.log(Level.INFO, "Sending prescription map to " + target.getUri());
			
			HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);

			Response response = target.request()
					.header(Header.ContentType.toString(), MediaType.APPLICATION_JSON)
					.post(Entity.json(jsonb.toJson(prescriptionMap)));
			
			String responseBody = response.readEntity(String.class);
			mmLog.log(Level.INFO, "ISOBUS Converter response: " + response.getStatus() + ":\n" + responseBody);

			String datetime = (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(Calendar.getInstance().getTime());
        	String basename = "AFC-PM-" + datetime;
        	String pmResponse = context.current_mission_dir + File.separator + basename + ".xml";
        	PrintWriter pw = null;
	        try {        	
	            pw = new PrintWriter(pmResponse);
	        	pw.println(responseBody);
	        }
	        catch (FileNotFoundException e) {
	        	mmLog.log(Level.WARNING, "Unable to store " + pmResponse);
	        }
	        finally {
	            if ( pw != null ) {
	                pw.close();
	            }
	        }
		} catch (NoSuchAlgorithmException e) {
			mmLog.log(Level.INFO, "Unable to send the prescription map to the ISOBUS Converter. SSL is not available locally.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (KeyManagementException e) {
			mmLog.log(Level.INFO, "Unable to send the prescription map to the ISOBUS Converter. Error with key management.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		} catch (ProcessingException e) {
			mmLog.log(Level.INFO, "Unable to send the prescription map to the ISOBUS Converter. Error processing the request.");
			if (context.debug) {
				mmLog.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}
