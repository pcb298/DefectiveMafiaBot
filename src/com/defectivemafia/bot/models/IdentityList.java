package com.defectivemafia.bot.models;

import java.util.Iterator;
import java.util.LinkedList;

import com.defectivemafia.bot.models.Identity.IdentityType;

public class IdentityList extends LinkedList<Identity> {
	private static final long serialVersionUID = 4147877699411869865L;

	public Identity find(IdentityType type) {
		Iterator<?> iter = this.iterator();
		while (iter.hasNext()) {
			Identity iden = (Identity) iter.next();
			if (iden.type == type) return iden;
		}
		return null;
	}
	public IdentityList findAll(IdentityType type) {
		IdentityList list = new IdentityList();
		Iterator<?> iter = this.iterator();
		while (iter.hasNext()) {
			Identity iden = (Identity) iter.next();
			if (iden.type == type) list.add(iden);
		}
		return list;
	}
}
