package com.kerbybit.chattriggers.core.triggers;

import com.kerbybit.chattriggers.core.handlers.ChatTriggerHandler;
import com.kerbybit.chattriggers.core.handlers.Handler;
import com.kerbybit.chattriggers.globalvars.global;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTChatTrigger extends Trigger {

	public CTChatTrigger() {
		super();
	}

	@SubscribeEvent
	public void onChatReceived(ClientChatReceivedEvent event){

		if (!global.canUse) return;

		for (Handler handler : handlers) {
			((ChatTriggerHandler) handler).handleEvents(event);
		}
	}
}
