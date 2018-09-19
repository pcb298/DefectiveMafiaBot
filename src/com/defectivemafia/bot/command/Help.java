package com.defectivemafia.bot.command;

import java.util.Map;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Help extends Command {
	private Map<String, Command> commands;
	public Help(Map<String, Command> commands) {
		this.commands = commands;
	}

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		String cs = String.join("\n", commands.keySet());
		channel.sendMessage(cs).queue();
	}

	@Override
	public String info() {
		return "List all commands";
	}

	@Override
	public String name() {
		return "help";
	}

	@Override
	public String group() {
		return "info";
	}

}
