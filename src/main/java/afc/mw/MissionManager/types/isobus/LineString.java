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
 * ISOBUS line string structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class LineString {
	public static final int POLYGON_EXTERIOR = 1;
	public static final int POLYGON_INTERIOR = 2;
	public static final int TRAM_LINE = 3;
	public static final int SAMPLING_ROUTE = 4;
	public static final int GUIDANCE_PATTERN = 5;
	public static final int DRAINAGE = 6;
	public static final int FENCE = 7;
	public static final int FLAG = 8;
	public static final int OBSTACLE = 9;
	
	public int lineStringType;				// REQUIRED
	public String lineStringDesignator;
	public String lineStringColour;
	public String lineStringIdRef;
	public ArrayList<Point> point;			// REQUIRED

	public LineString() {
		this.point = new ArrayList<Point>();
	}

	@JsonbProperty("LinestringType")
	public int getLineStringType() {
		return lineStringType;
	}

	public void setLineStringType(int lineStringType) {
		this.lineStringType = lineStringType;
	}

	@JsonbProperty("LinestringDesignator")
	public String getLineStringDesignator() {
		return lineStringDesignator;
	}

	public void setLineStringDesignator(String lineStringDesignator) {
		this.lineStringDesignator = lineStringDesignator;
	}

	@JsonbProperty("LinestringColour")
	public String getLineStringColour() {
		return lineStringColour;
	}

	public void setLineStringColour(String lineStringColour) {
		this.lineStringColour = lineStringColour;
	}

	@JsonbProperty("LinestringIdRef")
	public String getLineStringIdRef() {
		return lineStringIdRef;
	}

	public void setLineStringIdRef(String lineStringIdRef) {
		this.lineStringIdRef = lineStringIdRef;
	}

	@JsonbProperty("Point")
	public ArrayList<Point> getPoint() {
		return point;
	}

	public void setPoint(ArrayList<Point> point) {
		this.point = point;
	}
}
