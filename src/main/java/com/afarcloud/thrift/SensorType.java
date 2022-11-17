/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.afarcloud.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum SensorType implements org.apache.thrift.TEnum {
  algorithm(0),
  air_sensor(1),
  airNTPActuator(2),
  collar(3),
  environmental(4),
  flow_meter(5),
  gas(6),
  gps_tracker(7),
  grass_sensor(8),
  ISOBUSgw(9),
  silage(10),
  soil(11),
  tractor(12),
  weatherStation(13);

  private final int value;

  private SensorType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static SensorType findByValue(int value) { 
    switch (value) {
      case 0:
        return algorithm;
      case 1:
        return air_sensor;
      case 2:
        return airNTPActuator;
      case 3:
        return collar;
      case 4:
        return environmental;
      case 5:
        return flow_meter;
      case 6:
        return gas;
      case 7:
        return gps_tracker;
      case 8:
        return grass_sensor;
      case 9:
        return ISOBUSgw;
      case 10:
        return silage;
      case 11:
        return soil;
      case 12:
        return tractor;
      case 13:
        return weatherStation;
      default:
        return null;
    }
  }
}