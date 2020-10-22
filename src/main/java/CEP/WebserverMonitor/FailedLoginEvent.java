package CEP.WebserverMonitor;

import com.espertech.esper.common.client.EventBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FailedLoginEvent {

    private String clientAddress;
    private long timestamp;
    private String timeFormatted;
    private String username;
    private String password;

    public static final String REGEXP = "^Failed login for username ([\\w_]+) and password \\(md5 hashed\\) (\\w+)$";
    public static final String REGEXP_LIKE = "Failed login for username % and password (md5 hashed) %";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    FailedLoginEvent(EventBean event) {
        this.clientAddress = (String)event.get("clientAddress");
        this.timestamp = (long)event.get("timestamp");
        this.timeFormatted = (String)event.get("timeFormatted");
        parseMessage((String)event.get("message"));
    }

    public void parseMessage(String message) {
        Matcher m = PATTERN.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedLoginEvent. Could not parse the message " + message);
        username = m.group(1);
        password = m.group(2);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
