package CEP.WebserverMonitor;

import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

public class NeptuneErrorLogCEP {
    public NeptuneErrorLogCEP(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException {

        new EPAdapter().execute("get-user-error-event", "select * from ErrorEvent(loggInCommand=true)").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println(newData[0].get("clientIpAddress") + " message " + newData[0].get("message")
                            + " attempted to log in at " + newData[0].get("timeStamp"));
        });

        new EPAdapter().execute("create-last-3-event-window",
                "@public create window LastErrorEvents.win:time(" + alertPeriod + ") as ErrorEvent");

        new EPAdapter().execute("fill-window", "insert into LastErrorEvents select * from ErrorEvent(loggInCommand=true)");

        new EPAdapter().execute("count-consecutive-failures", "@public insert into ConsecutiveFailureErrorCount " +
                "select clientIpAddress, timeStamp, count(*) as counter from LastErrorEvents group by clientIpAddress");
                
        new EPAdapter().execute("get-alert", "insert into AlertError " +
            "select * from ConsecutiveFailureErrorCount(counter >= " + consecutiveFailed + ")").
            addListener((newData, __, ___, ____) -> {
                System.out.println("Alert: At " + newData[0].get("timeStamp") + " " + newData[0].get("clientIpAddress")
                        + " has many consecutive failed attempts!");
        });
    }
}
