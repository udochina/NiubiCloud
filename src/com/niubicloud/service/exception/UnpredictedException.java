package com.niubicloud.service.exception;

import java.io.IOException;
import java.net.SocketException;


/*
*	不可预测性异常
* 	一般不需要处理，因为大部分原因是客户端引起的
* 	（例如：客户端突然断开，而服务器正在写入IO、客户端发送了不符合HTTP协议的参数）
* 	如果捕获到这类异常，可以无需处理，往上抛出。框架会自行处理。
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
