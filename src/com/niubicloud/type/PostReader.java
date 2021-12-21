package com.niubicloud.type;

import com.niubicloud.exception.ProtocolException;

public class PostReader {
	public void handlePostData(Request req) throws ProtocolException {
		try {
			String contentType = req.get("Content-Type");
			if(contentType == null) {
				throw new ProtocolException();
			}
			int contentLength = Integer.parseInt(req.get("Content-Length"));
		} catch(Exception e) {
			if(e instanceof ProtocolException) {
				throw e;
			} else {
				throw new ProtocolException();
			}
		}
	}
}
