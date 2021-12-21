package com.niubicloud.type;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.niubicloud.exception.UnpredictedException;
import com.niubicloud.utils.DateUtil;

public class Respone {
	public static HashMap<String,String> StatusCode;
	
	static {
		StatusCode = new HashMap<String,String>();
		StatusCode.put("100", "Continue");
		StatusCode.put("101", "Switching Protocols");
		StatusCode.put("200", "OK");
		StatusCode.put("201", "Created");
		StatusCode.put("202", "Accepted");
		StatusCode.put("203", "Non-Authoritative Information");
		StatusCode.put("204", "No Content");
		StatusCode.put("304", "Not Modified");
		StatusCode.put("400", "Bad Request");
		StatusCode.put("403", "Forbidden");
		StatusCode.put("404", "Not Found");
		StatusCode.put("405", "Method Not Allowed");
		StatusCode.put("500", "	Internal Server Error");
		StatusCode.put("505", "HTTP Version not supported");
	}
	
	public static String getStatusCode(int code) {
		return StatusCode.get(String.valueOf(code));
	}
	
	public int code = 200;
	public String version = Request.VERSION_1_1;
	
	public StringTable headers = new StringTable();
	public StringTable cookies = null;
	public Request baseReq = null;
	public boolean noSendResult = false;
	
	public ServerOutputStream out;
	private boolean isSendHeader = false;
	private boolean isSendData = false;
	ResponeBuffer buffer;
	
	public Respone(OutputStream out) {
		this.out = new ServerOutputStream(out);
	}
	
	public void flushHeader(){
		try {
			if(isSendHeader)
				return;
			out.writeLine(version, " ", code, " ", getStatusCode(code));
			for(Entry<String,String> entry : headers.entrySet()) {
				out.writeLine(entry.getKey(),": ",entry.getValue());
			}
			if(cookies != null)
				for(String cookie : cookies.values()) {
				out.writeLine("Set-Cookie: ",cookie);
			}
			out.writeLine();
			out.flush();
			
			isSendHeader = true;
		} catch(IOException e) {
			throw new UnpredictedException(e);
		}
	}
	
	public ResponeBuffer buffer() {
		if(this.buffer == null) {
			this.buffer = new ResponeBuffer(this);
		}
		return this.buffer;
	}
	
	public void finish(InputStream is,boolean sendLength){
		try {
			if(sendLength && isSendHeader == false) {
				headers.put("Content-Length", String.valueOf(is.available()));
			}
			flushHeader();
			if(noSendResult) {
				isSendData = true;
				return;
			}
			
			byte[] buff = new byte[1024];
			int i = 0;
			
			for(;(i = is.read(buff, 0, buff.length)) != -1;) {
				out.write(buff, 0, i);
				out.flush();
			}
			out.flush();
			
			
			isSendData = true;
		} catch(IOException e) {
			throw new UnpredictedException(e);
		}
	}
	
	public void finish(String path){
		try {
			finish(new FileInputStream(path),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new UnpredictedException(e);
		}
	}
	
	public void finish() {
		if(isSendHeader == false) {
			headers.put("Content-Length", "0");
		}
		flushHeader();
		
		isSendData = true;
	}
	
	public void finishAuto(){
		if(isSendData == true) {
			return;
		}
		if(buffer != null) {
			buffer.finish();
		} else {
			finish();
		}
	}
	
	public void header(String name,String value) {
		headers.put(name, value);
	}
	
	public void setcookie(String name,String value,String domain,String path,int expireOfSecond,boolean httpOnly,boolean secure) {
		if(cookies == null) {
			cookies = new StringTable();
		}
		StringBuilder sb = new StringBuilder().append(name).append("=").append(value);
		if(path != null) {
			if(path.length() > 0) {
				sb.append("; path=").append(path);
			}
		}
		if(domain != null) {
			if(domain.length() > 0) {
				sb.append("; domain=").append(domain);
			}
		}
		if(expireOfSecond > 0) {
			sb.append("; expire=");
			sb.append(DateUtil.getGMTOffsetTime(expireOfSecond));
		}
		if(httpOnly) {
			sb.append("; httponly");
		}
		if(secure) {
			sb.append("; secure");
		}
		this.cookies.put(path, sb.toString());
	}
	public void setcookie(String name,String value,String domain,String path,int expireOfSecond) {
		setcookie(name,value,domain,path,expireOfSecond,false,false);
	}
	
	public void setcookie(String name,String value,String domain,String path) {
		setcookie(name,value,domain,path,0,false,false);
	}
	
	public void setcookie(String name,String value,String domain) {
		setcookie(name,value,domain,null,0,false,false);
	}
	
	public void setcookie2(String name,String value,String path) {
		setcookie(name,value,null,path,0,false,false);
	}
	
	public void setcookie(String name,String value) {
		setcookie(name,value,null,null,0,false,false);
	}
	
	public void session_start() {
		
	}
}
