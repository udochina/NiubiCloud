package com.niubicloud.database;

public class SQLRow {
	String str;
	
	public SQLRow(String rowCode) {
		this.str = rowCode;
	}
	
	public String getRowCode() {
		return str;
	}
}
