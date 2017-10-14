package com.uxcontry.geekserver;

import javax.net.ssl.SSLServerSocket;

import com.uxcontry.geekserver.ServerData.Host;
import com.uxcontry.geekserver.ServerData.VirtualHost;
import com.uxcontry.geekserver.debug.TestNativePage;

public class Main {
	public static GeekServer server;
	public static void main(String[] args) throws Exception {
		// TODO 自动生成的方法存根
			VirtualHost vh = new VirtualHost();
			//vh.mime.put("js", "text/js");
			//vh.defalut.add("my.smhtm");
			//vh.spare = 10;
			vh.root = "D:/";
			vh.nativePage.put("/native", new TestNativePage());
			Host h = new Host();
			h.name = "wwwtest.com";
			h.dir = "wwwtest";
			vh.host.add(h);
			h = new Host();
			h.name = "www.wwwtest.com";
			h.dir = "wwwtest";
			vh.host.add(h);
			ServerData.virtualHost.add(vh);
		Timer.initiate();
		server = new GeekServer();
		server.init();
		server.bind(80);
	}

}
