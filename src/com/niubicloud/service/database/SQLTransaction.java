package com.niubicloud.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLTransaction {
	
	public SQLTransaction start(String str,String user,String password) throws SQLException {
		return new SQLTransaction(str, user, password);
	}
	
	Connection conn;
	ArrayList<Statement> stats = new ArrayList<Statement>();
	
	public SQLTransaction(String str,String user,String password) throws SQLException {
		// TODO Auto-generated constructor stub
		this.conn = DriverManager.getConnection(str,user,password);
		conn.setAutoCommit(false);
	}
	
	void handleStatement(Statement obj) {
		stats.add(obj);
	}
	
	public void close() {
		try {
			for(Statement stat : stats) {
				if(stat != null) {
					stats.remove(stat);
					stat.close();
				}
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void commit() throws SQLException {
		conn.commit();
	}
	
	public void rollback(){
		try {
			conn.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
