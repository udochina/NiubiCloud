package com.niubicloud.loader;

import java.lang.reflect.InvocationTargetException;

import com.niubicloud.base.Controller;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;

public abstract class BaseLoader {
	public abstract boolean haveMethod(String name);
	public abstract boolean accessMethod(String name,String method);
	public abstract boolean call(String name,Request req,Respone result) throws InvocationTargetException;
	public abstract Controller getController();
}
