//package com.defectivemafia.bot.models;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.LinkedList;
//import java.util.List;
//
//import com.defectivemafia.bot.App;
//
//import net.zrx.StrUtil;
//
//public class GroupModerator extends Model {
//	
//	public static GroupModerator create(UserGroup group, User user) {
//		GroupModerator c = new GroupModerator();
//		c.group = group;
//		c.user = user;
//		c.save();
//		cache.setCache(c, group.id + user.id);
//		return c;
//	}
//	
//	public static List<GroupModerator> findByGroup(UserGroup group) {
//		List<GroupModerator> list = new LinkedList<GroupModerator>();
//		try {
//			Object[] values = {group.id};
//			ResultSet results = App.DB.prepare("SELECT * FROM group_moderator WHERE group_id = ?")
//					.bind(values)
//					.query();
//			while (results.next()) {
//				GroupModerator c = new GroupModerator();
//				c.id = results.getInt("id");
//				c.user = User.find(results.getString("user_id"));
//				c.group = group;
//				c.dateCreated = results.getTimestamp("date_created");
//				c.dateUpdated = results.getTimestamp("date_updated");
//				list.add(c);
//			}
//		} catch (SQLException e) {
//			App.Log.e("GroupModerator", e.getMessage());
//			e.printStackTrace();
//		}
//		return list;
//	}
//	
//	public static GroupModerator findByGroupAndUser(UserGroup group, User user) {
//		try {
//			Object[] values = {group.id, user.id};
//			ResultSet results = App.DB.prepare("SELECT * from group_moderator WHERE group_id = ? AND user_id = ? LIMIT 1")
//				.bind(values)
//				.query();
//			if (results.next()) {
//				GroupModerator c = getFromCacheOrCreate(group.id + user.id);
//				c.id = results.getInt("id");
//				c.user = user;
//				c.group = group;
//				c.dateCreated = results.getTimestamp("date_created");
//				c.dateUpdated = results.getTimestamp("date_updated");
//				return c;
//			} else {
//				return null;
//			}			
//		} catch (SQLException e) {
//			App.Log.e("GroupModerator", e.getMessage());
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private static GroupModerator getFromCacheOrCreate(String key) {
//		GroupModerator c = (GroupModerator) cache.getCached(GroupModerator.class.getSimpleName(), key);
//		if (c == null) {
//			App.Log.d("GroupModerator", "adding groupmoderator to cache");
//			c = new GroupModerator();
//			cache.setCache(c, key);
//		} else {
//			App.Log.d("GroupModerator", "using cached groupmoderator");
//		}
//		return c;
//	}
//
//	public UserGroup group;
//	public User user;
//	
//	@Override
//	public void save() {
//		try {
//			Object[] values = {id};
//			ResultSet results = App.DB.prepare("SELECT COUNT(*) as total FROM " + table() + " WHERE id = ? LIMIT 1")
//				  .bind(values)
//				  .query();
//			results.next();
//			
//			if (results.getInt(1) > 0) {
//				Log.d(TAG, "updating " + table() + ": " + id);
//				Object[] allValues = {group.id, user.id, id};
//				App.DB.prepare("UPDATE " + table() + " SET group_id = ?, user_id = ? WHERE id = ?")
//				  .bind(allValues)
//				  .update();
//			} else {
//				Log.d(TAG, "creating " + table());
//				Object[] createValues = {group.id, user.id};
//				ResultSet keys = App.DB.prepare("INSERT INTO " + table() + " (group_id, user_id) VALUES (?, ?)", true)
//				  .bind(createValues)
//				  .updateWithKeys();
//				
//				if (keys.next()) {
//					id = keys.getInt("id");
//				}				
//			}
//		} catch (SQLException e) {
//			Log.e(TAG, e.getMessage());
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public String table() {
//		return StrUtil.snakeize(getClass().getSimpleName());
//	}
//}
