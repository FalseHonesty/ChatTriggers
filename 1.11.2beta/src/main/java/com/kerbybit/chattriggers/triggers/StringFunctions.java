package com.kerbybit.chattriggers.triggers;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.file.FileHandler;
import com.kerbybit.chattriggers.globalvars.global;
import com.kerbybit.chattriggers.objects.ArrayHandler;
import com.kerbybit.chattriggers.objects.DisplayHandler;
import com.kerbybit.chattriggers.objects.JsonHandler;
import com.kerbybit.chattriggers.objects.ListHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kerbybit.chattriggers.triggers.StringHandler.stringFunctions;
import static java.lang.Math.abs;
import static net.minecraft.realms.RealmsMth.floor;

public class StringFunctions {
    static String doStringFunctions(String stringName, String func, String args, ClientChatReceivedEvent chatEvent, Boolean isAsync) {
        func = func.toUpperCase();

        args = nestedArgs(args, chatEvent, isAsync);

        String stringValue = getStringValue(stringName, isAsync);
        if (stringValue == null) {
            stringValue = createTempString(stringName, isAsync);
        }
        Double stringPos = getStringPos(stringName, isAsync);

        String returnString;

        returnString = doStringSetFunctions(stringPos, stringName, func, args);

        if (returnString == null) {
            returnString = doStringModifyFunctions(stringName, stringValue, func, args);
        }
        if (returnString == null) {
            returnString = doStringComparatorFunctions(stringValue, func, args);
        }
        if (returnString == null) {
            returnString = doStringMathFunctions(stringValue, func, args);
        }
        if (returnString == null) {
            returnString = doStringUserFunctions(stringValue, func, args, chatEvent);
        }

        if (returnString != null && getIsObject(returnString)) {
            return returnString;
        } else {
            if (returnString != null) {
                if (isAsync || stringPos == null) {
                    global.Async_string.put(stringName, returnString);
                } else {
                    if (stringPos >= 0) {
                        global.USR_string.get(floor(stringPos)).set(1, returnString);
                    } else {
                        global.TMP_string.get(floor(abs(stringPos) - 1)).set(1, returnString);
                    }
                }
            }
        }
        return "{string[" + stringName + "]}";
    }

    private static Boolean getIsObject(String in) {
        return in.startsWith("{list[") || in.startsWith("{array[")
                || in.startsWith("{display[") || in.startsWith("{json[");
    }

    private static String doStringSetFunctions(Double stringPos,String stringName, String func, String args) {
        args = addExtras(args);

        if (func.equals("SET")) {
            if (args.equals("~")) {
                if (stringPos == null) {
                    String set = addExtras(global.Async_string.get(stringName));
                    global.backupAsync_string.put(stringName, set);
                    return set;
                } else {
                    if (stringPos >= 0) {
                        String set = addExtras(global.USR_string.get(floor(stringPos)).get(1));
                        global.backupUSR_strings.get(floor(stringPos)).set(1, set);
                        return set;
                    } else {
                        String set = addExtras(global.TMP_string.get(floor(abs(stringPos)-1)).get(1));
                        global.backupTMP_strings.get(floor(abs(stringPos)-1)).set(1, set);
                        return set;
                    }
                }
            } else {
                if (stringPos == null) {
                    global.Async_string.put(stringName, args);
                    global.backupAsync_string.put(stringName, args);
                    return args;
                } else {
                    if (stringPos >= 0) {
                        global.USR_string.get(floor(stringPos)).set(1, args);
                        global.backupUSR_strings.get(floor(stringPos)).set(1, args);
                        return args;
                    } else {
                        global.TMP_string.get(floor(abs(stringPos) - 1)).set(1, args);
                        global.backupTMP_strings.get(floor(abs(stringPos) - 1)).set(1, args);
                        return args;
                    }
                }
            }
        } else if (func.equals("SAVE")) {
            String ret;
            if (args.equals("~")) {
                if (stringPos == null) {
                    String set = addExtras(global.Async_string.get(stringName));
                    global.backupAsync_string.put(stringName, set);
                    ret = set;
                } else {
                    if (stringPos >= 0) {
                        String set = addExtras(global.USR_string.get(floor(stringPos)).get(1));
                        global.backupUSR_strings.get(floor(stringPos)).set(1, set);
                        ret = set;
                    } else {
                        String set = addExtras(global.TMP_string.get(floor(abs(stringPos)-1)).get(1));
                        global.backupTMP_strings.get(floor(abs(stringPos)-1)).set(1, set);
                        ret = set;
                    }
                }
            } else {
                if (stringPos == null) {
                    global.Async_string.put(stringName, args);
                    global.backupAsync_string.put(stringName, args);
                    ret = args;
                } else {
                    if (stringPos >= 0) {
                        global.USR_string.get(floor(stringPos)).set(1, args);
                        global.backupUSR_strings.get(floor(stringPos)).set(1, args);
                        ret = args;
                    } else {
                        global.TMP_string.get(floor(abs(stringPos) - 1)).set(1, args);
                        global.backupTMP_strings.get(floor(abs(stringPos) - 1)).set(1, args);
                        ret = args;
                    }
                }
            }
            try {
                FileHandler.saveAll();
            } catch (IOException e) {
                ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));
            }
            return ret;
        }

        return null;
    }

    private static String removeExtras(String in) {
        return in.replace("(","stringOpenBracketF6cyUQp9stringOpenBracket")
                .replace(")","stringCloseBracketF6cyUQp9stringCloseBracket")
                .replace(",", "stringCommaReplacementF6cyUQp9stringCommaReplacement");
    }

    private static String removeExcludedExtras(String in) {
        return in.replace("'('", "stringOpenBracketF6cyUQp9stringOpenBracket")
                .replace("')'", "stringCloseBracketF6cyUQp9stringCloseBracket")
                .replace("','", "stringCommaReplacementF6cyUQp9stringCommaReplacement");
    }

    private static String addExtras(String in) {
        return in.replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
                .replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")")
                .replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",");
    }

    private static String doStringModifyFunctions(String stringName, String stringValue, String func, String args) {
        if (func.equals("REPLACE")) {
            args = removeExcludedExtras(args);
            if (args.contains(",") && args.split(",").length == 2) {
                return stringValue.replace(args.split(",")[0], args.split(",")[1]);
            } else {
                return stringValue.replace(args,"");
            }
        } else if (func.equals("REPLACEIGNORECASE")) {
            args = removeExcludedExtras(args);
            if (args.contains(",") && args.split(",").length == 2) {
                return stringValue.replaceAll("(?i)"+args.split(",")[0], args.split(",")[1]);
            } else {
                return stringValue.replaceAll("(?i)"+args, "");
            }
        } else if (func.equals("TRIM")) {
            return stringValue.trim();
        } else if (func.equals("PREFIX")) {
            return args + stringValue;
        } else if (func.equals("SUFFIX")) {
            return stringValue + args;
        } else if (func.equals("TOUPPER") || func.equals("TOUPPERCASE")) {
            return stringValue.toUpperCase();
        } else if (func.equals("TOLOWER") || func.equals("TOLOWERCASE")) {
            return stringValue.toLowerCase();
        } else if (func.equals("REMOVEFORMATTING") || func.equalsIgnoreCase("REMFORM")) {
            return stringValue.replaceAll("&[1-r]", "");
        } else if (func.equals("CAPITALIZEFIRSTWORD") || func.equals("CAPFIRST")) {
            if (stringValue.equals("")) return stringValue;
            else return stringValue.substring(0,1).toUpperCase()+stringValue.substring(1);
        } else if (func.equals("CAPITALIZEALLWORDS") || func.equals("CAPALL")) {
            return WordUtils.capitalizeFully(stringValue);
        } else if (func.equals("IGNOREESCAPE")) {
            return stringValue.replace("\\", "\\\\");
        } else if (func.equals("FIXLINKS")) {
            for (String value : stringValue.split(" ")) {
                String newvalue = ChatHandler.deleteFormatting(value);
                value = value.replace("...","TripleDotF6cyUQp9TripleDot");
                if (value.contains(".")) {
                    if (!(newvalue.toUpperCase().startsWith("HTTP://") || newvalue.toUpperCase().startsWith("HTTPS://"))) {
                        newvalue = "http://"+value;
                    }
                    stringValue = stringValue.replace(value.replace("...","TripleDotF6cyUQp9TripleDot"), "{link[" + value + "],[" + newvalue + "]}");
                }
            }
            return stringValue;
        } else if (func.equals("SUBSTRING")) {
            args = removeExcludedExtras(args);
            String[] subargs = args.split(",");
            if (subargs.length == 2) {
                int first = -1;
                int last = -1;
                Boolean getStart = false;
                Boolean startContain = false;
                Boolean startAlwaysNumber = false;
                Boolean getEnd = false;
                Boolean endContain = false;
                Boolean endAlwaysNumber = false;
                if (subargs[0].toUpperCase().contains("<START>") || subargs[0].toUpperCase().contains("<S>")) {
                    getStart = true;
                    subargs[0] = subargs[0].replaceAll("(?i)<start>", "").replaceAll("(?i)<s>", "");
                }
                if (subargs[0].toUpperCase().contains("<INCLUDE>") || subargs[0].toUpperCase().contains("<I>")) {
                    startContain = true;
                    subargs[0] = subargs[0].replaceAll("(?i)<include>", "").replaceAll("(?i)<i>", "");
                }
                if (subargs[0].toUpperCase().contains("<NUMBER>") || subargs[0].toUpperCase().contains("<N>")) {
                    startAlwaysNumber = true;
                    subargs[0] = subargs[0].replaceAll("(?i)<number>", "").replaceAll("(?i)<n>", "");
                }
                if (subargs[0].toUpperCase().contains("<TEXT>") || subargs[0].toUpperCase().contains("<T>")) {
                    subargs[0] = subargs[0].replaceAll("(?i)<text>", "").replaceAll("(?i)<t>", "");
                }


                if (subargs[1].toUpperCase().contains("<END>") || subargs[1].toUpperCase().contains("<E>")) {
                    getEnd = true;
                    subargs[1] = subargs[1].replaceAll("(?i)<end>", "").replaceAll("(?i)<e>", "");
                }
                if (subargs[1].toUpperCase().contains("<INCLUDE>") || subargs[1].toUpperCase().contains("<I>")) {
                    endContain = true;
                    subargs[1] = subargs[1].replaceAll("(?i)<include>", "").replaceAll("(?i)<i>", "");
                }
                if (subargs[1].toUpperCase().contains("<NUMBER>") || subargs[1].toUpperCase().contains("<N>")) {
                    endAlwaysNumber = true;
                    subargs[1] = subargs[1].replaceAll("(?i)<number>", "").replaceAll("(?i)<n>", "");
                }
                if (subargs[1].toUpperCase().contains("<TEXT>") || subargs[1].toUpperCase().contains("<T>")) {
                    subargs[1] = subargs[1].replaceAll("(?i)<text>", "").replaceAll("(?i)<t>", "");
                }

                String temp = removeExtras(stringValue);
                if (getStart) first = 0;
                if (getEnd) last = temp.length();
                if (first == -1) {
                    if (temp.contains(subargs[0]) && !startAlwaysNumber) {
                        if (startContain) first = temp.indexOf(subargs[0]);
                        else first = temp.indexOf(subargs[0]) + subargs[0].length();
                    } else {
                        try {
                            first = Integer.parseInt(subargs[0]);
                        } catch (NumberFormatException e) {
                            if (global.debug) ChatHandler.warn(ChatHandler.color("gray", "Did not find " + subargs[0] + " in string"));
                            return null;
                        }
                    }
                }
                if (last == -1) {
                    if (temp.contains(subargs[1]) && !endAlwaysNumber) {
                        if (endContain) last = temp.indexOf(subargs[1]) + subargs[1].length();
                        else last = temp.indexOf(subargs[1]);
                    } else {
                        try {
                            last = Integer.parseInt(subargs[1]);
                        } catch (NumberFormatException e) {
                            if (global.debug) ChatHandler.warn(ChatHandler.color("gray", "Did not find " + subargs[1] + " in string"));
                            return null;
                        }
                    }
                }

                if (first != -1 && last != -1) {
                    temp = temp.substring(first, last);
                    return addExtras(temp);
                }
            }
        } else if (func.equals("SPLIT")) {
            String[] splitString = stringValue.split(args);
            StringBuilder list = new StringBuilder("[");
            for (String value : splitString) {
                list.append(value).append(",");
            }
            list = new StringBuilder(list.substring(0, list.length()-1) + "]");

            ListHandler.getList("StringToList->"+stringName+"SPLIT-"+(ListHandler.getListsSize()+1), list.toString());
            return "{list[StringToList->"+stringName+"SPLIT-"+ListHandler.getListsSize()+"]}";
        }

        return null;
    }

    private static String doStringComparatorFunctions(String stringValue, String func, String args) {
        if (func.equals("EQUALS") || func.equals("=") || func.equals("==")) {
            return trimBool(stringValue.equals(args));
        } else if (func.equals("EQUALSIGNORECASE")) {
            return trimBool(stringValue.equalsIgnoreCase(args));
        } else if (func.equals("STARTSWITH")) {
            return trimBool(stringValue.startsWith(args));
        } else if (func.equals("STARTSWITHIGNORECASE")) {
            return trimBool(stringValue.toUpperCase().startsWith(args.toUpperCase()));
        } else if (func.equals("CONTAINS")) {
            return trimBool(stringValue.contains(args));
        } else if (func.equals("CONTAINSIGNORECASE")) {
            return trimBool(stringValue.toUpperCase().contains(args.toUpperCase()));
        } else if (func.equals("ENDSWITH")) {
            return trimBool(stringValue.endsWith(args));
        } else if (func.equals("ENDSWITHIGNORECASE")) {
            return trimBool(stringValue.toUpperCase().endsWith(args.toUpperCase()));
        }

        Double stringValueNumber;
        Double argsValueNumber;
        try {
            stringValueNumber = Double.parseDouble(stringValue.replace(",", ""));
            argsValueNumber = Double.parseDouble(args.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }

        if (func.equals("LESSTHAN") || func.equals("LT") || func.equals("<")) {
            return trimBool(stringValueNumber<argsValueNumber);
        } else if (func.equals("LESSTHANOREQUALTO") || func.equals("LTE") || func.equals("<=")) {
            return trimBool(stringValueNumber<=argsValueNumber);
        } else if (func.equals("GREATORTHAN") || func.equals("GT") || func.equals(">")) {
            return trimBool(stringValueNumber>argsValueNumber);
        } else if (func.equals("GREATORTHANOREQUALTO") || func.equals("GTE") || func.equals(">=")) {
            return trimBool(stringValueNumber>=argsValueNumber);
        }

        return null;
    }

    private static String trimBool(Boolean in) {
        if (in) {
            return "true";
        }
        return "false";
    }

    private static String doStringMathFunctions(String stringValue,String func, String args) {
        if (func.equals("LENGTH")) {
            return trimNumber(stringValue.length());
        } else if (func.equals("SIZE")) {
            return trimNumber(Minecraft.getMinecraft().fontRendererObj.getStringWidth(stringValue));
        }

        Double stringValueNumber;
        Double argsValueNumber;
        try {
            stringValueNumber = Double.parseDouble(stringValue.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }

        try {
            argsValueNumber = Double.parseDouble(args.replace(",", ""));
        } catch (NumberFormatException e) {
            argsValueNumber = null;
        }

        if (argsValueNumber != null) {
            if (func.equals("ADD") || func.equals("+")) {
                return trimNumber(stringValueNumber + argsValueNumber);
            } else if (func.equals("SUBTRACT") || func.equals("MINUS") || func.equals("-")) {
                return trimNumber(stringValueNumber - argsValueNumber);
            } else if (func.equals("MULTIPLY") || func.equals("TIMES") || func.equals("*")) {
                return trimNumber(stringValueNumber * argsValueNumber);
            } else if (func.equals("DIVIDE") || func.equals("/")) {
                return trimNumber(stringValueNumber / argsValueNumber);
            } else if (func.equals("DIVIDEGETPERCENT") || func.equals("DIVPERCENT") || func.equals("/%")) {
                return trimNumber((stringValueNumber / argsValueNumber) * 100);
            } else if (func.equals("POW") || func.equals("POWER") || func.equals("^")) {
                return trimNumber(Math.pow(stringValueNumber, argsValueNumber));
            } else if (func.equals("MOD") || func.equals("MODULUS") || func.equals("%")) {
                return trimNumber(stringValueNumber % argsValueNumber);
            } else if (func.equals("ABSOLUTE") || func.equals("ABS")) {
                return trimNumber(Math.abs(stringValueNumber));
            }
        }

        if (func.equals("ROUND")) {
            if (argsValueNumber == null) {
                return trimNumber(Math.round(stringValueNumber));
            } else {
                return trimNumber(Math.round(stringValueNumber * Math.pow(10, argsValueNumber)) / Math.pow(10, argsValueNumber));
            }
        } else if (func.equals("FLOOR")) {
            return trimNumber(Math.floor(stringValueNumber));
        } else if (func.equals("CEIL")) {
            return trimNumber(Math.ceil(stringValueNumber));
        }

        return null;
    }

    private static String trimNumber(Double number) {
        String numberValue = number + "";
        if (numberValue.endsWith(".0")) numberValue = numberValue.substring(0, numberValue.length()-2);
        return numberValue;
    }

    private static String trimNumber(Integer number) {
        return number+"";
    }
    private static String trimNumber(Long number) {return trimNumber((double) number);}

    private static String doStringUserFunctions(String stringValue, String func, String args, ClientChatReceivedEvent chatEvent) {
        for (List<String> function : global.function) {
            if (function.size() > 2) {
                String func_define = function.get(1);
                if (func_define.contains(".") && func_define.contains("(") && func_define.contains(")")) {
                    String func_name = func_define.substring(func_define.indexOf(".")+1, func_define.indexOf("(", func_define.indexOf("."))).toUpperCase();
                    if (func_name.equalsIgnoreCase(func)) {
                        String func_to = TagHandler.removeTags(func_define.substring(0, func_define.indexOf(".")));
                        String func_arg = func_define.substring(func_define.indexOf("(")+1, func_define.indexOf(")", func_define.indexOf(")")));
                        if (!func_arg.equals("")) {
                            String[] func_args = func_arg.split(",");
                            String[] func_args_to = args.split(",");
                            if (func_args.length == func_args_to.length) {
                                List<String> TMP_events = new ArrayList<String>();
                                for (int j = 2; j < function.size(); j++) {
                                    TMP_events.add(function.get(j));
                                }
                                return EventsHandler.doEvents(TMP_events, chatEvent, global.append(func_args, func_to), global.append(func_args_to, stringValue));
                            }
                        } else {
                            if (args.equals("")) {
                                List<String> TMP_events = new ArrayList<String>();
                                for (int j = 2; j < function.size(); j++) {
                                    TMP_events.add(function.get(j));
                                }
                                return EventsHandler.doEvents(TMP_events, chatEvent, new String[] {func_to}, new String[] {stringValue});
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String getStringValue(String stringName, Boolean isAsync) {
        if (isAsync) {
            for (Map.Entry<String, String> string : global.Async_string.entrySet()) {
                if (stringName.equals(string.getKey())) {
                    return string.getValue();
                }
            }
        } else {
            for (List<String> string : global.USR_string) {
                if (stringName.equals(string.get(0))) {
                    return string.get(1);
                }
            }

            for (List<String> string : global.TMP_string) {
                if (stringName.equals(string.get(0))) {
                    return string.get(1);
                }
            }
        }

        return null;
    }

    private static Double getStringPos(String stringName, Boolean isAsync) {

        Double i = 0.0;
        for (List<String> string : global.USR_string) {
            if (stringName.equals(string.get(0))) {
                return i;
            }
            i++;
        }

        if (!isAsync) {
            i = 1.0;
            for (List<String> string : global.TMP_string) {
                if (stringName.equals(string.get(0))) {
                    return -i;
                }
                i++;
            }
        }
        return null;
    }

    private static String createTempString(String stringName, Boolean isAsync) {
        if (isAsync) {
            global.Async_string.put(stringName, "");
            global.backupAsync_string.put(stringName, "");
            return "";
        } else {
            List<String> temp = new ArrayList<String>();
            temp.add(stringName);
            temp.add("");
            global.TMP_string.add(temp);
            global.backupTMP_strings.add(temp);
            return "";
        }
    }

    public static String nestedArgs(String args, ClientChatReceivedEvent chatEvent, Boolean isAsync) {
        args = stringFunctions(args, chatEvent, isAsync);
        while (args.contains("{array[") && args.contains("]}")) {
            args = ArrayHandler.arrayFunctions(args, chatEvent, isAsync);
            args = stringFunctions(args, chatEvent, isAsync);
        }
        while (args.contains("{display[") && args.contains("]}")) {
            args = DisplayHandler.displayFunctions(args, isAsync);
            args = stringFunctions(args, chatEvent, isAsync);
        }
        while (args.contains("{list[") && args.contains("]}")) {
            args = ListHandler.listFunctions(args, isAsync);
            args = stringFunctions(args, chatEvent, isAsync);
        }
        while (args.contains("{json[") && args.contains("]}")) {
            args = JsonHandler.jsonFunctions(args, isAsync);
            args = stringFunctions(args, chatEvent, isAsync);
        }
        while (args.contains("{string[") && args.contains("]}")) {
            args = stringFunctions(args, chatEvent, isAsync);
        }
        return args;
    }
}