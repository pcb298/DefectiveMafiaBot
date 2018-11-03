package com.defectivemafia.bot.command;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.UserGroup;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LeaveGroup extends Command {

	@Override
	public void run(MessageReceivedEvent event, String params) {
		String name = params.trim();
		UserGroup group = UserGroup.findByName(name);
		String message = "";
		if (group == null) {
			message = "🚫 The group *" + name + "* does not exist.";
		} else {
			Role role = App.snowflakeResolver.getRole(group.roleId);
			if (role == null) {
				message = "🚫 The role *" + name + "* does not exist. Get a staff member to fix this.";	
			} else if (group.isPrivate) {
				message = "🚫 The group *" + name + "* is private.";
			} else {
				event.getGuild().getController().removeRolesFromMember(event.getMember(), role).queue();
				message = "👍 Removed you from *" + name + "*";
				
			}
		}
		event.getChannel().sendMessage(message).queue();
	}

	@Override
	public String info() {
		return "Leave a group";
	}
	
	@Override
	public String name() {
		return "leave";
	}

}
