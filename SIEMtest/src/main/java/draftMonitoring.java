import java.io.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Scanner;

import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public class draftMonitoring {

    public static void main(String[] args) throws IOException {
        System.out.println("Please wait while I'm configuring the Event Processor... ");
        new EPAdapter().execute("get-all-access-event", "select * from AccessEvent").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println("Access Log Message:" + newData[0].get("ipAddress")
                            + " attempted to log in at " + newData[0].get("dateTime"));
                });

        new EPAdapter().execute("get-failed-Access-event", "insert into FailedEvent " +
                "select ipAddress, dateTime from AccessEvent(accepted=false)").addListener( (newData, __, ___, ____) -> {
            System.out.println("FailedLogMessage: Someone may be trying to hack your computer!");
        });


        new EPAdapter().execute("count-consecutive-failures", "@public insert into ConsecutiveFailureCount " +
                "select count(*) as counter from LastEvents(accepted=false)");

        new EPAdapter().execute("at least 3 login at last 5 minutes", "select count(*) from ConsecutiveFailureCount(5 min) having count(*) >= 3\n").
        addListener((newData, __, ___, ____) -> {
                    System.out.println("Alert: By " + formatDate((long) newData[0].get("dateTime"))
                            + " there has been " + newData[0].get("counter") + " consecutive attempts!");
                });

    }


    File file = new File("log.txt");
    Scanner scanner = null;
        try {
        scanner = new Scanner(file);
    } catch (
    FileNotFoundException e) {
        e.printStackTrace();
    }

    static AccessEvent getAnEvent() throws IOException {
        Process process = Runtime.getRuntime()
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        return new AccessEvent(new ObjectMapper().readValue(line, HashMap.class));
    }

    static String formatDate(long timestampInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'").
                format(new Date(
                        new Timestamp(timestampInMillis).getTime()));
    }

}
