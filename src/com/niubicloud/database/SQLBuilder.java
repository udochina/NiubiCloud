package com.niubicloud.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.niubicloud.base.Model;

public class SQLBuilder {
	
	public static class SQLArgumentException extends IllegalArgumentException {
		/**
		 * SQL参数异常
		 */
		private static final long serialVersionUID = 3502055254374354507L;
		
		public SQLArgumentException() {
			super("SQL Error");
		}
		
		public SQLArgumentException(String str) {
			super("SQL Error: " + str);
		}
		
	}
	
	public static class SQLCommand {
		String sqlCommand;
		Object[] args;
		
		public SQLCommand(String sqlCommand, Object[] args) {
			super();
			this.sqlCommand = sqlCommand;
			this.args = args;
		}
		
		public String getSqlCommand() {
			return sqlCommand;
		}
		public Object[] getArgs() {
			return args;
		}

		@Override
		public String toString() {
			return "SQLCommand [sqlCommand=" + sqlCommand + ", args=" + Arrays.toString(args) + "]";
		}
		
	}
	
	String DbType = null;
	String TabName = null;
	ArrayList<String> whereList = new ArrayList<String>();
	ArrayList<String> fieldList = new ArrayList<String>();
	ArrayList<String> updateRow = new ArrayList<String>();
	
	ArrayList<Object> args = new ArrayList<Object>();
	String orderBy = "";
	String groupBy = "";
	String having = "";
	String comment = "";
	
	boolean distinct = false;
	int limitMin = -1,limitMax = -1;
	
	
	public SQLBuilder() {
		this("",SQLDatabase.TYPE_MYSQL);
	}
	
	public SQLBuilder(String tabname) {
		this(tabname,SQLDatabase.TYPE_MYSQL);
	}
	
	public SQLBuilder(String tabname,String dbtype) {
		this.setTabName(tabname);
		this.setDbType(dbtype);
	}
	
	public SQLBuilder setTabName(String str) {
		this.TabName = str;
		return this;
	}
	
	public String getTabName() {
		return TabName;
	}
	
	public SQLBuilder setDbType(String dbtype) {
		this.DbType = dbtype;
		return this;
	}

	public String getDbType() {
		return DbType;
	}

	public SQLBuilder where(Object... args) {
		if(args.length < 2) {
			throw new SQLArgumentException();
		}
		String name = (String) args[0];
		String operation = ((String) args[1]).toLowerCase();
		StringBuilder sb = new StringBuilder(name);
		
		if("=".equals(operation) || ">".equals(operation) || "<".equals(operation) || "<>".equals(operation) || "<=".equals(operation)|| ">=".equals(operation)) {
			if(args.length < 3) {
				throw new SQLArgumentException();
			}
			sb.append(" ");
			sb.append(operation.toUpperCase());
			sb.append(" ");
			if(args[2].getClass() == float.class) {
				sb.append(((Number)args[2]).floatValue());
			} else if(args[2].getClass() == double.class) {
				sb.append(((Number)args[2]).doubleValue());
			} else if(args[2] instanceof Number) {
				sb.append(((Number)args[2]).longValue());
			} else if(args[2] instanceof SQLRow) {
				sb.append(((SQLRow)args[2]).getRowCode());
			} else {
				sb.append("?");
				this.args.add(args[2]);
			}
		} else if("in".equals(operation) || "not in".equals(operation)) {
			if(args.length < 3) {
				throw new SQLArgumentException();
			}
			sb.append(" ");
			sb.append(operation.toUpperCase());
			sb.append(" ");
			sb.append("(");
			
			for(int i = 2;i < args.length;i++) {
				if(i > 2) {
					sb.append(",");
				}
				if(args[i].getClass() == float.class) {
					sb.append(((Number)args[2]).floatValue());
				} else if(args[i].getClass() == double.class) {
					sb.append(((Number)args[2]).doubleValue());
				} else if(Number.class.isInstance(args[2])) {
					sb.append(((Number)args[2]).longValue());
				} else if(args[2] instanceof SQLRow) {
					sb.append(((SQLRow)args[2]).getRowCode());
				} else {
					sb.append("?");
					this.args.add(args[i]);
				}
			}
			sb.append(")");
		} else if(("between".equals(operation)  || "not between".equals(operation))&& args.length == 4) {
			sb.append(" ");
			sb.append(operation.toUpperCase());
			sb.append(" ");
			for(int i = 2;i < args.length;i++) {
				if(i > 2) {
					sb.append(" and ");
				}
				if(args[i].getClass() == float.class) {
					sb.append(((Number)args[2]).floatValue());
				} else if(args[i].getClass() == double.class) {
					sb.append(((Number)args[2]).doubleValue());
				} else if(args[i] instanceof Number) {
					sb.append(((Number)args[2]).longValue());
				} else if(args[2] instanceof SQLRow) {
					sb.append(((SQLRow)args[2]).getRowCode());
				} else {
					sb.append("?");
					this.args.add(args[i]);
				}
			}
		} else if(("like".equals(operation) || "not like".equals(operation)) && args.length == 3) {
			sb.append("?");
			this.args.add(args[2]);
		} else if("is null".equals(operation)) {
			sb.append(" ").append(operation.toUpperCase()).append(" ");
		} else if("not null".equals(operation)) {
			sb.append(" IS ").append(operation.toUpperCase()).append(" ");
		} else {
			throw new SQLArgumentException();
		}
		whereList.add(sb.toString());
		return this;
	}
	
	public SQLBuilder where(SQLRow row) {
		whereList.add(row.getRowCode());
		return this;
	}
	
	public SQLBuilder whereRow(String row) {
		whereList.add(row);
		return this;
	}
	
	public SQLBuilder field(String... args) {
		for(String str : args) {
			fieldList.add(str);
		}
		return this;
	}
	
	public SQLBuilder value(Model model) {
		args.add(model);
		return this;
	}
	
	public SQLBuilder value(Map<String,Object> args) {
		this.args.add(args);
		return this;
	}
	
	public SQLBuilder order(String str) {
		this.orderBy = str;
		return this;
	}
	
	public SQLBuilder groupBy(String str) {
		this.groupBy = str;
		return this;
	}
	
	public SQLBuilder having(String str) {
		this.having = str;
		return this;
	}
	
	public SQLBuilder limit(int max) {
		this.limitMax = max;
		return this;
	}
	
	public SQLBuilder limit(int min,int max) {
		this.limitMin = min;
		this.limitMax = max;
		return this;
	}
	
	public SQLBuilder distinct(boolean val) {
		this.distinct = val;
		return this;
	}
	
	public SQLBuilder comment(String str) {
		this.comment = str;
		return this;
	}
	
	public SQLBuilder updateRow(String str) {
		updateRow.add(str);
		return this;
	}
	
	public SQLBuilder updateRow(SQLRow row) {
		updateRow.add(row.getRowCode());
		return this;
	}
	
	public SQLBuilder updateRow(String name,SQLRow row) {
		updateRow.add(name + " = " + row.getRowCode());
		return this;
	}
	
	public SQLDatabase db() {
		if(this instanceof SQLDatabase) {
			return (SQLDatabase) this;
		}
		return null;
	}
	
	public SQLCommand selectSql() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		sb.append("SELECT ");
		if(this.distinct) {
			sb.append("DISTINCT ");
		}
		
		if(fieldList.size() == 0) {
			sb.append("*");
		} else {
			for(i = 0;i < fieldList.size();i++) {
				if(i > 0) {
					sb.append(",");
				}
				sb.append(fieldList.get(i));
			}
		}
		
		sb.append(" FROM ").append(TabName).append(" ");
		
		if(whereList.size() > 0) {
			sb.append("WHERE ");
			for(i = 0;i < whereList.size();i++) {
				if(i > 0) {
					sb.append(" AND ");
				}
				sb.append(whereList.get(i));
			}
			sb.append(" ");
		}
		
		if(!"".equals(orderBy)) {
			sb.append("ORDER BY ").append(orderBy).append(" ");
		}
		
		if(!"".equals(groupBy)) {
			sb.append("GROUP BY ").append(groupBy).append(" ");
		}
		
		if(!"".equals(having)) {
			sb.append("HAVING ").append(having).append(" ");
		}
		
		if(this.limitMin > -1 && this.limitMax > -1) {
			sb.append("LIMIT ").append(this.limitMin).append(",").append(this.limitMax).append(" ");
		} else if(this.limitMax > -1) {
			sb.append("LIMIT ").append(this.limitMax).append(" ");
		}
		
		if(!"".equals(comment)) {
			sb.append("/* ").append(comment).append(" */ ");
		}
		
		SQLCommand result = new SQLCommand(sb.toString(),args.toArray());
		return result;
	}
	
	public SQLCommand countSql() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		sb.append("SELECT ");
		if(this.distinct) {
			sb.append("DISTINCT ");
		}
		
		sb.append(" count(*) FROM ").append(this.TabName).append(" ");
		
		if(whereList.size() > 0) {
			sb.append(" WHERE ");
			for(i = 0;i < whereList.size();i++) {
				if(i > 0) {
					sb.append(" AND ");
				}
				sb.append(whereList.get(i));
			}
			sb.append(" ");
		}
		
		if(!"".equals(groupBy)) {
			sb.append("GROUP BY ").append(groupBy).append(" ");
		}
		
		if(!"".equals(having)) {
			sb.append("HAVING ").append(having).append(" ");
		}
		
		if(this.limitMin > -1 && this.limitMax > -1) {
			sb.append("LIMIT ").append(this.limitMin).append(",").append(this.limitMax).append(" ");
		} else if(this.limitMax > -1) {
			sb.append("LIMIT ").append(this.limitMax).append(" ");
		}
		
		SQLCommand result = new SQLCommand(sb.toString(),args.toArray());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public SQLCommand insertSql() {
		StringBuilder sb = new StringBuilder();
		
		if(args.size() == 0) {
			throw new SQLArgumentException("without call values(...) arguments. 缺少写入值参数!");
		} 
		
		sb.append("INSERT INTO ").append(TabName).append("  ");
		
		for(Object obj : args) {
			if((obj instanceof Map || obj instanceof Model) == false) {
				throw new SQLArgumentException("error argument for values(...)");
			}
		}
		
		if(fieldList.size() == 0) {
			try {
				Object obj = args.get(0);
				Set<String> keys;
				if(obj instanceof Map) {
					keys = ((Map<String, Object>) obj).keySet();
				} else {
					keys = ((Model)obj).keySet();
				}
				for(Object obj1 : keys) {
					if(obj1 instanceof String) {
						fieldList.add((String)obj1);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				throw new SQLArgumentException("error argument for values(...)");
			}
		}
		
		StringBuilder sb1 = new StringBuilder("VALUES (");
		sb.append("(");
		for(int i = 0;i < fieldList.size();i++) {
			if(i > 0) {
				sb.append(",");
				sb1.append(",");
			}
			sb.append(fieldList.get(i));
			sb1.append("?");
		}
		sb.append(") ");
		sb1.append(") ");
		
		String str1 = sb1.toString();
		ArrayList<Object> args1 = new ArrayList<Object>();
		for(Object val : args) {
			sb.append(str1);
			Map<String,Object> vals = (Map<String, Object>) val;
			for(String field : fieldList) {
				args1.add(vals.get(field));
			}
		}
		
		SQLCommand result = new SQLCommand(sb.toString(),args1.toArray());
		return result;
	}
	
	public SQLCommand updateSql(Map<String,Object> values) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if(whereList.size() == 0) {
			throw new SQLArgumentException("without call where(...) arguments. 缺少写入值参数!");
		}
		
		sb.append("UPDATE ").append(TabName).append(" SET ");
		
		ArrayList<Object> args1 = new ArrayList<Object>();
		
		int item1 = 0;
		if(fieldList.size() == 0) {
			for(Entry<String,Object> entry : values.entrySet()) {
				if(item1 > 0)
					sb.append(" , ");
				sb.append(entry.getKey()).append(" = ").append(" ? ");
				args1.add(entry.getValue());
				item1++;
			}
		} else {
			for(String name : fieldList) {
				if(item1 > 0)
					sb.append(" , ");
				Object val = values.get(name);
				sb.append(name).append(" = ").append(" ? ");
				args1.add(val);
				item1++;
			}
		}
		for(String row : updateRow) {
			if(item1 > 0)
				sb.append(" , ");
			sb.append(" ").append(row).append(" ");
			item1++;
		}
		
		sb.append(" WHERE ");
		for(i = 0;i < whereList.size();i++) {
			if(i > 0) {
				sb.append(" AND ");
			}
			sb.append(whereList.get(i));
		}
		sb.append(" ");
		args1.addAll(args);
		
		return new SQLCommand(sb.toString(),args1.toArray());
	}
	
	public SQLCommand deleteSql() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		sb.append("DELETE FROM ").append(TabName);
		
		if(whereList.size() > 0) {
			sb.append(" WHERE ");
			for(i = 0;i < whereList.size();i++) {
				if(i > 0) {
					sb.append(" AND ");
				}
				sb.append(whereList.get(i));
			}
			sb.append(" ");
		}
		return new SQLCommand(sb.toString(),args.toArray());
	}
}
