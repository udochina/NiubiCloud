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
		public String name;
		public String root;
		public List<Host> host = new ArrayList<Host>();
		public List<String> defalut = new ArrayList<String>();
		public Map<String,String> mime = new Hashtable<String,String>();
	}
	public static class Host{
		public String name,dir;
	}
	public static void read() throws Exception{
		File file = new File("server.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder=factory.newDocumentBuilder(); 
		Document document =  builder.parse(file);
		if(!document.getDocumentElement().getTagName().equals("server")){
			return;
		}
		NodeList list = document.getDocumentElement().getElementsByTagName("Host");
		for(int i=0;i<list.getLength();i++){
			Node n = list.item(i);
			
		}
	}
}
