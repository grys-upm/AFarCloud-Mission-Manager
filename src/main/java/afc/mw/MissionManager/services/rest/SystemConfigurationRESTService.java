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
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import afc.mw.MissionManager.MissionManagerContext;
import afc.mw.MissionManager.utils.SCRequestManager;

/**
 * REST service for the AFC System Configurator
 * 
 *  @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 */
@Path("/")
public class SystemConfigurationRESTService {

	private MissionManagerContext context = MissionManagerContext.getInstance();
    private Logger mmLog = context.mmLog;
    private Logger sciLog = context.sciLog;
    
	private long tic;
	private long toc;
	
    public SystemConfigurationRESTService() { }
    
    /**
     * Processes a new vehicle status request received from the System Configuration.
     * 
     * @param requestID The ID for the request.
     * @return The status of the request.
     */
    @PUT
    @Path("vehiclesStatusRequest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putVehicleStatusRequest(@FormParam("reqID") int requestID) {
    	mmLog.log(Level.INFO, "Received REST vehicle status update request with request ID " + requestID);
    	
    	tic = System.currentTimeMillis();
    	int status = SCRequestManager.getInstance().requestVehiclesStatus(requestID);
    	toc = System.currentTimeMillis();
    	
    	sciLog.log(Level.INFO, "vehicleStatusRequest," + requestID + "," + tic + "," + toc + "," + (toc-tic));
    	return Response.status(status).build();
    }
}
