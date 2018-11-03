package com.defectivemafia.bot.command;

import java.util.List;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.Event;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Channel extends Command {

	@Override
	public boolean authorized(Member author) {
		return author.hasPermission(Permission.KICK_MEMBERS);
	}

	@Override
	public void run(MessageReceivedEvent event, String params) {
		MessageChannel ch = event.getChannel();
		String[] parts = params.split("\\s+");
		String action = parts[0];
		String key, value;
		String message = "";
		com.defectivemafia.bot.models.Channel dbChannel;
		net.dv8tion.jda.core.entities.Channel c;
		
		switch (action) {
			case "list":
				List<com.defectivemafia.bot.models.Channel> dbChannels = com.defectivemafia.bot.models.Channel.all();
				message = "🗒 Channel references:\n```\n";
				if (dbChannels.size() > 0) {
					for (com.defectivemafia.bot.models.Channel channel : dbChannels) {
						message += '\n' + channel.name + " : " + channel.channelId;
					}
					message += "\n```";
				} else {
					message = "There are no channel references.";
				}
				break;
			case "add":
				key = parts[1];
				c = App.snowflakeResolver.getChannel(parts[2]);
				value = c.getId();
				dbChannel = com.defectivemafia.bot.models.Channel.findByName(key);
				if (dbChannel == null) {
					dbChannel = com.defectivemafia.bot.models.Channel.create(key, value);
					message = "👍 Created channel reference: *(" + key + ", #" + c.getName() + ")*";
				} else {
					dbChannel.channelId = value;
					dbChannel.save();
					message = "👍 Updated channel reference: *(" + key + ", #" + c.getName() + ")*";
				}
				break;
			case "remove":
				key = parts[1];
				dbChannel = com.defectivemafia.bot.models.Channel.findByName(key);
				if (dbChannel == null) {
					message = "🚫 The channel reference for *" + key + "* does not exist.";
				} else {
					List<Event> events = Event.all();
					boolean remove = true;
					for (Event e : events) {
						if (e.channel.name.equals(key)) {
							remove = false;
							break;
						}
					}
					if (remove) {
						value = dbChannel.channelId;
						c = App.snowflakeResolver.getChannel(value);
						dbChannel.remove();
						message = "👍 Removed channel reference: *(" + key + ", #" + c.getName() + ")*";
					} else {
						message = "🚫 The channel reference *" + key + "* is still in use.";					
					}
				}
				break;
		}
		
		ch.sendMessage(message).queue();
	}
	
	@Override
	public String name() {
		return "channels";
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [add <name> <#channel> | remove <name> | list]";
	}

	@Override
	public String info() {
		return "Set which channels the bot uses for things";
	}
	
	@Override
	public boolean valid(String params) {
		Log.d(TAG, params);
		return params.matches("(add\\s+[^\\s]+\\s+[^\\s]+|remove\\s+[^\\s]+|list)");
	}
	
	@Override
	public String group() {
		return "moderation";
	}

}
