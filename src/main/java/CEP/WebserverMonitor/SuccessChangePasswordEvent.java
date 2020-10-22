package CEP.WebserverMonitor;

import com.espertech.esper.common.client.EventBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuccessChangePasswordEvent {
    private String clientAddress;
    private long timestamp;
    private String timeFormatted;
    private String username;

    public static final String REGEXP = "^Successfully changed password for ([\\w_]+).$";
    public static final String REGEXP_LIKE = "Successfully changed password for %.";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    SuccessChangePasswordEvent(EventBean event) {
        this.clientAddress = (String)event.get("clientAddress");
        this.timestamp = (long)event.get("timestamp");
        this.timeFormatted = (String)event.get("timeFormatted");
        parseMessage((String)event.get("message"));
    }

    public void parseMessage(String message) {
        Matcher m = PATTERN.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a SuccessChangePasswordEvent. Could not parse the message " + message);
        username = m.group(1);
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
