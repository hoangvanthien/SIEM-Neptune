package CEP.WebserverMonitor;

import Utilities.Misc;
import com.espertech.esper.common.client.EventBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheAccessLogEvent {

    private String clientAddress; //
    private long timestamp;
    private String url; //
    private String httpStatusCode; //
    private String requestMethod; //
    private boolean badRequest;
    private String timeFormatted; //

    /**
     * 192.168.56.1 - - [16/Oct/2020:13:17:15 +0700] "POST /login.php HTTP/1.1" 401 1031
     * "http://192.168.56.101/login.php" "Mozilla/5.0 (Windows NT 10.0; Win64; x64)
     * AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36 Edg/86.0.622.38"
     **/

    // ([\d.]+) (\S+) (\S+) \[([\w:/]+\s[+-]\d{4})\] "([A-Z]+) \/([\w_.]+) ([\w/.]+)" (\d{3}) (\d+) "([^"]+)" "([^"]+)"
    public static final String REGEXP = "^([\\d.]+) " +
            "(\\S+) " +
            "(\\S+) " +
            "\\[([\\w:/]+\\s[+-]\\d{4})\\] " +
            "\"([A-Z]+) " +
            "(\\/[\\/\\w-_.]*) " +
            "([\\w/.]+)\" " +
            "(\\d{3}) " +
            "(\\d+) " +
            "\"([^\"]+)\" " +
            "\"([^\"]+)\"$";

    private static final int TIMESTAMP_GROUP = 4;
    private static final int CLIENT_ADDRESS_GROUP = 1;
    private static final int HTTP_STATUS_CODE_GROUP = 8;
    private static final int URL_GROUP = 6;
    private static final int REQUEST_METHOD_GROUP = 5;

    private static final Pattern PATTERN = Pattern.compile (REGEXP);

    public ApacheAccessLogEvent() {}

    public ApacheAccessLogEvent(String logline) {
        Matcher m = PATTERN.matcher (logline);
        if (!m.find()) {
            System.err.println("Cannot parse log line " + logline);
            throw new RuntimeException("Error parsing log line");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(m.group(TIMESTAMP_GROUP), formatter);
        long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        lastTimestamp = timestamp;

        init(timestamp, m.group(CLIENT_ADDRESS_GROUP), m.group(REQUEST_METHOD_GROUP), m.group(HTTP_STATUS_CODE_GROUP), m.group(URL_GROUP));
    }

    public ApacheAccessLogEvent(EventBean bean) {
        init((Long)bean.get("timestamp"), ""+bean.get("clientAddress"), ""+bean.get("requestMethod"), ""+bean.get("httpStatusCode"), ""+bean.get("url"));
    }

    protected void init(long timestamp, String clientAddress, String requestMethod, String httpStatusCode, String url) {
        this.timestamp = timestamp;
        this.clientAddress = clientAddress;
        this.requestMethod = requestMethod;
        this.httpStatusCode = httpStatusCode;
        this.url = url;
        this.badRequest = httpStatusCode.startsWith("4");
        this.timeFormatted = Misc.formatTime(timestamp);
    }

    public static ApacheAccessLogEvent nextEvent() throws IOException {
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        Process process = Runtime.getRuntime().exec("tail -n " + batchSize + " /var/log/apache2/access.log");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        long now = lastTimestamp == 0 ? System.currentTimeMillis()-5000 : lastTimestamp;
        while ((line = in.readLine()) != null) {
            ApacheAccessLogEvent event = new ApacheAccessLogEvent(line);
            if (now < lastTimestamp) queue.add(event);
        }
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    private static long lastTimestamp;
    private static int batchSize = 1;
    private static Queue<ApacheAccessLogEvent> queue = new ArrayDeque<>();

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public boolean isBadRequest() {
        return badRequest;
    }

    public void setBadRequest(boolean badRequest) {
        this.badRequest = badRequest;
    }

    public String getTimeFormatted() {
        return timeFormatted;
    }

    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }
}