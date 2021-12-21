package com.niubicloud.utils;

public class StringUtil {

	public static boolean isEmpty(String string) {
		// TODO Auto-generated method stub
		if(string == null)
			return true;
		if(string.length() == 0)
			return true;
		return false;
	}

}
