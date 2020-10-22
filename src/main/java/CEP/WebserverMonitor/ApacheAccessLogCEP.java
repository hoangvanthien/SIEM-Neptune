package CEP.WebserverMonitor;

import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

public class ApacheAccessLogCEP {
    public ApacheAccessLogCEP(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException {

        EPAdapter.quickExecute(
                "@public create window AAL_Latest.win:time(" + alertPeriod + ") as AAL_Event",
                "insert into AAL_Latest select * from AAL_Event",
                "@public insert into AAL_FailureCount select current_timestamp() as timestamp, url, count(*) as counter from AAL_Latest(httpStatusCode like '4%') group by url",
                "@public insert into AAL_Alert select * from AAL_FailureCount(counter >= " + consecutiveFailed + ")"
        );

        new EPAdapter().execute("select * from AAL_Event").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println("[" + newData[0].get("timeFormatted") + "] " +
                            newData[0].get("clientAddress") + " sent a " + newData[0].get("requestMethod") +
                            " to " + newData[0].get("url") + " and got status code " + newData[0].get("httpStatusCode"));
        });

        new EPAdapter().execute("select * from AAL_Alert").
            addListener((newData, __, ___, ____) -> {
                System.out.println("[" + Misc.formatTime((long)newData[0].get("timestamp")) + "] Alert: There have been too many " +
                        "bad requests to " + newData[0].get("url"));
        });
    }
}
