package net.zrx;

import java.util.HashMap;
import java.util.Map;

public class LRUList<T> {
	private int max = 10;
	private Map<String, LRUItem<T>> map = new HashMap<String, LRUItem<T>>();
	
	public LRUList() {}
	public LRUList(int max) {
		this.max = max;
	}
	
	public T get(String id) {
		LRUItem<T> item = map.get(id);
		incrementAge();
		if (item == null) return null;
		return item.read();
	}
	public T search(String search) {
		incrementAge();
		return null;
	}
	public void set(String id, T val) {
		LRUItem<T> item = map.get(id);
		if (item == null) {
			if (map.size() >= max) {
				long maxAge = 0;
				String oldest = null;
				for (String key : map.keySet()) {
					LRUItem<T> i = map.get(key);
					if (i.age > maxAge) {
						maxAge = i.age;
						oldest = key;
					}
				}
				map.remove(oldest);
			}
			item = new LRUItem<T>();
			item.write(val);
			map.put(id, item);
		}
		incrementAge();
	}
	private void incrementAge() {
		for (LRUItem<T> item : map.values()) {
			item.age++;
		}
	}
	class LRUItem<V> {
		public long age = 0;
		private V obj;
		public V read() {
			age = 0;
			return obj;
		}
		public void write(V obj) {
			this.obj = obj;
			age = 0;
		}
	}
}
