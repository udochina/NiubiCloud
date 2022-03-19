package com.niubicloud.base;

import java.util.Map;

import com.niubicloud.service.support.ValidateHelper;

public abstract class Validate {
	public abstract boolean execute(Map<String,String> values, ValidateHelper helper);
	
	public boolean execute(Map<String,String> value) {
		return this.execute(value, new ValidateHelper(value));
	}
	
	protected boolean $$(boolean... bs) {
		boolean r = false;
		for(boolean b : bs) {
			r = r || b;
		}
		return r;
	}

}
