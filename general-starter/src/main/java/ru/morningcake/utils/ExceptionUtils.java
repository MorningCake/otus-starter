package ru.morningcake.utils;

import lombok.experimental.UtilityClass;
import ru.morningcake.exception.data.EntityAlreadyExist;
import ru.morningcake.exception.data.EntityNotFoundException;

import java.util.UUID;

@UtilityClass
public class ExceptionUtils {

  public EntityNotFoundException getEntityNotFoundException(String className, UUID id) {
    return getEntityNotFoundException(className, id.toString());
  }
  public EntityNotFoundException getEntityNotFoundException(String className, String id) {
    return new EntityNotFoundException(className + " " + id + " is not found!", className);
  }

  public EntityAlreadyExist getEntityAlreadyExist(String className, UUID id) {
    return getEntityAlreadyExist(className, id.toString());
  }
  public EntityAlreadyExist getEntityAlreadyExist(String className, String id) {
    return new EntityAlreadyExist(className + " with " + id + " already exists!");
  }
  public EntityAlreadyExist getEntityAlreadyExist(String className, String attribute, String value) {
    return new EntityAlreadyExist(className + " with " + attribute + "=" + value + " already exists!");
  }


}
