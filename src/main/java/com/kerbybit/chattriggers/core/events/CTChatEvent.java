package com.kerbybit.chattriggers.core.events;

import com.kerbybit.chattriggers.chat.ChatHandler;

public class CTChatEvent extends CTEvent {

	public CTChatEvent(String arguments)  {
		this.args = arguments;
		this.name = "chat";
	}

	public void call() {
		cleanVariables();
		ChatHandler.warn(args);
	}
}
