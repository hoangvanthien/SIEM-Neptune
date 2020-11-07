package Utilities;

import Dashboard.Dashboard;
import com.espertech.esper.common.client.EventBean;

import javax.swing.table.DefaultTableModel;

/**
 * Adapter class that helps deliver the alerts and events from the CEP-triggers to the dashboard
 * @author Thien Hoang, Hieu Le
 */
public class DashboardAdapter {
    public static final int ALERT_TABLE = 0;
    public static final int ACCESS_LOG_TABLE = 1;
    public static final int ERROR_LOG_TABLE = 2;
    public static final int PORT_SCAN_TABLE = 3;

    static DefaultTableModel[] tables = null;

    public static void writeToTable(EventBean o, int i) {
        if (disabled) return;
        if (tables == null) {
            tables = new DefaultTableModel[]{Dashboard.dashboards.dtm0, Dashboard.dashboards.dtm, Dashboard.dashboards.dtm2, Dashboard.dashboards.dtm3};
        }
        if (i == ACCESS_LOG_TABLE) {
            tables[i].addRow(new Object[]{Misc.formatTime((long)o.get("timestamp")), o.get("clientAddress"), o.get("url"), o.get("httpStatusCode"), o.get("requestMethod")});
        } else if (i == ERROR_LOG_TABLE) {
            tables[i].addRow(new Object[]{Misc.formatTime((long)o.get("timestamp")), o.get("clientAddress"), o.get("url"), o.get("message")});
        } else if (i == PORT_SCAN_TABLE) {
            tables[i].addRow(new Object[]{Misc.formatTime((long)o.get("timestamp")), o.get("scanner"), o.get("targetAddress"), o.get("targetPort"), o.get("status"), o.get("type")});
        }
    }

    private static boolean disabled = true;
    public static void setDisabled(boolean disabled) {
        DashboardAdapter.disabled = disabled;
    }

    public static void alertHigh(String message) {
        tables[ALERT_TABLE].addRow(new Object[]{Misc.formatTime(System.currentTimeMillis()), "HIGH", message});
    }

    public static void alertLow(String message) {
        tables[ALERT_TABLE].addRow(new Object[]{Misc.formatTime(System.currentTimeMillis()), "LOW", message});
    }
}
