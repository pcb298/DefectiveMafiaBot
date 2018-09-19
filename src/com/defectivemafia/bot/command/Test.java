package com.defectivemafia.bot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Test extends Command {
	
	@Override
	public void run(MessageReceivedEvent e, String params) {
		com.defectivemafia.bot.models.User.create(e.getMember().getUser().getId());
	}

	@Override
	public String info() {
		return "test";
	}

	@Override
	public String name() {
		return "test";
	}

	@Override
	public String group() {
		return "test";
	}

}
