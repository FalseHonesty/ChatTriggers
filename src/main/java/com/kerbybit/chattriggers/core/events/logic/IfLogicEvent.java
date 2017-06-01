package com.kerbybit.chattriggers.core.events.logic;

import com.kerbybit.chattriggers.core.events.CTEvent;
import com.kerbybit.chattriggers.core.variables.CTBoolean;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;

public class IfLogicEvent extends CTEvent {

	private ArrayList<CTEvent> innerEvents;

	public IfLogicEvent(String arguments) {
		name = "if";
		args = arguments;

		innerEvents = new ArrayList<>();
	}

	public void addEvent(CTEvent ctEvent) {
		innerEvents.add(ctEvent);
	}

	public void clearEvents() {
		innerEvents.clear();
	}

	@Override
	public void call() {

		cleanVariables();
		addBuiltIns(null);

		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		String bool = null;
		try {
			bool = String.valueOf(engine.eval(args));
		} catch (ScriptException e) {
			e.printStackTrace();
		}

		if (bool == null || !new CTBoolean(bool).get()) {
			return;
		}

		for (CTEvent event : innerEvents) {
			event.call();
		}
	}
}
