package com.niubicloud.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.niubicloud.base.Model;

public class SQLDatabase extends SQLBuilder {
	/* 常用数据库类型 */
	public static final String TYPE_MYSQL = "mysql";
	public static final String TYPE_ORACLE = "oracle";
	public static final String TYPE_SQLSERVER = "sqlserver";
	public static final String TYPE_CLICKHOUSE = "clickhouse";
 	
	// Start
	private SQLTransaction tran;
	private Connection conn;
	
	public SQLDatabase(String str,String user,String password) throws SQLException {
		this.tran = null;
		this.conn = DriverManager.getConnection(str,user,password);
	}
	
	public SQLDatabase(SQLTransaction tran) {
		this.tran = tran;
		this.conn = tran.conn;
	}
	
	private void loadSqlArgument(PreparedStatement statement,Object[] args) throws SQLException {
		int i = 1;
		for(Object obj : args) {
			statement.setObject(i, obj);
			i++;
		}
	}
	
	private Model[] createResultModel(Class<? extends Model> model,ResultSet result) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//Model[] models= new Model[count + 1];
		ArrayList<Model> models = new ArrayList<Model>();
		while(result.next()) {
			Model m = model.getDeclaredConstructor().newInstance();
			models.add(m.readSql(result, result.getMetaData()));
		}
		Model result1[] = new Model[models.size()];
		for(int i = 0;i < models.size();i++) {
			result1[i] = models.get(i);
		}
		return result1;
	}
	
	public Model[] select(Class<? extends Model> model) throws SQLException {
		try { 
			//  statement = conn.createStatement();
			SQLCommand command = this.selectSql();
			PreparedStatement statement = conn.prepareStatement(command.getSqlCommand());
			loadSqlArgument(statement,command.getArgs());
			ResultSet result = statement.executeQuery();
			
			Model[] result1 = createResultModel(model,result);
			
			statement.close();
			result.close();
			
			return result1;
		} catch (SQLException e) {
			throw e;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public int count() throws SQLException {
		try { 
			//  statement = conn.createStatement();
			SQLCommand command = this.countSql();
			PreparedStatement statement = conn.prepareStatement(command.getSqlCommand());
			loadSqlArgument(statement,command.getArgs());
			ResultSet result = statement.executeQuery();
			
			int result1 = -1;
			if(result.next()) {
				result1 = result.getInt(1);
			}
			
			statement.close();
			result.close();
			
			return result1;
		} catch (SQLException e) {
			throw e;
		}
	}
	
	public int insert() throws SQLException {
		try {
			SQLCommand command = this.insertSql();
			PreparedStatement statement = conn.prepareStatement(command.sqlCommand);
			
			loadSqlArgument(statement,command.getArgs());
			
			int result = statement.executeUpdate();
			
			if(this.tran == null) {
				statement.close();
			} else {
				this.tran.handleStatement(statement);
			}
			
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	public int update(Map<String,Object> values) throws SQLException {
		try {
			SQLCommand command = this.updateSql(values);
			PreparedStatement statement = conn.prepareStatement(command.sqlCommand);
			
			loadSqlArgument(statement,command.getArgs());
			
			int result = statement.executeUpdate();
			
			if(this.tran == null) {
				statement.close();
			} else {
				this.tran.handleStatement(statement);
			}
			
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	public int delete() throws SQLException {
		try {
			SQLCommand command = this.deleteSql();
			PreparedStatement statement = conn.prepareStatement(command.sqlCommand);
			
			loadSqlArgument(statement,command.getArgs());
			
			int result = statement.executeUpdate();
			
			if(this.tran == null) {
				statement.close();
			} else {
				this.tran.handleStatement(statement);
			}
			
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	public void close() {
		if(this.tran == null) {
			try {
				this.conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.conn = null;
		this.tran = null;
	}
}
