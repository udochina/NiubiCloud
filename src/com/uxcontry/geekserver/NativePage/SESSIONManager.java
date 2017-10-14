package com.uxcontry.geekserver.NativePage;

import java.util.HashMap;
import java.util.Map;

public class SESSIONManager {
	private Map<String,SESSION> session = new HashMap<String,SESSION>();
	private long id = 10000;
	public SESSION getSession(String id,String mstr,NativePage np)
	{
		SESSION s = null;
		if(id!=null)
			s = session.get(id);
		if(s==null){
			s = new SESSION();
			s.last_used = System.currentTimeMillis();
			synchronized (this) {
				s.id = (""+(this.id++));
				s.mstr = ""+System.nanoTime() % 1000;
			}
			session.put(s.id, s);
			return s;
		}
		if(System.currentTimeMillis() - s.last_used >= SESSION.SESSION_TIMEOUT){
			session.remove(id);
			s = new SESSION();
			s.last_used = System.currentTimeMillis();
			synchronized (this) {
				s.id = (""+(this.id++));
				s.mstr = ""+System.nanoTime() % 781;
			}
			session.put(s.id, s);
			return s;
		}
		if(!s.mstr.equals(mstr)){
			session.remove(id);
			s = new SESSION();
			s.last_used = System.currentTimeMillis();
			synchronized (this) {
				s.id = (""+(this.id++));
				s.mstr = ""+System.nanoTime() % 998+1;
			}
			session.put(s.id, s);
			return s;
		}
		s.last_used = System.currentTimeMillis();
		return s;
	}
}
