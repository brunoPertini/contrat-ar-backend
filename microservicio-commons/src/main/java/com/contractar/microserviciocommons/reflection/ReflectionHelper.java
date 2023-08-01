package com.contractar.microserviciocommons.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public final class ReflectionHelper {

	
	/** Takes the no null fields from sourceObject, searches those fields setters on destinationObject and applies each one,
	 * passing the values found from sourceObject. For the reflection to proper work, objects full classes names are 
	 * mandatory.
	 * @param sourceObject
	 * @param destinationObject
	 * @param sourceClass
	 * @param destinationClass
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void applySetterFromExistingFields(Object sourceObject, Object destinationObject, String sourceClass,
			String destinationClass)
			throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> sourceClassType = Class.forName(sourceClass);
		Class<?> destinationClassType = Class.forName(destinationClass);

		LinkedHashMap<String, Object> notEmptyFields = new LinkedHashMap<String, Object>();
		Field[] fields = sourceClassType.getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldValue = field.get(sourceObject);
			if ((fieldValue instanceof Number && (((Number) fieldValue).intValue() > 0)) || fieldValue != null) {
				notEmptyFields.put(field.getName(), fieldValue);
			}
		}

		Method[] methods = destinationClassType.getMethods();

		for (String fieldName : notEmptyFields.keySet()) {
			String sufix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			String setterExpectedName = "set" + sufix;

			Method foundSetterMethod = null;

			while (foundSetterMethod == null) {
				for (Method method : methods) {
					if (method.getName().equals(setterExpectedName)) {
						foundSetterMethod = method;
					}
				}
			}

			foundSetterMethod.invoke(destinationObject, notEmptyFields.get(fieldName));
		}
	}
}
