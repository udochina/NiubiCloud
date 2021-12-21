package com.niubicloud.test;

import java.io.IOException;
import java.sql.SQLException;

import com.niubicloud.anno.ControllerMethod;
import com.niubicloud.anno.RuntimeController;
import com.niubicloud.base.Controller;
import com.niubicloud.base.Model;
import com.niubicloud.database.SQLBuilder;
import com.niubicloud.database.SQLDatabase;
import com.niubicloud.type.Request;
import com.niubicloud.type.Respone;

@RuntimeController(name="api")
public class TestController extends Controller {
	
	@ControllerMethod(contentType="text/html; charset=utf-8",GET=true,POST=true)
	public void index(Request req,Respone resp) {
		try {
			SQLDatabase db = new SQLDatabase("jdbc:mysql://127.0.0.1:3306/test","root","xhsw2016");
			// db.where("a","=","1");
			db.setTabName("test1");
			//db.where("id","=",2);
			
			Model[] model = db.select(TestModel.class);
			buffer().write(model);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resp.setcookie("a", "b", null, null, 3600, false, false);
		//buffer().write(req.path);
		//buffer().write("Hello world");
		
		//throw new RuntimeException();
	}
	
	@ControllerMethod(contentType="text/html; charset=utf-8",GET=true,POST=true)
	public void hello(Request req,Respone resp) {
		buffer().write("Hello world");
	}
}
