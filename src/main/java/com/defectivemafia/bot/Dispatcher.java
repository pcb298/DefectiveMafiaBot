package com.defectivemafia.bot;

import java.util.HashMap;
import java.util.Map;

import com.defectivemafia.bot.action.Action;
import com.defectivemafia.bot.action.Action.ActionMatch;
import com.defectivemafia.bot.action.ActionType;
import com.defectivemafia.bot.command.Command;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.zrx.Logger;

public class Dispatcher extends ListenerAdapter {
	private Map<String, Command> commands;
	private Map<ActionType, Map<String, Action>> actions;
	private static String TAG = "EventDispatcher";
	private static Logger Log = App.Log;
	
	public Dispatcher() {
		commands = new HashMap<String, Command>();
		actions = new HashMap<ActionType, Map<String, Action>>();
	}
	
	public Dispatcher registerCommand(Command command) throws Exception {
		if (commands.containsKey(command.name())) throw new Exception("The command " + command.name() + " is already registered");
		commands.put(command.name(), command);
		if (command.alias().length > 0) {
			for (String alias : command.alias()) {
				if (commands.containsKey(alias)) throw new Exception("The command " + alias + " is already registered");
				commands.put(alias, command);
			}
		}
		return this;
	}
	
	public Dispatcher registerAction(ActionType eventType, Action action) throws Exception {
		if (!actions.containsKey(eventType)) actions.put(eventType, new HashMap<String, Action>());
		Map<String, Action> actionsForEvent = actions.get(eventType);
		if (actionsForEvent.containsKey(action.shortName())) throw new Exception("The action " + action.shortName() + " is already registered");
		actionsForEvent.put(action.shortName(), action);
		return this;
	}
	
	public Map<String, Command> getCommands() {
		return commands;
	}
	
	public Map<String, Action> getActions() {
		Map<String, Action> as = new HashMap<String, Action>();
		for (Map<String, Action> am : actions.values()) {
			for (Action a : am.values()) {
				as.put(a.shortName(), a);
			}
		}
		return as;
	}
	
	@Override  
    public void onMessageReceived(MessageReceivedEvent e){
		try {
			if (!e.isFromType(ChannelType.TEXT)) return;
			if (e.getAuthor().isBot()) return;
			Message msg = e.getMessage();
			MessageChannel channel = e.getChannel();
			Member author = e.getMember();
			String content = msg.getContentRaw();
			if (content.startsWith(App.PREFIX)) {
				String name = content.split(" ")[0].substring(App.PREFIX.length()).trim();
				String params = content.substring(App.PREFIX.length() + name.length()).trim();
				Command command = commands.get(name);
				if (command == null) return;
				if (command.authorized(author)) {
					if (command.valid(params)) {
						command.run(e, params);
					} else {
						channel.sendMessage("Usage: " + command.usage().replace("{{prefix}}", App.PREFIX)).queue();
					}
				} else {
					channel.sendMessage("You are not authorized to do that.").queue();
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		runAction(e, actions.get(ActionType.GuildMemberJoin));
	}
	
	private void runAction(Event e, Map<String, Action> actionsForEvent) {
		try {
			for (Action action : actionsForEvent.values()) {
				ActionMatch am = action.shouldRun(e);
				if (am.matches) {
					action.run(e, am.match);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			ex.printStackTrace();
		}
	}
}
