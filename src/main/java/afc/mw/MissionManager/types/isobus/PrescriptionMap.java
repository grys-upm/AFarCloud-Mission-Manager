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
 * ISOBUS Prescription Map structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class PrescriptionMap {
	
	// TODO: Make versions and managements configurable
	public int versionMajor = 2;
	public int versionMinor = 3;
	public String managementSoftwareManufacturer = "UPM";
	public String managementSoftwareVersion = "1.0-rc";

	public Customer customer;
	public Farm farm;
	public ArrayList<OperationTechnique> operationTechnique;
	public Partfield partfield;
	public ArrayList<Product> product;
	public ArrayList<CropType> cropType;
	public ArrayList<CulturalPractice> culturalPractice;
	public Worker worker;
	public ArrayList<Task> task;
	
	public PrescriptionMap() {
		this.customer = new Customer();
		this.farm = new Farm();
		this.operationTechnique = new ArrayList<OperationTechnique>();
		this.partfield = new Partfield();
		this.product = new ArrayList<Product>();
		this.cropType = new ArrayList<CropType>();
		this.culturalPractice = new ArrayList<CulturalPractice>();
		this.worker = new Worker();
		this.task = new ArrayList<Task>();
	}

	public PrescriptionMap(double tasks, Customer customer, Farm farm, Partfield partField,
			ArrayList<Product> products, ArrayList<CropType> cropTypes,
			ArrayList<Task> task, Worker worker) {
		this.customer = customer;
		this.farm = farm;
		this.partfield = partField;
		this.product = products;
		this.cropType = cropTypes;
		this.task = task;
		this.worker = worker;
	}

	@JsonbProperty("VersionMajor")
	public int getVersionMajor() {
		return versionMajor;
	}

	public void setVersionMajor(int versionMajor) {
		this.versionMajor = versionMajor;
	}

	@JsonbProperty("VersionMinor")
	public int getVersionMinor() {
		return versionMinor;
	}

	public void setVersionMinor(int versionMinor) {
		this.versionMinor = versionMinor;
	}

	@JsonbProperty("ManagementSoftwareManufacturer")
	public String getManagementSoftwareManufacturer() {
		return managementSoftwareManufacturer;
	}

	public void setManagementSoftwareManufacturer(String managementSoftwareManufacturer) {
		this.managementSoftwareManufacturer = managementSoftwareManufacturer;
	}

	@JsonbProperty("ManagementSoftwareVersion")
	public String getManagementSoftwareVersion() {
		return managementSoftwareVersion;
	}

	public void setManagementSoftwareVersion(String managementSoftwareVersion) {
		this.managementSoftwareVersion = managementSoftwareVersion;
	}

	@JsonbProperty("OperationTechnique")
	public ArrayList<OperationTechnique> getOperationTechnique() {
		return operationTechnique;
	}

	public void setOperationTechnique(ArrayList<OperationTechnique> operationTechnique) {
		this.operationTechnique = operationTechnique;
	}

	@JsonbProperty("Customer")
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@JsonbProperty("Farm")
	public Farm getFarm() {
		return farm;
	}

	public void setFarm(Farm farm) {
		this.farm = farm;
	}

	@JsonbProperty("Partfield")
	public Partfield getPartfield() {
		return partfield;
	}

	public void setPartfield(Partfield partField) {
		this.partfield = partField;
	}

	@JsonbProperty("Product")
	public ArrayList<Product> getProduct() {
		return product;
	}

	public void setProduct(ArrayList<Product> product) {
		this.product = product;
	}

	@JsonbProperty("CropType")
	public ArrayList<CropType> getCropType() {
		return cropType;
	}

	public void setCropType(ArrayList<CropType> cropType) {
		this.cropType = cropType;
	}
	
	@JsonbProperty("CulturalPractice")
	public ArrayList<CulturalPractice> getCulturalPractice() {
		return culturalPractice;
	}

	public void setCulturalPractice(ArrayList<CulturalPractice> culturalPractice) {
		this.culturalPractice = culturalPractice;
	}

	@JsonbProperty("Task")
	public ArrayList<Task> getTask() {
		return task;
	}

	public void setTask(ArrayList<Task> task) {
		this.task = task;
	}

	@JsonbProperty("Worker")
	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}
}
