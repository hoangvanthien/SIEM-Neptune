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

    public NeptuneErrorLogEvent(EventBean bean) {
        init((Long)bean.get("timestamp"), ""+bean.get("clientAddress"), ""+bean.get("message"), ""+bean.get("url"));
    }

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

    public String getTimeFormatted() {
        return timeFormatted;
    }

    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}







