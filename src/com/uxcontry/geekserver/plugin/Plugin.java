package com.uxcontry.geekserver.plugin;

import java.io.File;
import java.io.InputStream;

import com.uxcontry.geekserver.GeekServer;

/*
 * GeekServer�������
 * BY ����
 */

public class Plugin {
	/*
	 * ����0��ʾ����ִ��
	 * ����1��ʾ������ִ��
	 * ����2��ʾ�ܾ�
	 */
	public int onRequsetHanlder(GeekServer.Connection con){
		return 0;
	}
	/*
	 * ����null��ʾ����������
	 */
	public InputStream onRequsetFile(String uri,File preFile,GeekServer.Connection con){
		return null;
	}
	

}
