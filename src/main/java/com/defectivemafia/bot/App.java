package com.defectivemafia.bot;

import com.defectivemafia.bot.command.Test;
import com.defectivemafia.bot.action.ActionType;
import com.defectivemafia.bot.action.RecordUserJoin;
import com.defectivemafia.bot.command.Channel;
import com.defectivemafia.bot.command.Connect;
import com.defectivemafia.bot.command.Disconnect;
import com.defectivemafia.bot.command.Groups;
import com.defectivemafia.bot.command.Help;
import com.defectivemafia.bot.command.JoinGroup;
import com.defectivemafia.bot.command.LeaveGroup;
import com.defectivemafia.bot.command.Ping;
import com.defectivemafia.bot.command.Profile;
import com.defectivemafia.bot.command.Roll;
import com.defectivemafia.bot.database.Database;
import com.defectivemafia.bot.events.EventsCommand;
import com.defectivemafia.bot.events.EventsManager;
import com.defectivemafia.bot.models.Event;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.zrx.KeyValueConfig;
import net.zrx.Logger;

public class App {
	private static String TAG = "App";
	public static String PREFIX = ".";
	public static final Logger Log = new Logger(Logger.DEBUG);
	public static Database DB;
	private static final String[] configKeys = {"bot.key","bot.guild", "db.uri","db.user","db.pass","db.name"};
	private static KeyValueConfig config = new KeyValueConfig(configKeys);
	public static EventsManager eventsManager = new EventsManager();
	private static Guild botGuild;
	public static SnowflakeResolver snowflakeResolver;
	public static RolesManager rolesManager;
    
    public static void main(String[] args) {
    	try {
	    	String token = config.getString("bot.key");
	    	if (token == null || token.length() == 0) {
	    		Log.e(TAG, "Please set configuration: missing bot.key");
	    		System.exit(0);
	    	}
	    	String guildId = config.getString("bot.guild");
	    	if (guildId == null || guildId.length() == 0) {
	    		Log.e(TAG, "Please set configuration: missing bot.guild");
	    		System.exit(0);
	    	}
	    	String dbURI = config.getString("db.uri");
	    	String dbUser = config.getString("db.user");
	    	String dbPass = config.getString("db.pass");
	    	if (dbURI == null || dbUser == null || dbPass == null || dbURI.length() == 0 || dbUser.length() == 0 || dbPass.length() == 0) {
	    		Log.e(TAG, "Please set configuration: missing db.?");
	    		System.exit(0);
	    	}
	    	DB = new Database(dbURI, dbUser, dbPass);
	        JDA jdabot = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking(); //.buildAsync();
	        Dispatcher dispatcher = new Dispatcher();
	        registerCommands(dispatcher);
	        registerActions(dispatcher);
	        
	        Log.i(TAG, "starting listener");
	        jdabot.addEventListener(dispatcher);  
	        Log.i(TAG, "active listener");
	        
	        jdabot.getPresence().setGame(Game.playing(App.PREFIX + "help"));
	        botGuild = jdabot.getGuildById(guildId);
	        if (botGuild == null) {
	        	Log.e(TAG, "Invalid bot.guild config");
	        	System.exit(1);
	        }
	        snowflakeResolver = new SnowflakeResolver(botGuild);
	        rolesManager = new RolesManager(botGuild);
	        
	        Log.i(TAG, "loading existing events");
	        eventsManager.addEvents(Event.all());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private static void registerCommands(Dispatcher dispatcher) {
    	Log.i(TAG, "registering commands");
    	try {
	    	dispatcher.registerCommand(new Ping());
	    	dispatcher.registerCommand(new Roll());
	    	dispatcher.registerCommand(new Connect());
	    	dispatcher.registerCommand(new Disconnect());
	    	dispatcher.registerCommand(new Profile());
	    	dispatcher.registerCommand(new Channel());
	    	dispatcher.registerCommand(new Help(dispatcher.getCommands()));
	    	
	    	dispatcher.registerCommand(new EventsCommand());
	    	dispatcher.registerCommand(new Groups());
	    	dispatcher.registerCommand(new JoinGroup());
	    	dispatcher.registerCommand(new LeaveGroup());
	    	
	    	dispatcher.registerCommand(new Test());
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage());
    	}
    }
    
    private static void registerActions(Dispatcher dispatcher) {
    	Log.i(TAG, "registering actions");
    	try {
    		dispatcher.registerAction(ActionType.GuildMemberJoin, new RecordUserJoin());
    	} catch (Exception e) {
    		Log.e(TAG, e.getMessage());
    	}
    }
}
