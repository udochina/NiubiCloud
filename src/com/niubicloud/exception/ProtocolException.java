package com.niubicloud.exception;

/*
 *	Э���쳣
 * 	һ�㲻��Ҫ������Ϊ�󲿷�ԭ���ǿͻ��������
 * 	�����磺�ͻ��˷����˲�����HTTPЭ������ݣ�
 * 	������������쳣���������账�������׳�����ܻ����д���
 */
@SuppressWarnings("serial")
public class ProtocolException extends UnpredictedException {

	public ProtocolException() {
		super("Http protocol parse error.");
		// TODO Auto-generated constructor stub
	}
}
