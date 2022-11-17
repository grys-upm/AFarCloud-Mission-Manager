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

package afc.mw.MissionManager;

/**
 * A specific exception for the Mission Manager. 
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 *
 */
public class MissionManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Exception to be thrown by the Mission Manager when an error occurs while processing
	 * a mission, and no message is associated to the error.
	 */
	public MissionManagerException(){
		super();
	}
	
	/**
	 * Exception to be thrown by the Mission Manager when an error occurs while processing
	 * a mission, and a descriptive message is associated to the error.
	 * 
	 * @param message The descriptive message of the error
	 */
	public MissionManagerException (String message) { 
		super(message);
	}	
}
