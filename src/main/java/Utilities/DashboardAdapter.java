package Utilities;

import Dashboard.Dashboard;
import com.espertech.esper.common.client.EventBean;

import javax.swing.table.DefaultTableModel;

public class DashboardAdapter {
    public static int ALERT_TABLE = 0;
    public static int ACCESS_LOG_TABLE = 1;
    public static int ERROR_LOG_TABLE = 2;
    public static int PORT_SCAN_TABLE = 3;
    static DefaultTableModel[] tables = {Dashboard.dashboards.dtm0, Dashboard.dashboards.dtm, Dashboard.dashboards.dtm2, Dashboard.dashboards.dtm3};
    public static void writeToTable(EventBean o, int i) {
        if (i == ACCESS_LOG_TABLE) {
            tables[i].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("httpStatusCode"), o.get("requestMethod")});
        } else if (i == ERROR_LOG_TABLE) {
            tables[i].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("message")});
        } else if (i == PORT_SCAN_TABLE) {
            tables[i].addRow(new Object[]{Misc.formatTime((long)o.get("timestamp")), o.get("targetAddress"), o.get("targetPort"), o.get("status"), o.get("type")});
        }
    }
    public static void alert(String message) {
        tables[ALERT_TABLE].addRow(new Object[]{Misc.formatTime(System.currentTimeMillis()), message});
    }
}
