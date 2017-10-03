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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import javax.activation.MimetypesFileTypeMap;

import com.uxcontry.geekserver.EMHTML.Maker;

/*
 * GeekServer服务器
 */

public class GeekServer {
	public static final boolean DEBUG = true;
	private static final String[] dangerWord = new String[]{"/.htaccess","/.rewrite","/nginx.conf","/httpd.conf",".sql"};
	private static final String[] defalut = new String[]{"index.html","index.htm","index.smhtm"};
	private static final int cgi_timeout = 10 * 1000;
	public static final String ServerHeader = "Server: GeekServer/1.1";
	
	private ServerSocket ss;
	private Queue<Connection> waitQueue = new LinkedBlockingQueue<Connection>();
	public MimetypesFileTypeMap mime = new MimetypesFileTypeMap();
	private ExecutorService handlerThreadPool = Executors.newFixedThreadPool(128); 
	private List<String> disconnectIP = new ArrayList<String>();
	private List<IP> IP = new ArrayList<IP>();
	private List<Cache> AllCache = new ArrayList<Cache>();
	private int total = 0;
	private int connect = 0;
	public GeekServer(){
		try {
			ss = new ServerSocket(80);
			ss.setReceiveBufferSize(1 * 1024);
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			onerror(e);
		}
	}
	public void init()
	{
		int i = 0;
		for(;i<5;i++){
			new AcceptThread().start();
		}
		i = 0;
		for(;i<16;i++){
			new WaitThread().start();
		}
	}
	private void onerror(Exception e){
		if(DEBUG){
			e.printStackTrace();
		}
	}
	public class AcceptThread extends Thread{
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
				total++;
				if(s!=null){
					IP ip = null;
					if((ip = readIP(s.getInetAddress().getHostAddress()))!=null){
						try {
							s.setSendBufferSize(10 * 1024);
						} catch (SocketException e) {
							// TODO 自动生成的 catch 块
							onerror(e);
						}
						Connection c = new Connection();
						c.s = s;
						c.IP = ip;
						c.startTime = System.currentTimeMillis();
						c.IP.connect++;
						waitQueue.add(c);
					}
				}
			}
		}
		public IP readIP(String address){
			for(String s : disconnectIP){
				if(s!=null){
					if(s.equals(address)){
						return null;
					}
				}
			}
			IP ip = null;
			for(IP i : IP){
				if(i!=null){
					if(i.address.equals(address)){
						ip = i;
						break;
					}
				}
			}
			if(ip==null){
				ip = new IP();
				ip.address = address;
				return ip;
			}
			if(ip.connect >= ServerConfig.MAX_IP_CONNECTION){
				return null;
			}
			return ip;
		}
	}
	/*
	 * 等待线程
	 */
	public class WaitThread extends Thread{
		private Connection[] connections = new Connection[128];
		private int len = 0;
		public void run()
		{
			this.setPriority(Thread.MAX_PRIORITY);
			for(;;){
				try {
					handler();
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					onerror(e);
				}
				try {
					sleep(8);
				} catch (InterruptedException e) {
				}
			}
		}
		private void handler() throws Exception {
			// TODO 自动生成的方法存根
			int point = 0;
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
						len--;
						break;
					}
					for(int read = 0;c.s.getInputStream().available() != 0 && read <= 50;read++){
						if(System.currentTimeMillis() - c.startTime >= ServerConfig.Timeout * 1000){
							c.IP.connect--;
							connections[point] = null;
							c.s.close();
							break;
						}
						if(c.s.isClosed()){
							c.IP.connect--;
							connections[point] = null;
							break;
						}
						char ch = (char) c.s.getInputStream().read();
							if(ch=='\n'){
								if(c.tempbuffer.equals("\r")){
									handlerThreadPool.execute(new HandlerThread(c));
									connections[point] = null;
									len--;
									break;
								} else {
									c.buffer.append(c.tempbuffer).append("\n");
									c.tempbuffer = "";
								}
							} else {
								c.tempbuffer += ch;
							}
							yield();
						}
				}
				point++;
			}
		}
	}
	public class HandlerThread extends Thread{
		private Connection con;
		private List<Header> header = new ArrayList<Header>();
		public HandlerThread(Connection c){
			con = c;
		}
		public void run()
		{
			try {
				long start = System.currentTimeMillis();
				int ret = handler();
				System.out.println(System.currentTimeMillis() - start);
				if(ret==0){
					con.s.close();
					con.IP.connect--;
				} else {
					con.buffer = new StringBuilder();
					con.tempbuffer = "";
					con.startTime = System.currentTimeMillis();
					waitQueue.add(con);
				}
				con = null;
				header = null;
				System.gc();
			} catch (Exception e) {
				//System.out.println(e.getClass().getName());
				onerror(e);
				if(!con.s.isClosed()){
					try {
						PrintWriter pw = new PrintWriter(con.s.getOutputStream());
						pw.println("HTTP/1.1 500 Internal Server Error");
						pw.println(ServerHeader);
						pw.println();
						pw.flush();
						con.s.close();
						con.IP.connect--;
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
					}
				}
			}
		}
		private int handler() throws Exception 
		{
			// TODO 自动生成的方法存根
			/*
			 * 初始化
			 */
			this.setPriority(Thread.MAX_PRIORITY-1);
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(con.buffer.toString().getBytes())));
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
			 * 解析HTTP头
			 */
			for(;;){
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
				h.name = name.toString();
				h.value = value.toString();
				header.add(h);
			}
			/*
			 * 读取Host
			 */
			String host = header("Host");
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
			 * 处理/server-bin/
			 */
			if(uri.startsWith("/server-bin")){
				if(uri.equals("/server-bin/about")){
					pw.println("HTTP/1.1 200 OK");
					pw.println(ServerHeader);
					pw.println();
					pw.flush();
					con.s.getOutputStream().write(ServerConfig.welcomeContent);
					con.s.getOutputStream().flush();
				} else if(uri.equals("/server-bin/check")){
					pw.println("HTTP/1.1 200 OK");
					pw.println(ServerHeader);
					pw.println();
					pw.flush();
					pw.println("Normal");
					pw.flush();
				} else {
					pw.println("HTTP/1.1 200 OK");
					pw.println(ServerHeader);
					pw.println("Connection: close");
					pw.println();
					pw.println("It work!");
					pw.flush();
				}
				return 0;
			}
			/*
			 * 处理文件
			 */
			String path = vhost.root + ((!(vhost.root.endsWith("/") && thisHost.dir.startsWith("/")))?"/":"") + thisHost.dir + uri;
			File f = new File(path);
			if(f.isDirectory()){
				boolean find = false;
				for(String s : vhost.defalut){
					if(new File(path+"/"+s).exists()){
						f = new File(path + "/" + s);
						find = true;
						break;
					}
				}
				if(!find){
					for(String s : defalut){
						if(new File(path+"/"+s).exists()){
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
			if(!f.exists()){
				f = new File(path+".html");
			}
			if(!f.exists()){
				f = new File(path+".smhtm");
			}
			if(!f.exists()){
				return error(pw,404,"Not Found","The requested file does not exist!");
			}
			if(!f.canRead()){
				return error(pw,403,"Forbidden","You haven't permission to browse!");
			}
			if(f.length() >= ServerConfig.MAX_SEND_FILE){
				return error(pw,403,"Forbidden","You haven't permission to browse!");
			}
			/*
			 * 处理smhtm
			 */
			if(f.getName().endsWith(".smhtm")){
				Maker.run(f, con.s, con.s.getOutputStream(), con.IP.address, uri, host, query, header("Set-Cookies"), header("User-Agant"), header("Referer"));
				return 0;
			}
			/*
			 * 检查是否一致
			 */
			if(header("If-None-Match")!=null){
				String etag = header("If-None-Match");
				if(etag.equals("\""+f.lastModified()+"-GEEKSERVER\"")){
					//pw.println("Etag: \""+f.lastModified()+"-GEEKSERVER\"");
					return  notModified(pw,"Etag: \""+f.lastModified()+"-GEEKSERVER\"");
				}
			}
			/*
			 * 生成返回头
			 */
			boolean useZip = false;
			boolean keepalive = (header("Connection")!=null && header("Connection").equals("keep-alive"));
			boolean writeData = !method.equals("HEAD");
			long writeLen = 0;
			if(method.equals("POST")){
				if(header("Content-Length")==null){
					return error(pw,411,"Length Required",null);
				} else {
					con.s.getInputStream().skip(Integer.parseInt(header("Content-Length")));
				}
			}
			if(f.length()==0 && ServerConfig.RETURN_204_NO_CONTENT)
				pw.println("HTTP/1.1 204 No Content");
			else
				pw.println("HTTP/1.1 200 OK");
			pw.println(ServerHeader);
			if(header("Accept-Encoding")!=null){
				if(header("Accept-Encoding").indexOf("gzip")!=-1 && f.length() <= ServerConfig.MAX_ZIP_FILE && f.length() >= 512){
					useZip = true;
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayInputStream cache = getCache(f);
			/*
			 * 生成内容信息
			 */
			// encoding
			if(useZip){
				pw.println("Content-Encoding: gzip");
				if(cache!=null)
					writeGzipStream(bos,cache);
				else
					writeGzipStream(bos,new FileInputStream(f));
				writeLen = bos.size();
			} else {
				writeLen = f.length();
			}
			// mime
			String type = null;
			type = vhost.mime.get(f.getName().substring(f.getName().lastIndexOf(".")+1));
			if(type!=null){
				pw.println("Content-Type: "+type);
			} else {
				type = mime.getContentType(f);
				if(type!=null) {
					pw.println("Content-Type: "+type);
				} else {
					pw.println("Conten-Type: file/unknown");
				}
			}
			// length
			pw.println("Content-Length: "+writeLen);
			//Etag
			pw.println("Etag: \""+f.lastModified()+"-GEEKSERVER\"");
			/*
			 * 生成其他头
			 */
			if(cache!=null){
				pw.println("X-Memory-Cache: HIT");
			} else {
				pw.println("X-Memory-Cache: MISS");
				if(f.length() <= ServerConfig.MAX_CACHE_FILE){
					Timer.setTimeout(new CacheAddTask(f), 3);
				}
			}
			if(keepalive){
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
				setPriority(Thread.MAX_PRIORITY-5);
			} else {
				setPriority(Thread.MAX_PRIORITY-2);
			}
			if(writeData){
				if(useZip){
					writeStream(con.s.getOutputStream(),new ByteArrayInputStream(bos.toByteArray()));
				} else {
					if(cache!=null){
						writeStream(con.s.getOutputStream(),cache);
					} else {
						writeStream(con.s.getOutputStream(),new FileInputStream(f));
					}
				}
			}
			//con.s.close();
			return (keepalive)?1:0;
		}
		public class Header{
			public String name,value;
		}
		public String header(String name){
			for(Header h : header){
				if(h.name.equals(name)){
					return h.value;
				}
			}
			return null;
		}
	}
	
	/*
	 * IP记录
	 */
	public class IP{
		public String address;
		public int connect = 1;
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
		public boolean keepAlive = false;
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
					cacheUsed -= c.content.length;
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
	 * 返回304
	 */
	public int notModified(PrintWriter pw,String addheader){
		pw.println("HTTP/1.1 304 Not Modified");
		pw.println(ServerHeader);
		pw.println(addheader);
		pw.println("Connection: close");
		pw.println();
		pw.flush();
		return 0;
	}
	/*
	 * 简单的错误处理
	 */
	public int error(PrintWriter pw,int code,String status,String title)
	{
		String ret = "<html><head><title>"+status+"</title></head><body><h2>Somthing error:</h2><h4><font color=\"red\">"+((title==null)?status:title)+"</font></h4><h4>You can <a href=\"javascript:location.reload();\">try again</a> or support for GeekServer!</h4><div align=\"center\"><hr/>Geek Server</div></body></html>";
		pw.print("HTTP/1.1 ");
		pw.print(code);
		pw.print(" ");
		pw.println(status);
		pw.flush();
		pw.println(ServerHeader);
		pw.println("Cache-Control: private");
		pw.println("Content-Length: "+ret.length());
		pw.println("Connection: close");
		pw.println();
		pw.flush();
		pw.print(ret);
		pw.flush();
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
		byte[] buffer = new byte[10 * 1024];
		int i = 0;
		for(;(i = is.read(buffer))!=-1;){
			os.write(buffer,0,i);
			os.flush();
			Thread.yield();
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
}
