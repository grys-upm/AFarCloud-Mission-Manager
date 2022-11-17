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

package afc.mw.MissionManager.types.isobus;

import javax.json.bind.annotation.JsonbProperty;

/**
 * ISOBUS operation technique reference structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class OperationTechniqueReference {
	
	public String operationTechniqueIdRef;

	public OperationTechniqueReference() {
		super();
	}

	public OperationTechniqueReference(String operationTechniqueIdRef) {
		this.operationTechniqueIdRef = operationTechniqueIdRef;
	}

	@JsonbProperty("OperationTechniqueIdRef")
	public String getOperationTechniqueIdRef() {
		return operationTechniqueIdRef;
	}

	public void setOperationTechniqueIdRef(String operationTechniqueIdRef) {
		this.operationTechniqueIdRef = operationTechniqueIdRef;
	}
}
