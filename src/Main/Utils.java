package Main;

import org.json.JSONArray;

/**
 * Class of utility functions used throughout the project
 * Author: Stefan
 */
public class Utils {
    /**
     * Returns the largest string that fits on the screen within the provided width
     * Author: Stefan
     *
     * @param str   String to truncate
     * @param width Width of bounds
     * @return Largest string that fits in the width
     */
    public static String getLargestStringInBounds(Project main, String str, int width) {
        if(str.length() <= 1 || main.textWidth(str) < width)
            return str;
        for(int currentLength = str.length() - 1; currentLength > 0; currentLength--)
            if(main.textWidth(str) < width)
                return str.substring(0, str.length() - 3) + "...";
            else
                str = str.substring(0, currentLength);
        return "";
    }

    /**
     * Converts integer arrays to comma separated strings
     * Author: Stefan
     *
     * @param array Array of ints
     * @return      Comma separated string of ints
     */
    public static String intArrayToCommaSeparatedString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for(int kid : array)
            sb.append(kid).append(",");
        return sb.toString();
    }

    /**
     * Converts an integer array to a comma separated string
     * Author: Stefan
     *
     * @param commaSeparatedString  Comma separated string of ints
     * @return                      Array of ints
     */
    public static int[] commaSeparatedStringToIntArray(String commaSeparatedString) {
        String[] intsAsStrings = commaSeparatedString.split(",");
        int[] output = new int[intsAsStrings.length];
        for(int i = 0; i < intsAsStrings.length; i++)
            output[i] = Integer.parseInt(intsAsStrings[i]);
        return output;
    }

    /**
     * Returns an int equivalent of a JSONArray (used for parsing kids)
     * Author: Stefan
     *
     * @param array JSON Array
     * @return      Equivalent int array
     */
    public static int[] JSONArrayToIntArray(JSONArray array) {
        int[] output = new int[array.length()];
        for(int i = 0; i < array.length(); i++)
            output[i] = array.optInt(i);
        return output;
    }

    /**
     * Removes illegal characters from a string to be used in an SQL query
     * Author: Stefan
     *
     * @param text
     * @return
     */
    public static String formatSQL(String text) {
        String[] segments = text.split("'");
        StringBuilder formattedString = new StringBuilder();
        for(String segment : segments)
            if(segment.length() != 0 && segment.charAt(0) == '\'')
                formattedString.append('\\').append(segment);
            else if(segment.length() != 0)
                formattedString.append(segment);

        return formattedString.toString();
    }
}
