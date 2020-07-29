package com.uxcontry.geekserver;

import com.uxcontry.geekserver.ServerData.Host;
import com.uxcontry.geekserver.ServerData.VirtualHost;

/*
 * �����õ���������
 * ���Է�����
 * 1.��hosts���127.0.0.1 wwwtest.com
 * 2.�������ļ��ŵ�D:\wwwtest��
 */

public class Main {
	public static GeekServer server;
	public static void main(String[] args) throws Exception {
		// TODO �Զ����ɵķ������
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
