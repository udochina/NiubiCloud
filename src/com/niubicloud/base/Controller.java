package com.niubicloud.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import com.niubicloud.base.anno.RuntimeController;
import com.niubicloud.service.exception.FinishRequest;
import com.niubicloud.service.HandleThread;
import com.niubicloud.service.type.ResponeBuffer;

public class Controller {
	// private Object object = new Object();
	private RuntimeController rc = null;
	
	public Controller() {
		try {
			this.rc = this.getClass().getDeclaredAnnotation(RuntimeController.class);
		} catch(Exception e) {
		}
	}
	
	public RuntimeController anno() {
		return this.rc;
	}
	
	public void onControllerCreated() {
	}
	
	protected String header(String name) {
		return HandleThread.currentRequest().header(name);
	}
	
	protected void header(String name,String value) {
		HandleThread.currentRespone().header(name, value);
	}
	
	protected ResponeBuffer buffer() {
		return HandleThread.currentRespone().buffer();
	}
	
	protected void finish() {
		HandleThread.currentRespone().finishAuto();
	}
	
	protected Map<String,String> get(){
		return HandleThread.currentRequest().get();
	}
	
	protected String get(String name) {
		return HandleThread.currentRequest().get(name);
	}
	
	protected Model get(Model model) {
		model.putAll(get());
		return model;
	}
	
	protected String method() {
		return HandleThread.currentRequest().method;
	}
	
	protected String getHeader(String name) {
		return HandleThread.currentRequest().header(name);
	}
	
	protected String getUserAgent() {
		return HandleThread.currentRequest().header("User-Agent");
	}
	
	protected Model get(Class<? extends Model> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Model model = clazz.getDeclaredConstructor().newInstance();
		model.putAll(get());
		return model;
	}
	
	protected Map<String,String> getcookie() {
		return HandleThread.currentRequest().getcookie();
	}
	
	protected String getcookie(String name) {
		return HandleThread.currentRequest().getcookie(name);
	}
	
	protected void quit() {
		throw new FinishRequest();
	}
}
