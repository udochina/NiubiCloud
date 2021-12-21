package com.niubicloud.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SessionImpl implements Map<String,Object>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4247890030173549599L;
	
	private HashMap<String,Object> map = new HashMap<String,Object>();
	private String id;
	
	public SessionImpl(String str) {
		this.id = str;
	}
	
	public abstract void onLoad(Map<String,Object> out);
	public abstract void onUpdate(Map<String,Object> in);
	public abstract void onSave(Map<String,Object> in);
	
	protected Map<String,Object> getInternalMap() {
		return this.map;
	}
	
	public String getId() {
		return this.id;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return map.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return map.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		// TODO Auto-generated method stub
		Object result = map.put(key, value);
		onUpdate(map);
		return result;
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		Object result =  map.remove(key);
		onUpdate(map);
		return result;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
		onUpdate(map);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		map.clear();
		onUpdate(map);
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return map.keySet();
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return map.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return map.entrySet();
	}
	
}
