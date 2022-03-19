package com.niubicloud.service.type;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.niubicloud.service.exception.UnpredictedException;

public class ResponeBuffer{
	Respone resp;
	ByteArrayOutputStream bos;
	//ServerOutputStream out;
	BufferedWriter bw;
	
	public ResponeBuffer(Respone resp) {
		super();
		this.resp = resp;
		this.bos = new ByteArrayOutputStream();
	}
	
	public ResponeBuffer write(Object... args) {
		StringBuilder sb = new StringBuilder();
		for(Object obj : args) {
			sb.append(obj.toString());
		}
		try {
			bos.write(sb.toString().getBytes("utf-8"));
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		}
		return this;
	}

	public ResponeBuffer writeln(Object... args) {
		StringBuilder sb = new StringBuilder();
		for(Object obj : args) {
			sb.append(obj.toString());
		}
		sb.append("\n");
		try {
			bos.write(sb.toString().getBytes("utf-8"));
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		}
		return this;
	}
	
	public ResponeBuffer writeIf(boolean val,Object... args){
		if(val == false) {
			return this;
		}
		StringBuilder sb = new StringBuilder();
		for(Object obj : args) {
			sb.append(obj.toString());
		}
		try {
			bos.write(sb.toString().getBytes("utf-8"));
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		}
		return this;
	}
	
	public int length() {
		return bos.size();
	}
	
	public ResponeBuffer reset() {
		bos.reset();
		return this;
	}
	
	public void finish(){
		try {
			bos.flush();
			resp.finish(new ByteArrayInputStream(bos.toByteArray()), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		}
	}
}
