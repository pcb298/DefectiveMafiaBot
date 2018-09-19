package com.defectivemafia.bot.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.defectivemafia.bot.App;

public class Channel extends Model {
	public static Channel create(String key, String value) {
		Channel c = new Channel();
		c.name = key;
		c.channelId = value;
		c.save();
		cache.setCache(c, key);
		return c;
	}
	
	public static List<Channel> all() {
		List<Channel> list = new LinkedList<Channel>();
		try {
			ResultSet results = App.DB.query("SELECT * FROM channel");
			while (results.next()) {
				Channel c = new Channel();
				c.id = results.getInt("id");
				c.name = results.getString("name");
				c.channelId = results.getString("channel_id");
				c.dateCreated = results.getTimestamp("date_created");
				c.dateUpdated = results.getTimestamp("date_updated");
				list.add(c);
			}
		} catch (SQLException e) {
			App.Log.e("Channel", e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	public static Channel find(int key) {
		try {
			Object[] values = {key};
			ResultSet results = App.DB.prepare("SELECT * from channel WHERE id = ? LIMIT 1")
				.bind(values)
				.query();
			if (results.next()) {
				Channel c = getFromCacheOrCreate(key);
				c.id = key;
				c.name = results.getString("name");
				c.channelId = results.getString("channel_id");
				c.dateCreated = results.getTimestamp("date_created");
				c.dateUpdated = results.getTimestamp("date_updated");
				return c;
			} else {
				return null;
			}			
		} catch (SQLException e) {
			App.Log.e("Channel", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static Channel findByName(String name) {
		try {
			Object[] values = {name};
			ResultSet results = App.DB.prepare("SELECT * from channel WHERE name = ? LIMIT 1")
				.bind(values)
				.query();
			if (results.next()) {
				Channel c = getFromCacheOrCreate(results.getInt("id"));
				c.id = results.getInt("id");
				c.name = name;
				c.channelId = results.getString("channel_id");
				c.dateCreated = results.getTimestamp("date_created");
				c.dateUpdated = results.getTimestamp("date_updated");
				return c;
			} else {
				return null;
			}			
		} catch (SQLException e) {
			App.Log.e("Channel", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private static Channel getFromCacheOrCreate(int key) {
		Channel c = (Channel) cache.getCached(Channel.class.getSimpleName(), key);
		if (c == null) {
			App.Log.d("Channel", "adding channel to cache");
			c = new Channel();
			cache.setCache(c, key);
		} else {
			App.Log.d("Channel", "using cached channel");
		}
		return c;
	}
	
	public String name;
	public String channelId;
	
	public void save() {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT COUNT(*) as total FROM " + table() + " WHERE id = ? LIMIT 1")
				  .bind(values)
				  .query();
			results.next();
			
			if (results.getInt(1) > 0) {
				Log.d(TAG, "updating " + table() + ": " + id);
				Object[] allValues = {name, channelId, id};
				App.DB.prepare("UPDATE " + table() + " SET name = ?, channel_id = ? WHERE id = ?")
				  .bind(allValues)
				  .update();
			} else {
				Log.d(TAG, "creating " + table());
				Object[] createValues = {name, channelId};
				ResultSet keys = App.DB.prepare("INSERT INTO " + table() + " (name, channel_id) VALUES (?, ?)", true)
				  .bind(createValues)
				  .updateWithKeys();
				
				if (keys.next()) {
					id = keys.getInt("id");
				}				
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
