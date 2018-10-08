package com.defectivemafia.bot.action;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;

public class RecordUserJoin extends Action {

	@Override
	public String description() {
		return "Record when users join the server";
	}

	@Override
	public ActionMatch shouldRun(Event event) {
		return new ActionMatch(true);
	}

	@Override
	public void run(Event event, String match) {
		// TODO Auto-generated method stub
		GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
		Log.d(TAG, "User joined: " + e.getMember().getEffectiveName());
	}

}
