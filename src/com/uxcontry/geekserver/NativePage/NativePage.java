package com.uxcontry.geekserver.NativePage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uxcontry.geekserver.GeekServer;
import com.uxcontry.geekserver.ServerData.VirtualHost;

public abstract class NativePage {
	private static final String x_header = "X-Powered-By: NativePage/1.1";
	private PrintWriter pw;
	private boolean end = false;
	public String host,uri,referer,userAgant,cookie,method;
	private String header = "",code = "200",status = "OK";
	public VirtualHost vhost;
	public Map<String,Object> Application;
	public SESSION SESSION;
	private OutputStream os;
	private HashMap<String,String> cookies = new HashMap<String,String>();
	public final void call(PrintWriter pw,OutputStream os,InputStream data,String method,String  host,String uri,String userAgant,String referer,String cookie,VirtualHost vhost){
		this.pw = pw;
		this.referer = referer;
		this.host = host;
		this.uri = uri;
		this.cookie = cookie;
		this.userAgant = userAgant;
		this.method = method;
		this.vhost = vhost;
		this.os = os;
		this.Application = vhost.application;
		parseCookie();
		Run();
		pw.flush();
	}
	public final void endHeader()
	{
		if(!end){
			end = true;
			pw.println("HTTP/1.1 "+code+" "+status);
			pw.println("Server: GeekServer/1.1");
			pw.print(header);
			pw.println(x_header);
			pw.println("Connection: close");
			pw.println();
			pw.flush();
		}
	}
	public void header(String name,String value)
	{
		header += new StringBuilder(name).append(": ").append(value).append("\r\n").toString();
	}
	public final SESSION session_start()
	{
		String session_id = cookies.get("SESSION");
		String[] str = null;
		if(session_id!=null){
			str = session_id.split(":");
		}
		if(cookie!=null && str!=null && str.length==2){
			SESSION = vhost.session.getSession(str[0],str[1],this);
		} else {
			SESSION = vhost.session.getSession(null,null,this);
		}
		header("Set-Cookie", "SESSION="+SESSION.id+":"+SESSION.mstr+" ;HttpOnly");
		return SESSION;
	}
	public final boolean empty(Object o){
		return o==null;
	}
	
	private void parseCookie()
	{
		if(cookie==null) return;
		String name = "",value = "";
		for(String chars : cookie.split(";"))
		{
			int i = 0;
			for(char ch : chars.trim().toCharArray()){
				if(ch=='=' && i==0){
					i++;
					continue;
				}
				if(i==0){
					name += ch;
				} else {
					value += ch;
				}
			}
			cookies.put(name, value);
			name = value = "";
		}
	}
	public String getCookie(String name){
		return cookies.get(name);
	}
	public final void echo(String str){
		if(!end){
			endHeader();
		}
		pw.println(str);
	}
	public final void outputFile(File f) throws FileNotFoundException, IOException{
		writeStream(os,new FileInputStream(f));
	}
	/*
	 * 从输出流写到输入流
	 */
	private void writeStream(OutputStream os,InputStream is) throws IOException
	{
		byte[] buffer = new byte[1024 * 1024];
		int i = 0;
		for(;(i = is.read(buffer))!=-1;){
			os.write(buffer,0,i);
			os.flush();
		}
	}
	
	public abstract void Run();
	public abstract void callback();
}
