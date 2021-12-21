package com.niubicloud.exception;

@SuppressWarnings("serial")
public class ModelTypeException extends RuntimeException {
	public ModelTypeException(String classname,String typeName,String name,String valueType) {
		super("In " + classname + ": '" + typeName +" " + name + "' but value type is " + valueType);
	}
}
