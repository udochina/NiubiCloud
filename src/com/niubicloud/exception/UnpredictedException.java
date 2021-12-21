package com.niubicloud.exception;

import java.io.IOException;
import java.net.SocketException;

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
