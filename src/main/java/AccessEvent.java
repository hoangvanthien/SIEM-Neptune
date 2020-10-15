import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessEvent { 

    private static String accessLogType = "apache"; // For Apache combined

    // private static String accessLogType = "rsyslog"; // In case of rsyslog

    private static int REQUEST_TIME_GROUP = 0;
    private static int CLIENT_REQUESTGROUP = 0;
    private static int HTTP_STATUS_CODE_GROUP = 0;
    private static int USER_NAME = 0;

    private String timeStamp;
    private String httpStatusCode;
    private String userName;
    private boolean accepted;

    private String getAccessLogRegex () throws Exception {

        String myRegex = "";
        if (accessLogType.equals ("apache")) {
            // 172.18.10.37 - - [12 / Aug / 2015: 07: 18: 45 -0300] "GET
            String regex1 = "^([\\d.]+)";                         // Client IP
            String regex2 = " (\\S+)";                             // -
            String regex3 = " (\\S+)";                             // -
            String regex4 = " \\[([\\w:/]+\\s[+\\-]\\d{4})\\]"; // Date
            String regex5 = " \"(.+?)\"";                       // request method and url
            String regex6 = " (\\d{3})";                           // HTTP code
            String regex7 = " (\\d+|(.+?))";                     // Number of bytes
            String regex8 = " \"([^\"]+|(.+?))\"";                 // Referer
            String regex9 = " \"([^\"]+|(.+?))\"";                // Agent

            USER_NAME = 3;
            REQUEST_TIME_GROUP = 4;
            CLIENT_REQUESTGROUP = 5;
            HTTP_STATUS_CODE_GROUP = 6;

            myRegex = regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7 + regex8 + regex9;
        }
        return myRegex;
    }

    public AccessEvent(String lineInput) throws Exception {
                // String clientHost = null;
        String requestTime = null;
        String clientRequest = null;
        String httpStatusCode = null;
        // String numOfBytes = null;
        // String referer = null;
        // String agent = null;
        int pos = 0;
        String deviceId = null;

        Pattern accessLogPattern = Pattern.compile (getAccessLogRegex (), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher accessLogEntryMatcher;
        accessLogEntryMatcher = accessLogPattern.matcher (lineInput);
        if (! accessLogEntryMatcher.matches ()) {
            System.out.println (": couldn't be parsed");
        }
        if (((String) accessLogEntryMatcher.group(HTTP_STATUS_CODE_GROUP)).equals("401")){
            this.accepted = false;
        } else {
            this.accepted = true;
        }
        this.timeStamp =  ((String) accessLogEntryMatcher.group (REQUEST_TIME_GROUP) + ";");
        this.httpStatusCode =  ((String) accessLogEntryMatcher.group (HTTP_STATUS_CODE_GROUP) + ";");
        this.userName =  ((String) accessLogEntryMatcher.group (USER_NAME));

    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public String getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public String getUserName() {
        return this.userName;
    }

    public boolean getAccepted() {
        return this.accepted;
    }
}