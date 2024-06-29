package mba.vm.smart.parking.util;

import org.owasp.encoder.Encode;

/**
 * SmartParking - mba.vm.smart.parking.util
 *
 * @author VictorModi
 * @description https://github.com/VictorModi/Smart_Parking
 * @email victormodi@outlook.com
 * @date 2024/6/4 下午1:17
 */
public class ContentFetch {
    public static String genRedirect(String name) {
        return "<script>alert(\"错误的访问方式!\");window.location = '/#" + name + "';</script>";
    }

    public static String escapeHtml(String input) {
        return Encode.forHtml(input);
    }

    public static String escapeJavaScript(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\'':
                    escaped.append("\\'");
                    break;
                case '\"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '/':
                    escaped.append("\\/");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
