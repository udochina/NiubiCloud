package com.niubicloud.service.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.niubicloud.Config;
import com.niubicloud.base.anno.ControllerMethod;
import com.niubicloud.base.anno.RuntimeController;
import com.niubicloud.base.Controller;
import com.niubicloud.service.loader.PathLoader;
import com.niubicloud.service.type.Request;
import com.niubicloud.service.type.Respone;

@RuntimeController
public class FileController extends Controller {
	String baseDir;
	
	public FileController(String baseDir) {
		this.baseDir = baseDir;
	}

	/*
	* 基于"长度+最后修改时间"做的简单ETag
	*/
	private String makeEtag(File file) {
		return new StringBuilder().append('"').append(file.length()).append('v').append(file.lastModified()).append('"').toString();
	}

	/*
	*	静态文件处理器
	 */
	@ControllerMethod(name="*", GET=true, HEAD=true)
	public void handle(Request req, Respone resp) {
		if(req.path.contains("/../")) {
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
		File file1 = null;
		if(file.exists() == false && (file1 = new File(filePath + ".html")).exists()) {
			file = file1;
		}
		if(file.exists() == false && (file1 = new File(filePath + ".htm")).exists()) {
			file = file1;
		}
		if(file.exists()) {
			if(file.canRead()) {
				resp.code = 200;
				if(Config.DEAFULT_ACCEP_REAGES)
					resp.header("Accept-Ranges", "bytes");
				resp.header("Content-Type",FileUtil.getMIME(FileUtil.getSuffixName(file.getAbsolutePath())));
				String etag = makeEtag(file);
				String ifnone = req.header("If-None-Match");
				resp.header("Etag",etag);
				
				if(ifnone != null) {
					if(etag.equals(ifnone)) {
						resp.code = 304;
						return;
					}
				}

				// resp.noDelay();

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
