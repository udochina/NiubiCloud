package com.uxcontry.geekserver.NativePage;

import java.io.PrintWriter;

import com.uxcontry.geekserver.GeekServer;

public abstract class NativePage {
	private PrintWriter pw;
	private boolean end = false;
	public String host,uri,referer,userAgant;
	public final void call(PrintWriter pw,String  host,String uri,String userAgant,String referer){
		this.pw = pw;
		this.referer = referer;
		this.host = host;
		this.uri = uri;
		this.userAgant = userAgant;
		pw.println("HTTP/1.1 200 OK");
		pw.println(GeekServer.ServerHeader);
		pw.flush();
		Run();
	}
	public final void endHeader()
	{
		if(!end){
			end = true;
			pw.println();
			pw.flush();
		}
	}
	public void header(String name,String value)
	{
		pw.println(new StringBuilder(name).append(": ").append(value).toString());
	}
	public void session_init()
	{
		
	}
	public abstract void Run();
}
