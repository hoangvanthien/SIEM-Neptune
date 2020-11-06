package Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that is used by the CEP to extract the information from the error log's message
 * @author Thien Hoang
 */
public class MessageParser {
    static final Pattern failedLoginPattern = Pattern.compile("^Failed login for username ([\\w_]+) and password \\(md5 hashed\\) (\\w+)$");
    static final Pattern failedRegisterPattern = Pattern.compile("^Failed to register for ([\\w_]+). Account already exists.$");
    static final Pattern successChangePasswordPattern = Pattern.compile("^Successfully changed password for ([\\w_]+).$");

    /**
     * Extract username or password from the message of a log entry related to a failed login attempt
     * @param message the log message
     * @param group 1 if you want to get username, 2 if you want to get password
     * @return the requested information
     */
    public static String parseFailedLogin(String message, int group) {
        Matcher m = failedLoginPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedLoginEvent. Could not parse the message " + message);
        return m.group(group);
    }

    /**
     * Extract username from the message of a log entry related to a failed register due to duplication
     * @param message the log message
     * @return the existed username
     */
    public static String parseFailedRegister(String message) {
        Matcher m = failedRegisterPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedRegisterEvent. Could not parse the message " + message);
        return m.group(1);
    }

    /**
     * Extract username from the message of a log entry related to a successful change in password
     * @param message the log message
     * @return the username who changed password successfully
     */
    public static String parseSuccessChangePassword(String message) {
        Matcher m = successChangePasswordPattern.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedRegisterEvent. Could not parse the message " + message);
        return m.group(1);
    }
}
