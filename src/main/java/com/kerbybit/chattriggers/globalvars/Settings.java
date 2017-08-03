package com.kerbybit.chattriggers.globalvars;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Settings {
    private static ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

    public static String[] col = new String[]{"&6","gold"};

    public static Boolean killfeedInNotify = false;
    public static Boolean killfeedFade = true;
    public static int notifySpeed = 10;
    public static int notifyPause = 50;
    public static int killfeedPause = 50;
    public static Double[] killfeedPosition = new Double[]{5.0/res.getScaledWidth(), 5.0/res.getScaledHeight()};

    public static Boolean commandT = true;
    public static Boolean commandTR = true;

    public static String lastOpened = "1997/01/01";

    public static Boolean isBeta = false;
    public static Boolean backupFiles = false;
    public static String version = "null";

    public static Boolean oldFormatting = false;

    public static String dateFormat = "dd/MM/yyyy";
    public static String timeFormat = "h:mm:ss";

    public static Boolean notifyBackground = true;

    public static Boolean killfeedBackground = false;
    public static boolean displayLinkWarning = true;
}
