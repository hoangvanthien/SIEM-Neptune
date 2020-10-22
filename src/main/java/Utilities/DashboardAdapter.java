package Utilities;

import Dashboard.Dashboard;
import com.espertech.esper.common.client.EventBean;

import javax.swing.table.DefaultTableModel;

public class DashboardAdapter {
    static DefaultTableModel[] tables = {Dashboard.dashboards.dtm, Dashboard.dashboards.dtm2, Dashboard.dashboards.dtm3};
    public static void writeToTable(EventBean o, int i) {
        if (i == 1) {
            tables[i - 1].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("httpStatusCode"), o.get("requestMethod")});
        } else if (i == 2) {
            tables[i - 1].addRow(new Object[]{o.get("timeFormatted"), o.get("clientAddress"), o.get("url"), o.get("message")});
        } else if (i == 3) {

        }
    }
}
