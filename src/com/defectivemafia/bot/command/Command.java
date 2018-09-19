package com.defectivemafia.bot.command;

import com.defectivemafia.bot.App;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.zrx.Logger;

public abstract class Command {
	protected final Logger Log = App.Log;
	protected final String TAG = getClass().getSimpleName();
	public Command() {
		Log.i(this.getClass().getSimpleName(), "loaded command: " + name());
	}

	public boolean authorized(Member author) {
		return true;
	};

	public abstract void run(MessageReceivedEvent event, String params);

	public String usage() {
		return "{{prefix}}" + name();
	}

	public abstract String info();

	public String name() {
		return this.getClass().getCanonicalName().toLowerCase();
	}
	
	public String[] alias() {
		return new String[0];
	}

	public boolean valid(String params) {
		return true;
	}
	
	public String group() {
		return Command.class.getCanonicalName().toLowerCase();
	}
}
