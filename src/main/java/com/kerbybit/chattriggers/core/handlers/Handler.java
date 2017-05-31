package com.kerbybit.chattriggers.core.handlers;

import com.kerbybit.chattriggers.core.events.CTEvent;

import java.util.ArrayList;

public class Handler {
	ArrayList<CTEvent> events;

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
