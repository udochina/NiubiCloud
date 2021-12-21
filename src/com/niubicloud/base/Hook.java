package com.niubicloud.base;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Hook {
	public class DEAFULT {
		public static final String HOOK_ERROR = "error";
		public static final String HOOK_NOFOUND = "notfound";
	}
	
	public abstract boolean execute(Controller controller,String name,Object args[]);
	
	private static final HashMap<String,ArrayList<Hook>> hooks = new HashMap<String,ArrayList<Hook>>();
	
	public static void add(String eventName,Hook handler) {
		ArrayList<Hook> value = hooks.get(eventName);
		if(value == null) {
			value = new ArrayList<Hook>();
			value.add(handler);
			hooks.put(eventName, value);
		} else {
			value.add(handler);
		}
	}
	
	public static boolean trigger(String eventName,Controller controller,Object... objects) {
		ArrayList<Hook> value = hooks.get(eventName);
		if(value == null) {
			return false;
		}
		for(Hook item : value) {
			if(item == null)
				continue;
			if(item.execute(controller, eventName, objects) == true) {
				return true;
			}
		}
		return false;
	}
}
