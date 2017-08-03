package com.kerbybit.chattriggers.util;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.triggers.EventsHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNoCallback;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

/**
 * Copyright (c) FalseHonesty 2017
 */
public class GuiYesNoImplementation implements GuiYesNoCallback {
    private String eventLink;

    public GuiYesNoImplementation(String eventLink) {
        this.eventLink = eventLink;
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            try {
                Desktop.getDesktop().browse(URI.create(EventsHandler.removeStringReplacements(eventLink)));
            } catch (IOException e) {
                ChatHandler.warn(ChatHandler.color("red", "Unable to open URL! IOException"));
            }
        } else {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}
