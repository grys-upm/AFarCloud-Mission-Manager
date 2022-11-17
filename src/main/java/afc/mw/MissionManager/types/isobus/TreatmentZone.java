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

import java.util.ArrayList;

import javax.json.bind.annotation.JsonbProperty;

/**
 * ISOBUS tratment zone structure
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class TreatmentZone {
	public int treatmentZoneCode;								// REQUIRED 
	public String treatmentZoneDesginator;
	public String treatmentZoneColour;
	public ArrayList<ProcessDataVariable> processDataVariable;
	
	public TreatmentZone() {
		this.processDataVariable = new ArrayList<ProcessDataVariable>();
	}

	public TreatmentZone(int treatmentZoneCode, String treatmentZoneDesginator,
			ArrayList<ProcessDataVariable> processDataVariables) {
		this.treatmentZoneCode = treatmentZoneCode;
		this.treatmentZoneDesginator = treatmentZoneDesginator;
		this.processDataVariable = processDataVariables;
	}

	@JsonbProperty("TreatmentZoneCode")
	public int getTreatmentZoneCode() {
		return treatmentZoneCode;
	}

	public void setTreatmentZoneCode(int treatmentZoneCode) {
		this.treatmentZoneCode = treatmentZoneCode;
	}

	@JsonbProperty("TreatmentZoneDesignator")
	public String getTreatmentZoneDesginator() {
		return treatmentZoneDesginator;
	}

	public void setTreatmentZoneDesginator(String treatmentZoneDesginator) {
		this.treatmentZoneDesginator = treatmentZoneDesginator;
	}

	@JsonbProperty("TreatmentZoneColour")
	public String getTreatmentZoneColour() {
		return treatmentZoneColour;
	}

	public void setTreatmentZoneColour(String treatmentZoneColour) {
		this.treatmentZoneColour = treatmentZoneColour;
	}

	@JsonbProperty("ProcessDataVariable")
	public ArrayList<ProcessDataVariable> getProcessDataVariable() {
		return processDataVariable;
	}

	public void setProcessDataVariable(ArrayList<ProcessDataVariable> processDataVariable) {
		this.processDataVariable = processDataVariable;
	}
}
