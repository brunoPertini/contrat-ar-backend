package com.contractar.microserviciocommons.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionHelper {

	/**
	 * Takes the no null fields from sourceObject, searches those fields setters on
	 * destinationObject and applies each one, passing the values found from
	 * sourceObject. For the reflection to proper work, objects full classes names
	 * are mandatory.
	 * 
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
		Field[] fields = getAllFieldsOfHierachy(sourceClassType);

		for (Field field : fields) {
			field.setAccessible(true);
			Object fieldValue = field.get(sourceObject);
			boolean isNumberField = fieldValue instanceof Number;
			if ((isNumberField && (((Number) fieldValue).intValue() > 0)) || (!isNumberField && fieldValue != null)) {
				notEmptyFields.put(field.getName(), fieldValue);
			}
		}

		Method[] methods = destinationClassType.getMethods();

		for (String fieldName : notEmptyFields.keySet()) {
			String sufix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			String setterExpectedName = "set" + sufix;

			Method foundSetterMethod = null;

			boolean finishedLooping = false;

			while (foundSetterMethod == null && !finishedLooping) {
				for (Method method : methods) {
					if (method.getName().equals(setterExpectedName)) {
						foundSetterMethod = method;
					}
				}

				finishedLooping = true;

			}

			if (foundSetterMethod != null) {
				foundSetterMethod.invoke(destinationObject, notEmptyFields.get(fieldName));
			}
		}
	}

	public static Field[] getAllFieldsOfHierachy(Class clazz) {
		List<Field> fields = new ArrayList<Field>();
		Class currentClass = clazz;
		while (!currentClass.getSimpleName().equals("Object")) {
			Field[] currentClassFields = currentClass.getDeclaredFields();
			for (Field field : currentClassFields) {
				fields.add(field);
			}

			currentClass = currentClass.getSuperclass();
		}

		return fields.toArray(new Field[fields.size()]);
	}

	public static Map<String, Object> getObjectFields(Object obj) throws IllegalAccessException {
		Map<String, Object> map = new HashMap<>();
		Class<?> clazz = obj.getClass();
		while (!clazz.getSimpleName().equals("Object")) {
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(obj);
				if (value != null) {
					map.put(field.getName(), value);
				}
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return map;
	}
	
	/**
	 * 
	 * @param o
	 * @return The object's class full name, i.e, including it's package structure
	 */
	public static String getObjectClassFullName(Object o) {
		String classSimpleName = o.getClass().getSimpleName();
		
		String classPackage = o.getClass().getPackage().getName();
		
		return classPackage + "." + classSimpleName;
	}
}
