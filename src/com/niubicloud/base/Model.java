package com.niubicloud.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.niubicloud.NiubiCloud;
import com.niubicloud.base.anno.ModelMethod;
import com.niubicloud.service.exception.ModelTypeException;
import com.niubicloud.service.type.CacheFieldTable;
import com.niubicloud.service.type.ValueTable;

public class Model extends HashMap<String,Object> {
	private static HashMap<Class<? extends Model>,CacheFieldTable> clazzTable = new HashMap<Class<? extends Model>,CacheFieldTable>();
	
	private CacheFieldTable cacheField = null;
	
	public Model() {
		super();
		init();
	}
	
	private void init() {
		Class<? extends Model> clazz = this.getClass();
		cacheField = clazzTable.get(clazz);
		if(cacheField != null) {
			return;
		}
		String names[] = null;
		cacheField = new CacheFieldTable();
		
		for(Class<? extends Model> clazz1 = clazz; clazz1.equals(Model.class) == false ;clazz1 = (Class<? extends Model>) clazz1.getSuperclass()) {
			try {
				Field field = clazz1.getDeclaredField("ModelFields");
				names = (String[])field.get(null);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				continue;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(String name : names) {
				String fieldName = name;
				String varName = name;
				
				if(name.contains("->")) {
					String[] result = name.split("->");
					fieldName = result[0].trim();
					varName = result[1].trim();
				}
				
				try {
					cacheField.put(varName, clazz1.getDeclaredField(fieldName));
				} catch (NoSuchFieldException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		clazzTable.put(clazz, cacheField);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return cacheField.size() + super.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return cacheField.isEmpty() && super.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return cacheField.containsKey(key) || super.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return cacheField.containsValue(value) || super.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		// TODO Auto-generated method stub
		if(cacheField.containsKey(key)) {
			Field field = cacheField.get(key);
			try {
				return field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		return super.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		// TODO Auto-generated method stub
		if(cacheField.containsKey(key)) {
			Field field = cacheField.get(key);
			try {
				field.setAccessible(true);
				if(value != null) {
					if(value instanceof Number) {
						if(field.getType() == int.class) {
							field.setInt(this, ((Number)value).intValue());
							return value;
						} else if(field.getType() == long.class) {
							field.setLong(this, ((Number)value).longValue());
							return value;
						} else if(field.getType() == short.class) {
							field.setShort(this, ((Number)value).shortValue());
							return value;
						} else if(field.getType() == byte.class) {
							field.setByte(this, ((Number)value).byteValue());
							return value;
						} else if(field.getType() == float.class) {
							field.setFloat(this, ((Number)value).floatValue());
							return value;
						} else if(field.getType() == double.class) {
							field.setDouble(this, ((Number)value).doubleValue());
							return value;
						}
					}
					if(field.getType() == boolean.class && field.getType() == boolean.class) {
						field.setBoolean(this,((Boolean)value).booleanValue());
					}
				}
				field.set(this, value);
				
				return value;
			} catch (IllegalArgumentException | IllegalAccessException | ClassCastException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if(NiubiCloud.DEBUG) {
					e.printStackTrace();
					throw new ModelTypeException(this.getClass().getName(),field.getType().getName(),key,value.getClass().getName());
				}
				return null;
			}
		}
		return super.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return super.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO Auto-generated method stub
		for(Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		TreeSet<String> result = new TreeSet<String>();
		result.addAll(cacheField.keySet());
		result.addAll(super.keySet());
		return result;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		TreeSet<Object> result = new TreeSet<Object>();
		for(Field field : cacheField.values()) {
			try {
				result.add(field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
			}
		}
		result.addAll(super.values());
		return result;
	}
	
	public class CustomEntry implements Entry<String, Object> {
		String name;
		Field field;
		
		public CustomEntry(String name,Field field) {
			this.name = name;
			this.field = field;
		}

		@Override
		public String getKey() {
			// TODO Auto-generated method stub
			return name;
		}

		@Override
		public Object getValue() {
			// TODO Auto-generated method stub
			try {
				return field.get(Model.this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
			}
			return null;
		}

		@Override
		public Object setValue(Object value) {
			// TODO Auto-generated method stub
			try {
				field.setAccessible(true);
				field.set(field, value);
				return value;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
			}
			return null;
		}
		
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		LinkedHashSet<Entry<String, Object>> result = new LinkedHashSet<Entry<String, Object>>();
		Set<Entry<String, Object>> result1 = super.entrySet();
		
		for(Entry<String,Object> entry : result1) {
			if(cacheField.containsKey(entry.getKey()) == false) {
				result.add(entry);
			}
		}
		for(String name : cacheField.keySet()) {
			result.add(new CustomEntry(name,cacheField.get(name)));
		}
		
		return result;
	}
	
	public ValueTable toValues() {
		ValueTable result = new ValueTable();
		for(Entry<String,Field> entry : cacheField.entrySet()) {
			String name = entry.getKey();
			Field field = entry.getValue();
			
			if(field == null) {
				continue;
			}
			
			Class<?> clazz = field.getType();
			try {
				if(clazz.equals(int.class)) {
					result.put(name, field.getInt(this));
				} else if(clazz.equals(long.class)) {
					result.put(name, field.getLong(this));
				} else if(clazz.equals(float.class)) {
					result.put(name, field.getFloat(this));
				} else if(clazz.equals(double.class)) {
					result.put(name, field.getDouble(this));
				} else if(clazz.equals(byte.class)) {
					result.put(name, field.getByte(this));
				} else if(clazz.equals(boolean.class)) {
					result.put(name, field.getBoolean(this));
				} else {
					Object val = field.get(this);
					if(val == null) {
						result.put(name, null);
					}
					if(val instanceof String) {
						result.put(name, (String) val);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(Entry<String, Object> entry : this.entrySet()) {
			result.putOrNotFound(entry.getKey(), entry.getValue());
		}
		
		return result;
	}
	
	public Model readSql(ResultSet rs,ResultSetMetaData rsmd) throws SQLException {
		int count = rsmd.getColumnCount();
		
		for(int i = 0; i < count ;i++) {
			String name = rsmd.getColumnName(i + 1);
			Object obj = rs.getObject(name);
			this.put(name, obj);
		}
		return this;
	}
	
	public void execute() {
		Method[] methods = this.getClass().getDeclaredMethods();
		for(Method mod : methods) {
			ModelMethod mm = mod.getAnnotation(ModelMethod.class);
			if(mm == null) {
				continue;
			}
			try {
				mod.invoke(this);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				if(NiubiCloud.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
}
