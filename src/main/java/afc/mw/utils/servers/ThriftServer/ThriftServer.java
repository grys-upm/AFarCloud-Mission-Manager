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

package afc.mw.utils.servers.ThriftServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import com.afarcloud.thrift.MissionManagerService;

import afc.mw.MissionManager.MissionManagerContext;
import afc.mw.MissionManager.services.thrift.MissionManagerThriftServiceHandler;

/**
 * Mission Manager Thrift server
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class ThriftServer {
	MissionManagerContext context = MissionManagerContext.getInstance();
	
	private static final String MISSION_MANAGER_SERVICE_NAME = "MissionManagerService";	
	
    private int thriftPort = context.thriftPort;
    private Logger mmLog = context.mmLog; 
    private boolean debug = context.debug;

    private TSimpleServer server;
    
    public static ThriftServer instance = null;    
        
    private ThriftServer() { }

    public static ThriftServer getInstance() {
    	if(instance == null)
    		instance = new ThriftServer();

    	return instance;	
    }    
    
    /**
     * Starts the Thrift service as a new thread
     */
    public void startThriftServer(String type) {
    	
    	try {
			server = getServer(type);
		} catch (Exception e) {
			mmLog.log(Level.SEVERE, "Unable to start the thrift server");
			if (debug) {
				mmLog.log(Level.SEVERE, e.getMessage(), e);
			}
		}
    	
		Runnable runnableServer = new Runnable() {
			public void run() {
				mmLog.log(Level.INFO, "Thrift server started");
				server.serve();
			}
		};

		(new Thread(runnableServer)).start();
    }

    /**
     * Stops the Thrift service as a new thread
     */
    public void stopThriftServer() {
    	mmLog.log(Level.INFO, "Stopping thrift server...");
        server.stop();
    	mmLog.log(Level.INFO, "Thrift server stopped");
    }

    /**
     * Creates a Thrift server of the desired type.
     * 
     * @param type			Type of thrift server (simple|multiplex)
     * @return				The created server.
     * @throws Exception	When it was not possible to create the server.
     */
    private TSimpleServer getServer(String type) throws Exception {
    	if (type.equalsIgnoreCase("simplex")) {
			MissionManagerThriftServiceHandler missionManagerServiceHandler = new MissionManagerThriftServiceHandler();
			
			MissionManagerService.Processor processor = new MissionManagerService.Processor(missionManagerServiceHandler);
			
			TServerTransport serverTransport = new TServerSocket(thriftPort);
			TSimpleServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			
			return server;
    		
    	}
    	else if (type.equalsIgnoreCase("multiplex")) {
			TMultiplexedProcessor processor = new TMultiplexedProcessor();

			MissionManagerThriftServiceHandler missionManagerServiceHandler = new MissionManagerThriftServiceHandler();

			processor.registerProcessor(MISSION_MANAGER_SERVICE_NAME,
					new MissionManagerService.Processor(missionManagerServiceHandler));

			TServerTransport serverTransport = new TServerSocket(thriftPort);

			TTransportFactory factory = new TFramedTransport.Factory();

			TServer.Args args = new TServer.Args(serverTransport);
			args.processor(processor);
			args.transportFactory(factory);
			TSimpleServer server = new TSimpleServer(args);

			return server;
    	}
    	
    	throw new Exception("Unrecognized server type: " + type);
    }
    
    /**
     * Creates a simplex Thrift server
     * 
     * @return A simplex Thrift server
     */
    private TSimpleServer getSimplexServer () {    	
    	MissionManagerThriftServiceHandler handler;
    	MissionManagerService.Processor processor;
    	
    		try {
    			handler = new MissionManagerThriftServiceHandler();
    			processor = new MissionManagerService.Processor(handler);

    			TServerTransport serverTransport = new TServerSocket(thriftPort);

    			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

    			System.out.println("Starting the simple server...");
    			
    			Runnable simple = new Runnable() {
    				public void run() {
    	    			server.serve();
    				}
    			};
    			
    			new Thread(simple).start();
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	
		return null;
    }
    
    /**
     * Creates a multiplex Thrift server
     * 
     * @param processor				The processor associated to the multiplexed server.
     * @return						A multiplexed server.
     * @throws TTransportException	If it was not possible to create the server.
     */
    private TSimpleServer getMultiplexServer(TMultiplexedProcessor processor) throws TTransportException {
        MissionManagerThriftServiceHandler missionManagerServiceHandler = new MissionManagerThriftServiceHandler();

		processor.registerProcessor(MISSION_MANAGER_SERVICE_NAME,
				new MissionManagerService.Processor(missionManagerServiceHandler));

		TServerTransport serverTransport = new TServerSocket(thriftPort);

		TTransportFactory factory = new TFramedTransport.Factory();

		TServer.Args args = new TServer.Args(serverTransport);
		args.processor(processor);
		args.transportFactory(factory);
		TSimpleServer server = new TSimpleServer(args);
		
		return server;
    }    
}
