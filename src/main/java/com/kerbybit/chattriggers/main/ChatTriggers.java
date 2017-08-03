package com.kerbybit.chattriggers.main;

import java.io.IOException;

import com.kerbybit.chattriggers.commands.CommandReference;
import com.kerbybit.chattriggers.gui.DisplayOverlay;
import com.kerbybit.chattriggers.objects.DisplayRenderer;
import com.kerbybit.chattriggers.objects.JsonHandler;
import com.kerbybit.chattriggers.objects.ListHandler;
import com.kerbybit.chattriggers.references.AsyncHandler;
import com.kerbybit.chattriggers.references.BugTracker;
import com.kerbybit.chattriggers.objects.DisplayHandler;
import com.kerbybit.chattriggers.overlay.KillfeedHandler;
import com.kerbybit.chattriggers.overlay.NotifyHandler;
import com.kerbybit.chattriggers.triggers.StringHandler;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import org.lwjgl.input.Keyboard;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.commands.CommandT;
import com.kerbybit.chattriggers.commands.CommandTR;
import com.kerbybit.chattriggers.commands.CommandTrigger;
import com.kerbybit.chattriggers.file.FileHandler;
import com.kerbybit.chattriggers.globalvars.global;
import com.kerbybit.chattriggers.gui.GuiTriggerList;
import com.kerbybit.chattriggers.references.Reference;
import com.kerbybit.chattriggers.triggers.EventsHandler;
import com.kerbybit.chattriggers.triggers.TriggerHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class ChatTriggers {
	private static KeyBinding altGuiKey;
    private static KeyBinding displayKey;
    private static KeyBinding displayMenuKey;

	@EventHandler
	public void init(FMLInitializationEvent event) throws ClassNotFoundException, IOException {
		MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);

        ClientCommandHandler.instance.registerCommand(new CommandTrigger());
        ClientCommandHandler.instance.registerCommand(new CommandT());
        ClientCommandHandler.instance.registerCommand(new CommandTR());
        
        altGuiKey = new KeyBinding("Trigger GUI", Keyboard.KEY_L, "ChatTriggers");
        displayKey = new KeyBinding("Killfeed Position", Keyboard.KEY_K, "ChatTriggers");
        displayMenuKey = new KeyBinding("Alternate Display Screen", Keyboard.KEY_F4, "ChatTriggers");

        ClientRegistry.registerKeyBinding(altGuiKey);
        ClientRegistry.registerKeyBinding(displayKey);
        ClientRegistry.registerKeyBinding(displayMenuKey);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileHandler.saveAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (global.canUse) {
			if (altGuiKey.isPressed()) {
				GuiTriggerList.inMenu = -1;
				global.showAltInputGui = true;
			}
            if (displayKey.isPressed()) {
                global.showDisplayGui = true;
            }
            if (displayMenuKey.isPressed()) {
                global.displayMenu = !global.displayMenu;
                try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
            }
		}
	}
	
	@SubscribeEvent
	public void onRightClickPlayer(EntityInteractEvent e) {
        try {
            if (global.canUse) {
                if (e.entity.equals(Minecraft.getMinecraft().thePlayer)) {
                    if (e.target instanceof EntityPlayer) {
                        TriggerHandler.onRightClickPlayer(e);
                    }
                }
            }
        } catch (Exception exception) {
            BugTracker.show(exception, "onRightClickPlayer");
        }
	}

	@SubscribeEvent
    public void onSoundPlay(PlaySoundEvent e) {
	    try {
            if (global.canUse) {
                TriggerHandler.onSoundPlay(e);
            }
        } catch (Exception exception) {
	        BugTracker.show(exception, "onSoundPlay");
        }
    }
	
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent e) throws IOException, ClassNotFoundException {
        try {
            if (global.canUse) {
                TriggerHandler.onChat(e);
            }
        } catch (Exception exception) {
            BugTracker.show(exception, "chat");
        }
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
        if (global.canUse) {
            global.worldLoaded=true;
            NotifyHandler.systimeResetNotify();
        }
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e) {
		if (global.canUse) {
			global.worldIsLoaded=false;
			global.chatQueue.clear();
			global.commandQueue.clear();
            DisplayHandler.clearDisplays();
		}
	}

	@SubscribeEvent
	public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {
		if (global.canUse) {
		    try {
                CommandReference.clickCalc();

                KillfeedHandler.drawKillfeed(event);
                NotifyHandler.drawNotify(event);

                DisplayRenderer.drawDisplays(event);


                GuiTriggerList.openGui();
                DisplayOverlay.openGui();

                FileHandler.firstFileLoad();

                try {
                    TriggerHandler.worldLoadTriggers();
                } catch (NullPointerException e) {
                    //Catch for replay mod
                }

                TriggerHandler.newDayTriggers();
                global.worldLoaded=false;
		    } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) throws ClassNotFoundException {
		if (global.canUse) {
		    StringHandler.updateMarkedStrings();
		    if (global.saveSoon) {
		        try {FileHandler.saveAll();}
		        catch (IOException exception) {
		            ChatHandler.warn(ChatHandler.color("red", "Something went wrong while loading the files after an import!"));
		        }
		        global.saveSoon = false;
            }



			KillfeedHandler.tickKillfeed();

			FileHandler.tickImports();

            try {
			    TriggerHandler.onClientTickTriggers();
            } catch (Exception exception) {
                BugTracker.show(exception, "onClientTick");
            }

            try {
                AsyncHandler.asyncTick();
            } catch (Exception exception) {
                BugTracker.show(exception, "async");
            }

            EventsHandler.eventTick();
            global.ticksElapsed++;

            JsonHandler.trimJsons();
            ListHandler.trimLists();

			ChatHandler.onClientTick();
		} else {
            Minecraft.getMinecraft().gameSettings.invertMouse = global.inverted;
        }
	}

	@SubscribeEvent
    public void onMouseEvent(MouseEvent e) {
	    if (e.button == 0 && e.buttonstate) {
	        global.clicks.add(20);
        }
        if (e.button == 1 && e.buttonstate) {
	        global.rclicks.add(20);
        }
    }
}