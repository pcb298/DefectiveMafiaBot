package com.defectivemafia.bot.command;

import com.defectivemafia.bot.models.Identity;
import com.defectivemafia.bot.models.IdentityList;
import com.defectivemafia.bot.models.Identity.IdentityType;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Profile extends Command {

	@Override
	public void run(MessageReceivedEvent e, String params) {
		com.defectivemafia.bot.models.User dbUser = com.defectivemafia.bot.models.User.find(e.getMember().getUser().getId());
		MessageChannel channel = e.getChannel();
		IdentityList identities = Identity.byUser(dbUser);
		IdentityType idenType = IdentityType.NIL;
		switch(params) {
			case "steam":
				// https://steamcommunity.com/profiles/76561197995773793/?xml=1
				idenType = IdentityType.STEAM;
				break;
			case "blizzard":
			case "battletag":
				idenType = IdentityType.BLIZZARD;
				break;
			case "epic":
				idenType = IdentityType.EPIC;
				break;
		}
		IdentityList list = identities.findAll(idenType);
		if (list.size() == 0) {
			channel.sendMessage("You do not have a " + params + " account connected.").queue();
		} else {
			String[] values = new String[list.size()];
			int i = 0;
			for (Identity iden : list) {
				values[i++] = iden.value;
			}
			channel.sendMessage(String.join(", ", values)).queue();
		}
	}

	@Override
	public String info() {
		return "Display a profile";
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [steam|blizzard|epic]";
	}

	@Override
	public String name() {
		return "profile";
	}

	@Override
	public boolean valid(String params) {
		return params.matches("(steam|blizzard|epic)");
	}

	@Override
	public String group() {
		return "social";
	}

}
