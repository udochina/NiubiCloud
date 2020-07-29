package com.uxcontry.geekserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.FileNameMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import javax.activation.MimetypesFileTypeMap;
import com.uxcontry.geekserver.GeekServer.HandlerThread.Header;

/*
 * GeekServer服务器 V1.6
 * BY 恋空
 */

public class GeekServer 
{
	public static final boolean DEBUG = false;
	private static final String[] dangerWord = new String[]{"/.htaccess","/.rewrite","/nginx.conf","/httpd.conf","/.control"};
	private static final String[] defalut = new String[]{"index.html","index.htm","defalut.html","defalut.htm"};
	public static final String ServerHeader = "Server: GeekServer/1.6";
	
	/*
	 * 消息队列
	 */
	private Queue<Connection> waitQueue = new LinkedBlockingQueue<Connection>();
	/*
	 * Mime池
	 */
	//public MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
	private FileNameMap mime = URLConnection.getFileNameMap();
	private Map<String,String> mimeTable = new HashMap<String,String>();
	/*
	 * 线程池
	 */
	private ExecutorService handlerThreadPool = Executors.newFixedThreadPool(128); 
	/*
	 * 禁止IP
	 */
	private List<String> disconnectIP = new ArrayList<String>();
	/*
	 * IP记录，后期改成分段存储
	 */
	private List<IP> IP = new ArrayList<IP>();
	/*
	 * 内存缓存
	 */
	private List<Cache> AllCache = new ArrayList<Cache>();
	/*
	 * 总连接数
	 */
	private int total = 0;
	/*
	 * 10秒连接数
	 *  超过50就触发CC保护模块
	 */
	private  int con10 = 0;
	private int running = 0;
	private int waiting = 0;
	private List<ServerSocket> serverSocketList = new ArrayList<ServerSocket>();
	private String XKey = "";
	// 变量区结束
	
	public GeekServer(){
	}
	public void init()
	{
		mime_init();
		int i = 0;
		for(;i<10;i++){
			new WaitThread().start();
		}
		XKey = String.format("%x",System.currentTimeMillis() % 10000 + System.nanoTime());
		Runtime.getRuntime().addShutdownHook(new destroyThread());
		Timer.setInterval(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized(GeekServer.this){
					con10 = 0;
				}
				XKey = String.format("%x",System.currentTimeMillis() % 10000 + System.nanoTime());
			}
			
		}, 10);
	}
	private void mime_init(){
		mimeTable.put("html", "text/html");
		mimeTable.put(".css", "text/css");
		mimeTable.put(".js", "text/x-javascript");
		mimeTable.put(".appcache", "text/cache-manifest");
	}
	private void bind(int port,boolean https,int threadnum) throws Exception
	{
		int i = 0;
		ServerSocket ss = null;
		if(!https)
			ss = new ServerSocket(port);
		serverSocketList.add(ss);
		ss.setPerformancePreferences(0, 1, 2);
		ss.setReceiveBufferSize(3000);
		for(;i < threadnum;i++){
			new AcceptThread(ss).start();
		}
	}
	
	public void bind(int port,boolean https)
	{
		try {
			bind(port,https,8);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			onerror(e);
		}
	}
	public void bind(int port)
	{
		try {
			bind(port,false,8);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			onerror(e);
		}
	}
	
	private void onerror(Exception e)
	{
		if(DEBUG){
			e.printStackTrace();
		}
	}
	/*
	 * 检查
	 */
	public boolean check() {
		// TODO Auto-generated method stub
		return true;
	}
	/*
	 * 接受线程
	 */
	public class AcceptThread extends Thread
	{
		private ServerSocket ss;
		public AcceptThread(ServerSocket ss) {
			this.ss = ss;
		}
		public void run()
		{
			this.setPriority(Thread.MAX_PRIORITY-1);
			for(;;){
				Socket s = null;
				for(;s==null;) {
					try {
						yield();
						s = ss.accept();
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
					}
				}
				this.setPriority(Thread.MAX_PRIORITY);
				synchronized(GeekServer.this){
					total++;
					con10++;
				}
				if(s!=null){
					IP ip = readIP(s.getInetAddress().getHostAddress());
					if(ip!=null){
						try {
							s.setSendBufferSize(128 * 1024);
							s.setReceiveBufferSize(40 * 1024);
						} catch (SocketException e) {
							// TODO 自动生成的 catch 块
							onerror(e);
							continue;
						}
						Connection c = new Connection();
						c.s = s;
						c.IP = ip;
						c.startTime = System.currentTimeMillis();
						synchronized(c.IP){
							c.IP.connect++;
						}
						waitQueue.add(c);
					} else {
						try {
							s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			}
		}
		
	}
	/*
	 * 查找IP
	 */
	public synchronized IP readIP(String address){
		for(String s : disconnectIP){
			if(s!=null){
				if(s.equals(address)){
					return null;
				}
			}
		}
		IP ip = null;
		for(Object i : IP.toArray()){
			if(i!=null){
				if(((IP)i).address.equals(address)){
					ip = (IP)i;
					break;
				}
			}
		}
		if(ip==null){
			ip = new IP();
			ip.address = address;
			IP.add(ip);
			return ip;
		}
		if(ip.connect >= MaxIPConnection()){
			return null;
		}
		return ip;
	}
	public int MaxIPConnection(){
		return  ServerConfig.MAX_IP_CONNECTION;
	}
	/*
	 * 等待线程
	 */
	private int wait_id = 0;
	public class WaitThread extends Thread
	{
		private Connection[] connections = new Connection[128];
		private int len = 0;
		public void run()
		{
			int sleep_time = 0;
			synchronized (this) {
				sleep_time = wait_id * 10;
				wait_id++;
			}
			if(sleep_time!=0){
				try {
					sleep(sleep_time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
			this.setPriority(Thread.MAX_PRIORITY);
			for(;;){
				try {
					handler();
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					onerror(e);
				}
				try {
					sleep(3);
				} catch (InterruptedException e) {
				}
			}
		}
		private void handler() throws Exception 
		{
			// TODO 自动生成的方法存根
			int point = 0;
			/*
			 * 遍历队列
			 */
			for(Connection c : connections){
				if(c==null){
					if((connections[point] = waitQueue.poll())!=null){
						len++;
					}
				}
				if(c!=null){
					if(c.s.isClosed()){
						c.IP.connect--;
						connections[point] = null;
						continue;
					}
					/*
					 * 如果有数据就读入缓冲区
					 */
					for(int read = 0;c.s.getInputStream().available()!=0 && read <= 1024;read++)
					{
						if(System.currentTimeMillis() - c.startTime >= ServerConfig.Timeout * 1000){
							synchronized(c.IP){
								c.IP.connect--;
							}
							connections[point] = null;
							c.s.close();
							break;
						}
						if(c.s.isClosed()){
							synchronized(c.IP){
								c.IP.connect--;
							}
							connections[point] = null;
							break;
						}
						if(c.buffer.length() >=1024){
							synchronized(c.IP){
								c.IP.connect--;
							}
							connections[point] = null;
							c.s.close();
							break;
						}
						if(!c.tempbuffer.startsWith("Cookie: ") && !c.tempbuffer.startsWith("User-Agent: ")){
							if(c.tempbuffer.length() >= 300){
								synchronized(c.IP){
									c.IP.connect--;
								}
								connections[point] = null;
								c.s.close();
								break;
							}
						}
						char ch = (char) c.s.getInputStream().read();
						//System.out.print(ch);
							if(ch=='\n') {
								if(!c.tempbuffer.endsWith("\r")){
									c.s.getOutputStream().flush();
									c.s.close();
									synchronized(c.IP){
										c.IP.connect--;
									}
									connections[point] = null;
									break;
								}
								/*
								 * 读取完成
								 */
								if(c.tempbuffer.equals("\r")){
									/*
									 * 提前的浏览器检查
									 */
									if(ServerConfig.MUST_BROWSER_CHECK){
										if(c.check_status!=2){
											c.s.close();
											synchronized(c.IP){
												c.IP.connect--;
											}
											connections[point] = null;
											break;
										}
									}
									/*
									 * 处理请求
									 */
									if(waiting <= 64){
										handlerThreadPool.execute(new HandlerThread(c));
										connections[point] = null;
										len--;
										break;
									} else {
										c.s.getOutputStream().write(("HTTP/1.1 503 Service Unavailable\r\n"+ServerHeader+"\r\nRetry-After: 10\r\n\r\nServer busy,Please try again later.").getBytes());
										c.s.getOutputStream().flush();
										c.s.close();
										synchronized(c.IP){
											c.IP.connect--;
										}
										connections[point] = null;
										break;
									}
								} else {
									/*
									 * 完成一行
									 */
									if(c.tempbuffer.startsWith("User-Agent: ") || c.tempbuffer.startsWith("Accept: ")){
										c.check_status++;
									}
									c.buffer.append(c.tempbuffer).append("\n");
									c.tempbuffer = "";
								}
							} else {
								c.tempbuffer += ch;
							}
						}
					yield();
				}
				point++;
			}
		}
	}
	public class HandlerThread extends Thread{
		private Connection con;
		public HandlerThread(Connection c){
			waiting++;
			con = c;
		}
		public void run()
		{
			try {
				waiting--;
				if(con.s.isClosed()){
					return;
				}
				running++;
				//long start = System.currentTimeMillis();
				//System.out.println(con.buffer);
				int ret = handler();
				//System.out.println(System.currentTimeMillis() - start);
				if(ret==0){
					/*
					 * 关闭
					 */
					con.s.close();
					synchronized(con.IP){
						con.IP.connect--;
					}
					con.header = null;
				} else {
					/*
					 * 长连接
					 */
					con.buffer = new StringBuilder();
					con.tempbuffer = "";
					con.startTime = System.currentTimeMillis();
					con.check_status = 0;
					waitQueue.add(con);
					con.header = new ArrayList<Header>();
				}
			} catch (Exception e) {
				//System.out.println(e.getClass().getName());
				if(!e.getClass().getName().equals("java.net.SocketException")){
					onerror(e);
				}
				if(!con.s.isClosed()){
					try {
						PrintWriter pw = new PrintWriter(con.s.getOutputStream());
						pw.println("HTTP/1.1 500 Internal Server Error");
						pw.println(ServerHeader);
						pw.println("Connection: close");
						pw.println();
						if(DEBUG){
							e.printStackTrace(pw);
						}
						pw.flush();
						con.s.close();
						synchronized(con.IP){
							con.IP.connect--;
						}
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
					}
				}
			}
			con = null;
			running--;
		}
		private int handler() throws Exception 
		{
			// TODO 自动生成的方法存根
			/*
			 * 初始化
			 */
			this.setPriority(Thread.MAX_PRIORITY);
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(con.buffer.toString().getBytes())));
			con.buffer = null;
			con.tempbuffer = null;
			PrintWriter pw = new PrintWriter(con.s.getOutputStream());
			//pw.write("HTTP/1.1 200 OK\n\nHello.");
			String /*ver = "",*/uri = "",method = "";
			String line = br.readLine();
			int i = 0;
			/*
			 * 解析
			 */
			for(char ch : line.toCharArray()){
				if(ch==' '){
					i++;
					continue;
				}
				if(i==0){
					method += ch;
				} else if(i==1){
					uri += ch;
				}/* else {
					ver += ch;
				}*/
			}
			if(i<=1){
				return error(pw,400,"Bad Request",null);
			}
			if(!method.equals("GET") && !method.equals("POST") && !method.equals("HEAD")){
				return error(pw,405,"Method Not Allowed",null);
			}
			/*
			 * 解析Uri和Query
			 */
			String uri1 = "",query = "";
			i = 0;
			for(char ch : uri.toCharArray()){
				if(ch=='?' && i==0){
					i++;
					continue;
				}
				if(i==0){
					uri1 += ch;
				} else {
					query += ch;
				}
			}
			uri = uri1;
			uri1 = null;
			/*
			 * 危险检测
			 */
			for(String s : dangerWord){
				if(s.equals(uri) || uri.endsWith(s)){
					return error(pw,403,"Forbidden",null);
				}
			}
			
			/*
			 * 解析HTTP头
			 */
			for(;;)
			{
				line = br.readLine();
				if(line==null){
					break;
				}
				i = 0;
				StringBuilder name = new StringBuilder(),value = new StringBuilder();
				for(char ch : line.toCharArray()){
					if((ch==':' && i==0) || (ch==' ' && i==1)){
						i++;
						continue;
					}
					if(i==0){
						name.append(ch);
					} else {
						value.append(ch);
					}
				}
				if(i<=1){
					return error(pw,400,"Bad Request",null);
				}
				Header h = new Header();
				h.name = name.toString().toLowerCase();
				h.value = value.toString();
				con.header.add(h);
			}
			String host = header("Host");
			if(host==null){
				host = header("X-Online-Host");
			}
			if(host==null || host.trim().length()==0) {
				return error(pw,403,"Forbidden","Host error!");
			}
			/*
			 * 拒绝不声明数据大小的POST请求
			 */
			if(method.equals("POST")){
				if(header("Content-Length")==null){
					return error(pw,411,"Length Required",null);
				}
				if(Integer.parseInt(header("Content-Length")) > ServerConfig.MAX_POST_DATA){
					return error(pw,411,"Length Required",null);
				}
			}
			/*
			 * 读取Host
			 */
			com.uxcontry.geekserver.ServerData.Host thisHost = null;
			com.uxcontry.geekserver.ServerData.VirtualHost vhost = null;
			for(com.uxcontry.geekserver.ServerData.VirtualHost vh : ServerData.virtualHost){
				for(com.uxcontry.geekserver.ServerData.Host h : vh.host){
					if(h!=null && h.name!=null){
						if(h.name.equals(host)){
							vhost = vh;
							thisHost = h;
							break;
						}
					}
				}
			}
			if(vhost == null){
				return error(pw,403,"Forbidden","Host is not bind!");
			}
			if(vhost.status==1){
				return error(pw,403,"Forbidden","Host is closed!");
			}
			if(vhost.checkBrowser){
				if(header("User-Agent")==null || header("Accept")==null){
					return error(pw,403,"Forbidden","Browser check failed.");
				}
			}
			/*
			 * 处理/server-bin/
			 */
			if(uri.startsWith("/server-bin")) {
				/*
				 * 调试
				 */
				yield();
				pw.println("HTTP/1.1 200 OK");
				pw.println(ServerHeader);
				pw.println("Cache-Control: no-cache");
				pw.println("Connection: close");
				pw.println();
				pw.flush();
				if(uri.startsWith("/server-bin/debug")){
					if(!DEBUG){
						pw.println("Debug mode not enabled");
					} else {
						pw.println("Status: "+((running<=100)?"Normal":"Busy"));
						pw.println("Total Connection: "+total);
						pw.println("Running Thread: "+running);
						pw.println("Wait Thread: "+waiting);
						pw.println("Total Cache: "+AllCache.size());
						pw.println();
						pw.println(ServerHeader);
					}
					pw.flush();
				} else if(uri.equals("/server-bin/about")) {
					pw.flush();
					con.s.getOutputStream().write(ServerConfig.welcomeContent);
					con.s.getOutputStream().flush();
				} else if(uri.equals("/server-bin/check")) {
					boolean reslut = check();
					if(reslut)
						pw.println("Check finish,No Error");
					else
						pw.println("Check finish,Have some error!");
				} else if(uri.equals("/server-bin/server.js")) {
					pw.println();
				} else if(uri.equals("/server-bin/clearCache")){
					if(DEBUG){
						AllCache.clear();
						pw.println("Finish");
					} else {
						pw.println("DEBUG Mode not enabled");
					}
				} else {
					pw.println("It work!");
				}
				pw.flush();
				return 0;
			}
			
			
			if(vhost.root==null || thisHost.dir==null){
				return error(pw,404,"Not Found","The requested file does not exist!");
			}
			/*
			 * 是否长连接
			 */
			boolean keepalive = (header("Connection")!=null && header("Connection").toLowerCase().equals("keep-alive")) && vhost.canKeepAlive && con.max>0;
			/*
			 * 不长连接就释放输入缓冲区
			 */
			if(!keepalive){
				con.s.shutdownInput();
			}
			/*
			 * 处理转发
			 */
			String url = vhost.redirect.get(uri);
			if(url!=null){
				pw.println("HTTP/1.1 301 Moved Permanently");
				pw.println(ServerHeader);
				pw.println("Location: "+url);
				if(keepalive){
					pw.println("Keep-Alive: timeout=10, max="+con.max);
					con.max--;
					pw.println("Connection: keep-alive");
				} else {
					pw.println("Connection: close");
				}
				pw.println();
				pw.flush();
				return keepalive?1:0;
			}
			/*
			 * 处理文件
			 */
			String path = vhost.root + ((!(vhost.root.endsWith("/") && thisHost.dir.startsWith("/")))?"/":"") + thisHost.dir + uri;
			File f = new File(path);
			if(f.isDirectory()){
				boolean find = false;
				for(String s : vhost.defalut){
					if(new File(path+"/"+s).exists() && f.canRead()){
						f = new File(path + "/" + s);
						find = true;
						break;
					}
				}
				if(!find){
					for(String s : defalut){
						if(new File(path+"/"+s).exists() && f.canRead()){
							f = new File(path + "/" + s);
							find = true;
							break;
						}
					}
				}
				if(!find){
					return error(pw,404,"Not Found","The requested file does not exist!");
				}
			}
			if(!f.exists() || !f.canRead()){
				f = new File(path+".html");
			}
			
			if(!f.exists()){
				return error(pw,404,"Not Found","The requested file does not exist!");
			}
			if(!f.canRead()){
				return error(pw,403,"Forbidden","You haven't permission to browse!");
			}
			/*
			 * 文件过大
			 */
			if(f.length() >= ServerConfig.MAX_SEND_FILE){
				return error(pw,403,"Forbidden","You haven't permission to browse!");
			}
			
			/*
			 * 生成返回头
			 */
			boolean useZip = false;
			boolean writeData = !method.equals("HEAD") && f.length()>0;
			int writeLen = 0;
			if(method.equals("POST")) {
				if(header("Content-Length")==null){
					return error(pw,411,"Length Required",null);
				} else {
					keepalive = false;
				}
			}
			
			/*
			 * 检查是否一致
			 */
			String etag = String.format("\"GS-%x\"", f.lastModified());
			if(header("If-None-Match") != null){
				String metag = header("If-None-Match");
				if(metag.equals(etag)) {
					return  notModified(pw,"Etag: "+etag,keepalive);
				}
			}
			/*
			 * 确定是否压缩传输和缓存
			 */
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayInputStream cache = getCache(f);
			InputStream data = null;
			String contentRange = null;
			String encode = null;
			if(header("Accept-Encoding")!=null){
				//决定是否进行zip压缩
				if(header("Accept-Encoding").indexOf("gzip")!=-1 && header("Ranges")==null && f.length() <= ServerConfig.MAX_ZIP_FILE && f.length() >= ServerConfig.MIN_ZIP_FILE && header("Ranges")==null){
					useZip = true;
					encode = "gzip";
				}
			}
			if(cache!=null){
				data = cache;
			} else {
				data = new FileInputStream(f);
			}
			if(useZip){
				if(encode.equals("gzip")){
					writeGzipStream(bos,data);
					writeLen = bos.size();
				}
			} else {
				bos.close();
				bos = null;
				writeLen = (int) f.length();
			}
			/*
			 * 生成响应头
			 */
			if(header("Range")!=null) {
				/*
				 * 断点续传
				 */
				int[] ranges = parseRanges(header("Range"));
				if(ranges==null) {
					return error(pw,416,"Requested Range Not Satisfiable","Range error!");
				} else{
					if(ranges[0]<=0 || ranges[0]>=f.length()){
						return error(pw,416,"Requested Range Not Satisfiable","Range error!");
					}
					if(ranges.length==1) {
						data.skip(ranges[0]);
						writeLen -= ranges[0];
					} else {
						if(ranges[1]<=ranges[0] || ranges[1]>=f.length()){
							return error(pw,416,"Requested Range Not Satisfiable","Ranges error!");
						}
						data.skip(ranges[0]);
						writeLen = ranges[1] - ranges[0];
					}
					if(ranges[0] == ranges[1]){
						pw.println("HTTP/1.1 204 No Content");
					} else {
						pw.println("HTTP/1.1 206 Partial Content");
					}
					contentRange = makeContentRanges("bytes",ranges[0],(ranges.length==1)?(int)f.length():ranges[1],(int)f.length());
				}
			} else {
				if(f.length()==0 && ServerConfig.RETURN_204_NO_CONTENT){
					pw.println("HTTP/1.1 204 No Content");
				} else {
					pw.println("HTTP/1.1 200 OK");
				}
				
			}
			
			pw.println(ServerHeader);
			pw.println("Accept-Ranges: bytes");
			pw.flush();
			/*
			 * 生成HTTP头
			 */
			// encoding
			if(useZip){
				pw.println("Content-Encoding: "+encode);
			}
			// mime
			String type = null;
			type = vhost.mime.get(f.getName().substring(f.getName().lastIndexOf(".")+1));
			if(type!=null){
				pw.println("Content-Type: "+type);
			} else {
				type = mimeTable.get(f.getName().substring(f.getName().lastIndexOf(".")));
				if(type!=null) {
					pw.println("Content-Type: "+type);
				} else {
					type = mime.getContentTypeFor(f.getName());
					if(type!=null) {
						pw.println("Content-Type: "+type);
					}
				}
			}
			// length
			pw.println("Content-Length: "+writeLen);
			//Range
			if(contentRange!=null){
				pw.println("Content-Range: "+contentRange);
			}
			//Etag
			if(etag != null){
				pw.println("Etag: "+etag);
			}
			
			/*
			 * 生成调试头
			 */
			if(DEBUG){
				if(cache!=null){
					pw.println("X-Memory-Cache: HIT");
				} else {
					pw.println("X-Memory-Cache: MISS");
				}
			}
			
			/*
			 * 添加缓存
			 */
			if(cache==null){
				if(f.length() <= ServerConfig.MAX_CACHE_FILE && f.length()+cacheUsed <= ServerConfig.MAX_CACHE_USED && ServerConfig.Enable_Cache){
					Timer.setTimeout(new CacheAddTask(f), 2);
				}
			}
			/*
			 * 长连接
			 */
			if(keepalive){
				pw.println("Keep-Alive: timeout=10, max="+con.max);
				con.max--;
				pw.println("Connection: keep-alive");
			} else {
				pw.println("Connection: close");
			}
			pw.println();
			pw.flush();
			
			/*
			 * 开始返回数据
			 */
			if(writeLen >= 100 * 1024 * 1024){
				setPriority(Thread.MAX_PRIORITY-3);
			} else {
				setPriority(Thread.MAX_PRIORITY-2);
			}
			
			if(writeData){
				if(useZip){
					ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
					writeStream(con.s.getOutputStream(),bis);
					bis.close();
				} else {
					writeStream(con.s.getOutputStream(),data,writeLen);
				}
			}
			con.s.getOutputStream().flush();
			/*
			 * 释放无用IO
			 */
			if(bos!=null){
				bos.close();
			} 
			if(data!=null){
				data.close();
			}
			if(cache!=null) {
				cache.close();
			}
			/*
			 * 返回是否长连接
			 */
			return (keepalive)?1:0;
		}
		public class Header{
			public String name,value;
		}
		public String header(String name){
			name = name.toLowerCase();
			for(Header h : con.header){
				if(h.name.equals(name)){
					return h.value;
				}
			}
			return null;
		}
		/*
		 * 返回304
		 */
		public int notModified(PrintWriter pw,String addheader,boolean keepalive){
			pw.println("HTTP/1.1 304 Not Modified");
			pw.println("XF-Key: "+XKey);
			pw.println(ServerHeader);
			if(addheader!=null){
				pw.println(addheader);
			}
			if(keepalive){
				pw.println("Keep-Alive: timeout=10, max="+con.max);
				con.max--;
				pw.println("Connection: keep-alive");
			} else {
				pw.println("Connection: close");
			}
			pw.println();
			pw.flush();
			return keepalive?1:0;
		}
	}
	
	/*
	 * IP记录
	 */
	public class IP{
		public String address;
		public volatile int connect = 0;
		/*
		 * 0为未验证
		 * 1为已发送验证
		 * 2为已验证
		 */
		public int status = 0;
		public int con10 = 0;
	}
	/*
	 * 连接
	 */
	public class Connection{
		public Socket s;
		public IP IP;
		public InputStream content;
		public StringBuilder buffer = new StringBuilder();
		public String tempbuffer= "";
		public long time;
		public int shouldRead;
		public long startTime;
		public List<Header> header = new ArrayList<Header>();
		public String addHeader;
		public int max = ServerConfig.MAX_KEEPALIVE_CONNECTION;
		public int check_status = 0;
	}
	/*
	 * 内存缓存
	 */
	public int cacheUsed = 0;
	public class Cache{
		public String path;
		public byte[] content;
		public long last;
	}
	/*
	 * 查找缓存
	 */
	public ByteArrayInputStream getCache(File f)
	{
		for(Object obj : AllCache.toArray()){
			Cache c = (Cache) obj;
			if(c!=null){
				if(c.path.equals(f.getAbsolutePath())){
					if(c.last == f.lastModified()){
						return new ByteArrayInputStream(c.content);
					} else {
						cacheUsed -= c.content.length;
						AllCache.remove(c);
						return null;
					}
				}
			}
		}
		return null;
	}
	
	/*
	 * 添加缓存
	 */
	public void registerCache(File f) throws Exception
	{
		for(Object obj : AllCache.toArray()){
			Cache c = (Cache) obj;
			if(c.path.equals(f.getAbsolutePath())){
				if(c.last == f.lastModified()){
					return;
				} else {
					AllCache.remove(c);
					break;
				}
			}
		}
		if(f.length()+cacheUsed <= ServerConfig.MAX_CACHE_USED){
			Cache c = new Cache();
			c.path = f.getAbsolutePath();
			c.last = f.lastModified();
			c.content = new byte[(int) f.length()];
			new FileInputStream(f).read(c.content);
			cacheUsed += c.content.length;
			AllCache.add(c);
		}
	}
	/*
	 * 生成URL
	 */
	public String makeUrl(boolean https,String host,String uri,String query)
	{
		String ret = https?"https://":"http://";
		ret += host;
		if(!uri.startsWith("/")){
			ret += "/";
		}
		ret += uri;
		if(query!=null){
			ret += "?" + query;
		}
		return ret;
	}
	
	/*
	 * 简单的错误处理
	 */
	public int error(PrintWriter pw,int code,String status,String title)
	{
		//new Exception().printStackTrace();
		String ret = "<html><head><title>"+status+"</title></head><body><h2>Somthing error:</h2><h4><font color=\"red\">"+((title==null)?status:title)+"</font></h4><h4>You can <a href=\"javascript:location.reload();\">try again</a> or support for GeekServer!</h4><div align=\"center\"><hr/>Geek Server</div></body></html>";
		pw.print("HTTP/1.1 ");
		pw.print(code);
		pw.print(" ");
		pw.println(status);
		pw.println("XF-Key: "+XKey);
		pw.println(ServerHeader);
		pw.println("Cache-Control: no-cache");
		pw.println("Content-Length: "+ret.length());
		pw.println("Connection: close");
		pw.println();
		pw.flush();
		pw.print(ret);
		pw.flush();
		ret = null;
		return 0;
	}
	/*
	 * 获取最后更改日期
	 */
	public String stampToDate(long lt){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return "\""+res+"\"";
    }
	/*
	 * 从输出流写到输入流
	 */
	public void writeStream(OutputStream os,InputStream is) throws Exception
	{
		byte[] buffer = new byte[128 * 1024];
		int i = 0;
		for(;(i = is.read(buffer))!=-1;){
			os.write(buffer,0,i);
			os.flush();
		}
	}
	/*
	 * 写入指定字节
	 */
	public void writeStream(OutputStream os,InputStream is,int len) throws Exception
	{
		byte[] buffer = new byte[128 * 1024];
		int i = 0;
		int i1 = len;
		for(;(i = is.read(buffer))!=-1;){
			if(i1 >= i){
				os.write(buffer,0,i);
			} else {
				os.write(buffer, 0, i1);
				os.flush();
				return;
			}
			i1 -= i;
			os.flush();
		}
	}
	/*
	 * 从输出流压缩写到输入流
	 */
	public void writeGzipStream(OutputStream os,InputStream is) throws Exception
	{
		   GZIPOutputStream gos = new GZIPOutputStream(os);
		   int count;  
		   byte data[] = new byte[1024 * 1024];  
		   while ((count = is.read(data)) != -1) {  
		       gos.write(data, 0, count);  
		       Thread.yield();
		   }  
		   gos.finish();
		   gos.flush();
		   gos.close();
	}
	/*
	 * 解析Ranges
	 */
	public int[] parseRanges(String ranges) throws NumberFormatException
	{
		if(!ranges.startsWith("bytes=")){
			return null;
		}
		int i = 0;
		String start = "",end = "";
		for(char ch : ranges.toCharArray()){
			if((ch=='=' && i==0) || (ch=='-' && i==1)){
				i++;
				continue;
			}
			if(i==1) {
				start += ch;
			} else if(i>=2) {
				end += ch;
			}
		}
		if(!end.equals("")){
			return new int[]{Integer.parseInt(start),Integer.parseInt(end)};
		} else {
			return new int[]{Integer.parseInt(start)};
		}
	}
	/*
	 * 生成Content-Ranges
	 */
	public String makeContentRanges(String unit,int start,int end,int size){
		return "Content-Ranges: "+unit+" "+start+"-"+end+"/"+size;
	}
	/*
	 * 解析Cookies
	 */
	public HashMap<String,String> parseCookie(String cookie)
	{
		if(cookie==null) return null;
		HashMap<String,String> ret = new HashMap<String,String>();
		String name = "",value = "";
		for(String chars : cookie.split(";"))
		{
			int i = 0;
			for(char ch : chars.trim().toCharArray()){
				if(ch=='=' && i==0){
					i++;
					continue;
				}
				if(i==0){
					name += ch;
				} else {
					value += ch;
				}
			}
			ret.put(name, value);
			name = value = "";
		}
		return ret;
	}
	/*
	 * 添加缓存的任务
	 */
	public class CacheAddTask implements Runnable{
		private File f;
		public CacheAddTask(File f){
			this.f = f;
		}
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			try {
				registerCache(f);
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
			}
		}
		
	}
	/*
	 * 退出钩子
	 */
	public class destroyThread extends Thread
	{
		public void run()
		{
			for(ServerSocket ss : serverSocketList){
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}
	}
}
