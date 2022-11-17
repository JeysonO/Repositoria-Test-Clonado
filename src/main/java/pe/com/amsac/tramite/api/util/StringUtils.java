package pe.com.amsac.tramite.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    protected StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isEmpty(String string) {
        return string == null || string != null && "".equals(string.trim());
    }

    public static String lpad(String text, int length, char character) {
        return pad(text, length, character, "left");
    }

    public static String rpad(String text, int length, char character) {
        return pad(text, length, character, "right");
    }

    public static String ltrim(String text, char character) {
        return trim(text, character, "left");
    }

    public static String rtrim(String text, char character) {
        return trim(text, character, "right");
    }

    public static List<String> split(String text, String regex) {
        String newText = text;

        ArrayList splitText;
        int endIndex;
        for(splitText = new ArrayList(); !"".equals(text); newText = newText.substring(endIndex + 1)) {
            int beginIndex = 0;
            endIndex = text.indexOf(regex);
            if (endIndex == -1) {
                splitText.add(text);
                break;
            }

            splitText.add(text.substring(beginIndex, endIndex));
        }

        return splitText;
    }

    public static String deleteCharsNotPrintable(String text) {
        String newText = text;

        int beginIndex;
        String highAscii;
        for(beginIndex = text.indexOf("^"); beginIndex != -1; beginIndex = newText.indexOf("^")) {
            highAscii = newText.substring(beginIndex);
            highAscii = highAscii.substring(0, 3);
            newText = replace(newText, highAscii, " ");
        }

        for(beginIndex = newText.indexOf("&#"); beginIndex != -1; beginIndex = newText.indexOf("&#")) {
            highAscii = newText.substring(beginIndex);
            highAscii = highAscii.substring(0, highAscii.indexOf(";") + 1);
            newText = replace(newText, highAscii, " ");
        }

        return newText;
    }

    public static String replace(String text, String toReplace, String replacement) {
        int loc = text.indexOf(toReplace);
        return loc < 0 ? text : text.substring(0, loc) + replacement + text.substring(loc + toReplace.length());
    }

    public static String replaceAll(String text, String toReplace, String replacement) {
        String newText = text;

        while(true) {
            int loc = newText.indexOf(toReplace);
            if (loc < 0) {
                return newText;
            }

            newText = replace(newText, toReplace, replacement);
        }
    }

    public static boolean validateRegEx(String regEx, String text) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        boolean flag = m.matches();
        return flag;
    }

    private static String pad(String text, int length, char character, String side) {
        String padText = "";
        String auxText = "";
        int textLength = text.length();
        if (textLength < length) {
            for(int i = 0; i < length - textLength; ++i) {
                auxText = auxText + character;
            }

            if (side.equals("left")) {
                padText = auxText + text;
            } else if (side.equals("right")) {
                padText = text + auxText;
            }
        } else {
            padText = text.substring(0, length);
        }

        return padText;
    }

    private static String trim(String text, char character, String side) {
        int beginIndex = 0;
        int endIndex = text.length();
        int i;
        if (side.equals("left")) {
            for(i = 0; i < text.length() && text.charAt(i) == character; ++i) {
                ++beginIndex;
            }
        } else if (side.equals("right")) {
            for(i = text.length() - 1; i <= 0 && text.charAt(i) == character; --i) {
                --endIndex;
            }
        }

        return text.substring(beginIndex, endIndex);
    }

    public static String getGroupvalidateRegEx(String regEx, String text) {
        Pattern p = Pattern.compile(regEx, 2);
        Matcher m = p.matcher(text);
        return m.find() ? m.group() : null;
    }
}
