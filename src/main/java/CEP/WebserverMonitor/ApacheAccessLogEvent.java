package CEP.WebserverMonitor;

import Utilities.Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent a log entry in the file access.log produced by Apache2
 * @author Quan Nguyen, Thien Hoang
 */
public class ApacheAccessLogEvent {

    private String clientAddress; //
    private long timestamp;
    private String url; //
    private String httpStatusCode; //
    private String requestMethod; //

    /**
     * Regular expression to match with the log entry in access.log
     */
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
     * Constructor
     * Create an event object by parsing the log entry
     * @param logline the log entry
     * @throws Exception when the log entry cannot be parsed
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

    private void init(long timestamp, String clientAddress, String requestMethod, String httpStatusCode, String url) {
        this.timestamp = timestamp;
        this.clientAddress = clientAddress;
        this.requestMethod = requestMethod;
        this.httpStatusCode = httpStatusCode;
        this.url = url;
    }

    /**
     * Get an event from the log file access.log
     * The event will always be the latest event that has not been processed.
     * If there are multiple such events, the earliest event is returned.
     * If there are no new events, null will be returned.
     * @return null or an event in access.log
     * @throws IOException thrown when failed to read the log file
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
    private static final int batchSize = 1;
    private static final Queue<ApacheAccessLogEvent> queue = new ArrayDeque<>();

    /**
     * Get client's IP address
     * @return client's IP address
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * Set client's IP address
     * @param clientAddress client's IP address
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     * Get timestamp of the event
     * This is the timestamp recorded in the log entry, converted into number of seconds since Epoch
     * @return timestamp of the event
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set timestamp of the event
     * This is the timestamp recorded in the log entry, converted into number of seconds since Epoch
     * @param timestamp timestamp of the event
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the requested URL
     * The URL upon which the client made the request
     * @return URL of the request
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the requested URL
     * The URL upon which the client made the request
     * @param url URL of the request
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the status code to the request
     * @return the status code
     */
    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Set the status code to the request
     * @param httpStatusCode the status code
     */
    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Get the request method of the request
     * Example: ``POST'', ``GET'', ...
     * @return the request method
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Set the request method of the request
     * Example: ``POST'', ``GET'', ...
     * @param requestMethod the request method
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}