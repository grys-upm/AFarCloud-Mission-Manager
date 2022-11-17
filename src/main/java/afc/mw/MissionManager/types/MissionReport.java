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
package afc.mw.MissionManager.types;

import java.util.ArrayList;

/**
 * Mission report structure
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionReport {
	public static final byte INTERNAL_ERROR = (byte) 0xff;
	public static final byte MISSION_REPORT_VALID = 0x00;
	public static final byte MISSION_REPORT_INVALID_MISSION_ID = 0x01;
	public static final byte MISSION_REPORT_INVALID_MISSION_NOT_ACTIVE = 0x02;
	public static final byte MISSION_REPORT_INVALID_MISSION_NO_MISSION = 0x03;
	public static final byte MISSION_REPORT_INVALID_VEHICLE_ID = 0x10;

	public int sequence_number;
	public int mission_id;
	public int vehicle_id;
	public int mission_status_id;
	public ArrayList<CommandStatus> command_report_array = new ArrayList<CommandStatus>();
	public long last_update;
}
