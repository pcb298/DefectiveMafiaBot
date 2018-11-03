package com.defectivemafia.bot.command;

import com.defectivemafia.bot.models.Identity;
import com.defectivemafia.bot.models.Identity.IdentityType;
import com.defectivemafia.bot.models.IdentityList;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Disconnect extends Command {

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		com.defectivemafia.bot.models.User dbUser = com.defectivemafia.bot.models.User.find(e.getMember().getUser().getId());
		IdentityType idenType = IdentityType.NIL;
		IdentityList identities = Identity.byUser(dbUser);
		switch (params) {
			case "steam":
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
		Identity iden = identities.find(idenType);
		if (iden == null) {
			channel.sendMessage("🚫 Could not find a matching connected account.").queue();
		} else {
			iden.remove();
			channel.sendMessage("👍 Disconnected your account.").queue();
		}
	}

	@Override
	public String info() {
		return "Disconnect a games account";
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [steam|battletag|epic]";
	}

	@Override
	public String name() {
		return "disconnect";
	}

	@Override
	public boolean valid(String params) {
		return params.matches("(steam|battletag|blizzard|epic)");
	}

	@Override
	public String group() {
		return "social";
	}

}
