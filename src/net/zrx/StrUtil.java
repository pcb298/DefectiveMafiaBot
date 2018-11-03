package net.zrx;

public class StrUtil {
	public static String camelize(String str) {
		String[] parts = str.split("_");
		if (parts.length == 0) return "";
		String res = parts[0];
		for (int i = 1; i < parts.length; i++) {
			res += ("" + parts[i].charAt(0)).toUpperCase() + parts[i].substring(1);
		}
		return res;
	}
	
	public static String snakeize(String str) {
		if (str.length() == 0) return "";
		String res = ("" + str.charAt(0)).toLowerCase();
		for (String s : str.substring(1).split("")) {
			if (s.matches("[A-Z]")) res += '_';
			res += s.toLowerCase();
		}
		return res;
	}
}
