package com.niubicloud.base;

import java.util.Map;

import com.niubicloud.support.VaildateHelper;

public abstract class Vaildate {
	public abstract boolean execute(Map<String,String> values,VaildateHelper helper);
	
	public boolean execute(Map<String,String> value) {
		return this.execute(value, new VaildateHelper(value));
	}
	
	protected boolean $$(boolean... bs) {
		boolean r = false;
		for(boolean b : bs) {
			r = r || b;
		}
		return r;
	}
}
