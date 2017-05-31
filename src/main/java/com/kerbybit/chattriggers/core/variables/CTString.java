package com.kerbybit.chattriggers.core.variables;

import net.minecraft.util.ChatComponentText;

public class CTString implements Variable {

    private String value;

    public CTString(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }

    public void set(String newVal) {
        value = newVal;
    }

    @Override
    public void call(String operation, String args) {
        switch (operation.toLowerCase()) {
            case "append":
                opAppend(args);
			case "remform":
				opRemForm();
        }
    }

	@Override
	public String getStringValue() {
		return value;
	}

	private void opAppend (String args) {
		value += args;
    }

    private void opRemForm() {
		ChatComponentText cct = new ChatComponentText(value);
		value = cct.getUnformattedText();
    }
}
