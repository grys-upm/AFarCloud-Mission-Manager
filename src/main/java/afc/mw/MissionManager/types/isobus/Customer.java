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
 * ISOBUS customer structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Customer {

	public String customerId;			// REQUIRED
	public String customerDesignator;	// REQUIRED
	public String customerPOBox;
	public String customerStreet;
	public String customerPhone;
	public String customerMobile;
	public String customerFax;
	public String customerEMail;
	public String customerPostalCode;
	public String customerCity;
	public String customerState;
	public String customerCountry;
	
	public Customer() {	}

	@JsonbProperty("CustomerId")
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@JsonbProperty("CustomerDesignator")
	public String getCustomerDesignator() {
		return customerDesignator;
	}

	public void setCustomerDesignator(String customerDesignator) {
		this.customerDesignator = customerDesignator;
	}

	@JsonbProperty("CustomerPOBox")
	public String getCustomerPOBox() {
		return customerPOBox;
	}

	public void setCustomerPOBox(String customerPOBox) {
		this.customerPOBox = customerPOBox;
	}

	@JsonbProperty("CustomerStreet")
	public String getCustomerStreet() {
		return customerStreet;
	}

	public void setCustomerStreet(String customerStreet) {
		this.customerStreet = customerStreet;
	}
	
	@JsonbProperty("CustomerPhone")
	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	@JsonbProperty("CustomerMobile")
	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	@JsonbProperty("CustomerFax")
	public String getCustomerFax() {
		return customerFax;
	}

	public void setCustomerFax(String customerFax) {
		this.customerFax = customerFax;
	}

	@JsonbProperty("CustomerEMail")
	public String getCustomerEMail() {
		return customerEMail;
	}

	public void setCustomerEMail(String customerEMail) {
		this.customerEMail = customerEMail;
	}

	@JsonbProperty("CustomerPostalCode")
	public String getCustomerPostalCode() {
		return customerPostalCode;
	}

	public void setCustomerPostalCode(String customerPostalCode) {
		this.customerPostalCode = customerPostalCode;
	}

	@JsonbProperty("CustomerCity")
	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	@JsonbProperty("CustomerState")
	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	@JsonbProperty("CustomerCountry")
	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}
}
