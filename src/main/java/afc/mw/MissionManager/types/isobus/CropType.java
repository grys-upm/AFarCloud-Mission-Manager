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
 * ISOBUS crop type structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class CropType {
	public String cropTypeId;					// REQUIRED
	public String cropTypeDesignator;			// REQUIRED
	public ArrayList<CropVariety> cropVariety;
	
	public CropType() {	}

	public CropType(String cropTypeId, String cropTypeDesignator) {
		this.cropTypeId = cropTypeId;
		this.cropTypeDesignator = cropTypeDesignator;
	}

	public CropType(String cropTypeId, String cropTypeDesignator, ArrayList<CropVariety> cropVariety) {
		this.cropTypeId = cropTypeId;
		this.cropTypeDesignator = cropTypeDesignator;
		this.cropVariety = cropVariety;
	}

	@JsonbProperty("CropTypeId")
	public String getCropTypeId() {
		return cropTypeId;
	}
	
	public void setCropTypeId(String cropTypeId) {
		this.cropTypeId = cropTypeId;
	}
	
	@JsonbProperty("CropTypeDesignator")
	public String getCropTypeDesignator() {
		return cropTypeDesignator;
	}
	
	public void setCropTypeDesignator(String cropTypeDesignator) {
		this.cropTypeDesignator = cropTypeDesignator;
	}
	
	@JsonbProperty("CropVariety")
	public ArrayList<CropVariety> getCropVariety() {
		return cropVariety;
	}
	
	public void setCropVariety(ArrayList<CropVariety> cropVariety) {
		this.cropVariety = cropVariety;
	}
}
