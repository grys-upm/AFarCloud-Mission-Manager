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
 * ISOBUS point structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Point {
	public final static int FLAG = 1;
	public final static int OTHER = 2;
	/** Entry point of a field */
	public final static int FIELD_ACCESS = 3;
	/** Location where product is stored on a field */
	public final static int STORAGE = 4;
	/** Any kind of obstacle, e.g. used to generate warnings when approaching an obstacle */
	public final static int OBSTACLE = 5;
	/** Start point of a guidance line, e.g. the A point in an A-B line */
	public final static int GUIDANCE_REFERENCE_A = 6;
	/** End point of a guidance line, e.g. the B point in an A-B line */
	public final static int GUIDANCE_REFERENCE_B = 7;
	/** Centre point of a guidance pivot pattern */
	public final static int GUIDANCE_REFERENCE_CENTER = 8;
	/** Guidance related point that is neither an A, B, or center reference point */
	public final static int GUIDANCE_REFERENCE_POINT = 9;
	/** Location in e.g. a Partfield that identifies the field for administrative purposes */ 
	public final static int PARTFIELD_REFERENCE_POINT = 10;
	public final static int HOMEBASE = 11;
	
	public int pointType;					// REQUIRED
	public String pointDesignator;
	public double pointNorth;				// REQUIRED
	public double pointEast;				// REQUIRED
	public String pointColour;
	public String pointId;

	
	public Point() {	}
	
	public Point(int pointType, double pointNorth, double pointEast) {
		this.pointType = pointType;
		this.pointNorth = pointNorth;
		this.pointEast = pointEast;
	}

	public int getPointType() {
		return pointType;
	}

	public void setPointType(int pointType) {
		this.pointType = pointType;
	}

	public String getPointDesignator() {
		return pointDesignator;
	}

	public void setPointDesignator(String pointDesignator) {
		this.pointDesignator = pointDesignator;
	}

	public double getPointNorth() {
		return pointNorth;
	}

	public void setPointNorth(double pointNorth) {
		this.pointNorth = pointNorth;
	}

	public double getPointEast() {
		return pointEast;
	}

	public void setPointEast(double pointEast) {
		this.pointEast = pointEast;
	}

	public String getPointColour() {
		return pointColour;
	}

	public void setPointColour(String pointColour) {
		this.pointColour = pointColour;
	}

	public String getPointId() {
		return pointId;
	}

	public void setPointId(String pointId) {
		this.pointId = pointId;
	}
}
