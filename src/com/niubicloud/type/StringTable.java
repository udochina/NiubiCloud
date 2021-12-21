package com.niubicloud.type;

import java.util.HashMap;

public class StringTable extends HashMap<String,String> {
	/**
	 * 	SerialID
	 */
	private static final long serialVersionUID = -873282873151183126L;

	public static StringTable parseFromEasyText(StringTable result,String text) {
		String strs[] = text.split(";");
		
		if(result == null || text == null) {
			return result;
		}
		if(text.length() == 0) {
			return result;
		}
		
		for(String str1 : strs) {
			String items[] = str1.split("=",2);
			if(items.length == 0) {
				continue;
			}
			if(items.length == 1) {
				result.put(items[0], "");
			} else {
				result.put(items[0], items[1]);
			}
		}
		return result;
	}
}
