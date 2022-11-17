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

/**
 * ISOBUS product groups
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class ProductGroup {
	public String productGroupId;				// REQUIRED
	public String productGroupDesignator;		// REQUIRED
	public String culturalPracticeDesignator;	// REQUIRED
	
	public ProductGroup() {	}

	public ProductGroup(String productGroupId, String productGroupDesignator, String culturalPracticeDesignator) {
		this.productGroupId = productGroupId;
		this.productGroupDesignator = productGroupDesignator;
		this.culturalPracticeDesignator = culturalPracticeDesignator;
	}

	public String getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(String productGroupId) {
		this.productGroupId = productGroupId;
	}

	public String getProductGroupDesignator() {
		return productGroupDesignator;
	}

	public void setProductGroupDesignator(String productGroupDesignator) {
		this.productGroupDesignator = productGroupDesignator;
	}

	public String getCulturalPracticeDesignator() {
		return culturalPracticeDesignator;
	}

	public void setCulturalPracticeDesignator(String culturalPracticeDesignator) {
		this.culturalPracticeDesignator = culturalPracticeDesignator;
	}
}
