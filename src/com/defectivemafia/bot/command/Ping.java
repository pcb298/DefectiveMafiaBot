package com.defectivemafia.bot.command;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Ping extends Command {

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		channel.sendMessage("pong!").queue();
	}

	@Override
	public String info() {
		return "ping the bot";
	}

	@Override
	public String name() {
		return "ping";
	}

	@Override
	public String group() {
		return "util";
	}

}
