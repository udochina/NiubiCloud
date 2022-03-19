package com.niubicloud.service.support;

import java.util.Map;
import java.util.regex.Pattern;

public class ValidateHelper {
	Map<String,String> param;
	
	public ValidateHelper(Map<String,String> param) {
		this.param = param;
	}
	
	public boolean require(String... keys) {
		for(String name : keys) {
			if(this.param.containsKey(name) == false) {
				return true;
			}
		}
		return false;
	}
	
	public boolean string(String name,int min,int max) {
		return string(name,min,max,"");
	}
	
	public boolean string(String name,int min,int max,String keywords) {
		String val = this.param.get(name);
		if(val == null)
			return true;
		if(val.length() > max || val.length() < min) {
			return true;
		}
		for(String keyword : keywords.split(",")) {
			if(val.contains(keywords)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean number(String name,int min,int max) {
		int i;
		try {
			i= Integer.parseInt(this.param.get(name));
		} catch(NumberFormatException e) {
			return true;
		}
		if(i < min || i > max) {
			return true;
		}
		return false;
	}
	
	public boolean decide(String name,double min,double max) {
		double i;
		try {
			i= Double.parseDouble(this.param.get(name));
		} catch(NumberFormatException e) {
			return true;
		}
		if(i < min || i > max) {
			return true;
		}
		return false;
	}
	
	public boolean keyword(String... strings) {
		for(String val : param.values()) {
			for(String keyword : strings) {
				if(val.contains(keyword)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean regex(String regex,String... args) {
		boolean ret = false;
		Pattern p = Pattern.compile(regex);
		
		for(String str : args) {
			ret = ret || p.matcher(str).matches();
		}
		return ret;
	}
	
	public boolean email(String args) {
		return regex("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*",args);
	}
}
