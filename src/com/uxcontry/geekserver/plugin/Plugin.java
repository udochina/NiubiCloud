package com.uxcontry.geekserver.plugin;

import java.io.File;
import java.io.InputStream;

import com.uxcontry.geekserver.GeekServer;

/*
 * GeekServer插件机制
 * BY 恋空
 */

public class Plugin {
	/*
	 * 返回0表示继续执行
	 * 返回1表示不继续执行
	 * 返回2表示拒绝
	 */
	public int onRequsetHanlder(GeekServer.Connection con){
		return 0;
	}
	/*
	 * 返回null表示不覆盖数据
	 */
	public InputStream onRequsetFile(String uri,File preFile,GeekServer.Connection con){
		return null;
	}
	

}
