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
 * ISOBUS farm structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Farm {
	public String farmId;			// REQUIRED
	public String farmDesignator;	// REQUIRED
	public String farmStreet;
	public String farmPOBox;
	public String farmPostalCode;
	public String farmCity;
	public String farmState;
	public String farmCountry;
	public String customerIdRef;
	
	public Farm() {	}

	@JsonbProperty("FarmId")
	public String getFarmId() {
		return farmId;
	}

	public void setFarmId(String farmID) {
		this.farmId = farmID;
	}

	@JsonbProperty("FarmDesignator")
	public String getFarmDesignator() {
		return farmDesignator;
	}

	public void setFarmDesignator(String farmDesignator) {
		this.farmDesignator = farmDesignator;
	}

	@JsonbProperty("FarmStreet")
	public String getFarmStreet() {
		return farmStreet;
	}

	public void setFarmStreet(String farmStreet) {
		this.farmStreet = farmStreet;
	}

	@JsonbProperty("FarmPOBox")
	public String getFarmPOBox() {
		return farmPOBox;
	}

	public void setFarmPOBox(String farmPOBox) {
		this.farmPOBox = farmPOBox;
	}
	
	@JsonbProperty("FarmPostalCode")
	public String getFarmPostalCode() {
		return farmPostalCode;
	}

	public void setFarmPostalCode(String farmPostalCode) {
		this.farmPostalCode = farmPostalCode;
	}

	@JsonbProperty("FarmCity")
	public String getFarmCity() {
		return farmCity;
	}

	public void setFarmCity(String farmCity) {
		this.farmCity = farmCity;
	}

	@JsonbProperty("FarmState")
	public String getFarmState() {
		return farmState;
	}

	public void setFarmState(String farmState) {
		this.farmState = farmState;
	}

	@JsonbProperty("FarmCountry")
	public String getFarmCountry() {
		return farmCountry;
	}

	public void setFarmCountry(String farmCountry) {
		this.farmCountry = farmCountry;
	}

	@JsonbProperty("CustomerIdRef")
	public String getCustomerIdRef() {
		return customerIdRef;
	}

	public void setCustomerIdRef(String customerIdRef) {
		this.customerIdRef = customerIdRef;
	}
}
