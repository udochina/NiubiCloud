package com.niubicloud.service.type;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ServerOutputStream extends DataOutputStream {
	
	public ServerOutputStream(OutputStream out) {
		super(out);
	}
	
	
	public void writeAuto(Object... objs) throws IOException {
		Object[] val = objs;
		if(val.length == 1 && val[0] instanceof Object[]) {
			val = (Object[]) val[0];
		}
		for(Object obj : val) {
			this.writeBytes((String)String.valueOf(obj));
		}
		this.flush();
	}

	public void writeLine(Object... objs) throws IOException {
		Object[] val = objs;
		if(val.length == 1 && val[0] instanceof Object[]) {
			val = (Object[]) val[0];
		}
		for(Object obj : val) {
			this.writeBytes((String)String.valueOf(obj));
		}
		this.writeBytes("\r\n");
		this.flush();
	}
	
	public void write(Double obj) throws IOException {
		// TODO Auto-generated method stub
		this.writeBytes(String.valueOf(obj));
	}

	public void write(Float obj) throws IOException {
		// TODO Auto-generated method stub
		this.writeFloat(obj);
	}

	public void write(Boolean obj) throws IOException {
		// TODO Auto-generated method stub
		this.writeBytes(((Boolean)obj)?"true":"false");
	}
}
