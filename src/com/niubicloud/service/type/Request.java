package com.niubicloud.service.type;

import java.util.Map;

import com.niubicloud.service.loader.BaseLoader;

public class Request {	
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_HEAD ="HEAD";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String VERSION_1_0 = "HTTP/1.0";
	public static final String VERSION_1_1 = "HTTP/1.1";
	
	public String method = METHOD_GET;
	public String uri = "";
	public String domain = "";
	public String path = "";
	public String fileName = "";
	public String version = VERSION_1_1;
	public StringTable headers = new StringTable();
	StringTable query = new StringTable();
	StringTable cookie = new StringTable();
	public PostReader post = null;
	
	public BaseLoader controller = null;
	public String handleMethodName = null;
	
	public String get(String name) {
		return query.get(name);
	}

	public Map<String, String> get() {
		// TODO Auto-generated method stub
		return this.query;
	}
	
	public String getcookie(String name) {
		// TODO Auto-generated method stub
		return this.cookie.get(name);
	}

	public Map<String, String> getcookie() {
		// TODO Auto-generated method stub
		return this.cookie;
	}

	public String header(String string) {
		// TODO Auto-generated method stub
		return headers.get(string);
	}
	
	public boolean isAjax() {
		String str =  headers.get("X-Requested-With");
		if(str == null)
			return false;
		return "XMLHttpRequest".equals(str.trim());
	}
	
	public boolean isPjax() {
		String str =  headers.get("X-Pjax");
		if(str == null)
			return false;
		return "true".equals(str.trim());
	}
}
