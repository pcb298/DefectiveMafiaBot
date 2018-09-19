package com.defectivemafia.bot.command;

import java.util.LinkedList;
import java.util.List;

import com.defectivemafia.bot.App;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Roll extends Command {
	
	private final String TAG = "Roll";
	private final String simpleRollPattern = "\\d+";
	private final String rollPattern = "\\d*d\\d+!?(?:\\s*\\+\\s*(?:\\d*d\\d+!?|\\d+!?))*(?:\\s*>\\s*\\d+)?";

	@Override
	public void run(MessageReceivedEvent e, String params) {
		MessageChannel channel = e.getChannel();
		User author = e.getAuthor();
		String input = params.replaceAll("\\s", "");
		Result res = diceroll(input);
		if (res == null) {
			channel.sendMessage("Please only roll up to 100 dice").queue();
		} else {
			if (res.type == "default") {
				channel.sendMessage(author.getAsMention() + " rolled a " + res.total + ".\n\n`" + String.join(" + ", res.parts) + "`").queue();
			} else {
				channel.sendMessage(author.getAsMention() + " rolled a " + res.total + ".\n\tTo hit: " + res.target + "\n\tResult: " + res.message + "\n\n`" + String.join(" + ", res.parts) + "`").queue();
			}
		}
	}

	@Override
	public String usage() {
		return "{{prefix}}" + name() + " [<?number> | <?number>d<number><?!><?+<additional dice>><?+<number>>]";
	}

	@Override
	public String info() {
		return "Roll a die (default d6), or many dice. Examples: **roll 3**, **roll d20+3d4!+2**";
	}

	@Override
	public String name() {
		return "roll";
	}
	
	@Override
	public String[] alias() {
		String[] a = {"r", "dice"};
		return a;
	}

	@Override
	public boolean valid(String params) {
		return params.length() == 0 || params.matches(simpleRollPattern) || params.matches(rollPattern);
	}

	@Override
	public String group() {
		return "fun";
	}
	
	private int randInt(int num) {
		return (int) Math.floor((Math.random() * num) + 1);
	}
	
	private void exploderoll(int num, Result res) {
		int rand = randInt(num);
		res.total += rand;
		res.parts.add("" + rand);
		if (rand == num) exploderoll(num, res);
	}

	private Result diceroll(String str) {
		Result res = new Result();
		if (str.length() == 0) {
			App.Log.d(TAG, "rolling empty");
			int rand = randInt(6);
			res.total += rand;
			res.parts.add("" + rand);
			return res;
		} else if (str.matches("^\\d+$")) { // simple number, roll n d6s
			if (Integer.parseInt(str) > 100) return null;
			App.Log.d(TAG, "rolling simple: " + str);
			for (int i = 0; i < Integer.parseInt(str); i++) {
				int rand = randInt(6);
				res.total += rand;
				res.parts.add("" + rand);
			}
			return res;
		} else {
			App.Log.d(TAG, "rolling complex: " + str);
			String[] parts = str.split(">");
			String[] sections = parts[0].split("\\+");
			int compare = 0;
			if (parts.length > 1 && parts[1].length() > 0 && parts[1].matches("\\d+")) compare = Integer.parseInt(parts[1]);
			for (String s : sections) {
				if (s.matches("\\d*d\\d+!?")) { //1d6!
					String[] _parts = s.replaceAll("!", "").split("d");
					boolean explode = s.indexOf('!') > -1;
					int times = 1;
					if (_parts[0].length() > 0) times = Integer.parseInt(_parts[0]);
					if (times > 100) return null;
					for (int i = 0; i < times; i++) {
						int rand = randInt(Integer.parseInt(_parts[1]));
						res.total += rand;
						res.parts.add("" + rand);
						if (explode && rand == Integer.parseInt(_parts[1])) exploderoll(Integer.parseInt(_parts[1]), res);
					}
				} else if (s.matches("\\d+")) { // 1
					res.total += Integer.parseInt(s);
					res.parts.add(s);
				}
			}
			if (compare > 0) {
				res.type = "detail";
				if (res.total >= compare) res.message = "Success!";
				else res.message = "Failure.";
				res.target = compare;
			}
			return res;
		}
	}
	
	class Result {
		public String type;
		public String message;
		public int total;
		public int target;
		public List<String> parts;
		
		public Result() {
			type = "default";
			message = "";
			total = 0;
			target = -1;
			parts = new LinkedList<String>();
		}
	}

}
