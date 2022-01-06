package com.niubicloud.test;

import java.io.IOException;

import com.niubicloud.base.Controller;
import com.niubicloud.base.Hook;
import com.niubicloud.loader.PackageLoader;
import com.niubicloud.loader.PathLoader;
import com.niubicloud.service.MainService;

public class ProxyEntry extends Hook {
	public static void main(String args[]) {
		/*try {
			MainService server = new MainService();

			new PackageLoader(server,"com.niubicloud.test");
			server.pathLoader = new PathLoader("D:\\test");
			server.bind(8081);
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Hook.add("error", new ProxyEntry());*/
		System.out.println(getIMEI());
	}

	private static String getIMEI() {// calculator IMEI
		int r1 = 8600000 + new java.util.Random().nextInt(90000);
		int r2 = 1000000 + new java.util.Random().nextInt(9000000);
		String input = r1 + "" + r2;
		char[] ch = input.toCharArray();
		int a = 0, b = 0;
		for (int i = 0; i < ch.length; i++) {
			int tt = Integer.parseInt(ch[i] + "");
			if (i % 2 == 0) {
				a = a + tt;
			} else {
				int temp = tt * 2;
				b = b + temp / 10 + temp % 10;
			}
		}
		int last = (a + b) % 10;
		if (last == 0) {
			last = 0;
		} else {
			last = 10 - last;
		}
		return input + last;
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
