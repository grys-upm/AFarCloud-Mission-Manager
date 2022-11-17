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
 * ISOBUS task structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Task {
	public String taskId;								// REQUIRED
	public String taskDesignator;
	public String customerIdRef;
	public String farmIdRef;
	public String partfieldIdRef;
	public String responsibleWorkerIdRef;
	public int defaultTreatmentZoneCode;
	public int taskStatus;
	public int positionLostTreatmentZoneCode;
	public int outOfFieldTreatmentZoneCode;
	/**
	 * Inside a task, treatment zones can be referenced. Each TreatmentZone
	 * has a special meaning concerning the included ProcessDataVariables.
	 * The values of those ProcessDataVariables are to be applied globally
	 * to the whole task.
	 */
	public ArrayList<TreatmentZone> treatmentZone;
	public OperTechPractice operTechPractice;
	public Grid grid;
	
	public Task() {
		this.treatmentZone = new ArrayList<TreatmentZone>();
	}

	public Task(String taskId, String taskDesignator, String customerIdRef, String farmIdRef, String partFieldIdRef,
			String responsibleWorkerIdRef, int defaultTreatmentsZoneCode, ArrayList<TreatmentZone> treatmentZone,
			Grid grid) {
		this.taskId = taskId;
		this.taskDesignator = taskDesignator;
		this.customerIdRef = customerIdRef;
		this.farmIdRef = farmIdRef;
		this.partfieldIdRef = partFieldIdRef;
		this.responsibleWorkerIdRef = responsibleWorkerIdRef;
		this.defaultTreatmentZoneCode = defaultTreatmentsZoneCode;
		this.treatmentZone = treatmentZone;
		this.grid = grid;
	}

	@JsonbProperty("TaskId")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@JsonbProperty("TaskDesignator")
	public String getTaskDesignator() {
		return taskDesignator;
	}

	public void setTaskDesignator(String taskDesignator) {
		this.taskDesignator = taskDesignator;
	}

	@JsonbProperty("CustomerIdRef")
	public String getCustomerIdRef() {
		return customerIdRef;
	}

	public void setCustomerIdRef(String customerIdRef) {
		this.customerIdRef = customerIdRef;
	}

	@JsonbProperty("FarmIdRef")
	public String getFarmIdRef() {
		return farmIdRef;
	}

	public void setFarmIdRef(String farmIdRef) {
		this.farmIdRef = farmIdRef;
	}

	@JsonbProperty("PartfieldIdRef")
	public String getPartfieldIdRef() {
		return partfieldIdRef;
	}

	public void setPartfieldIdRef(String partfieldIdRef) {
		this.partfieldIdRef = partfieldIdRef;
	}

	@JsonbProperty("ResponsibleWorkerIdRef")
	public String getResponsibleWorkerIdRef() {
		return responsibleWorkerIdRef;
	}

	public void setResponsibleWorkerIdRef(String responsibleWorkerIdRef) {
		this.responsibleWorkerIdRef = responsibleWorkerIdRef;
	}

	@JsonbProperty("DefaultTreatmentZoneCode")
	public int getDefaultTreatmentZoneCode() {
		return defaultTreatmentZoneCode;
	}

	public void setDefaultTreatmentZoneCode(int defaultTreatmentZoneCode) {
		this.defaultTreatmentZoneCode = defaultTreatmentZoneCode;
	}

	@JsonbProperty("TreatmentZone")
	public ArrayList<TreatmentZone> getTreatmentZone() {
		return treatmentZone;
	}

	public void setTreatmentZone(ArrayList<TreatmentZone> treatmentZone) {
		this.treatmentZone = treatmentZone;
	}


	@JsonbProperty("Grid")
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	@JsonbProperty("TaskStatus")
	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

	@JsonbProperty("PositionLostTreatmentZoneCode")
	public int getPositionLostTreatmentZoneCode() {
		return positionLostTreatmentZoneCode;
	}

	public void setPositionLostTreatmentZoneCode(int positionLostTreatmentZoneCode) {
		this.positionLostTreatmentZoneCode = positionLostTreatmentZoneCode;
	}

	@JsonbProperty("OutOfFieldTreatmentZoneCode")
	public int getOutOfFieldTreatmentZoneCode() {
		return outOfFieldTreatmentZoneCode;
	}

	public void setOutOfFieldTreatmentZoneCode(int outOfFieldTreatmentZoneCode) {
		this.outOfFieldTreatmentZoneCode = outOfFieldTreatmentZoneCode;
	}

	@JsonbProperty("OperTechPractice")
	public OperTechPractice getOperTechPractice() {
		return operTechPractice;
	}

	public void setOperTechPractice(OperTechPractice operTechPractice) {
		this.operTechPractice = operTechPractice;
	}	
}
