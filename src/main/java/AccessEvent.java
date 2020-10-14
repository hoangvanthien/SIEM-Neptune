import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessEvent { 

    private static String accessLogType = "apache"; // For Apache combined

    // private static String accessLogType = "rsyslog"; // In case of rsyslog

    private static int REQUEST_TIME_GROUP = 0;
    private static int CLIENT_REQUESTGROUP = 0;
    private static int HTTP_STATUS_CODE_GROUP = 0;
    private static int USER_NAME = 0;

    private static int ERROR_TIME_STAMP = 0;
    private static int CLIENT_IPADDRESS = 0;
    private static int MESSAGE = 0;

    private String errorTimeStamp;
    private String clientIpAddress;
    private String message;
    private boolean loggInCommand;
    private boolean sameIpAddress;
    private String lastIpAddress = "";

    private String TimeStamp;
    private String HttpStatusCode;
    private String UserName;
    private boolean accepted;

    private void processLine (String lineInput) throws Exception {

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
        if (((String) accessLogEntryMatcher.group (HTTP_STATUS_CODE_GROUP)).equals("200")){
            this.accepted = true;
        } else if (((String) accessLogEntryMatcher.group (HTTP_STATUS_CODE_GROUP)).equals("401")){
            this.accepted = false;
        }
        System.out.print ("[");
        this.TimeStamp =  ((String) accessLogEntryMatcher.group (REQUEST_TIME_GROUP) + ";");
        this.HttpStatusCode =  ((String) accessLogEntryMatcher.group (HTTP_STATUS_CODE_GROUP) + ";");
        this.UserName =  ((String) accessLogEntryMatcher.group (USER_NAME));
        System.out.print(this.UserName + " " + this.accepted);
        System.out.println ("]");
    }

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

    public AccessEvent(String lineInput) {

        String[] parseErrorLog = lineInput.split("\\[", 5);

        String[] clientIpAndMess = parseErrorLog[parseErrorLog.length - 1].split("AH");
        
        this.errorTimeStamp = parseErrorLog[1].replace("]", "");
        this.clientIpAddress =  (clientIpAndMess[0].replace("]", "")).contains("client") ? clientIpAndMess[0].replace("]", "").split(" ")[1] : "";
        this.message = clientIpAndMess[1].contains("user") ? "AH" + clientIpAndMess[1] : "";
        this.loggInCommand = !this.message.equals("");
        
        if (this.loggInCommand) {
            // String[] getUserName = this.message.split(":");
            String nowIpAddress = this.clientIpAddress;

            this.sameIpAddress = this.lastIpAddress.equals(nowIpAddress) && nowIpAddress != "" ;
            this.lastIpAddress = nowIpAddress;

        }

    }

    public String getErrorTimeStamp() {
        return this.errorTimeStamp;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getLoggInCommand() {
        return this.loggInCommand;
    }

    public boolean getSameIpAddress() {
        return this.sameIpAddress;
    }

    public String getLastIpAddress() {
        return this.lastIpAddress;
    }

    public String getTimeStamp() {
        return this.TimeStamp;
    }

    public String getHttpStatusCode() {
        return this.HttpStatusCode;
    }

    public String getUserName() {
        return this.UserName;
    }

    public boolean getAccepted() {
        return this.accepted;
    }
}