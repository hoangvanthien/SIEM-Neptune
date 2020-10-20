package CEP.WebserverMonitor;

import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

public class ApacheAccessLogCEP {
    public ApacheAccessLogCEP(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException {

        new EPAdapter().execute("AAL-get-event", "select * from AAL_Event").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println("[" + newData[0].get("timeFormatted") + "] " +
                            newData[0].get("clientAddress") + " sent a " + newData[0].get("requestMethod") +
                            " to " + newData[0].get("url") + " and got status code " + newData[0].get("httpStatusCode"));
        });

        new EPAdapter().execute("AAL-create-latest-event-window",
                "@public create window AAL_Latest.win:time(" + alertPeriod + ") as AAL_Event");

        new EPAdapter().execute("AAL-fill-window", "insert into AAL_Latest select * from AAL_Event(httpStatusCode like '4%')");

        new EPAdapter().execute("AAL-count-failures", "@public insert into AAL_FailureCount " +
                "select current_timestamp() as timestamp, url, count(*) as counter from AAL_Latest group by url");
                
        new EPAdapter().execute("AAL-get-alert", "insert into AAL_Alert " +
            "select * from AAL_FailureCount(counter >= " + consecutiveFailed + ")").
            addListener((newData, __, ___, ____) -> {
                System.out.println("Alert: By " + newData[0].get("timestamp") + " there have been too many " +
                        "bad requests to " + newData[0].get("url"));
        });
    }
}
