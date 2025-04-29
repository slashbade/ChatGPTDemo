package markdown;

import java.util.Vector;

public class StringUtils {
    public static Vector split(String str, char delimiter) {
        if (str == null || str.length() == 0) {
            return new Vector();
        }

        Vector result = new Vector();
        int start = 0;
        int index;

        while ((index = str.indexOf(delimiter, start)) >= 0) {
            result.addElement(str.substring(start, index));
            start = index + 1;
        }

        // Add last segment
        if (start <= str.length()) {
            result.addElement(str.substring(start));
        }

        return result;
    }
    
    public static String replace(String source, String target, String replacement) {
        if (source == null || target == null || target.length() == 0) {
            return source;
        }
        StringBuffer sb = new StringBuffer();
        int start = 0;
        int index = 0;
        while ((index = source.indexOf(target, start)) != -1) {
            sb.append(source.substring(start, index));
            sb.append(replacement);
            start = index + target.length();
        }
        sb.append(source.substring(start));
        return sb.toString();
    }
}