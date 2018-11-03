package com.defectivemafia.bot.events;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.defectivemafia.bot.App;
import com.defectivemafia.bot.models.Event;

import net.zrx.Logger;

public class EventsManager {
	private final ScheduledExecutorService scheduler;
	private Map<Integer, ScheduledFuture<?>> events;
	private Map<String, ScheduledFuture<?>> reminders;
	private Logger Log = App.Log;
	private final String TAG = getClass().getSimpleName();
	private DateFormat df = DateFormat.getInstance();
	
	public EventsManager() {
		// https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
		scheduler = Executors.newScheduledThreadPool(1);
		events = new HashMap<Integer, ScheduledFuture<?>>();
		reminders = new HashMap<String, ScheduledFuture<?>>();
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				clean();
			}
		}, 2, 30, TimeUnit.MINUTES);
	}
	
	public void addEvent(Event event) {
		if (events.containsKey(event.id)) {
			Log.e(TAG, "an event with id '" + event.id + "' is already registered");
			return;
		}
		EventReminder reminder30 = new EventReminder(event, 30);
		EventReminder reminder5 = new EventReminder(event, 5);
		long wait = event.time.getTime() - System.currentTimeMillis();
		
		if (event.shouldRepeat) {
			Log.i(TAG, "scheduled repeating event to run at " + df.format(event.time) + " - " + event.name);
			long repeatAfter = event.repeatAfter * 60 * 60 * 1000;
			events.put(event.id, scheduler.scheduleWithFixedDelay(event, wait, repeatAfter, TimeUnit.MILLISECONDS));
			reminders.put(event.id + "-reminder-30", scheduler.scheduleWithFixedDelay(reminder30, wait - (30 * 60 * 1000), repeatAfter, TimeUnit.MILLISECONDS));
			reminders.put(event.id + "-reminder-5", scheduler.scheduleWithFixedDelay(reminder5, wait - (5 * 60 * 1000), repeatAfter, TimeUnit.MILLISECONDS));
		} else {
			Log.i(TAG, "scheduled one-time event to run at " + df.format(event.time) + " - " + event.name);
			events.put(event.id, scheduler.schedule(event, wait, TimeUnit.MILLISECONDS));
			reminders.put(event.id + "-reminder-30", scheduler.schedule(reminder30, wait - (30 * 60 * 1000), TimeUnit.MILLISECONDS));
			reminders.put(event.id + "-reminder-5", scheduler.schedule(reminder5, wait - (5 * 60 * 1000), TimeUnit.MILLISECONDS));
		}
	}
	
	public void cancel(Event event) {
		events.get(event.id).cancel(true);
		reminders.get(event.id + "-reminder-30").cancel(true);
		reminders.get(event.id + "-reminder-5").cancel(true);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void clean() {
		Log.i(TAG, "cleaning finished events");
		for (int i : events.keySet()) {
			ScheduledFuture<?> f = events.get(i);
			if (f.isDone()) events.remove(i);
		}
		for (String s : reminders.keySet()) {
			ScheduledFuture<?> r = reminders.get(s);
			if (r.isDone()) reminders.remove(s);
		}
	}
	
	public void addEvents(List<Event> events) {
		for (Event e : events) addEvent(e);
	}
}
