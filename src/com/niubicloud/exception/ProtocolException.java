package com.niubicloud.exception;

/*
 *	协议异常
 * 	一般不需要处理，因为大部分原因是客户端引起的
 * 	（例如：客户端发送了不符合HTTP协议的数据）
 * 	如果捕获到这类异常，可以无需处理，往上抛出。框架会自行处理。
 */
@SuppressWarnings("serial")
public class ProtocolException extends UnpredictedException {

	public ProtocolException() {
		super("Http protocol parse error.");
		// TODO Auto-generated constructor stub
	}
}
