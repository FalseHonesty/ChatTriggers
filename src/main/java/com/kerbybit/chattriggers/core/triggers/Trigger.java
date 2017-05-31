package com.kerbybit.chattriggers.core.triggers;

import com.kerbybit.chattriggers.core.handlers.Handler;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public abstract class Trigger {
	ArrayList<Handler> handlers;

	public Trigger() {
		handlers = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addHandler(Handler handler) {
		handlers.add(handler);
	}

	public void clearHandlers(){handlers.clear();}

	public void triggered() {
		for (Handler handler : handlers) {
			handler.handleEvents();
		}
	}
}
