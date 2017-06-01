package com.kerbybit.chattriggers.core.events;

import com.kerbybit.chattriggers.core.variables.CTString;
import com.kerbybit.chattriggers.core.variables.Variable;
import com.kerbybit.chattriggers.globalvars.global;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public abstract class CTEvent {
	protected String name;
	protected String args;

	public abstract void call();

	protected void cleanVariables() {
		while (args.contains("{var[")) {
			int index = args.indexOf("{var[") + 5;
			String varName = args.substring(index, args.indexOf("]"));

			Variable var = global.variables.get(varName);
			String varValue = var == null ? "null" : var.toString();
			args = args.replace("{var[" + varName + "]}", varValue);
		}
	}

	public void addBuiltIns(ClientChatReceivedEvent event) {
		if (args.contains("{msg}") && event != null) {
			if (global.variables.containsKey("msg")) {
				global.variables.replace("msg", new CTString(event.message.getFormattedText()));
			} else {
				global.variables.put("msg", new CTString(event.message.getFormattedText()));
			}

			args = args.replaceAll("\\{msg}", "{var[msg]}");
		}
	}
}
