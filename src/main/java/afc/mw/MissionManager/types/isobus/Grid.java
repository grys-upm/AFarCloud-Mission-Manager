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
 * ISOBUS grid structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class Grid {
	/** Minimum north position of the grid WGS84 */
	public double gridMinimumNorthPosition;
	/** Minimum east position of the grid WGS84 */
	public double gridMinimumEastPosition;
	/** North direction gridcell size WGS84 */
	public double gridCellNorthSize;
	/** East direction gridcell size WGS84 */
	public double gridCellEastSize;
	/** Number of the gridcells in east direction */
	public int gridMaximumColumn;
	/** Number of the gridcells in north direction */
	public int gridMaximumRow;
	
	public int gridType;
	public int treatmentZoneCode;
	/** 
	 * The grid cells of a grid contain a reference to a TreatmentZone
	 * or a process data variable value
	 */
	public ArrayList<Integer> gridCell;
	
	public Grid() {
		this.gridCell = new ArrayList<Integer>();
	}

	public Grid(double gridMinimumNorthPosition, double gridMinimumEastPosition, double gridCellNorthSize,
			double gridCellEastSize, int gridMaximumColumn, int gridMaximumRow, ArrayList<Integer> gridCells) {
		this.gridMinimumNorthPosition = gridMinimumNorthPosition;
		this.gridMinimumEastPosition = gridMinimumEastPosition;
		this.gridCellNorthSize = gridCellNorthSize;
		this.gridCellEastSize = gridCellEastSize;
		this.gridMaximumColumn = gridMaximumColumn;
		this.gridMaximumRow = gridMaximumRow;
		this.gridCell = gridCells;
	}

	@JsonbProperty("GridMinimumNorthPosition")
	public double getGridMinimumNorthPosition() {
		return gridMinimumNorthPosition;
	}

	public void setGridMinimumNorthPosition(double gridMinimumNorthPosition) {
		this.gridMinimumNorthPosition = gridMinimumNorthPosition;
	}

	@JsonbProperty("GridMinimumEastPosition")
	public double getGridMinimumEastPosition() {
		return gridMinimumEastPosition;
	}

	public void setGridMinimumEastPosition(double gridMinimumEastPosition) {
		this.gridMinimumEastPosition = gridMinimumEastPosition;
	}

	@JsonbProperty("GridCellNorthSize")
	public double getGridCellNorthSize() {
		return gridCellNorthSize;
	}

	public void setGridCellNorthSize(double gridCellNorthSize) {
		this.gridCellNorthSize = gridCellNorthSize;
	}

	@JsonbProperty("GridCellEastSize")
	public double getGridCellEastSize() {
		return gridCellEastSize;
	}

	public void setGridCellEastSize(double gridCellEastSize) {
		this.gridCellEastSize = gridCellEastSize;
	}

	@JsonbProperty("GridMaximumColumn")
	public int getGridMaximumColumn() {
		return gridMaximumColumn;
	}

	public void setGridMaximumColumn(int gridMaximumColumn) {
		this.gridMaximumColumn = gridMaximumColumn;
	}

	@JsonbProperty("GridMaximumRow")
	public int getGridMaximumRow() {
		return gridMaximumRow;
	}

	public void setGridMaximumRow(int gridMaximumRow) {
		this.gridMaximumRow = gridMaximumRow;
	}

	@JsonbProperty("GridType")
	public int getGridType() {
		return gridType;
	}

	public void setGridType(int gridType) {
		this.gridType = gridType;
	}

	@JsonbProperty("TreatmentZoneCode")
	public int getTreatmentZoneCode() {
		return treatmentZoneCode;
	}

	public void setTreatmentZoneCode(int treatmentZoneCode) {
		this.treatmentZoneCode = treatmentZoneCode;
	}

	@JsonbProperty("GridCell")
	public ArrayList<Integer> getGridCell() {
		return gridCell;
	}

	public void setGridCell(ArrayList<Integer> gridCell) {
		this.gridCell = gridCell;
	}
}
