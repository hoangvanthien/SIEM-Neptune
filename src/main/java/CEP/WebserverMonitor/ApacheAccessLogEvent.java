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
/**
 * Read and parse the Apache access log file
 * @author Hoang Van Thien
 * @author Nguyen Hoang Quan
 */
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

    /**
     *@param LOG_ENTRY_PATTERN define regular expression pattern of log entry
     *@param TIMESTAMP_GROUP define the position of timestamp element in log entry pattern
     *@param CLIENT_ADDRESS_GROUP define the position of client address element in log entry pattern
     *@param HTTP_STATUS_CODE_GROUP define the position of HTTP code element in log entry pattern
     *@param URL_GROUP define the position of URL element in log entry pattern
     *@param REQUEST_METHOD_GROUP define the position of request method element in log entry pattern
     *@param PATTERN call "compile" method to compile LOG_ENTRY_PATTERN
     */
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

    /**
     *
     * @param logline a line of access log file
     * @throws Exception indicate condition that application might want to catch
     */
    public ApacheAccessLogEvent(String logline) throws Exception {
        Matcher m = PATTERN.matcher (logline);
        if (!m.find()) {
            System.err.println("Cannot parse log line " + logline);
            throw new Exception("Error parsing log line");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(m.group(TIMESTAMP_GROUP), formatter);
        long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        lastTimestamp = timestamp;

        init(timestamp, m.group(CLIENT_ADDRESS_GROUP), m.group(REQUEST_METHOD_GROUP), m.group(HTTP_STATUS_CODE_GROUP), m.group(URL_GROUP));
    }

    /**
     * wrap the Apache access log file's line
     * @param bean initialized Apache access log event object
     */
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

    /**
     * @return the element at the front of of the queue
     */
    public static ApacheAccessLogEvent nextEvent() throws IOException {
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        Process process = Runtime.getRuntime().exec("tail -n " + batchSize + " /var/log/apache2/access.log");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        long now = lastTimestamp == 0 ? System.currentTimeMillis()-5000 : lastTimestamp;
        while ((line = in.readLine()) != null) {
            ApacheAccessLogEvent event = null;
            try {
                event = new ApacheAccessLogEvent(line);
            } catch (Exception ignored) {
            
            }
            if (now < lastTimestamp) queue.add(event);
        }
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    private static long lastTimestamp;
    private static int batchSize = 1;
    private static Queue<ApacheAccessLogEvent> queue = new ArrayDeque<>();

    /**
     *
     * @return a string contain client's address
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     *
     * @param clientAddress a ip address of client
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     *
     * @return a string contain date/time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp a date/time of Apache access log event
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return a string contain URL of log line
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url a address of web
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return a string contain HTTP status code line
     */
    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     *
     * @param httpStatusCode a HTTP address of web
     */
    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     *
     * @return a string contain method of request
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     *
     * @param requestMethod a method of request
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     *
     * @return
     */
    public boolean isBadRequest() {
        return badRequest;
    }

    /**
     *
     * @param badRequest
     */
    public void setBadRequest(boolean badRequest) {
        this.badRequest = badRequest;
    }

    /**
     *
     * @return DateTimeFormat object
     */
    public String getTimeFormatted() {
        return timeFormatted;
    }

    /**
     *
     * @param timeFormatted initialized time Formatter
     */
    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }
}
