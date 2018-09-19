package com.defectivemafia.bot.command;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.UserGroup;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.RoleManager;

public class Groups extends Command {

	@Override
	public void run(MessageReceivedEvent event, String params) {
		MessageChannel channel = event.getChannel();
		String[] parts = params.split("\\s+");
		UserGroup group;
		String name;
		String message = "";
		Role role;
		String rest;
		Pattern pattern;
		Matcher matcher;
		// someone please refactor this into separate methods
		switch (parts[0]) {
			case "list":
				List<UserGroup> groups = UserGroup.all();
				if (groups.size() > 0) {
					message = "🗒 Groups:\n```";
					for (UserGroup g : groups) {
						role = App.snowflakeResolver.getRole(g.roleId);
						List<Member> groupMembers = App.rolesManager.membersWithRole(role);
						message += g.name + ": " + groupMembers.size() + (g.maxCapacity > 0 ? " / " + g.maxCapacity : "") + " members" + (g.isPrivate ? " (private)" : "") + "\n";
					}
					message += "```";
				} else message = "There are no groups.";
				break;
			case "members":
				name = params.substring(parts[0].length()).trim();
				role = App.snowflakeResolver.getRole(name);
				if (UserGroup.findByName(name) != null) {
					List<Member> groupMembers = App.rolesManager.membersWithRole(role);
					if (groupMembers.size() > 0) {
						message = "🗒 Members of *" + name + ":*\n```";
						for (Member m : groupMembers) {
							message += m.getEffectiveName() + "\n";
						}
						message += "```";
					} else {
						message = "The group *" + name + "* is empty.";
					}
				} else {
					message = "🚫 There is no group called *" + name + "*";
				}
				break;
			case "add":
				name = params.substring(parts[0].length()).trim();
				role = App.snowflakeResolver.getRole(name);
				if (role == null) role = App.rolesManager.createRole(name);
				if (UserGroup.findByName(name) == null) {
					group = UserGroup.create(name, role.getId(), 0, false);
					message = "👍 Created group *" + name + "*";
				} else {
					message = "🚫 A group called *" + name + "* already exists";
				}
				break;
			case "remove":
				name = params.substring(parts[0].length()).trim();
				group = UserGroup.findByName(name);
				if (group == null) {
					message = "🚫 There is no group called *" + name + "*";
				} else {
					role = App.snowflakeResolver.getRole(group.roleId);
					group.remove();
					role.delete().queue();
					message = "👍 Removed group *" + name + "*";
				}
				break;
			case "set":
				rest = params.substring(parts[0].length()).trim();
				pattern = Pattern.compile("(.+)\\s+(name|color|capacity|private)\\s+(.+)");
				matcher = pattern.matcher(rest);
				if (matcher.find()) {
					name = matcher.group(1);
					String value = matcher.group(3);
					group = UserGroup.findByName(name);
					if (group == null) {
						message = "🚫 There is no group called *" + name + "*";
					} else {
						switch (matcher.group(2)) {
							case "name":
								role = App.snowflakeResolver.getRole(group.roleId);
								if (role != null) {
									RoleManager rm = role.getManager();
									rm.setName(value).queue();
									group.name = value;
									group.save();
									message = "👍 Renamed group *" + name + "* to *" + value + "*";
								} else {
									message = "🚫 Could not find role called *" + name + "*";
								}
								break;
							case "color":
								role = App.snowflakeResolver.getRole(group.roleId);
								if (role != null) {
									if (value.matches("#?[\\dabcdefABCDEF]{6}")) {
										RoleManager rm = role.getManager();
										rm.setColor(new Color(Integer.parseInt(value.replace("#", ""), 16))).queue();
										message = "👍 Changed color of group *" + name + "* to *" + value + "*";
									} else {
										message = "🚫 Color must be in hexadecimal color format (#123456)";
									}
								} else {
									message = "🚫 Could not find role called *" + name + "*";
								}
								break;
							case "capacity":
								if (value.matches("\\d+")) {
									group.maxCapacity = Integer.parseInt(value);
									group.save();
									message = "👍 Changed capacity of group *" + name + "* to *" + value + "*";
								} else {
									message = "🚫 Capacity must be a number";
								}
								break;
							case "private":
								if (value.matches("(yes|no)")) {
									group.isPrivate = value.equals("yes");
									group.save();
									message = "👍 Group *" + name + "* is " + (group.isPrivate ? "now" : "no longer") + " private";
								} else {
									message = "🚫 Value must be \"yes\" or \"no\"";
								}
								break;
						}
					}
				}
				break;
			case "addmember":
			case "removemember":
				rest = params.substring(parts[0].length()).trim();
				pattern = Pattern.compile("(.+)\\s+(?:to|from)\\s+(.+)");
				matcher = pattern.matcher(rest);
				if (matcher.find()) {
					name = matcher.group(2);
					group = UserGroup.findByName(name);
					Member member = App.snowflakeResolver.getMember(matcher.group(1));
					if (group == null) {
						message = "🚫 There is no group called *" + name + "*";
					} else if (member == null) {
						message = "🚫  Could not find user called *" + matcher.group(1) + "*";
					} else {
						role = App.snowflakeResolver.getRole(group.roleId);
						if (role == null) {
							message = "🚫 The role *" + name + "* does not exist. Get a staff member to fix this.";
						} else {
							if (parts[0].equals("addmember")) {
								event.getGuild().getController().addRolesToMember(member, role).queue();
								message = "👍 Added " + member.getEffectiveName() + " to *" + name + "*";
							} else {
								event.getGuild().getController().removeRolesFromMember(member, role).queue();
								message = "👍 Removed " + member.getEffectiveName() + " from *" + name + "*";
							}
						}
					}
				}
				break;
		}
		
		channel.sendMessage(message).queue();
	}
	
	@Override
	public boolean authorized(Member member) {
		return member.hasPermission(Permission.KICK_MEMBERS);
	}

	@Override
	public String info() {
		return "Manage user groups (0 capacity = no limit)";
	}
	
	@Override
	public String name() {
		return "group";
	}
	
	@Override
	public String[] alias() {
		String[] a = {"g"};
		return a;
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [list | [add | remove | members] <name> | set <name> [name | color | capacity | private] <value> | [addmember | removemember] <#user> to <name>]";
	}
	
	@Override
	public boolean valid(String params) {
		return params.matches("(list|add\\s+.+|remove\\s+.+|set\\s+.+\\s+(?:name|color|capacity|private)\\s+.+|members\\s+.+|(?:addmember|removemember).+\\s+(?:to|from)\\s+.+)");
	}

}
