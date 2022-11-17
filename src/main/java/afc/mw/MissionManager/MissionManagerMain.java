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

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import org.apache.log4j.BasicConfigurator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import afc.mw.utils.servers.ThriftServer.ThriftServer;

/**
 * Mission Manager Main
 *
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 */
public class MissionManagerMain {
	private static final String KEY_STORE_FILE = "keyStoreFile";
	private static final String KEY_STORE_PASS = "keyStorePass";
	public static final String REST_SERVICE_BASE_HOST = "https://0.0.0.0";

	public static String BASE_URI = "";

	static MissionManagerContext context = MissionManagerContext.getInstance();

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		final ResourceConfig rc = new ResourceConfig().packages("afc.mw.MissionManager.services.rest");

		if (context.isHttpsEnabled()) {
			SSLContextConfigurator sslContext = new SSLContextConfigurator();
			
			sslContext.setKeyStoreFile(KEY_STORE_FILE);
			sslContext.setKeyStorePass(KEY_STORE_PASS);
			
			context.mmLog.log(Level.INFO, "Creating a SECURE HTTP server instance...");
			
			return GrizzlyHttpServerFactory.createHttpServer(
					URI.create(BASE_URI),
					rc, 
					false,
					new SSLEngineConfigurator(sslContext));
			
		} else {
			context.mmLog.log(Level.INFO, "Creating a BASIC HTTP server instance...");

			return GrizzlyHttpServerFactory.createHttpServer(
					URI.create(BASE_URI),
					rc,
					false);
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args Command line arguments (ignored).
	 * @throws IOException If there has been any exception while trying to create either the Thrift or the REST servers.
	 */
	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure();

		BASE_URI = REST_SERVICE_BASE_HOST + ":" + context.restPort + "/" + context.restBaseURI;

		System.setProperty("user.language", "en");
		System.setProperty("java.net.preferIPv4Stack", "true");

		context.mmLog.log(Level.INFO, "Starting Thrift Server");
		ThriftServer.getInstance().startThriftServer(context.thriftStyle);

		context.mmLog.log(Level.INFO, "Starting REST Server");
		final HttpServer server = startServer();
		server.start();
	}
}
