package com.defectivemafia.bot.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.defectivemafia.bot.App;

import net.zrx.StrUtil;

public class UserGroup extends Model {
	
	public static UserGroup create(String name, String roleId, int maxCapacity, boolean isPrivate) {
		UserGroup c = new UserGroup();
		c.name = name;
		c.roleId = roleId;
		c.maxCapacity = maxCapacity;
		c.isPrivate = isPrivate;
		c.save();
		cache.setCache(c, name);
		return c;
	}
	
	public static List<UserGroup> all() {
		List<UserGroup> list = new LinkedList<UserGroup>();
		try {
			ResultSet results = App.DB.query("SELECT * FROM user_group");
			while (results.next()) {
				UserGroup c = new UserGroup();
				c.id = results.getInt("id");
				c.name = results.getString("name");
				c.roleId = results.getString("role_id");
				c.maxCapacity = results.getInt("max_capacity");
				c.isPrivate = results.getBoolean("is_private");
				c.dateCreated = results.getTimestamp("date_created");
				c.dateUpdated = results.getTimestamp("date_updated");
				list.add(c);
			}
		} catch (SQLException e) {
			App.Log.e("Group", e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	public static UserGroup findByName(String name) {
		try {
			Object[] values = {name};
			ResultSet results = App.DB.prepare("SELECT * from user_group WHERE name = ? LIMIT 1")
				.bind(values)
				.query();
			if (results.next()) {
				UserGroup c = getFromCacheOrCreate(name);
				c.id = results.getInt("id");
				c.name = name;
				c.roleId = results.getString("role_id");
				c.maxCapacity = results.getInt("max_capacity");
				c.isPrivate = results.getBoolean("is_private");
				c.dateCreated = results.getTimestamp("date_created");
				c.dateUpdated = results.getTimestamp("date_updated");
				return c;
			} else {
				return null;
			}			
		} catch (SQLException e) {
			App.Log.e("Group", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private static UserGroup getFromCacheOrCreate(String key) {
		UserGroup c = (UserGroup) cache.getCached(UserGroup.class.getSimpleName(), key);
		if (c == null) {
			App.Log.d("Group", "adding group to cache");
			c = new UserGroup();
			cache.setCache(c, key);
		} else {
			App.Log.d("Group", "using cached group");
		}
		return c;
	}
	
	public String name;
	public String roleId;
	public int maxCapacity = 0;
	public boolean isPrivate = false;

	@Override
	public void save() {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT COUNT(*) as total FROM " + table() + " WHERE id = ? LIMIT 1")
				  .bind(values)
				  .query();
			results.next();
			
			if (results.getInt(1) > 0) {
				Log.d(TAG, "updating " + table() + ": " + id);
				Object[] allValues = {name, roleId, maxCapacity, isPrivate, id};
				App.DB.prepare("UPDATE " + table() + " SET name = ?, role_id = ?, max_capacity = ?, is_private = ? WHERE id = ?")
				  .bind(allValues)
				  .update();
			} else {
				Log.d(TAG, "creating " + table());
				Object[] createValues = {name, roleId, maxCapacity, isPrivate};
				ResultSet keys = App.DB.prepare("INSERT INTO " + table() + " (name, role_id, max_capacity, is_private) VALUES (?, ?, ?, ?)", true)
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
	
	@Override
	public String table() {
		return StrUtil.snakeize(getClass().getSimpleName());
	}
}
