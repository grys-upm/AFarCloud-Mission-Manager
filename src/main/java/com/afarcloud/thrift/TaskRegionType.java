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

public enum TaskRegionType implements org.apache.thrift.TEnum {
  Point(0),
  Column(1),
  Area(2),
  Circle(3),
  Dynamic(4);

  private final int value;

  private TaskRegionType(int value) {
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
  public static TaskRegionType findByValue(int value) { 
    switch (value) {
      case 0:
        return Point;
      case 1:
        return Column;
      case 2:
        return Area;
      case 3:
        return Circle;
      case 4:
        return Dynamic;
      default:
        return null;
    }
  }
}
