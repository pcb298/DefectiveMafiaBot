package net.zrx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class KeyValueConfig {
	private String configFile = "config.properties";
	private Properties props;
	private InputStream in;
	private OutputStream out;
	
	public KeyValueConfig(String[] keys) {
		try {
			load(keys);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public KeyValueConfig(String[] keys, String path) {
		configFile = path;
		try {
			load(keys);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void load(String[] keys) throws IOException {
		try {
			File nf = new File(configFile);
			props = new Properties();
			if (nf.exists()) {
				in = new FileInputStream(configFile);
				System.out.println("reading config file: " + configFile);
				props.load(in);
			} else {
				nf.createNewFile();
				out = new FileOutputStream(configFile);
				for (String key : keys) {
					props.setProperty(key, "");
				}
				System.out.println("writing config file: " + configFile);
				props.store(out, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) in.close();
			if (out != null) out.close();
		}
	}
	
	public boolean setValue(String key, String value) {
		if (props != null) props.setProperty(key, value);
		return true;
	}
	public boolean setValue(String key, Number value) {
		if (props != null) props.setProperty(key, value.toString());
		return true;
	}
	public boolean setValue(String key, boolean value) {
		if (props != null) props.setProperty(key, value ? "true" : "false");
		return true;
	}
	
	public String getString(String key) {
		if (props == null) return "could not get props";
		return props.getProperty(key);
	}
	public boolean getBoolean(String key) {
		if (props == null) return false;
		return props.getProperty(key).equals("true");
	}
}
