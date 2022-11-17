# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Released]
## [2.0.0] - 2021-11-10
### Added
- SCI Log messages to:
  - MissionManager.
- Javadoc comments.

### Removed
- IReport interface has been removed, as it is no longer in use in the Mission Manager.

## [1.5.0-alpha1] - 2021-08-31
### Added
- Support to HTTP SSL.

## [1.3.2] - 2021-02-15
### Added
- Log messages for tracing the processing of mission reports.
- Removed To-Do comments.

### Fixed
- Bug in remote configuration of HMAC signature enable flag.
- Bug reading configuration from stored.properties overwriting the configuration from config.properties and local.properties for some properties when not defined in stored.properties.
- Typos in error messages related to Mission Report processing.

## Changed
- Error message when a null field is detected in the Mission Report is now a bit more clear.

## [1.3.1] - 2021-02-11
### Added
- Mission Report validity check now also verifies if the vehicle reporting is participating in the mission.
- New property to control if HMAC mission signature is enabled.

### Changed
- Mission Report validity check is moved to the Mission Manager.

### Fixed
- Update the HMAC shared secret via REST configuration now correctly updates the HMAC shared secret.

## [1.3.0-alpha] - 2021-02-09
### Added
- REST service for MPR <-> MMAN interaction.
- New properties for:
  - The shared secret between the MM and the DDS.
  - The System Configuration (pre-mission) report topic.
- Vehicle plan in JSON format is signed before publishing to the DDS through the MQTT.

### Changed
- Pre-mission procedure is now changed to follow the updated interaction:
  - Publish request on SC topic.
  - Subscribe to SC Report topic.
  - Await for report published on SC Report or timeout.

## [1.0.0-rc5] - 2020-09-21
### Fixed
- Bug in sending abort mission (either soft or hard). It only sent aborts to vehicles registered as AUAV, ignoring the rest of UV.

## [1.0.0-rc4c] - 2020-09-16
### Changed
- Changed missionId sent to the ISOBUS Converter to the original integer value instead of being hashed.

## [1.0.0-rc4b] - 2020-09-15
### Added
- Extended validation results with human friendly messages
- MMT client for notification of validation results, focused on errors.
- Improved handling of sequence numbers with resilience and configuration.
- New properties:
  - `mmt.enabled_notifications`, used for allowing the MMT to enable or disable notifications from the Mission Manager.
  - `last_sequence_number`, used for storing the last used sequence number. For now it is a unique sequence number for the whole system. In further updates it could be split in a different sequence numbering scheme for each communication.
- Updated the REST configuration for including the new properties added.

## [1.0.0-rc4] - 2020-09-14
### Changed
- Mission validation has been moved from the Mission Parser to its own class.

## [1.0.0-rc3c] - 2020-09-09
### Added
- Workaround for when the received mission has not set the mission name (issue MQTT topic level).

### Changed
- Updated to the last thrift definitions (issue CommandTypes enumeration values).

## [1.0.0-rc3] - 2020-09-08
### Added
- README file added to the documentation
- Workaround for when the received mission has a **null** *forbiddenArea*, despite being required in the agreed mission format. 
- Possibility of live configuration of all the MQTT properties.
- New mission validation for when the received mission has no commands set for AUVs (**invalid mission**).

## [1.0.0-rc2] - 2020-09-01
### Added
- Configuring the retained property for MQTT is now possible through the REST interface.
- The response received from the ISOBUS Converter is stored in a file for further analysis.
- Insecure handler at the REST client for the ISOBUS Converter for their self-signed certificate.
- Copyright notice to every source file.
- License folder and license files.

### Changed
- The REST interface for configuration is now handled by one unique method at the top '/conf/' resource.
- The REST interface for configuration now allows parameters being passed both as query and form params.
- The REST client for ISOBUS Converter now uses an insecure connection for trusting self-signed certificates.
- Removed getters and setters from the Prescription Map classes because somehow, the ISOBUS Converter returns an error when a field from the JSON Schema is included being not required.

### Fixed
- Several minor bugs at the Prescription Map structure.

## [1.0.0-rc1] - 2020-08-24
### Added
- Parser method for ISOBUS plans to prescription maps has been added to the Mission Parser.
- REST Client updated with a method to send the prescription map to the ISOBUS Converter.
- MissionManager updated to parse the prescription map for Tractors and send it to the ISOBUS Converter.

### Changed
- Thrift libraries have been updated to the final version for AFC Y2.
- Updated the vehicle filter in the Mission Manager to the new types in the thrift types for Y2.
- Updated the MissionParser to handle the new long time values in the tasks instead of the old integer ones.
- Updated the CommandStartTypeComparator to handle the new long time values in the tasks instead of the old integer ones.
- SciLog now also logs the maximum, total and free memory at the time of the log.
- Prescription map classes updated to the final version agreed for Y2.

### Fixed
- Bug in the remote configuration for the max vehicles for the SC.
- Minor bug in the generation of the CSV file for the command list when the command list is null.

### Removed
- REST services for testing the prescription map have been removed.

## [1.0.0-0.8.3] - 2020-07-29
### Added
- New property for configuring the maximum number of vehicles for SC tests: sc.max_vehicles
- New REST service for remotely configuring the maximum number of vehicles for SC tests.

### Changed
- SCService now uses the maximum number of vehicles for tests as configured in the properties file.
- SCService now gets the list of registered vehicles from DQ. If the list is empty, then it uses the SCMaxVehicles value as default.
- SCService now returns a 304 status for successive calls on a pending request (it was previously 100).
- Call to REST client to store the mission through the DQ is now run in a separate thread.1

### Fixed
- Minor bug in REST client for DQ service retrieving the list of registered vehicles.

## [1.0.0-0.8.2] - 2020-07-27
### Added
- Calls to REST Data Query services:
  - Storing the full mission converted to JSON as received from the MMT.
  - Requesting the list of available vehicles for using it with the SC service. This call is still pending further tests with the other endpoint to handle the response properly.
- New REST service for accessing to the logs and missions related folders.

## [1.0.0-0-8.1] - 2020-07-27
### Added
- New configuration parameters for accessing to the DataQuery service: dq.server and dq.port.
- New configuration parameters for accessing to the ISOBUS Converter service: isobusconverter.server and isobusconverter.port.
- New REST (PUT) services for remotely configuring the following parameters:
  - REST entry point: MissionManager/conf/topicsMQTT
    - mqtt.topic.mission
    - mqtt.topic.system_configuration
  - REST entry point: MissionManager/conf/DQService
    - dq.server
    - dq.port
  - REST entry point: MissionManager/conf/ISOBUSConverterService
    - isobusconverter.server
    - isobusconverter.port
- New constructors for several ISOBUS classes to ease the generation of a prescription map.
- New REST service for retrieving a sample prescription map (for test purposes).

### Changed
- Logs moved to the log folder.
- Mission related files moved to missions folder.

### Fixed
- REST Thrift Server port configuration.

## [1.0.0-0.7.2] - 2020-07-13
### Changed
- The call for storing the mission as separate CSV files have been moved from MissionManager to MissionManagerThriftServiceHandler, before replacing the NaN values in the mission.
- Refactored methods:
  - MissionParser.removeNaN has been refactored to MissionParser.replaceNaN
  - MissionParser.toCSV has been refactored to MissionParser.exportToCSV
  - JsonB has been configured to not include null values.
  
### Removed
- Cleaned unused imports.

## [1.0.0-0.7.1] - 2020-07-09
### Added
- Vehicle types RAUV included as AUV. This may need further clarification.
- New configuration style:
  - Internal configuration (default values) is read from resources/config.properties as usual.
  - Local configuration can be loaded from a local file named local.properties. The properties set in the local.properties file override the properties from resources/config.properties.
  - Stored configuration is saved and loaded to and from a local file named stored.properties. The properties read from stored.properties override the properties from local.properties and config.properties.
  - If stored.properties does not exist, it is created to store updated properties from the REST interface.

### Removed
- TaskStartTimeComparator is not used, and not expected to be used.

### Fixed
- Minor bug in mission CSV analysis generation: new line per entry
- Minor bug in mission CSV analysis generation: corrected field separator in forbidden areas list

## [1.0.0-0.7.0] - 2020-07-08
### Added
- ISOBUS prescription map data types.
- Command comparator per start time.
- Logs
  - Scientific logs for usual operations.
- Parser
  - Commands are sorted by start time on parsing the vehicle plans.
  - Vehicle plans are now also saved in files in JSON format. The name of the file follows the pattern: AFC-VP-$date-$requestId-$missionId-$vehicleId.json
  - The received mission is parsed and saved as a CSV for each of its fields/attributes:
    - AFC-CSV-$date-$requestId-$missionId-navigationArea.csv
    - AFC-CSV-$date-$requestId-$missionId-forbiddenArea.csv
    - AFC-CSV-$date-$requestId-$missionId-homeLocation.csv
    - AFC-CSV-$date-$requestId-$missionId-tasks.csv
    - AFC-CSV-$date-$requestId-$missionId-vehicles.csv
    - AFC-CSV-$date-$requestId-$missionId-commands.csv

### Changed
- Mission Manager internal data types have been moved to afc.mw.MissionManager.types
- Normal Log handlers now records class, method, thread and exception (if thrown).
- Scientific log handler now records class, method and thread.
. Clean operating logs.

### Removed
- Package for loading stored missions and generating test missions is no longer needed. This functionality has been moved to the MMT MockUp.

### Fixed
- Duplicate entries in the log files has been corrected.