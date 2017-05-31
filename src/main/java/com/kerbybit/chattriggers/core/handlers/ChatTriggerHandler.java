package com.kerbybit.chattriggers.core.handlers;

import com.kerbybit.chattriggers.core.events.CTEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ChatTriggerHandler extends Handler {

	public ChatTriggerHandler() {
		super();
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
