import java.io.*;
import java.lang.String;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessEvent {
    private static final Logger logger = Logger.getLogger("Access");

    private String ipAddress;
    private String user;
    private long dateTime;
    private String method;
    private String endpoint;
    private String protocol;
    private int responseCode;
    private boolean accepted;
    private String timeFormatted;


    AccessEvent(String ipAddress, String user, long dateTime, String method, String endpoint, String protocol, String responseCode) {
        this.ipAddress = ipAddress;
        this.user = user;
        this.dateTime = dateTime;
        this.method = method;
        this.endpoint = endpoint;
        this.protocol = protocol;
        this.responseCode = Integer.parseInt(responseCode);

    }

    public AccessEvent() {

    }

    public String getIpAddress() {
        return ipAddress;
    }
    public String getUser() {
        return user;
    }
    public long getDateTime() {
        return dateTime;
    }
    public String getMethod() {
        return method;
    }
    public String getEndpoint() {
        return endpoint;
    }
    public String getProtocol() {
        return protocol;
    }
    public int getResponseCode() {
        return responseCode;
    }
    public boolean isAccepted() {
        return accepted;
    }
    public String getTimeFormatted() {
        return timeFormatted;
    }


    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }


    private static final String LOG_ENTRY_PATTERN =
            // 1:IP          3:user 4:date time                   5:method 6:req 7:proto   8:respcode
            "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) ";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);


    public static AccessEvent parseLogLine(String logline) {
        Matcher m = PATTERN.matcher(logline);
        if (!m.find()) {
            logger.log(Level.ALL, "Cannot parse log line" + logline);
            throw new RuntimeException("Error parsing log line");
        }

        return new AccessEvent(m.group(1), m.group(3), Long.parseLong(m.group(4)),
                m.group(5), m.group(6), m.group(7), m.group(8));

    }

    @Override
    public String toString() {
        return String.format("%s %s [%s] \"%s %s %s\" %s",
                ipAddress, user, dateTime, method, endpoint,
                protocol, responseCode);
    }

//    public AccessEvent(HashMap<String, String> map) {
//        dateTime = Long.parseLong(map.get(dateTime))/1000;
//        ipAddress = map.get(ipAddress);
//        String ResponseCode = map.get(responseCode);
//        if (ResponseCode.equals("200")) this.accepted = true;
//        else if (ResponseCode.equals("401")) this.accepted = false;
//        else throw new RuntimeException("Unknown result (" + responseCode + ").");
//        Timestamp ts = new Timestamp(dateTime);
//        Date date = new Date(ts.getTime());
//        DateFormat f = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
//        timeFormatted = f.format(date);
//    }

    private String errorTimeStamp;
    private String clientIpAddress;
    private String message;
    private boolean loggInCommand;
    private boolean sameIpAddress;
    private String lastIpAddress = "";

    public void processErrorLine(String lineInput) throws Exception {

        String[] parseErrorLog = lineInput.split("\\[", 5);

        String[] clientIpAndMess = parseErrorLog[parseErrorLog.length - 1].split("AH");

        this.errorTimeStamp = parseErrorLog[1].replace("]", "");
        this.clientIpAddress =  (clientIpAndMess[0].replace("]", "")).
                contains("client") ? clientIpAndMess[0].replace("]", "").
                split(" ")[1] : "";
        this.message = clientIpAndMess[1].contains("user") ? "AH" + clientIpAndMess[1] : "";
        this.loggInCommand = !this.message.equals("");

        if (this.loggInCommand) {
            // String[] getUserName = this.message.split(":");
            String nowIpAddress = this.clientIpAddress;

            this.sameIpAddress = this.lastIpAddress.equals(nowIpAddress) && nowIpAddress != "" ;
            this.lastIpAddress = nowIpAddress;

        }
    }

     }







