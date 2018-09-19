package com.defectivemafia.bot.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.defectivemafia.bot.App;

import net.zrx.UniquenessConstraintViolationException;

public class Identity extends Model {
	public static enum IdentityType {
		NIL,
		STEAM,
		BLIZZARD,
		EPIC
	}
	public static IdentityList byUser(User user) {
		ResultSet results = App.DB.query("SELECT * FROM identity WHERE user_id = " + user.id);
		IdentityList identities = new IdentityList();
		try {
			while (results.next()) {
				Identity i = Identity.getFromCacheOrCreate(results.getInt("id"));
				i.user = user;
				i.type = IdentityType.values()[results.getInt("type")];
				i.value = results.getString("value");
				i.dateCreated = results.getTimestamp("date_created");
				i.dateUpdated = results.getTimestamp("date_updated");
				identities.add(i);
			}
			return identities;
		} catch (SQLException e) {
			App.Log.e("Identity", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public static IdentityList byTypeAndValue(IdentityType type, String value) {
		try {
			Object[] values = {type.ordinal(), value};
			ResultSet results = App.DB.prepare("SELECT * FROM identity WHERE type = ? AND VALUE = ?")
				.bind(values).query();
			IdentityList identities = new IdentityList();
			while (results.next()) {
				Identity i = Identity.getFromCacheOrCreate(results.getInt("id"));
				i.type = IdentityType.values()[results.getInt("type")];
				i.value = results.getString("value");
				i.dateCreated = results.getTimestamp("date_created");
				i.dateUpdated = results.getTimestamp("date_updated");
				identities.add(i);
			}
			return identities;
		} catch (SQLException e) {
			App.Log.e("Identity", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public static Identity create(User user, IdentityType type, String value) throws UniquenessConstraintViolationException {
		if (Identity.byTypeAndValue(type, value).size() > 0) {
			throw new UniquenessConstraintViolationException("Identities must have unique values");
		} else {
			Identity iden = new Identity();
			iden.user = user;
			iden.type = type;
			iden.value = value;
			iden.save();
			cache.setCache(iden, iden.id);
			return iden;
		}
	}
	private static Identity getFromCacheOrCreate(int id) {
		Identity iden = (Identity) cache.getCached(Identity.class.getSimpleName(), id);
		if (iden == null) {
			App.Log.d("Identity", "adding identity to cache");
			iden = new Identity(id);
			cache.setCache(iden, id);
		} else {
			App.Log.d("Identity", "using cached identity");
		}
		return iden;
	}
	
	public IdentityType type;
	public String value;
	public User user;
	
	private Identity() {
		
	}
	private Identity(int id) {
		this.id = id;
	}
	public void save() {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT COUNT(*) as total FROM " + table() + " WHERE id = ? LIMIT 1")
				  .bind(values)
				  .query();
			results.next();
			
			if (results.getInt(1) > 0) {
				Log.d(TAG, "updating " + table());
				Object[] allValues = {user.id, type.ordinal(), value, id};
				App.DB.prepare("UPDATE " + table() + " SET user_id = ?, type = ?, value = ? WHERE id = ?")
				  .bind(allValues)
				  .update();
			} else {
				Log.d(TAG, "creating " + table());
				Object[] createValues = {user.id, type.ordinal(), value};
				ResultSet keys = App.DB.prepare("INSERT INTO " + table() + " (user_id, type, value) VALUES (?, ?, ?)", true)
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
