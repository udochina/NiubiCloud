package com.niubicloud.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;

import com.niubicloud.Config;
import com.niubicloud.NiubiCloud;
import com.niubicloud.base.Hook;
import com.niubicloud.exception.FinishRequest;
import com.niubicloud.exception.ProtocolException;
import com.niubicloud.exception.UnpredictedException;
import com.niubicloud.loader.BaseLoader;
import com.niubicloud.loader.ControllerLoader;
import com.niubicloud.loader.PathLoader;
import com.niubicloud.service.MainService.Connection;
import com.niubicloud.type.HeaderParser;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;
import com.niubicloud.type.ServerOutputStream;
import com.niubicloud.utils.StringUtil;

public class HandleThread extends Thread {
	public static Request currentRequest() {
		Thread t = Thread.currentThread();
		if(t instanceof HandleThread) {
			return ((HandleThread)t).req;
		}
		return null;
	}
	
	public static Respone currentRespone() {
		Thread t = Thread.currentThread();
		if(t instanceof HandleThread) {
			return ((HandleThread)t).resp;
		}
		return null;
	}
	
	
	public Request req;
	public Respone resp;
	public Connection conn;
	private MainService service;
	boolean isKeepAlive;
	
	public HandleThread(MainService service,Connection conn) {
		super();
		this.service = service;
		this.conn = conn;
	}
	
	public void run() {
		main();
		this.conn = null;
	}
	
	public void main() {
		try {
			handle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof SocketException) {
				if(conn.s.isClosed() == false) {
					try {
						conn.s.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
				}
				conn.destroy();
				return;
			}
			if(e instanceof UnpredictedException) {
				if(((UnpredictedException)e).isSocketException()) {
					this.conn.destroy();
					return;
				}
			}
			e.printStackTrace();
		}
		try {
			// 不符合KeepAlive条件则关闭
			if(resp.headers.get("Content-Length") == null) {
				isKeepAlive = true;
			}
			if(isKeepAlive) {
				service.addRequestFromHandle(conn);
			} else {
				this.conn.s.close();
				this.conn.destroy();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	private void handle() throws Exception {
		req = new Request();
		resp = new Respone(this.conn.s.getOutputStream(),conn);
		
		try {
			HeaderParser.parse(conn, req);
		} catch(ProtocolException e) {
			returnReq(400);
			return;
		}
		
		String path = req.uri;
		if(req.uri.contains("?")) {
			String strs[] = req.uri.split("\\?",2);
			path = strs[0];
			HeaderParser.handleGetQuery(req,strs[1]);
		}
		req.uri = path;
		resp.headers.putAll(service.headers);
		handleKeepAlive();
		
		BaseLoader loader = findController(path.split("/"));
		if(loader == null) {
			if(service.pathLoader.haveMethod(req.path)) {
				loader = service.pathLoader;
			} else {
				returnReq(404);
				return;
			}
		}
		if(loader.haveMethod(req.handleMethodName) == false && loader.haveMethod("*") == false) {
			returnReq(404);
			return;
		} else if(loader.haveMethod("*") == true) {
			req.handleMethodName = "*";
		}
		if(loader.accessMethod(req.handleMethodName, req.method) == false) {
			returnReq(405);
			return;
		}
		
		HeaderParser.parseCookie(req);
		
		try {
			if(loader.call(req.handleMethodName,req,resp) == false) {
				return500(resp,null);
				return;
			}
		} catch(InvocationTargetException e) {
			Throwable e1 = e.getTargetException();
			if(e1 instanceof FinishRequest) {
			} else if(e1 instanceof ProtocolException) {
				returnReq(400);
				return;
			} else if(e1 instanceof PathLoader.UnfoundException) {
				returnReq(404);
				return;
			} else if(e1 instanceof UnpredictedException) {
				return;
			} else {
				if(Hook.trigger("error", loader.getController(),req, resp, e1) == false) {
					return500(resp, e1);
				}
			}
			return;
		} catch(Exception e) {
			return500(resp,e);
			return;
		}
		resp.finishAuto();
	}
	
	private void returnReq(int code) throws IOException {
		if(code == 404) {
			if(Hook.trigger("rewrite", null, req, resp) == false) {
				resp.code = code;
				Hook.trigger("notfound", null, req, resp);
				resp.buffer().write(code," ",Respone.getStatusCode(code)).finish();
			}
		} else {
			resp.code = code;
			resp.buffer().write(code," ",Respone.getStatusCode(code)).finish();
		}
	}
	
	private void return500(Respone resp,Throwable e) throws IOException {
		resp.code = 500;
		//resp.finish();
		resp.buffer().reset().write("500 Internal Server Error").finish();
	}
	
	private BaseLoader findController(String[] pathSplit) {
		BaseLoader loader;
		
		if(pathSplit.length <= 0 || (pathSplit.length == 1 && "".equals(pathSplit[0]))) {
			loader = service.controllers.get("index");
			req.path = "/";
			req.fileName = "index";
			
		} else  {
			int i = 0;
			if("".equals(pathSplit[0])) {
				i++;
			}
			loader = service.controllers.get(pathSplit[i]);
			req.path = req.uri;
			req.fileName = req.uri;
			
			if(pathSplit.length - i == 2) {
				req.handleMethodName = pathSplit[i + 1];
			} else {
				req.handleMethodName = "index";
			}
		}
		
		req.controller = loader;
		
		return loader;
	}
	
	private void handleKeepAlive() {
		isKeepAlive = ("GET".equals(req.method) || "HEAD".equals(req.method)) && Request.VERSION_1_1.equals(req.version) && "keep-alive".equals(req.header("Connection"));
		if(isKeepAlive) {
			resp.header("Connection", "keep-alive");
			resp.header("Keep-Alive",String.format("timeout=%d,max=%d",service.getKeepAliveTimeout(),(service.getKeepAliveMax() - conn.conCount)));
		} else {
			resp.header("Connection", "close");
		}
	}
}
