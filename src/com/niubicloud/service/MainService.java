package com.niubicloud.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.niubicloud.Config;
import com.niubicloud.base.Controller;
import com.niubicloud.loader.BaseLoader;
import com.niubicloud.loader.ControllerLoader;
import com.niubicloud.loader.PathLoader;

import javax.net.ssl.SSLServerSocketFactory;

public class MainService extends ServiceImpl {
	private int port;
	private Queue<Connection> waitQueue = new LinkedBlockingQueue<Connection>();
	// private ExecutorService handlerThreadPool = Executors.newFixedThreadPool(128);
	
	private int keepAliveTimeout = Config.DEAFULT_KEEPALIVE_TIMEOUT,keepAliveMax = Config.DEAFULT_KEEPALIVE_MAX;

	private int ReadThreadNum = Config.DEAFULT_READ_THREAD_NUM;

	public PathLoader pathLoader = null;
	public HashMap<String,BaseLoader> controllers = new HashMap<String,BaseLoader>();

	public MainService() {
		headers.put("Server", "NiubiCloud");
	}

	public void bind(int port) throws IOException {
		ServerSocket ss = new ServerSocket(port);
		internBind(ss);
	}

	public void bindSsl(SSLServerSocketFactory factory) throws IOException {
		ServerSocket ss = factory.createServerSocket(port);
		internBind(ss);
	}

	private void internBind(ServerSocket ss) throws SocketException {
		ss.setSoTimeout(100);
		ss.setReceiveBufferSize(1000);
		ss.setPerformancePreferences(1, 1, 2);
		ss.setReuseAddress(true);
		new WaitThread(ss).start();
	}

	public void start() {
		int i = 0;
		for(i = 0; i < ReadThreadNum ;i++) {
			new ReadThread(i).start();
		}
	}
	
	public int getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	public int getKeepAliveMax() {
		return keepAliveMax;
	}

	public void setKeepAliveMax(int keepAliveMax) {
		this.keepAliveMax = keepAliveMax;
	}

	public HashMap<String,String> headers = new HashMap<String,String>();
	
	public int getReadThreadNum() {
		return ReadThreadNum;
	}

	public void setReadThreadNum(int readThreadNum) {
		ReadThreadNum = readThreadNum;
	}

	public class Connection {
		Socket s;
		InputStream is;
		int lastBuffer = -1;
		public StringBuilder headerBuffer = new StringBuilder();
		long createTime;
		int conCount = 0;
		
		public Connection(Socket s,InputStream is){
			this.s = s;
			this.is = is;
			this.createTime = System.currentTimeMillis();
		}
		
		public Connection(Socket s) throws IOException {
			this(s,s.getInputStream());
		}

		public Connection reuse() {
			// TODO Auto-generated method stub
			Connection conn = new Connection(s,is);
			conn.conCount = this.conCount + 1;
			return conn;
		}
	}
	
	public class WaitThread extends Thread {
		private ServerSocket ss;

		public WaitThread(ServerSocket ss) {
			this.ss = ss;
		}

		public void run() {
			for(;;) {
				try {
					Socket s = ss.accept();
					if(s == null)
						continue;
					s.setReceiveBufferSize(60 *1024);
					s.setSendBufferSize(50 * 1024);
					s.setSoTimeout(10);
					waitQueue.add(new Connection(s));
				} catch(SocketTimeoutException e) {
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	public class ReadThread extends Thread {
		private Connection[] connections = new Connection[Config.DEAFULT_MAX_CONNECTION_PER_THREAD];
		private int num = 0;
		private int currentThreadId;

		public ReadThread(int currentThreadId) {
			this.currentThreadId = currentThreadId;
		}

		public void run() {
			for(;;) {
				handle();
				try {
					if(num == 0 && waitQueue.size() == 0) {
						if(currentThreadId < 1) {
							Thread.sleep(0);
						} else {
							Thread.sleep(100);
						}
					} else {
						Thread.sleep(1);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		
		public void handle() {
			for(int point = 0; point < connections.length ;point++){
				Connection conn = connections[point];
				if(conn != null) {
					if (conn.s.isClosed()) {
						connections[point] = null;
						num--;
						continue;
					}
					try {
						if (worker(conn)) {
							connections[point] = null;
							num--;
							new HandleThread(MainService.this, conn).start();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						if (conn.s.isClosed() == false) {
							try {
								conn.s.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								// e1.printStackTrace();
							}
						}
						connections[point] = null;
						num--;
						continue;
					}
				} else {
					if((connections[point] = waitQueue.poll()) != null) {
						num++;
					}
				}
			}
		}
		
		private boolean worker(Connection conn) throws IOException {
			try {
				if(conn.is.available() > 0) {
					int i = 0;
					for(;(i = conn.is.read()) != -1;) {
						if(i == '\n') {
							if(conn.lastBuffer == '\r' && conn.headerBuffer.length() >= 3)
								if (conn.headerBuffer.charAt(conn.headerBuffer.length() - 2) == '\n' && conn.headerBuffer.charAt(conn.headerBuffer.length() - 3) == '\r')
								{
									conn.headerBuffer.append((char)i);
									return true;
								}
						}
						conn.lastBuffer = i;
						conn.headerBuffer.append((char)i);
					}
				}
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
			}
		}
	}
	
	public void registerController(String name,Class<? extends Controller> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		controllers.put(name, new ControllerLoader(clazz));
	}
	
	void addRequestFromHandle(Connection conn) {
		// System.out.println("keepalive");
		// System.out.println(conn.s.hashCode());
		waitQueue.add(conn.reuse());
	}
}
