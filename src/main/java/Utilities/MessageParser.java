package Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {
    static final Pattern failedLoginPattern = Pattern.compile("^Failed login for username ([\\w_]+) and password \\(md5 hashed\\) (\\w+)$");
    static final Pattern failedRegisterPattern = Pattern.compile("^Failed to register for ([\\w_]+). Account already exists.$");
    static final Pattern successChangePasswordPattern = Pattern.compile("^Successfully changed password for ([\\w_]+).$");
    public static String parseFailedLogin(String message, int group) {
        Matcher m = failedLoginPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedLoginEvent. Could not parse the message " + message);
        return m.group(group);
    }
    public static String parseFailedRegister(String message) {
        Matcher m = failedRegisterPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedRegisterEvent. Could not parse the message " + message);
        return m.group(1);
    }
    public static String parseSuccessChangePassword(String message) {
        Matcher m = successChangePasswordPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedRegisterEvent. Could not parse the message " + message);
        return m.group(1);
    }
}
