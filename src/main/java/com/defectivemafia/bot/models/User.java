package com.defectivemafia.bot.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.defectivemafia.bot.App;

public class User extends Model {
	public String id;
	public List<Identity> identities;
	
	// https://dev.doctormckay.com/topic/433-whats-in-a-steamid/
	
	public static User create(String id) {
		User user = User.getFromCacheOrCreate(id);
		user.save();
		return user;
	}
	public static User find(String id) {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT * from user WHERE discord_id = ? LIMIT 1")
				.bind(values)
				.query();
			
			if (!results.next()) return User.create(id);
			
			User user = User.getFromCacheOrCreate(id);
			
			user.identities = Identity.byUser(user);
			user.dateCreated = results.getTimestamp("date_created");
			user.dateUpdated = results.getTimestamp("date_updated");
			
			return user;
		} catch (SQLException e) {
			App.Log.e("User", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	private static User getFromCacheOrCreate(String id) {
		User user = (User) cache.getCached(User.class.getSimpleName(), id);
		if (user == null) {
			App.Log.d("User", "adding user to cache");
			user = new User(id);
			cache.setCache(user, id);
		} else {
			App.Log.d("User", "using cached user");
		}
		return user;
	}
	
	public User(String discordId) {
		this.id = discordId;
	}
	public void save() {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT COUNT(*) as total FROM " + table() + " WHERE discord_id = ? LIMIT 1")
				  .bind(values)
				  .query();
			results.next();
			
			if (results.getInt(1) > 0) {
				/*Log.d(TAG, "updating User");
				Object[] allValues = {id};
				App.DB.prepare("UPDATE " + table() + " SET WHERE discord_id = ?")
				  .bind(allValues)
				  .update();*/
			} else {
				Log.d(TAG, "creating User");
				App.DB.prepare("INSERT INTO " + table() + " (discord_id) VALUES (?)")
				  .bind(values)
				  .update();
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
