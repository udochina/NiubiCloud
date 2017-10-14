package com.uxcontry.geekserver.debug;

import com.uxcontry.geekserver.NativePage.NativePage;
import com.uxcontry.geekserver.NativePage.NativePageCreater;
import com.uxcontry.geekserver.NativePage.SESSION;

public class TestNativePage extends NativePageCreater {

	@Override
	public NativePage create() {
		// TODO Auto-generated method stub
		return new MyNativePage();
	}
	public class MyNativePage extends NativePage
	{
		@Override
		public void Run() {
			// TODO Auto-generated method stub
			SESSION s = session_start();
			//header("Set-Cookie","abc=abc");
			endHeader();
			String str = (String) SESSION.get("hello"); 
			if(str==null){
				echo("Hello!");
				SESSION.put("hello", "<html><body>hi,<script>document.write(document.cookie);</script></body></html>");
			} else {
				echo(str);
			}
			
		}

		@Override
		public void callback() {
			// TODO Auto-generated method stub
			
		}
	}

}
