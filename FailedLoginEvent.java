package CEP.WebserverMonitor;

import com.espertech.esper.common.client.EventBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Read and parse the failed login event notification
 *@authod Hoang Van Thien
 */
public class FailedLoginEvent {

    private String clientAddress;
    private long timestamp;
    private String timeFormatted;
    private String username;
    private String password;

    public static final String REGEXP = "^Failed login for username ([\\w_]+) and password \\(md5 hashed\\) (\\w+)$";
    public static final String REGEXP_LIKE = "Failed login for username % and password (md5 hashed) %";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    /**
     * wrap the failed login event object
     * @param event Initialized failed login event object
     */
    FailedLoginEvent(EventBean event) {
        this.clientAddress = (String)event.get("clientAddress");
        this.timestamp = (long)event.get("timestamp");
        this.timeFormatted = (String)event.get("timeFormatted");
        parseMessage((String)event.get("message"));
    }

    /**
     *Parse the message line of failed login message
     *@param message initialized message
     */
    public void parseMessage(String message) {
        Matcher m = PATTERN.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedLoginEvent. Could not parse the message " + message);
        username = m.group(1);
        password = m.group(2);
    }

    /**
     * return the user's username
     * @return a string contain user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * set a variable for username
     * @param username initialized user's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * return a user's password
     * @return a string contain user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set a variable for user's password
     * @param password intinialized user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * return a client's ip address
     * @return a string contain client's address
     */
    public String getClientAddress() {
        return clientAddress;
    }

    /**
     * set a variable for client's ip address
     * @param clientAddress initialized client's address
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    /**
     * return a date/time of the event
     * @return the string contain date/time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * set a variable for timestamp event
     * @param timestamp initialized time stamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * return object with date/time formatted
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
}
