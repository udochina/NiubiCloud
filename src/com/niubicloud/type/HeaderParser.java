package com.niubicloud.type;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.niubicloud.NiubiCloud;
import com.niubicloud.exception.ProtocolException;
import com.niubicloud.exception.UnpredictedException;
import com.niubicloud.service.MainService;
import com.niubicloud.service.MainService.Connection;

public class HeaderParser {
	public static void handleGetQuery(Request req,String query) {
		String items[] = query.split("&");
		req.query.clear();
		
		for(String item : items) {
			String[] temp = item.split("=",2);
			if(temp.length == 0) {
				continue;
			}
			if(temp.length == 1) {
				req.query.put(temp[0], "");
			} else if(temp.length == 2) {
				try {
					req.query.put(temp[0],URLDecoder.decode(temp[1],"utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					throw new UnpredictedException(e);
				}
			}
		}
	}
	
	public static void parse(Connection conn,Request req) throws ProtocolException {
		String[] lines = conn.headerBufffer.toString().split("\r\n");
		int i = 0;
		String str = lines[i++];
		String[] item = str.split(" ");
		
		if(item.length != 3) {
			throw new ProtocolException();
		}
		req.method = item[0];
		req.uri = item[1];
		req.version = item[2];
		
		for(; i < lines.length ;i++) {
			str = lines[i];
			if(str == null)
				continue;
			if(str.contains(": ") == false) {
				throw new ProtocolException();
			}
			item = str.split(": ",2);
			req.headers.put(item[0], item[1]);
		}
	}
	
	public static void parseCookie(Request req) throws ProtocolException {
		String cookie = req.headers.get("Cookie");
		if(cookie == null) {
			return;
		}
		cookie = cookie.trim();
		if(cookie.length() == 0) {
			return;
		}
		String cookies[] = cookie.split(";");
		for(String item : cookies) {
			String temp[] = item.split("=",2);
			if(temp.length != 2) 
				continue;
			try {
				req.cookie.put(temp[0], URLDecoder.decode(temp[1],"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				throw new UnpredictedException(e);
			}
		}
	}
}
