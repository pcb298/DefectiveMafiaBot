package com.defectivemafia.bot.database;

import java.util.HashMap;
import java.util.Map;

import com.defectivemafia.bot.models.Model;

import net.zrx.LRUList;

public class ModelCache {
	private static ModelCache cache;
	public static ModelCache getInstance() {
		if (cache == null) cache = new ModelCache();
		return cache;
	}
	
	private Map<String, LRUList<Model>> caches;
	
	public ModelCache() {
		caches = new HashMap<String, LRUList<Model>>();
	}
	
	public Model getCached(String model, String id) {
		LRUList<Model> list = caches.get(model);
		if (list == null) return null;
		return list.get(id);
	}
	public Model getCached(String model, int id) {
		return getCached(model, "" + id);
	}
	public Model searchCache(String model, String search) {
		LRUList<Model> list = caches.get(model);
		if (list == null) return null;
		return list.search(search);
	}
	public void setCache(Model model, String id) {
		String name = model.getClass().getSimpleName();
		LRUList<Model> list = caches.get(name);
		if (list == null) {
			list = new LRUList<Model>(10);
			caches.put(name, list);
		}
		list.set(id, model);
	}
	public void setCache(Model model, int id) {
		setCache(model, "" + id);
	}
}
