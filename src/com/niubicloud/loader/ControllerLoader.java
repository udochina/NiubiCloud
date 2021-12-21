package com.niubicloud.loader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.niubicloud.anno.ControllerMethod;
import com.niubicloud.anno.RuntimeController;
import com.niubicloud.base.Controller;
import com.niubicloud.exception.FinishRequest;
import com.niubicloud.exception.UnpredictedException;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;
import com.niubicloud.type.StringTable;

public class ControllerLoader extends BaseLoader {
	public Class<? extends Controller> mClazz;
	public Controller controller = null;
	public String name = null;
	public HashMap<String,Method> mMethods = new HashMap<String,Method>();
	public HashMap<String,ControllerMethod> cMethods = new HashMap<String,ControllerMethod>();
	
	public ControllerLoader(Class<? extends Controller> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.mClazz = clazz;
		init();
	}
	
	@SuppressWarnings("unchecked")
	public ControllerLoader(String path) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this((Class<? extends Controller>) Class.forName(path));
	}
	
	private void init() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.controller = createController();
		RuntimeController anno = mClazz.getAnnotation(RuntimeController.class);
		if(anno == null)
			return;
		this.name = anno.name();
		
		Method[] methods = mClazz.getMethods();
		for(Method method : methods) {
			ControllerMethod cMethod = method.getAnnotation(ControllerMethod.class);
			if(cMethod == null) {
				continue;
			}
			String name = cMethod.name();
			if(name == null) {
				name = method.getName();
			}
			if("".equals(name)) {
				name = method.getName();
			}
			mMethods.put(name, method);
			cMethods.put(name, cMethod);
		}
		
		controller.onControllerCreated();
	}
	
	private Controller createController() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {	
		return mClazz.getDeclaredConstructor().newInstance();
	}
	
	public boolean haveMethod(String name) {
		return mMethods.containsKey(name);
	}
	
	public boolean accessMethod(String name,String method) {
		ControllerMethod mod = cMethods.get(name);
		if(mod == null) {
			return false;
		}
		if("GET".equals(method)) {
			return mod.GET();
		} else if("POST".equals(method)) {
			return mod.POST();
		} else if("POST".equals(method)) {
			return mod.POST();
		} else if("HEAD".equals(method)) {
			return mod.HEAD();
		} else if("PUT".equals(method)) {
			return mod.PUT();
		} else if("DELETE".equals(method)) {
			return mod.DELETE();
		}
		return false;
	}
	
	public boolean call(String name,Request req,Respone result) throws InvocationTargetException {
		ControllerMethod mod = cMethods.get(name);
		Method method = mMethods.get(name);
		RuntimeController rc = this.controller.anno();
		
		if(rc != null) {
			StringTable.parseFromEasyText(result.headers, rc.headers());
		}
		StringTable.parseFromEasyText(result.headers, mod.headers());
		result.header("Content-Type", mod.contentType());
		
		if(req.method.equals("HEAD")) {
			result.noSendResult = true;
		}
		
		try {
			method.invoke(this.controller, req, result);
			return true;
		} catch (IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		} catch(InvocationTargetException e) {
			throw e;
		}
		// return false;
	}

	@Override
	public Controller getController() {
		// TODO Auto-generated method stub
		return this.controller;
	}
}
