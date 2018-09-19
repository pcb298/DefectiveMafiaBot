package com.defectivemafia.bot.events;

import java.awt.Color;
import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.Event;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.zrx.Logger;

public class EventReminder implements Runnable {

	private Event event;
	private int minsPrior;
	private String TAG = getClass().getSimpleName();
	private Logger Log = App.Log;
	
	public EventReminder(Event event, int minsPrior) {
		this.event = event;
		this.minsPrior = minsPrior;
	}
		
	@Override
	public void run() {
		long diff = Math.round((event.time.getTime() - System.currentTimeMillis()) / 1000 / 60);

		// dont fire reminder if reminder time has passed
		if (diff < minsPrior) return;
		
		Log.d(TAG, "Firing event reminder for: " + event.name);		
		TextChannel tChannel = (TextChannel) App.snowflakeResolver.getChannel(event.channel.channelId);
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Reminder: " + event.name + " in " + diff + " minutes");
		eb.setColor(Color.YELLOW);
		eb.setDescription(event.description);
		eb.setAuthor("Event reminder from " + App.snowflakeResolver.getMember(event.user.id).getEffectiveName());
		
		MessageBuilder msg = new MessageBuilder();
		if (event.mention != null && event.mention.length() > 0) {
			msg.append(event.mention);
		}
		msg.setEmbed(eb.build());
		tChannel.sendMessage(msg.build()).queue();
	}

}
