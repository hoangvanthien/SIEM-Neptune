package CEP.WebserverMonitor;

import com.espertech.esper.common.client.EventBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *read and parse the failed register duplicate event
 *@author Hoang Van Thien
 */
public class FailedRegisterDuplicateEvent {

    private String clientAddress;
    private long timestamp;
    private String timeFormatted;
    private String username;

    public static final String REGEXP = "^Failed to register for ([\\w_]+). Account already exists.$";
    public static final String REGEXP_LIKE = "Failed to register for %. Account already exists.";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    /**
     *wrap the failed register duplicate event
     * @param event initialized failed register duplicate event object
     */
    FailedRegisterDuplicateEvent(EventBean event) {
        this.clientAddress = (String)event.get("clientAddress");
        this.timestamp = (long)event.get("timestamp");
        this.timeFormatted = (String)event.get("timeFormatted");
        parseMessage((String)event.get("message"));
    }

    /**
     * read and parse message line
     * @param message initialized message
     */
    public void parseMessage(String message) {
        Matcher m = PATTERN.matcher(message);
        if (!m.find()) throw new RuntimeException("This is not a FailedRegisterDuplicateEvent. Could not parse the message " + message);
        username = m.group(1);
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
     * @param username initialized username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * return a client's ip address
     * @return a string contain ip address
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
     * @return a string contain date and time
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * set a variable for timestamp event
     * @param timestamp initialized timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * return object with date/time formatted
     * @return date/time values matching the specified pattern
     */
    public String getTimeFormatted() {
        return timeFormatted;
    }

    /**
     * Formats and parses dates and times
     * @param timeFormatted initialized time formatter
     */
    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }
}
