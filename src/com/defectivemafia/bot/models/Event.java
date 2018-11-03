package com.defectivemafia.bot.models;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.defectivemafia.bot.App;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

public class Event extends Model implements Runnable {
	public static List<Event> all() {
		try {
			ResultSet results = App.DB.query("SELECT * FROM event");
			List<Event> events = new LinkedList<Event>();
			while (results.next()) {
				Event e = new Event();
				e.id = results.getInt("id");
				e.user = User.find(results.getString("user_id"));
				e.name = results.getString("name");
				e.description = results.getString("description");
				e.time = results.getTimestamp("time");
				e.shouldRepeat = results.getBoolean("should_repeat");
				e.repeatAfter = results.getLong("repeat_after");
				e.channel = Channel.find(results.getInt("channel_id"));
				e.mention = results.getString("mention");
				e.dateCreated = results.getTimestamp("date_created");
				e.dateUpdated = results.getTimestamp("date_updated");
				events.add(e);
			}
			return events;
		} catch (SQLException e) {
			App.Log.e("Identity", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static Event create(User user, String name, String description, Date date, boolean repeat, long after, int channelId, String mention) {
		// do not cache events, they are managed by EventsManager
		Event e = new Event();
		e.user = user;
		e.name = name;
		e.description = description;
		e.time = new Timestamp(date.getTime());
		e.shouldRepeat = repeat;
		e.repeatAfter = after;
		e.channel = Channel.find(channelId);
		e.mention = mention;
		e.save();
		return e;
	}
	
	public static Event find(int id) {
		try {
			Object[] values = {id};
			ResultSet results = App.DB.prepare("SELECT * from event WHERE id = ? LIMIT 1")
				.bind(values)
				.query();
			if (results.next()) {
				Event e = new Event();
				e.id = results.getInt("id");
				e.user = User.find(results.getString("user_id"));
				e.name = results.getString("name");
				e.description = results.getString("description");
				e.time = results.getTimestamp("time");
				e.shouldRepeat = results.getBoolean("should_repeat");
				e.repeatAfter = results.getLong("repeat_after");
				e.channel = Channel.find(results.getInt("channel_id"));
				e.mention = results.getString("mention");
				e.dateCreated = results.getTimestamp("date_created");
				e.dateUpdated = results.getTimestamp("date_updated");
				return e;
			} else {
				return null;
			}			
		} catch (SQLException e) {
			App.Log.e("Event", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public User user;
	public String name;
	public String description;
	public Timestamp time;
	public boolean shouldRepeat;
	public long repeatAfter; // number of hours after which the event will repeat
	public Channel channel;
	public String mention;
	
	@Override
	public void run() {
		Log.d(TAG, "Firing event: " + name);
		if (shouldRepeat) {
			time = new Timestamp(time.getTime() + (repeatAfter * 3600 * 1000));
			save();
		} else {
			remove();
		}
		
		TextChannel tChannel = (TextChannel) App.snowflakeResolver.getChannel(channel.channelId);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(name);
		eb.setColor(Color.CYAN);
		eb.setDescription(description);
		eb.setAuthor("Event from " + App.snowflakeResolver.getMember(user.id).getEffectiveName());
		
		MessageBuilder msg = new MessageBuilder();
		if (mention != null && mention.length() > 0) {
			msg.append(mention);
		}
		msg.setEmbed(eb.build());
		tChannel.sendMessage(msg.build()).queue();
	}
	
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
				Object[] allValues = {user.id, name, description, time, shouldRepeat, repeatAfter, channel.id, mention, id};
				App.DB.prepare("UPDATE " + table() + " SET user_id = ?, name = ?, description = ?, time = ?, should_repeat = ?, repeat_after = ?, channel_id = ?, mention = ? WHERE id = ?")
				  .bind(allValues)
				  .update();
			} else {
				Log.d(TAG, "creating " + table());
				Object[] createValues = {user.id, name, description, time, shouldRepeat, repeatAfter, channel.id, mention};
				ResultSet keys = App.DB.prepare("INSERT INTO " + table() + " (user_id, name, description, time, should_repeat, repeat_after, channel_id, mention) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", true)
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
