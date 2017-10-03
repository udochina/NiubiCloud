package com.uxcontry.geekserver.EMHTML;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Maker {
	public static final String afterName = ".smhtm";
	public static void run(File f,Socket s,OutputStream os,String ip,String uri,String host,String query,String cookies,String ua,String referer) throws Exception
	{
		PrintWriter pw = new PrintWriter(os);
		pw.println("HTTP/1.1 200 OK");
		pw.println("Content-Type: text/html");
		pw.println("Cache-Control: private");
		pw.println("Server: GeekServer/1.1");
		pw.println("X-Powered-By: FreeMarker/2.3.25");
		pw.println("Connection: close");
		pw.flush();
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setDirectoryForTemplateLoading(f.getParentFile());
		cfg.setDefaultEncoding("UTF-8");
		Template temp = cfg.getTemplate(f.getName());
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("Version", "1.0");
		root.put("Uri", uri);
		root.put("Host", host);
		root.put("Query", query);
		root.put("ClientIP", ip);
		if(referer!=null)
			root.put("Referer", referer);
		if(cookies!=null)
			root.put("Cookies", cookies);
		if(ua!=null)
			root.put("UserAgent", ua);
		root.put("Time", System.currentTimeMillis());
		root.put("Server", "GeekServer/1.1");
		temp.process(root, new OutputStreamWriter(os));
		s.close();
	}
}
