package net.zrx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private short logLevel = 2;
	public static short DEBUG = 0;
	public static short INFO = 1;
	public static short ERROR = 2;
	public static short SILENT = 3;
	
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public Logger() {
		
	}
	public Logger(short lvl) {
		this.logLevel = lvl;
	}
	
	public void d(String tag, Object obj) {
		if (logLevel > 0) return;
		if (obj == null) obj = "null";
		System.out.println(df.format(new Date()) + " [" + tag + "/d] " + obj.toString());
	}
	public void i(String tag, Object obj) {
		if (logLevel > 1) return;
		if (obj == null) obj = "null";
		System.out.println(df.format(new Date()) + " [" + tag + "/i] " + obj.toString());
	}
	public void e(String tag, Object obj) {
		if (logLevel > 2) return;
		if (obj == null) obj = "null";
		System.err.println(df.format(new Date()) + " [" + tag + "/e] " + obj.toString());
	}
	public void d(Object obj) {
		d("NOTAG", obj);
	}
	public void i(Object obj) {
		i("NOTAG", obj);
	}
	public void e(Object obj) {
		e("NOTAG", obj);
	}
}
