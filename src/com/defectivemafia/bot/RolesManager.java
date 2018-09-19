package com.defectivemafia.bot;

import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.requests.restaction.RoleAction;

public class RolesManager {
	
	private Guild guild;
	
	public RolesManager(Guild guild) {
		this.guild = guild;
	}
	
	public Role createRole(String name) {
		RoleAction ra = guild.getRoles().get(0).createCopy();
		ra.setName(name)
		  .setColor(0xFF99aab5)
		  .setHoisted(false)
		  .setMentionable(true)
		  .setPermissions(0L);
		return ra.complete();
	}
	
	public List<Member> membersWithRole(Role role) {
		return guild.getMembersWithRoles(role);
	}
}
