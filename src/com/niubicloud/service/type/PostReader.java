package com.niubicloud.service.type;

import com.niubicloud.service.exception.ProtocolException;

/*
* POST数据处理
* ！！！未完成！！！
 */
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
