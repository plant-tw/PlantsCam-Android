package com.piccollage.util;

import android.text.TextUtils;
import com.piccollage.util.config.Consts;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;


public class StringUtils {

    static public String stringsJoinWith(Collection<String> strings, String separator) {
        if (strings == null) {
            throw new IllegalArgumentException("strings cannot be null.");
        }
        String[] strArray = new String[strings.size()];
        strings.toArray(strArray);
        return stringsJoinWith(strArray, separator);
    }

    static public String stringsJoinWith(String[] strings, String separator) {
        if (strings == null) {
            throw new IllegalArgumentException("strings cannot be null.");
        }
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s).append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    static public float[] strTofloatArr(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        String[] strArr = str.split(Consts.SPLITTER);
        float[] ret = new float[strArr.length];
        for (int i = 0; i < strArr.length; i++)
            ret[i] = Float.parseFloat(strArr[i]);
        return ret;
    }

    /**
     * <p>
     * This is a helper function that transforms the numbers into<br/>
     * human-readable string when the number is bigger than 10 times the unit ('k', 'M', 'B', 'T').<br/><br/>
     * To make it more constant, this function will return at least three digits.<br/>
     * Note that this currently supports to "Trillion".
     * </p>
     *
     * <p>
     * Here's the pseudo-code:<br/>
     * If number < than 10,000, return the formatted text with comma.<br/>
     * If number is < than 100 times of the unit,
     *    return the formatted text with one digit after decimal point.<br/>
     * Otherwise, return the formatted text without decimal point.<br/>end<br/>
     * </p>
     * <p>
     * Example:<br/>
     * <table border="1">
     * <tr><th>Input</th><th>Output</th></tr>
     * <tr><td>9,999</td><td>9,999</td></tr>
     * <tr><td>10,000</td><td>10.0K</td></tr>
     * <tr><td>99,900</td><td>99.9K</td></tr>
     * <tr><td>100,000</td><td>100K</td></tr>
     * <tr><td>100,000,000</td><td>100M</td></tr>
     * </table>
     * </p>
     *
     * @param number the original integer number
     * @return the transformed human-readable string with 'K', 'M', 'B', 'T'
     */
    public static String getReadableNumber(int number) {
        if (number <= 0) return "0";
        final String[] units = new String[] { "", "K", "M", "B", "T" };

        DecimalFormat formatter = new DecimalFormat("###0");

        int digitGroups = (int) (Math.log10(number / 10) / 3); // Math.log10(1000) = 3
        // For number smaller than 1000
        if (digitGroups <= 0) {
            return formatter.format(number);
        }
        // For number larger than 10 quadrillion
        if (digitGroups >= units.length) {
            return new DecimalFormatSymbols().getInfinity();
        }
        double condensedNumber = number / Math.pow(1000, digitGroups);
        // If condensed number is only 10 times the unit,
        // show additional digit after decimal point
        if (condensedNumber < 100) {
            formatter = new DecimalFormat("##0.0");
        }
        formatter.setRoundingMode(RoundingMode.FLOOR);
        return formatter.format(condensedNumber) + units[digitGroups];
    }

    public static String capitalizeFirstLetter(String original) {
        if (TextUtils.isEmpty(original)) {
            return original;
        }
        return original.substring(0, 1).toUpperCase(Locale.getDefault()) + original.substring(1);
    }

    public static String truncate(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }
}
