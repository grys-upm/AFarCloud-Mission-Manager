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
 * ISOBUS worker structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Worker {
	public String workerId;				// REQUIRED
	public String workerDesignator;		// REQUIRED
	public String workerFirstName;
	public String workerPOBox;
	public String workerStreet;
	public String workerPhone;
	public String workerMobile;
	public String workerEMail;
	public String workerPostalCode;
	public String workerCity;
	public String workerState;
	public String workerCountry;
	public String workerLicenseNumber;
	
	public Worker() {	}

	@JsonbProperty("WorkerId")
	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	@JsonbProperty("WorkerDesignator")	
	public String getWorkerDesignator() {
		return workerDesignator;
	}

	public void setWorkerDesignator(String workerDesignator) {
		this.workerDesignator = workerDesignator;
	}

	// TODO: Add JsonbProperty annotations for optional fields after Y2
	public String getWorkerFirstName() {
		return workerFirstName;
	}

	public void setWorkerFirstName(String workerFirstName) {
		this.workerFirstName = workerFirstName;
	}

	public String getWorkerPOBox() {
		return workerPOBox;
	}

	public void setWorkerPOBox(String workerPOBox) {
		this.workerPOBox = workerPOBox;
	}

	public String getWorkerStreet() {
		return workerStreet;
	}

	public void setWorkerStreet(String workerStreet) {
		this.workerStreet = workerStreet;
	}

	public String getWorkerPhone() {
		return workerPhone;
	}

	public void setWorkerPhone(String workerPhone) {
		this.workerPhone = workerPhone;
	}

	public String getWorkerMobile() {
		return workerMobile;
	}

	public void setWorkerMobile(String workerMobile) {
		this.workerMobile = workerMobile;
	}

	public String getWorkerEMail() {
		return workerEMail;
	}

	public void setWorkerEMail(String workerEMail) {
		this.workerEMail = workerEMail;
	}

	public String getWorkerPostalCode() {
		return workerPostalCode;
	}

	public void setWorkerPostalCode(String workerPostalCode) {
		this.workerPostalCode = workerPostalCode;
	}

	public String getWorkerCity() {
		return workerCity;
	}

	public void setWorkerCity(String workerCity) {
		this.workerCity = workerCity;
	}

	public String getWorkerState() {
		return workerState;
	}

	public void setWorkerState(String workerState) {
		this.workerState = workerState;
	}

	public String getWorkerCountry() {
		return workerCountry;
	}

	public void setWorkerCountry(String workerCountry) {
		this.workerCountry = workerCountry;
	}

	public String getWorkerLicenseNumber() {
		return workerLicenseNumber;
	}

	public void setWorkerLicenseNumber(String workerLicenseNumber) {
		this.workerLicenseNumber = workerLicenseNumber;
	}
}
