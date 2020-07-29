package com.uxcontry.geekserver;

import com.uxcontry.geekserver.ServerData.Host;
import com.uxcontry.geekserver.ServerData.VirtualHost;

/*
 * 调试用的启动方法
 * 调试方法：
 * 1.给hosts添加127.0.0.1 wwwtest.com
 * 2.把内容文件放到D:\wwwtest下
 */

public class Main {
	public static GeekServer server;
	public static void main(String[] args) throws Exception {
		// TODO 自动生成的方法存根
			VirtualHost vh = new VirtualHost();
			vh.root = "D:/";
			//vh.nativePage.put("/native", new TestNativePage());
			Host h = new Host();
			h.name = "wwwtest.com";
			h.dir = "Maxhtml.js";
			vh.host.add(h);
			h = new Host();
			h.name = "www.wwwtest.com";
			h.dir = "wwwtest";
			vh.host.add(h);
			h = new Host();
			h.name = "192.168.43.98";
			h.dir = "Maxhtml.js";
			vh.host.add(h);
			ServerData.virtualHost.add(vh);
		Timer.initiate();
		server = new GeekServer();
		server.init();
		server.bind(80);
	}

}
