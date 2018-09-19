package com.defectivemafia.bot.command;

import java.util.List;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.UserGroup;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class JoinGroup extends Command {

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
				List<Member> groupMembers = App.rolesManager.membersWithRole(role);
				if (group.maxCapacity == 0 || group.maxCapacity > groupMembers.size()) {
					event.getGuild().getController().addRolesToMember(event.getMember(), role).queue();
					message = "👍 Added you to *" + name + "*";
				} else {
					message = "🚫 The group *" + name + "* is full.";
				}
			}
		}
		event.getChannel().sendMessage(message).queue();
	}

	@Override
	public String info() {
		return "Join a group";
	}
	
	@Override
	public String name() {
		return "join";
	}

}
