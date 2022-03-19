package com.niubicloud.database;

public enum SQLFunction {
	uuid,
	now;
	
	public String toSQLString(String dbtype) {
		if(this == uuid) {
			if("oracle".equals(dbtype)) {
				return "sys_guid()";
			} else if("pgsql".equals(dbtype)) {
				return "uuid_generate_v4()";
			} else if("clickhouse".equals(dbtype)) {
				return "generateuidv4()";
			} else {
				// MySQL
				return "uuid()";
			}
		} else if(this == now) {
			return "now()";
		}
		return "";
	}
	
	public SQLRow toSQLRow(String dbtype) {
		return new SQLRow(this.toSQLString(dbtype));
	}
}
