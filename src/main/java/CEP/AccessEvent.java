package CEP;

import java.lang.String;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessEvent {
    private static final Logger logger = Logger.getLogger("AccessEvent");

    private String ipAddress;
    private String user;
    private long timestamp;
    private String method;
    private String endpoint;
    private String protocol;
    private int responseCode;
    private boolean accepted;
    private String timeFormatted;


    private void init(String ipAddress, String user, long timestamp, String method, String endpoint, String protocol, int responseCode) {
        this.ipAddress = ipAddress;
        this.user = user;
        this.timestamp = timestamp;
        this.method = method;
        this.endpoint = endpoint;
        this.protocol = protocol;
        this.responseCode = responseCode;
        Timestamp ts = new Timestamp(timestamp);
        Date date = new Date(ts.getTime());
        DateFormat f = new SimpleDateFormat("dd/MMM/yyyy' 'HH:mm:ss");
        this.timeFormatted = f.format(date);
    }

    public AccessEvent() {

    }

    private static final String LOG_ENTRY_PATTERN =
            // 1:IP          3:user 4:date time                   5:method 6:req 7:proto   8:respcode
            "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) ";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);


    public AccessEvent(String logline) {
        Matcher m = PATTERN.matcher(logline);
        if (!m.find()) {
            logger.log(Level.ALL, "Cannot parse log line" + logline);
            throw new RuntimeException("Error parsing log line");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(m.group(4), formatter);
        long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        init(m.group(1), m.group(3), timestamp,
                m.group(5), m.group(6), m.group(7), Integer.parseInt(m.group(8)));

    }

    @Override
    public String toString() {
        return String.format("%s %s [%s] \"%s %s %s\" %s",
                ipAddress, user, timeFormatted, method, endpoint,
                protocol, responseCode);
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public String getUser() {
        return user;
    }
    public long getTimestamp() {
        return timestamp;
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
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

}







