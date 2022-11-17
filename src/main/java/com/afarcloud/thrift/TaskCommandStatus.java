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

public enum TaskCommandStatus implements org.apache.thrift.TEnum {
  NotAssigned(0),
  NotStarted(1),
  Running(2),
  Finished(3),
  Stopped(4);

  private final int value;

  private TaskCommandStatus(int value) {
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
  public static TaskCommandStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return NotAssigned;
      case 1:
        return NotStarted;
      case 2:
        return Running;
      case 3:
        return Finished;
      case 4:
        return Stopped;
      default:
        return null;
    }
  }
}