package com.defectivemafia.bot.models;

import java.sql.Timestamp;
import java.util.List;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.database.ModelCache;

import net.zrx.Logger;

public abstract class Model {
	protected static final ModelCache cache = ModelCache.getInstance();
	protected Logger Log = App.Log;
	protected final String TAG = getClass().getSimpleName();
	public int id = -1;
	public Timestamp dateCreated;
	public Timestamp dateUpdated;
	
	public static Model create(String id) {
		return null;
	}
	public static Model create(int id) {
		return null;
	}
	public static Model find(String id) {
		return null;
	}
	public static Model find(int id) {
		return find("" + id);
	}
	public static List<? extends Model> where(String sqlWhere) {
		return null;
	}
	protected String table() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	public abstract void save();
	public int remove() {
		return App.DB.update("DELETE FROM " + table() + " WHERE id = " + id);
	}
}
