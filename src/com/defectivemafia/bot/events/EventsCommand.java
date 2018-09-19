package com.defectivemafia.bot.events;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.command.Command;
import com.defectivemafia.bot.models.Channel;
import com.defectivemafia.bot.models.Event;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EventsCommand extends Command {
	private DateFormat df = DateFormat.getInstance();
	private final String ADDREGEX = "name:.+\\ndescription:.+\\ntime: *\\d{1,2}\\/\\d{1,2}\\/\\d{4} \\d{2}:\\d{2}(\\nrepeat:.+\\nwait:.+)?(\\nchannel:.+)?(\\nmention:.+)?";
	
	@Override
	public boolean authorized(Member author) {
		return author.hasPermission(Permission.KICK_MEMBERS);
	}

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		if (params.length() == 0 || params.equals("list")) {
			List<Event> events = Event.all();
			String out = "🗒 Events:\n```";
			if (events.size() > 0) {
				for (Event ev : events) {
					String spaces = ("" + ev.id).replaceAll(".", " ");
					out += "\n" + ev.id + "\t" + ev.name + " at " + df.format(ev.time) + " in #" + App.snowflakeResolver.getChannel(ev.channel.channelId).getName();
					out += "\n" + spaces + "\t" + (ev.shouldRepeat ? ("Repeats every " + ev.repeatAfter + " hours") : "Does not repeat");
					out += "\n" + spaces + "\t" + ev.description;
				}
				out += "```";
			} else {
				out = "There are no events.";
			}
			channel.sendMessage(out).queue();
		} else {
			String[] parts = params.split("\\s");
			String action = parts[0];
			Event ev;
			EventsManager em = App.eventsManager;
			switch (action) {
				case "add":
					com.defectivemafia.bot.models.User dbUser = com.defectivemafia.bot.models.User.find(e.getMember().getUser().getId());
					if (params.substring(action.length()).trim().matches(ADDREGEX)) {
						try {
							EventParts eventParts;
							eventParts = getParts(params.substring(action.length()).trim());
							ev = Event.create(dbUser, eventParts.name, eventParts.description, eventParts.time, eventParts.repeat, eventParts.after, eventParts.channel, eventParts.mention);
							em.addEvent(ev);
							channel.sendMessage("👍 Added new event: " + ev.name).queue();
						} catch (ParseException e1) {
							channel.sendMessage("🚫 Date must be in format: mm/dd/yyyy hh:mm").queue();
						} catch (Exception e1) {
							channel.sendMessage("" + e1.getMessage()).queue();
							e1.printStackTrace();
						}
					} else {
						channel.sendMessage(getHelp()).queue();
					}
					break;
				case "remove":
					int id = Integer.parseInt(parts[1]);
					ev = Event.find(id);
					ev.remove();
					em.cancel(ev);
					channel.sendMessage("👍 Removed event " + ev.id + ": " + ev.name).queue();
					break;
			}
		}
	}

	@Override
	public String info() {
		return "Manage server events";
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [?list | [add <details>| remove <id>]]";
	}

	@Override
	public String name() {
		return "events";
	}

	@Override
	public boolean valid(String params) {
		return params.matches("(?:list|(?:add\\s(?:\n|.)+|remove\\s\\d+))?");
	}

	@Override
	public String group() {
		return "moderation";
	}
	
	private EventParts getParts(String params) throws Exception {
		EventParts parts = new EventParts();
		
		String[] lines = params.split("\\n");
		String section = null;
		String rest;
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("name:")) {
				section = "name";
			} else if (line.startsWith("description:")) {
				section = "description";
			} else if (line.startsWith("time:")) {
				section = "time";
			} else if (line.startsWith("repeat:")) {
				section = "repeat";
			} else if (line.startsWith("wait:")) {
				section = "wait";
			} else if (line.startsWith("channel:")) {
				section = "channel";
			} else if (line.startsWith("mention:")) {
				section = "mention";
			}
			if (line.startsWith(section + ":")) rest = line.substring(section.length() + 1).trim();
			else rest = line.trim();
			
			String[] negative = {"0", "no", "false"};
			String[] positive = {"1", "yes", "true"};
			switch (section) {
				case "name":
					parts.name = parts.name.length() > 0 ? parts.name + "\n" + rest : rest;
					break;
				case "description":
					parts.description = parts.description.length() > 0 ? parts.description + "\n" + rest : rest;
					break;
				case "time":
					DateFormat format = new SimpleDateFormat("M/d/yyyy k:m:s", Locale.ENGLISH);
					parts.time = format.parse(rest + ":00");	
					break;
				case "repeat":
					parts.repeat = Arrays.asList(negative).contains(rest) 
						? false 
						: Arrays.asList(positive).contains(rest) ? true : false;
					break;
				case "wait":
					parts.after = Long.parseLong(rest);
					break;
				case "channel":
					Channel ch = Channel.findByName(rest);
					if (ch == null) {
						throw new Exception("🚫 Could not find channel reference: *" + rest + "*");
					} else {
						parts.channel = ch.id;
					}
					break;
				case "mention":
					if (rest.equals("@everyone") || rest.equals("@here"))
						parts.mention = rest;
					else {
						Member member = App.snowflakeResolver.getMember(rest);
						if (member != null) {
							parts.mention = member.getAsMention();
						} else {
							Role role = App.snowflakeResolver.getRole(rest);
							if (role != null) {
								parts.mention = role.getAsMention();
							} else {
								throw new Exception("🚫 Could not find mentionable: *" + rest + "*");
							}
						}
					}
					break;
			}
		}
		
		return parts;
	}
	
	private String getHelp() {
		return "```add name: <name>\n"
			 + "    description: <desc>\n"
			 + "    time: mm/dd/yyyy hh:mm\n"
			 + "    repeat: [yes | no] (default: no)\n"
			 + "    wait: <hours> (default: 168)\n"
			 + "    channel: <reference>\n"
			 + "    mention: <@mention> (optional)```";
	}
	
	class EventParts {
		public String name = "";
		public String description = "";
		public Date time = new Date();
		public boolean repeat = false;
		public long after = 24 * 7;
		public int channel = -1;
		public String mention = "";
	}
}
