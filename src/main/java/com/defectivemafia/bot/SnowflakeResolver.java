package com.defectivemafia.bot;

import java.util.List;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class SnowflakeResolver {
	private Guild guild;
	
	public SnowflakeResolver(Guild guild) {
		this.guild = guild;
	}
	
	public Channel getChannel(String name) {
		Channel c = null;
		if (name.matches("(?:<#)?\\d{18}(?:>)?")) {
			name = name.replaceAll("[^\\d]", "");
			c = guild.getTextChannelById(name);
			if (c != null) return c;
			c = guild.getVoiceChannelById(name);
			if (c != null) return c;
		}
		List<? extends Channel> list = guild.getTextChannelsByName(name, true);
		if (list.size() > 0)
			c = (TextChannel) list.get(0);
		if (c != null) return c;
		list = guild.getVoiceChannelsByName(name, true);
		if (list.size() > 0)
			c = (VoiceChannel) list.get(0);
		return c;
	}
	
	public Category getCategory(String name) {
		Category c = null;
		if (name.matches("\\d{18}")) {
			c = guild.getCategoryById(name);
			if (c != null) return c;			
		}
		List<Category> list = guild.getCategoriesByName(name, true);
		if (list.size() > 0)
			c = list.get(0);
		return c;
	}
	
	public Member getMember(String name) {
		Member m = null;
		if (name.matches("(?:<@!?)?\\d{18}(?:>)?")) {
			m = guild.getMemberById(name);
			if (m != null) return m;			
		}
		List<Member> list = guild.getMembersByNickname(name, true);
		if (list.size() > 0)
			m = list.get(0);
		if (m != null) return m;
		list = guild.getMembersByName(name, true);
		if (list.size() > 0)
			m = list.get(0);
		return m;
	}
	
	public Role getRole(String name) {
		Role r = null;
		if (name.matches("(?:<@&)?\\d{18}(?:>)?")) {
			r = guild.getRoleById(name);
			if (r != null) return r;
		}
		List<Role> list = guild.getRolesByName(name, true);
		if (list.size() > 0)
			r = list.get(0);
		return r;
	}
}
