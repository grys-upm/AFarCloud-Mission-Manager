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
 * ISOBUS partfield structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Partfield {
	public String partfieldId;			// REQUIRED
	public String partfieldDesignator;	// REQUIRED
	public long partfieldArea;			// REQUIRED
	public String farmIdRef;
	public String customerIdRef;
	public String cropTypeIdRef;        // REQUIRED
	public Polygon polygon;             // REQUIRED
	
	public Partfield() {
		
	}

	@JsonbProperty("PartfieldId")
	public String getPartfieldId() {
		return partfieldId;
	}

	public void setPartfieldId(String partfieldId) {
		this.partfieldId = partfieldId;
	}

	@JsonbProperty("PartfieldDesignator")
	public String getPartfieldDesignator() {
		return partfieldDesignator;
	}

	public void setPartfieldDesignator(String partfieldDesignator) {
		this.partfieldDesignator = partfieldDesignator;
	}

	@JsonbProperty("PartfieldArea")
	public long getPartfieldArea() {
		return partfieldArea;
	}

	public void setPartieldArea(long partfieldArea) {
		this.partfieldArea = partfieldArea;
	}
	
	@JsonbProperty("FarmIdRef")	
	public String getFarmIdRef() {
		return farmIdRef;
	}

	public void setFarmIdRef(String farmIdRef) {
		this.farmIdRef = farmIdRef;
	}

	@JsonbProperty("CustomerIdRef")
	public String getCustomerIdRef() {
		return customerIdRef;
	}

	public void setCustomerIdRef(String customerIdRef) {
		this.customerIdRef = customerIdRef;
	}

	@JsonbProperty("CropTypeIdRef")
	public String getCropTypeIdRef() {
		return cropTypeIdRef;
	}

	public void setCropTypeIdRef(String cropTypeIdRef) {
		this.cropTypeIdRef = cropTypeIdRef;
	}

	@JsonbProperty("Polygon")
	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
}
