package com.uxcontry.geekserver;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class ServerData {
	public static final List<VirtualHost> virtualHost = new ArrayList<VirtualHost>();
	
	public static class VirtualHost{
		/*
		 * 0��ʾ������������
		 * 1��ʾCDN����
		 */
		public int type = 0;
		public String name;
		public String root;
		public int status = 0; 		//0����,1�ر�
		public List<Host> host = new ArrayList<Host>();
		public List<String> defalut = new ArrayList<String>();
		public Map<String,String> mime = new Hashtable<String,String>();
		public boolean canKeepAlive = true;
		public Map<String,String> redirect = new HashMap<String,String>();
		public Map<String, Object> application = new HashMap<String,Object>();
		
		public boolean checkBrowser = true;
		// �Ƿ�������������
	}
	public static class Host{
		public String name,dir;
		public boolean includeNativePage = true;
	}
}