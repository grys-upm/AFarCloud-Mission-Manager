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

package afc.mw.MissionManager.utils;

/**
 * The possible validation results
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public enum ValidationResult {
	VALID(0, "The mission is valid."),
	NO_MISSION_NAME(300, "The mission does not have a mission name."),
	NO_HOME_LOCATION(301, "The mission does not include a home location."),
	NO_FORBIDDEN_AREA(302, "The mission does not include a forbidden area."),
	NO_COMMANDS_WARN(303, "The mission includes some unmanned vehicles without commands."),
	NO_PRESCRIPTION_MAP_WARN(304, "The mission includes some tractors without prescription maps."),
	NO_COMMANDS_PM_WARN(305, "The mission includes both unmanned vehicles without commands and tractors without prescription maps."),
	NO_MISSION_ID(400, "The mission does not have a mission ID."),
	NO_NAVIGATION(401, "The mission does not include a navigation area."),
	NO_VEHICLES(402, "The mission does not include any vehicle."),
	NO_TASKS(403, "The mission does not include tasks."),
	NO_COMMANDS(404, "The mission includes only unmanned vehicles with no commands."),
	NO_PRESCRIPTION_MAP(405, "The mission includes only tractors with no prescription maps."),
	MISSION_NULL(500, "The mission is empty (unassigned)."),
	MISSION_EMPTY(501, "The mission is empty.");

	private final int code;
	private final String description;

	private ValidationResult(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}
}
