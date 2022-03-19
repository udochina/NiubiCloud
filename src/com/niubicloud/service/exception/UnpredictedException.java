package com.niubicloud.service.exception;

import java.io.IOException;
import java.net.SocketException;


/*
*	����Ԥ�����쳣
* 	һ�㲻��Ҫ������Ϊ�󲿷�ԭ���ǿͻ��������
* 	�����磺�ͻ���ͻȻ�Ͽ���������������д��IO���ͻ��˷����˲�����HTTPЭ��Ĳ�����
* 	������������쳣���������账�������׳�����ܻ����д���
 */
@SuppressWarnings("serial")
public class UnpredictedException extends RuntimeException {
	Exception e;
	
	UnpredictedException(String str) {
		super("Unpredicted Exception: " + str);
		this.e = this;
	}
	
	public UnpredictedException(Exception e){
		super("Unpredicted Exception",e);
		this.e = e;
	}
	
	public Exception getException() {
		return e;
	}
	
	public boolean isSocketException() {
		return e instanceof SocketException;
	}
}
