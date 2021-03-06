package com.kerbybit.chattriggers.references;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.commands.CommandTrigger;
import com.kerbybit.chattriggers.globalvars.Settings;
import com.kerbybit.chattriggers.globalvars.global;
import com.kerbybit.chattriggers.triggers.EventsHandler;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BugTracker {
    public static void send() {
        if (global.bugReport.size() > 0) {
            Thread threadSubmitBugReport = new Thread(() -> {
                try {
                    ChatHandler.warn("&7Sending bug report...");
                    StringBuilder bug = new StringBuilder();

                    if (global.bugLastCommand.equals("")) bug.append(global.bugLastEvent).append("\n\n");
                    else bug.append(global.bugLastCommand).append("\n\n");


                    for (String b : global.bugReport) {
                        bug.append(b).append("\n");
                    }

                    URL url = new URL("http://ct.kerbybit.com/bugreport/");
                    Map<String,Object> params = new LinkedHashMap<>();
                    params.put("name", Minecraft.getMinecraft().thePlayer.getDisplayNameString());
                    params.put("uuid", Minecraft.getMinecraft().thePlayer.getUniqueID());
                    params.put("bug", bug.toString());

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postDataBytes);
                    new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    ChatHandler.warn(Settings.col[0] + "Bug report submitted successfully!");
                    global.bugReport.clear();
                } catch (Exception e) {
                    ChatHandler.warn("&4An error occurred while submitting a bug report!");
                    ChatHandler.warn("&4Is ct.kerbybit.com down?");
                }
            });
            threadSubmitBugReport.start();
        }
    }

    public static void showMore() {
        ChatHandler.warnBreak(0);


        if (!global.bugLastCommand.equals("")) {
            ChatHandler.warn("red", "I don't know much, but I know this might be the cause");
            ChatHandler.warn(" " + ChatHandler.ignoreFormatting(global.bugLastCommand));
        } else if (!global.bugLastEvent.equals("")) {
            ChatHandler.warn("red", "I don't know much, but I know this might be the cause");
            ChatHandler.warn(" " + ChatHandler.ignoreFormatting(global.bugLastEvent));
        } else {
            ChatHandler.warn("red", "I have no extra info about this error to show :(");
        }

        ChatHandler.warnBreak(1);
    }

    public static void show(Exception e) {
        show(e, "none", null);
    }
    public static void show(Exception e, String type) {
        show(e, type, null);
    }

    public static void show(Exception e, String type, String extra) {
        if (e != null) {
            e.printStackTrace();

            for (StackTraceElement stack : e.getStackTrace()) {
                global.bugReport.add(stack.toString());
            }
        }
        if (extra != null) {
            global.bugLastCommand = extra;
            global.bugLastEvent = "";
        } else {
            if (type.equals("command")) {
                global.bugLastCommand = global.lastCommand;
                global.bugLastEvent = "";
            } else {
                global.bugLastCommand = "";
                global.bugLastEvent = global.lastEvent;
            }
        }
        ChatHandler.warn(ChatHandler.color("darkred",getError(type)));
        ChatHandler.warn("&f >> clickable(&cSubmit but report,run_command,/trigger submitbugreport,Send a bug report)");
        if (extra != null)
            ChatHandler.warn("&f >> clickable(&cShow mow about this error,run_command,/trigger showbugreport,Show more)");

        for (int i=0; i<global.onUnknownError.size(); i++) {
            //add all events to temp list
            List<String> TMP_events = new ArrayList<>();
            for (int j=2; j<global.onUnknownError.get(i).size(); j++) {TMP_events.add(global.onUnknownError.get(i).get(j));}

            //do events
            EventsHandler.doEvents(TMP_events, null);
        }
    }

    private static String getError(String type) {
        switch (type.toLowerCase()) {
            case "command":
                return "An unknown error occurred while performing this command";
            case "chat":
                return "An unknown error has occurred while executing \"&cchat&4\"";
            case "onrightclickplayer":
                return "An unknown error has occurred while executing \"&conRightClickPlayer&4\"";
            case "onworldload":
                return "An unknown error has occurred while executing \"&conWorldLoad&4\"";
            case "onclienttick":
                return "An unknown error has occurred while executing \"&conClientTick&4\"";
            case "async":
                CommandTrigger.commandLoad();
                return "An unknown error has occurred while executing \"&casync&4\"";
            case "onsoundplay":
                return "An unknown error has occurred while executing \"&conSoundPlay&4\"";
            case "event":
                return "An unknown error has occured while executing \"&cevent&4\"";
            default:
                return "An unknown error has occurred";
        }
    }
}
