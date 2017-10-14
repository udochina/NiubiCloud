package com.uxcontry.geekserver.NativePage;

import java.util.HashMap;

public class SESSION extends HashMap<String,Object> {
	public static final int SESSION_TIMEOUT = 30 * 60 * 1000;
	public String id;
	public String mstr;
	public long last_used;
}
