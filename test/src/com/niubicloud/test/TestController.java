package com.niubicloud.test;

import com.niubicloud.base.Controller;
import com.niubicloud.base.Model;
import com.niubicloud.base.anno.ControllerMethod;
import com.niubicloud.base.anno.RuntimeController;
import com.niubicloud.database.SQLDatabase;
import com.niubicloud.service.type.Request;
import com.niubicloud.service.type.Respone;

import java.sql.SQLException;

@RuntimeController(name="api")
public class TestController extends Controller {
	
	@ControllerMethod(contentType="text/html; charset=utf-8",GET=true,POST=true)
	public void index(Request req,Respone resp) {
		try {
			SQLDatabase db = new SQLDatabase("jdbc:mysql://127.0.0.1:3306/test", "root", "123456");
			// db.where("a","=","1");
			db.setTabName("test1");
			//db.where("id","=",2);
			Model[] model = db.select(TestModel.class);
			for(Model model1 : model) {
				buffer().write(((TestModel)model1).value);
				buffer().write("\n");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// resp.setcookie("a", "b", null, null, 3600, false, false);
		//buffer().write(req.path);
		//buffer().write("Hello world");
		
		//throw new RuntimeException();
	}
	
	@ControllerMethod(contentType="text/html; charset=utf-8",GET=true,POST=true)
	public void hello(Request req,Respone resp) {
		buffer().write("Hello world");
		var classloader = ClassLoader.getSystemClassLoader();
	}
}
