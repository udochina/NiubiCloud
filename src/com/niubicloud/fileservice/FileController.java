package com.niubicloud.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.niubicloud.Config;
import com.niubicloud.anno.ControllerMethod;
import com.niubicloud.anno.RuntimeController;
import com.niubicloud.base.Controller;
import com.niubicloud.loader.PathLoader;
import com.niubicloud.loader.PathLoader.UnfoundException;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;

@RuntimeController
public class FileController extends Controller {
	String baseDir;
	
	public FileController(String baseDir) {
		this.baseDir = baseDir;
	}
	
	private String makeEtag(File file) {
		return new StringBuilder().append('"').append(file.length()).append('v').append(file.lastModified()).append('"').toString();
	}
	
	private static String getSuffixName(String path) {
		 String fileName = path.substring(path.lastIndexOf("\\")+1);
	     String[] strArray = fileName.split("\\.");
	     int suffixIndex = strArray.length -1;
	     return strArray[suffixIndex];
	}
	
	@ControllerMethod(name="*", GET=true, HEAD=true)
	public void handle(Request req,Respone resp) {
		if(req.path.contains("../")) {
			resp.code = 403;
			return;
		}
		String path = req.path;
		if("".equals(path)) {
			path = "/" + req.fileName;
		} else if(path.endsWith("/")) {
			path += req.fileName;
		}
		String filePath = baseDir + path;
		File file = new File(filePath);
		if(file.exists() == false && req.fileName.contains(".") == false) {
			file = new File(filePath + ".html");
		}
		if(file.exists()) {
			if(file.canRead()) {
				resp.code = 200;
				if(Config.DEAFULT_ACCEP_REAGES)
					resp.header("Accept-Ranges", "bytes");
				String etag = makeEtag(file);
				String ifnone = req.header("If-None-Match");
				resp.header("Etag",etag);
				
				if(ifnone != null) {
					if(etag.equals(ifnone)) {
						resp.code = 304;
						return;
					}
				}
				
				try {
					resp.finish(new FileInputStream(file), true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					resp.code = 404;
					throw new PathLoader.UnfoundException();
				}
			} else {
				resp.code = 403;
			}
		} else {
			resp.code = 404;
			throw new PathLoader.UnfoundException();
		}
	}
}
