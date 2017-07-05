package com.example.movierama.helpers;

import java.util.List;

/**
 * Created by Chris on 16/1/2016.
 */
public class GeneralUtils {

    /**
     * Creates a string from a list of string separated with commas ','.
     *
     * @param list the list of strings
     * @return the string formatted
     */
    public static String stringToShowFromStringList(List<String> list) {
        String result = "";

        if (list != null && list.size() > 0) {
            for (String s : list) {
                result += s + ", ";
            }

            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    /**
     * Checks if string is null or empty string
     * @param string the string we want to check
     * @return true if is null or empty string
     */
    public static boolean stringIsNullOrEmpty(String string) {
        if (string != null && string.isEmpty() == false) {
            return false;
        }
        return true;
    }
}
