package com.kerbybit.chattriggers.core.handlers;

import com.kerbybit.chattriggers.core.events.CTEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ChatTriggerHandler extends Handler {

	public ChatTriggerHandler(String name) {
		super();

		this.name = name == null || name == "" ? "chat" : name;
	}

	public void handleEvents(ClientChatReceivedEvent event) {
		for (CTEvent ctEvent : events) {
			ctEvent.addBuiltIns(event);
			ctEvent.call();
		}
	}

	@Override
	public void handleEvents(){}
}
