package com.niubicloud.test;

import java.io.IOException;

import com.niubicloud.base.Controller;
import com.niubicloud.base.Hook;
import com.niubicloud.service.loader.PackageLoader;
import com.niubicloud.service.loader.PathLoader;
import com.niubicloud.service.MainService;

public class ProxyEntry extends Hook {
	public static void main(String args[]) {
		try {
			MainService server = new MainService();

			new PackageLoader(server,"com.niubicloud.test");
			server.pathLoader = new PathLoader("D:\\test");
			server.bind(8081);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Hook.add("error", new ProxyEntry());
	}

	@Override
	public boolean execute(Controller controller, String name, Object[] args) {
		// TODO Auto-generated method stub
		if(args[2] != null) {
			((Error)args[2]).printStackTrace();
		}
		return true;
	}
}
