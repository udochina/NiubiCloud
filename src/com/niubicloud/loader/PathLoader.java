package com.niubicloud.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import com.niubicloud.base.Controller;
import com.niubicloud.fileservice.FileController;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;

public class PathLoader extends BaseLoader {
	public static class UnfoundException extends RuntimeException {
	}

	private FileController controller;

	public PathLoader(String baseDir) {
		this.controller = new FileController(baseDir);
		this.controller.onControllerCreated();
	}

	@Override
	public boolean haveMethod(String name) {
		// TODO Auto-generated method stub
		if("*".equals(name)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean accessMethod(String name, String method) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean call(String name, Request req, Respone result) throws InvocationTargetException {
		// TODO Auto-generated method stub
		try {
			controller.handle(req, result);
		} catch(Throwable e) {
			throw new InvocationTargetException(e);
		}
		return true;
	}

	@Override
	public Controller getController() {
		// TODO Auto-generated method stub
		return controller;
	}
}
