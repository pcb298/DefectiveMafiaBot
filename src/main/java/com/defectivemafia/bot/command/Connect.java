package com.defectivemafia.bot.command;

import com.defectivemafia.bot.models.Identity;
import com.defectivemafia.bot.models.Identity.IdentityType;
import com.defectivemafia.bot.models.IdentityList;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.zrx.UniquenessConstraintViolationException;

public class Connect extends Command {

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		String[] parts = params.split(" ");
		com.defectivemafia.bot.models.User dbUser = com.defectivemafia.bot.models.User.find(e.getMember().getUser().getId());
		String id = parts[1];
		IdentityList identities = Identity.byUser(dbUser);
		IdentityType idenType = IdentityType.NIL;
		switch (parts[0]) {
			case "steam":
				if (id.matches("\\d{17,18}")/* || id.matches("\\[\\w:\\d+:\\d+(?:\\d+)?\\]") || id.matches("STEAM_\\d:\\d:\\d+")*/) {
					idenType = IdentityType.STEAM;
				} else {
					channel.sendMessage("🚫 Invalid SteamID: please use Steam64 format."/*, Steam2, or Steam3 format."*/).queue();					
					return;
				}
				break;
			case "blizzard":
			case "battletag":
				if (id.matches(".{3,11}#\\d{4}")) {
					idenType = IdentityType.BLIZZARD;
				} else {
					channel.sendMessage("🚫 Invalid BattleTag: please use name#0000 format.").queue();
					return;
				}
				break;
			case "epic":
				idenType = IdentityType.EPIC;
				break;
		}
		Identity iden = identities.find(idenType);
		if (iden != null && iden.value.equals(id)) {
			channel.sendMessage("🚫 That account is already connected.").queue();
			return; // identity of same type and value already exists
		} else {
			try {
				Identity.create(dbUser, idenType, id);
			} catch (UniquenessConstraintViolationException ex) {
				channel.sendMessage("🚫 That account is already connected.").queue();
				ex.printStackTrace();
			}
			channel.sendMessage("👍 Connected your account.").queue();
		}
	}

	@Override
	public String info() {
		return "Connect a games account";
	}
	
	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [steam|battletag|epic] <id>";
	}

	@Override
	public String name() {
		return "connect";
	}

	@Override
	public boolean valid(String params) {
		return params.matches("(steam|battletag|blizzard|epic)\\s+.+");
	}

	@Override
	public String group() {
		return "social";
	}

}
