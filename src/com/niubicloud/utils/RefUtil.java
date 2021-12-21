package com.niubicloud.utils;

import java.lang.reflect.Field;

public class RefUtil {
	public static String getStringByField(Field field,Object obj) throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = field.getType();
		if(clazz.equals(int.class)) {
			return String.valueOf(field.getInt(obj));
		} else if(clazz.equals(long.class)) {
			return String.valueOf(field.getLong(obj));
		} else if(clazz.equals(float.class)) {
			return String.valueOf(field.getFloat(obj));
		} else if(clazz.equals(double.class)) {
			return String.valueOf(field.getDouble(obj));
		} else if(clazz.equals(byte.class)) {
			return String.valueOf(field.getByte(obj));
		} else if(clazz.equals(boolean.class)) {
			return String.valueOf(field.getBoolean(obj));
		} else {
			Object val = field.get(obj);
			if(val == null)
				return null;
			if(val instanceof String) {
				return (String)val;
			} else {
				return null;
			}
		}
	}
	
	public static void setFieldByString(Field field,Object obj,String str) throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = field.getType();
		field.setAccessible(true);
		if(clazz.equals(int.class)) {
			field.setInt(obj, Integer.parseInt(str));
		} else if(clazz.equals(long.class)) {
			field.setLong(obj, Long.parseLong(str));
		} else if(clazz.equals(float.class)) {
			field.setFloat(obj, Float.parseFloat(str));
		} else if(clazz.equals(double.class)) {
			field.setDouble(obj, Double.parseDouble(str));
		} else if(clazz.equals(byte.class)) {
			field.setByte(obj, Byte.parseByte(str));
		} else if(clazz.equals(String.class)) {
			if(str == null)
				return;
			if(clazz.equals(String.class)) {
				field.set(obj, str);
			}
		}
	}
}
