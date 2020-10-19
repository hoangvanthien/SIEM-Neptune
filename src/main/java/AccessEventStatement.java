import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

public class AccessEventStatement {
    public AccessEventStatement(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException {

        new EPAdapter().execute("get-user-access-event", "select * from AccessEvent").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println(newData[0].get("userName") + " code: " + newData[0].get("httpStatusCode")
                            + " attempted to log in at " + newData[0].get("timeStamp"));
        });

        new EPAdapter().execute("create-last-3-event-window",
                "@public create window LastEvents.win:time(" + alertPeriod + ") as AccessEvent");

        new EPAdapter().execute("fill-window", "insert into LastEvents select * from AccessEvent(accepted=false)");

        new EPAdapter().execute("count-consecutive-failures", "@public insert into ConsecutiveFailureCount " +
                "select userName, timeStamp, httpStatusCode, count(*) as counter from LastEvents group by userName");
                
        new EPAdapter().execute("get-alert", "insert into Alert " +
            "select * from ConsecutiveFailureCount(counter >= " + consecutiveFailed + ")").
            addListener((newData, __, ___, ____) -> {
                System.out.println("Alert: At " + newData[0].get("timeStamp") + " " + newData[0].get("userName")
                        + " has many consecutive failed attempts!");
        });
    }
}
