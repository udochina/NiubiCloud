package com.uxcontry.geekserver;

import java.io.UnsupportedEncodingException;

public class ServerConfig {
	static{
		try {
			welcomeContent = "<html><head><meta charset=\"utf-8\" /><title>Welcome to GeekServer!</title></head><body><h3>欢迎使用GeekServer!</h3><h4><font color=\"blue\">GeekServer(极客服务器)是由国人开发的服务器软件，采用独特的防拥堵结构，<br/>结合长连接和压缩传输等功能为一体，可以满足一些高并发要求。</font></h4><br/><hr/><div align=\"center\"><h4>Geek Server/1.1</h4></div></body></html>".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public static final int MAX_IP_CONNECTION = 20;
	public static byte[] welcomeContent;
	public static final int Timeout = 30;
	public static final int MAX_CACHE_USED = 10 * 1024 * 1024;
	public static final long MAX_CACHE_FILE = 1 * 1024 * 1024;
	public static final int MAX_SEND_FILE = 512 * 1024 * 1024;
	public static final int MAX_ZIP_FILE = 10 * 1024 * 1024;
	public static final int MAX_CONNECTION_ON_10 = 50;
	public static boolean RETURN_204_NO_CONTENT = true;
}
