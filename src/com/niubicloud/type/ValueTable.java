package com.niubicloud.type;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.niubicloud.base.Model;

public class ValueTable {
	public static abstract class Value {
		public Class<?> getTypeClass() {
			if(this instanceof IntValue) {
				return int.class;
			} else if(this instanceof BoolValue) {
				return boolean.class;
			} else if(this instanceof FloatValue) {
				return float.class;
			} else if(this instanceof DoubleValue) {
				return double.class;
			} else if(this instanceof ByteValue) {
				return byte.class;
			} else if(this instanceof StringValue) {
				return String.class;
			}
			return void.class;
		}
		
		public String toString() {
			return "void";
		}
		
		public Object getObj() {
			return null;
		}
		
		public Value copy() {
			return null;
		}
	}
	
	public static class IntValue extends Value {
		public int value = 0;
		
		public Object getObj() {
			return (Object) value;
		}
		
		public String toString() {
			return "int(" + String.valueOf(value) + ")";
		}
		
		public Value copy() {
			IntValue result = new IntValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class BoolValue extends Value {
		public boolean value = false;
		
		public String toString() {
			return "bool(" + String.valueOf(value) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			BoolValue result = new BoolValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class LongValue extends Value {
		public long value = 0L;
		
		public String toString() {
			return "long(" + String.valueOf(value) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			LongValue result = new LongValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class ByteValue extends Value {
		public byte value = 0;
		
		public String toString() {
			return "byte(" + String.valueOf(value) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			ByteValue result = new ByteValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class FloatValue extends Value {
		public float value = 0f;
		
		public String toString() {
			return "float(" + String.valueOf(value) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			FloatValue result = new FloatValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class DoubleValue extends Value {
		public Double value = 0d;
		
		public String toString() {
			return "double(" + String.valueOf(value) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			DoubleValue result = new DoubleValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class StringValue extends Value {
		public String value = null;
		
		public String toString() {
			return "String: " + String.valueOf(value);
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			StringValue result = new StringValue();
			result.value = this.value;
			return result;
		}
	}
	
	public static class ModelValue extends Value {
		public Model value = null;
		
		public String toString() {
			return "Module(" + ((value == null) ? "null" : value.toString()) + ")";
		}
		
		public Object getObj() {
			return (Object) value;
		}
		
		public Value copy() {
			ModelValue result = new ModelValue();
			result.value = this.value;
			return result;
		}
	}
	
	/* 类内函数开始 */
	
	private HashMap<String,Value> valueMap = new HashMap<String,Value>();
	
	public int getInt(String name,int defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof IntValue) {
			return ((IntValue)v).value;
		}
		return defaultVal;
	}
	
	public boolean getBool(String name,boolean defaultVal) {
		Value v = valueMap.get(name);
		if(v instanceof BoolValue) {
			return ((BoolValue)v).value;
		}
		return defaultVal;
	}
	
	public long getLong(String name,long defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof LongValue) {
			return ((LongValue)v).value;
		}
		return defaultVal;
	}
	public byte getByte(String name,byte defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof ByteValue) {
			return ((ByteValue)v).value;
		}
		return defaultVal;
	}
	
	public float getFloat(String name,float defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof FloatValue) {
			return ((FloatValue)v).value;
		}
		return defaultVal;
	}
	
	public double getDouble(String name,double defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof DoubleValue) {
			return ((DoubleValue)v).value;
		}
		return defaultVal;
	}
	
	public String get(String name,String defaultVal) {
		Value v = valueMap.get(name);
		if(v != null && v instanceof StringValue) {
			return ((StringValue)v).value;
		}
		return defaultVal;
	}
	
	public Object getValue(String name) {
		Value v = valueMap.get(name);
		if(v != null) {
			return v.getObj();
		}
		return null;
	}
	
	
	public void put(String name,Object obj) {
		if(obj instanceof Integer) {
			this.put(name, (int)obj);
		} else if(obj instanceof Long) {
			this.put(name, (long)obj);
		} else if(obj instanceof Boolean) {
			this.put(name, (boolean)obj);
		} else if(obj instanceof Float) {
			this.put(name, (float)obj);
		} else if(obj instanceof Double) {
			this.put(name, (double)obj);
		} else if(obj instanceof Byte) {
			this.put(name, (byte)obj);
		} else if(obj instanceof String) {
			this.put(name, (String)obj);
		} else if(obj instanceof Model) {
			this.put(name, (Model)obj);
		} else {
			this.put(name, obj.toString());
		}
	}
	
	public void put(String name,String value) {
		Value v = valueMap.get(name);
		StringValue v1;
		if(v == null || v instanceof StringValue == false) {
			v1 = new StringValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (StringValue) v;
			v1.value = value;
		}
	}
	public void put(String name,int value) {
		Value v = valueMap.get(name);
		IntValue v1;
		if(v == null || v instanceof IntValue == false) {
			v1 = new IntValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (IntValue) v;
			v1.value = value;
		}
	}
	public void put(String name,long value) {
		Value v = valueMap.get(name);
		LongValue v1;
		if(v == null || v instanceof LongValue == false) {
			v1 = new LongValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (LongValue) v;
			v1.value = value;
		}
	}
	public void put(String name,boolean value) {
		Value v = valueMap.get(name);
		BoolValue v1;
		if(v == null || v instanceof BoolValue == false) {
			v1 = new BoolValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (BoolValue) v;
			v1.value = value;
		}
	}
	
	public void put(String name,float value) {
		Value v = valueMap.get(name);
		FloatValue v1;
		if(v == null || v instanceof FloatValue == false) {
			v1 = new FloatValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (FloatValue) v;
			v1.value = value;
		}
	}
	public void put(String name,double value) {
		Value v = valueMap.get(name);
		DoubleValue v1;
		if(v == null || v instanceof DoubleValue == false) {
			v1 = new DoubleValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (DoubleValue) v;
			v1.value = value;
		}
	}
	public void put(String name,byte value) {
		Value v = valueMap.get(name);
		ByteValue v1;
		if(v == null || v instanceof ByteValue == false) {
			v1 = new ByteValue();
			v1.value = value;
			valueMap.put(name, v1);
		} else {
			v1 = (ByteValue) v;
			v1.value = value;
		}
	}
	
	public void putOrNotFound(String name,Object obj) {
		if(valueMap.containsKey(name)) {
			return;
		}
		this.put(name, obj);
	}
	
	public Set<Entry<String,Value>> entry() {
		return valueMap.entrySet();
	}
	
	public ValueTable remove(String name) {
		valueMap.remove(name);
		return this;
	}
	
	public ValueTable visible(String...args) {
		ValueTable result = new ValueTable();
		
		for(String name : args) {
			Value val = valueMap.get(name);
			
			if(val == null) {
				continue;
			}
			
			valueMap.put(name, val.copy());
		}
		
		return result;
	}
	
	public ValueTable hide(String... args) {
		for(String name : args) {
			this.remove(name);
		}
		
		return this;
	}
	
	public String toString() {
		return valueMap.toString();
	}
}
