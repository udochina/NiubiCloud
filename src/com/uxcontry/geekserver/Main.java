package com.uxcontry.geekserver;

import javax.net.ssl.SSLServerSocket;

import com.uxcontry.geekserver.ServerData.Host;
import com.uxcontry.geekserver.ServerData.VirtualHost;

public class Main {
	public static GeekServer server;
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		if(GeekServer.DEBUG){
			VirtualHost vh = new VirtualHost();
			//vh.mime.put("js", "text/js");
			//vh.defalut.add("my.smhtm");
			vh.root = "D:/";
			Host h = new Host();
			h.name = "wwwtest.com";
			h.dir = "wwwtest";
			vh.host.add(h);
			h = new Host();
			h.name = "www.wwwtest.com";
			h.dir = "wwwtest";
			vh.host.add(h);
			ServerData.virtualHost.add(vh);
		}
		Timer.initiate();
		server = new GeekServer();
		server.init();
	}

}
