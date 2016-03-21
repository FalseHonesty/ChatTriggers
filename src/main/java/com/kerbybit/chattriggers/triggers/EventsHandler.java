package com.kerbybit.chattriggers.triggers;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.globalvars.global;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class EventsHandler {
	public static void doEvents(List<String> tmp_tmp_event, ClientChatReceivedEvent chatEvent) {
		List<String> tmp_event = new ArrayList<String>(tmp_tmp_event);
		String[] snull = null;
		doEvents(tmp_event, chatEvent, snull, snull);
	}
	
	public static void doEvents(List<String> tmp_tmp_event, ClientChatReceivedEvent chatEvent, String[] toreplace, String[] replacement) {
		List<String> tmp_event = new ArrayList<String>(tmp_tmp_event);
		String stringCommaReplace = "stringCommaReplacementF6cyUQp9stringCommaReplacement";
		
		if (toreplace != null) {
			for (int i=0; i<toreplace.length; i++) {
				List<String> temporary = new ArrayList<String>();
				temporary.add("TriggerArgument"+i+"-"+global.TMP_string.size());
				temporary.add(replacement[i]);
				for (int j=0; j<tmp_event.size(); j++) {tmp_event.set(j, tmp_event.get(j).replace(toreplace[i],"{string[TriggerArgument"+i+"-"+global.TMP_string.size()+"]}"));}
				global.TMP_string.add(temporary);
			}
		}
		
		for (int i=0; i<tmp_event.size(); i++) {
		//SETUP
			String TMP_e = tmp_event.get(i);
			String TMP_c = "";
			if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e="";}
			else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ")+1, TMP_e.length());}
			int TMP_t = 50;
			int TMP_p = global.notifySize;
			int TMP_v = 10000;
			int TMP_pi = 1;
			
		//setup backup for functions so strings dont get overwritten
			StringHandler.resetBackupStrings();
			
		//built in strings
			TMP_e = StringHandler.builtInStrings(TMP_e, chatEvent);
			
		//user strings and functions
			TMP_e = TMP_e.replace("{string<", "{string[").replace("{array<", "{array[").replace(">}", "]}");
			
			TMP_e = StringHandler.stringFunctions(TMP_e);
			TMP_e = ArrayHandler.arrayFunctions(TMP_e);
			TMP_e = StringHandler.stringFunctions(TMP_e);
			
		//tags
			try {
				if (TMP_e.contains("<time=") && TMP_e.contains(">")) {
					TMP_t = Integer.parseInt(TagHandler.eventTags(1, TMP_e));
					TMP_e = TMP_e.replace("<time="+TMP_t+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<time=t> t must be an integer!"));}
			
			try {
				if (TMP_e.contains("<pos=") && TMP_e.contains(">")) {
					TMP_p = Integer.parseInt(TagHandler.eventTags(2, TMP_e));
					TMP_e = TMP_e.replace("<pos="+TMP_p+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<pos=p> p must be an integer!"));}
			
			try {
				if (TMP_e.contains("<vol=") && TMP_e.contains(">")) {
					TMP_v = Integer.parseInt(TagHandler.eventTags(3, TMP_e));
					TMP_e = TMP_e.replace("<vol="+TMP_v+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<vol=v> v must be an integer!"));}
			
			try {
				if (TMP_e.contains("<pitch") && TMP_e.contains(">")) {
					TMP_pi = Integer.parseInt(TagHandler.eventTags(4, TMP_e));
					TMP_e = TMP_e.replace("<pitch"+TMP_pi+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<pitch=p> p must be an integer!"));}
			
			
		//add formatting where needed
			if (TMP_c.equalsIgnoreCase("SAY") || TMP_c.equalsIgnoreCase("CHAT") || TMP_c.equalsIgnoreCase("KILLFEED") || TMP_c.equalsIgnoreCase("NOTIFY")) {
				if (TMP_c.equalsIgnoreCase("SAY")) {if (Minecraft.getMinecraft().isSingleplayer()==false) {TMP_e = ChatHandler.addFormatting(TMP_e);}} 
				else {TMP_e = ChatHandler.addFormatting(TMP_e);}
			}
			
		//non-logic events
			if (TMP_c.equalsIgnoreCase("TRIGGER")) {doTrigger(TMP_e, chatEvent);}
			TMP_e = TMP_e.replace(stringCommaReplace, ",");
			if (TMP_c.equalsIgnoreCase("SAY")) {global.chatQueue.add(TMP_e);}
			if (TMP_c.equalsIgnoreCase("CHAT")) {ChatHandler.warn(TMP_e);}
			if (TMP_c.equalsIgnoreCase("DO") && global.debug==true) {ChatHandler.warn(TMP_e);}
			if (TMP_c.equalsIgnoreCase("SOUND")) {Minecraft.getMinecraft().thePlayer.playSound(TMP_e, TMP_v, TMP_pi);}
			if (TMP_c.equalsIgnoreCase("CANCEL") && chatEvent!=null) {chatEvent.setCanceled(true);}
			if (TMP_c.equalsIgnoreCase("KILLFEED")) {global.killfeed.add(TMP_e); global.killfeedDelay.add(TMP_t);}
			if (TMP_c.equalsIgnoreCase("NOTIFY")) {
				global.notify.add(TMP_e);
				List<Float> temp_list = new ArrayList<Float>();
				temp_list.add((float) 0);temp_list.add((float) -1000);
				temp_list.add((float) TMP_p);temp_list.add((float) TMP_t);
				temp_list.add((float) 0);temp_list.add((float) -1000);
				global.notifyAnimation.add(temp_list);
				global.notifySize++;
			}
			if (TMP_c.equalsIgnoreCase("COMMAND")) {global.commandQueue.add(TMP_e);}
			if (TMP_c.equalsIgnoreCase("COPY")) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(TMP_e), null);
			}
			if (TMP_c.equalsIgnoreCase("URL")) {
				try {Desktop.getDesktop().browse(URI.create(TMP_e));}
				catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Unable to open URL! IOExeption"));}
			}
			if (TMP_c.equalsIgnoreCase("SKIN")) {
				String[] args = TMP_e.split(" ");
				if (args[0].equalsIgnoreCase("CAPE")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.CAPE)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.CAPE)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("JACKET")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.JACKET)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.JACKET)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("LEFT_SLEEVE")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.LEFT_SLEEVE)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.LEFT_SLEEVE)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("RIGHT_SLEEVE")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.RIGHT_SLEEVE)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.RIGHT_SLEEVE)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("LEFT_PANTS_LEG")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.LEFT_PANTS_LEG)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.LEFT_PANTS_LEG)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("RIGHT_PANTS_LEG")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.RIGHT_PANTS_LEG)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.RIGHT_PANTS_LEG)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("HAT")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, false);
						} else {
							if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.HAT)) {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, false);
							} else {
								Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, true);
							}
						}
					} else {
						if (Minecraft.getMinecraft().gameSettings.getModelParts().contains(EnumPlayerModelParts.HAT)) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, false);
						} else {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, true);
						}
					}
				} else if (args[0].equalsIgnoreCase("ALL")) {
					if (args.length>=2) {
						if (args[1].equalsIgnoreCase("ON") || args[1].equalsIgnoreCase("TRUE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, true);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, true);
						} else if (args[1].equalsIgnoreCase("OFF") || args[1].equalsIgnoreCase("FALSE")) {
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, false);
							Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, false);
						} else {ChatHandler.warn(ChatHandler.color("red", "Please specify 'on' or 'off' in event:skin all"));}
					} else {ChatHandler.warn(ChatHandler.color("red", "Please specify 'on' or 'off' in event:skin all"));}
				}
			}
			
			
		//logic events
			if (TMP_c.equalsIgnoreCase("ASYNC")) {
				int tabbed_logic = 0;
				List<String> eventsToAsync = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) {
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToAsync.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
					}
					
					eventsToAsync.remove(0);
					eventsToAsync.remove(eventsToAsync.size()-1);
					global.asyncEvents.clear();
					global.asyncEvents.addAll(eventsToAsync);
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					          EventsHandler.doEvents(global.asyncEvents, null);
					          global.asyncEvents.clear();
					     }
					});
					t1.start();
					
				}
				
				//move i
				i += eventsToAsync.size();
			}
			
			if (TMP_c.equalsIgnoreCase("WAIT")) {
				int tabbed_logic = 0;
				List<String> eventsToWait = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) { //check for events after if event
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							if (chatEvent!=null) {tmp_event.set(j, tmp_event.get(j).replace("{msg}", chatEvent.message.getFormattedText()));}
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToWait.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					eventsToWait.remove(0);
					eventsToWait.remove(eventsToWait.size()-1);
					try {
						int TMP_time = Integer.parseInt(TMP_e);
						if (TMP_time>0) {
							global.waitEvents.add(eventsToWait);
							global.waitTime.add(Integer.parseInt(TMP_e));
						} else {
							ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
						ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
					}
				}
				
				//move i
				i += eventsToWait.size();
			}
			
			if (TMP_c.equalsIgnoreCase("FOR")) {
				int tabbed_logic = 0;
				List<String> eventsToFor = new ArrayList<String>();
				String[] tmp_valuefor = TMP_e.split(":");
				String valin = "";
				String valfrom = "";
				List<String> arrayto = new ArrayList<String>();
				if (tmp_valuefor.length==2) {
					valin = tmp_valuefor[0].trim();
					valfrom = tmp_valuefor[1].trim();
				} else {ChatHandler.warn(ChatHandler.color("red", "Malformed FOR loop!"));}
				for (int j=0; j<global.USR_array.size(); j++) {
					if (global.USR_array.get(j).get(0).equals(valfrom)) {
						arrayto.addAll(global.USR_array.get(j));
					}
				}
				
				if (i+1 < tmp_event.size()) {
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToFor.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
				}
				
				eventsToFor.remove(0);
				eventsToFor.remove(eventsToFor.size()-1);
				
				
				if (arrayto.size()>0 && eventsToFor.size() > 0) {
					for (int j=1; j<arrayto.size(); j++) {
						String[] first = {valin};
						String[] second = {arrayto.get(j)};
						doEvents(eventsToFor, chatEvent, first, second);
					}
				}
				
				//move i
				i += eventsToFor.size();
			}
			
			if (TMP_c.equalsIgnoreCase("IF")) {
				int tabbed_logic = 0;
				List<String> eventsToIf = new ArrayList<String>();
				List<String> eventsToElse = new ArrayList<String>();
				Boolean gotoElse = false;
				
				if (i+1 < tmp_event.size()-1) { //check for events after if event
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//move to else
							if (tmp_event.get(j).toUpperCase().startsWith("ELSE") && tabbed_logic==1) {gotoElse=true;}
							
							//add to list
							if (gotoElse==false) {eventsToIf.add(tmp_event.get(j));} 
							else {eventsToElse.add(tmp_event.get(j));}
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					//move i to end of if
					i += eventsToIf.size()+eventsToElse.size()-2;
					
					//&& || ^
					String[] checkSplit = TMP_e.split(" ");
					for (int j=1; j<checkSplit.length; j++) {
						if (checkSplit[j].equals("&&")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") && checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
						if (checkSplit[j].equals("||")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") || checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
						if (checkSplit[j].equals("^")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") ^ checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
					}
					TMP_e = "";
					for (String value : checkSplit) {TMP_e += value + " ";}
					TMP_e = TMP_e.trim();
					
					//check condition and do events
					if (TMP_e.equalsIgnoreCase("TRUE") || TMP_e.equalsIgnoreCase("NOT FALSE")) {
						if (eventsToIf.size()>0) {
							eventsToIf.remove(0);
							doEvents(eventsToIf, chatEvent);
						}
					} else {
						if (eventsToElse.size()>0) {
							eventsToElse.remove(0);
							doEvents(eventsToElse, chatEvent);
						}
					}
				}
			}
			
			
			if (TMP_c.equalsIgnoreCase("CHOOSE")) {
				int tabbed_logic = 0;
				List<List<String>> eventsToChoose = new ArrayList<List<String>>();
				List<String> eventsToChooseSub = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) {
					for (int j=i; j<tmp_event.size(); j++) {

						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//check if first level event
							if (tabbed_logic==1) {
								if (eventsToChooseSub.size() > 0) { //add more than first level events
									List<String> tmp_list2nd = new ArrayList<String>(eventsToChooseSub);
									eventsToChoose.add(tmp_list2nd);
									eventsToChooseSub.clear(); //clears sub choice to add first level event
								}
								eventsToChooseSub.add(tmp_event.get(j));
								List<String> tmp_list1st = new ArrayList<String>(eventsToChooseSub);
								eventsToChoose.add(tmp_list1st); //add first level event
								eventsToChooseSub.clear(); //clear sub choose
							}
							
							//check if greater than first level event
							if (tabbed_logic>1) {eventsToChooseSub.add(tmp_event.get(j));}
							
							//check for last event to group and close any leftover sub choose
							if (j == tmp_event.size()-1 && eventsToChoose.size() > 0) {eventsToChoose.add(eventsToChooseSub);}
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
							
							//check again for first level event
							if (tabbed_logic==1) {
								if (eventsToChooseSub.size() > 0) {//add more than first level events
									List<String> tmp_list3rd = new ArrayList<String>(eventsToChooseSub);
									eventsToChoose.add(tmp_list3rd);
									eventsToChooseSub.clear(); //clear sub choose
								}
							}
						}
					
						//check if choose exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					//random number
					int rand = randInt(1,eventsToChoose.size()-2);
					
					//do events
					doEvents(eventsToChoose.get(rand), chatEvent);
					
					//move i to closing end
					int moveEvents = 0;
					for (int j=0; j<eventsToChoose.size(); j++) {moveEvents += eventsToChoose.get(j).size();}
					i += moveEvents-1;
				}
			}
		}
	}
	
	public static void doTrigger(String triggerName, ClientChatReceivedEvent chatEvent) {
		try {
			//run trigger by number
			int num = Integer.parseInt(triggerName);
			if (num >= 0 && num < global.trigger.size()) {
				//add all events to temp list
				List<String> TMP_events = new ArrayList<String>();
				for (int i=2; i<global.trigger.get(num).size(); i++) {TMP_events.add(global.trigger.get(num).get(i));}
				
				//do events
				doEvents(TMP_events, chatEvent);
			}
		} catch (NumberFormatException e1) { 
			//run trigger by name
			for (int k=0; k<global.trigger.size(); k++) {
				String TMP_trig = global.trigger.get(k).get(1);
				
				TMP_trig = TagHandler.removeTags(TMP_trig);
				
				//check match
				if (TMP_trig.equals(triggerName)) {
					//add all events to temp list
					List<String> TMP_events = new ArrayList<String>();
					for (int i=2; i<global.trigger.get(k).size(); i++) {TMP_events.add(global.trigger.get(k).get(i));}
					
					//do events
					doEvents(TMP_events, chatEvent);
				} else {
					if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
						String TMP_trigtest = TMP_trig.substring(0,TMP_trig.indexOf("("));
						if (triggerName.startsWith(TMP_trigtest) && triggerName.endsWith(")")) {
							String TMP_argsIn = triggerName.substring(triggerName.indexOf("(")+1, triggerName.length()-1);
							String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(")+1, TMP_trig.length()-1);
							String[] argsIn = TMP_argsIn.split(",");
							String[] argsOut = TMP_argsOut.split(",");
							if (argsIn.length == argsOut.length) {
								List<String> TMP_events = new ArrayList<String>();
								for (int j=2; j<global.trigger.get(k).size(); j++) {TMP_events.add(global.trigger.get(k).get(j));}
								doEvents(TMP_events, chatEvent, argsOut, argsIn);
							}
						}
					}
				}
			}
		}
	}
	
	public static void eventTick() {
		try {
			global.playerHealth = (int) Minecraft.getMinecraft().thePlayer.getHealth();
		} catch (NullPointerException e1) {/*do nothing*/}
		
		try {Minecraft.getMinecraft().thePlayer.isServerWorld();} 
		catch (NullPointerException e1) {
			if (global.waitEvents.size()>0) {
				global.waitEvents.clear();
				global.waitTime.clear();
			}
			if (global.asyncEvents.size()>0) {
				global.asyncEvents.clear();
			}
		}
		
		if (global.waitEvents.size()==0 && global.asyncEvents.size()==0 && global.TMP_string.size()>0) {global.TMP_string.clear();}
		
		if (global.waitEvents.size()>0) {
			if (global.waitEvents.size() == global.waitTime.size()) {
				for (int i=0; i<global.waitTime.size(); i++) {
					if (global.waitTime.get(i)>0) {
						global.waitTime.set(i, global.waitTime.get(i)-1);
					} else {
						doEvents(global.waitEvents.get(i), null);
						global.waitEvents.remove(i);
						global.waitTime.remove(i);
					}
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red","SOMETHING WENT WRONG!!! (wait event/time unsynced)"));
				global.waitEvents.clear();
				global.waitTime.clear();
			}
		}
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
}