package com.defectivemafia.bot.action;

import com.defectivemafia.bot.App;

import net.dv8tion.jda.core.events.Event;
import net.zrx.Logger;

public abstract class Action {
	
	protected final Logger Log = App.Log;
	protected final String TAG = getClass().getSimpleName();
	
	public Action() {
		Log.i(this.getClass().getSimpleName(), "loaded action: " + name());
	}
	
	public String name() {
		return getClass().getCanonicalName();
	}
	
	public String shortName() {
		return name().toLowerCase().replaceAll("\\W", "");
	}
	
	public abstract String description();
	public abstract ActionMatch shouldRun(Event event);
	public abstract void run(Event event, String match);
	
	public class ActionMatch {
		public boolean matches;
		public String match;
		public ActionMatch(boolean matches) {
			this.matches = matches;
			this.match = "";
		}
		public ActionMatch(boolean matches, String match) {
			this.matches = matches;
			this.match = match;
		}
	}
}
