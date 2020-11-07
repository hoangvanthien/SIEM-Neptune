package Utilities;

import Dashboard.Dashboard;
import com.espertech.esper.common.client.EventBean;

import javax.swing.table.DefaultTableModel;

/**
 * sestup and create tables in dashboard
 * @author Hoang Van Thien
 */
public class DashboardAdapter {
    public static final int ALERT_TABLE = 0;
    public static final int ACCESS_LOG_TABLE = 1;
    public static final int ERROR_LOG_TABLE = 2;
    public static final int PORT_SCAN_TABLE = 3;

    static DefaultTableModel[] tables = {Dashboard.dashboards.dtm0, Dashboard.dashboards.dtm, Dashboard.dashboards.dtm2, Dashboard.dashboards.dtm3};

    /**
     * add row of information to table
     * @param o event to get data
     * @param i instance represent table
     */
    public static void writeToTable(EventBean o, int i) {
        if (disabled) return;
        if (i == ACCESS_LOG_TABLE) {
            tables[i].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("httpStatusCode"), o.get("requestMethod")});
        } else if (i == ERROR_LOG_TABLE) {
            tables[i].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("message")});
        } else if (i == PORT_SCAN_TABLE) {
            tables[i].addRow(new Object[]{Misc.formatTime((long)o.get("timestamp")), o.get("scanner"), o.get("targetAddress"), o.get("targetPort"), o.get("status"), o.get("type")});
        }
    }

    private static boolean disabled = false;
    public static void setDisabled(boolean disabled) {
        DashboardAdapter.disabled = disabled;
    }

    /**
     * set high priority of table
     * @param message instance for notification
     */
    public static void alertHigh(String message) {
        tables[ALERT_TABLE].addRow(new Object[]{Misc.formatTime(System.currentTimeMillis()), "HIGH", message});
    }

    /**
     * set low priority of table
     * @param message instance for notification
     */
    public static void alertLow(String message) {
        tables[ALERT_TABLE].addRow(new Object[]{Misc.formatTime(System.currentTimeMillis()), "LOW", message});
    }
}
