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
 * Read and parse the Neptune Error Log file
 * @author Hoang Van Thien
 */
public class NeptuneErrorLogEvent {

    private String clientAddress;
    private long timestamp;
    private String timeFormatted;
    private String message;
    private String url;

    /**
     * [Thu Oct 15 23:43:32.213121 2020] [php7:notice] [pid 845] [client 192.168.56.1:54587]
     * Neptune: Unauthorized access to /special/code01542.php. User has not logged in.,
     * referer: http://192.168.56.101/special/
     */

    /**
     * set the pattern for error log to parse
     * @param LOG_ENTRY_PATTERN define regular expression pattern of log entry
     * @param TIMESTAMP_GROUP define the position of timestamp element in log entry pattern
     * @param CLIENT_ADDRESS_GROUP define the position of ip address element in log entry pattern
     * @param CLIENT_PORT_GROUP define the position of port number element in log entry pattern
     * @param LOG_MESSAGE_GROUP define the position of log message element in log entry pattern
     * @param URL_GROUP define the position of URL element in log entry pattern
     * @param PATTERN call "compile" method to compile LOG_ENTRY_PATTERN
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
     * read and parse log line of log file
     * @param logline a line of Neptune Error Log file
     * @throws Exception indicate condition that application might catch
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

    /**
     * wrap the Neptune Error Log file's lines
     * @param bean initialized Neptune error log event object
     */
    public NeptuneErrorLogEvent(EventBean bean) {
        init((Long)bean.get("timestamp"), ""+bean.get("clientAddress"), ""+bean.get("message"), ""+bean.get("url"));
    }

    /**
     * Initialize object for log line
     * @param timestamp a date/time of Neptune error log file
     * @param clientAddress a ip address of client
     * @param message a message of Neptune error log file
     * @param url a address of web
     */
    protected void init(long timestamp, String clientAddress, String message, String url) {
        this.clientAddress = clientAddress;
        this.timestamp = timestamp;
        this.message = message;
        this.url = url;
        Timestamp ts = new Timestamp(timestamp);
        Date date = new Date(ts.getTime());
        DateFormat f = new SimpleDateFormat("dd/MMM/yyyy' 'HH:mm:ss");
        this.timeFormatted = f.format(date);
    }

    /**
     * check for the next event
     * @return the element at the head of the queue.
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
    private static int batchSize = 5;
    private static Queue<NeptuneErrorLogEvent> queue = new ArrayDeque<>();

    /**
     * return the client's ip address
     * @return a string contain ip address of client
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * set the field for client's ip address of error log
     * @param clientAddress a client's ip address
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     * return the date/time of error log event
     * @return a string contain date/time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * set the field for timestamp of error log
     * @param timestamp date/time of Neptune error log event
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * return return object with date/time formatted
     * @return a DateTimeFormat object
     */
    public String getTimeFormatted() {
        return timeFormatted;
    }

    /**
     * Formats and parses dates and times
     * @param timeFormatted initialized time Formatter
     */
    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }

    /**
     * return the message of error log
     * @return a string contain notification of Neptune error log file
     */
    public String getMessage() {
        return message;
    }

    /**
     * set the field for message of error log
     * @param message a notification of failed login event
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * return a URL of web of error log
     * @return a string contain URL of web
     */
    public String getUrl() {
        return url;
    }

    /**
     * set the field for url of error log
     * @param url a address of web
     */
    public void setUrl(String url) {
        this.url = url;
    }
}







