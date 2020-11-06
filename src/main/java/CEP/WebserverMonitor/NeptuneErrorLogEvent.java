package CEP.WebserverMonitor;

import com.espertech.esper.common.client.EventBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent a log entry in the file error.log produced by the Webserver (via PHP's error_log(string) function)
 * @author Quan Nguyen, Thien Hoang
 */
public class NeptuneErrorLogEvent {

    private String clientAddress;
    private long timestamp;
    private String message;
    private String url;

    /**
     * Regular expression to match with the log entry in error.log
     */
    public static final String REGEXP = "^\\[([\\w:. ]+)] " +
            "\\[([\\w:]+)] " +
            "\\[pid (\\d+)] " +
            "\\[client ([\\d.]+):(\\d+)] " +
            "Neptune: (.+), " +
            "referer: https?://[\\w.]+/([\\w/.]+)$";
    private static final int TIMESTAMP_GROUP = 1;
    private static final int CLIENT_ADDRESS_GROUP = 4;
    private static final int CLIENT_PORT_GROUP = 5;
    private static final int LOG_MESSAGE_GROUP = 6;
    private static final int URL_GROUP = 7;
    private static final Pattern PATTERN = Pattern.compile(REGEXP);

    public NeptuneErrorLogEvent() {}

    /**
     * Constructor
     * Create an event object by parsing the log entry
     * @param logline the log entry
     * @throws Exception when the log entry cannot be parsed
     */
    public NeptuneErrorLogEvent(String logline) throws Exception {
        Matcher m = PATTERN.matcher(logline);
        if (!m.find()) {
//            System.err.println("Cannot parse log line " + logline);
            throw new Exception("Not Neptune log entry");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss.SSSSSS yyyy", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(m.group(TIMESTAMP_GROUP), formatter);
        long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        lastTimestamp = timestamp;
        init(timestamp, m.group(CLIENT_ADDRESS_GROUP), m.group(LOG_MESSAGE_GROUP), m.group(URL_GROUP));
    }

    protected void init(long timestamp, String clientAddress, String message, String url) {
        this.clientAddress = clientAddress;
        this.timestamp = timestamp;
        this.message = message;
        this.url = url;
    }

    /**
     * Get an event from the log file error.log
     * The event will always be the latest event that has not been processed.
     * If there are multiple such events, the earliest event is returned.
     * If there are no new events, null will be returned.
     * @return null or an event in error.log
     * @throws IOException thrown when failed to read the log file
     */
    public static NeptuneErrorLogEvent nextEvent() throws IOException {
        if (!queue.isEmpty()) {
            return queue.poll();
        }
        Process process = Runtime.getRuntime().exec("tail -n " + batchSize + " /var/log/apache2/error.log");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        long now = lastTimestamp == 0 ? System.currentTimeMillis()-5000 : lastTimestamp;
        while ((line = in.readLine()) != null) {
            NeptuneErrorLogEvent event = null;
            try {
                event = new NeptuneErrorLogEvent(line);
            } catch (Exception ignored) {

            }
            if (now < lastTimestamp) queue.add(event);
        }
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    private static long lastTimestamp;
    private static final int batchSize = 5;
    private static final Queue<NeptuneErrorLogEvent> queue = new ArrayDeque<>();

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
     * Get the message of the Webserver
     * This message is written to the log by PHP's built in function error_log
     * @return the log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message of the Webserver
     * This message is written to the log by PHP's built in function error_log
     * @param message the log message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the URL that recorded this event
     * The URL of the PHP script which wrote the log entry
     * @return URL of the logger
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the URL that recorded this event
     * The URL of the PHP script which wrote the log entry
     * @param url URL of the logger
     */
    public void setUrl(String url) {
        this.url = url;
    }
}







