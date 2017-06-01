package com.kerbybit.chattriggers.core.handlers;

import com.kerbybit.chattriggers.core.events.CTEvent;

import java.util.ArrayList;

public class Handler {
	protected ArrayList<CTEvent> events;
	protected String name;

	public Handler() {
		events = new ArrayList<>();
	}

	public void addEvent(CTEvent ctEvent) {
		events.add(ctEvent);
	}

	public void clearEvents() {events.clear();}

	public void handleEvents(){
		for (CTEvent event : events) {
			event.call();
		}
	}
}
