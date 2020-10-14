import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.IOException;

public class MonitorEvent {
    private static String fileErrorlog = "/var/log/apache2/error.log";    
    private static String fileAccesslog = "/var/log/apache2/access.log";
    private static ArrayList<String> allLines = new ArrayList<String>();
    private static int currentLine = 0;

    public static void main (String [] args) throws Exception {
        System.out.println("Please wait while I'm configuring the Event Processor... ");
        new EPAdapter().execute("get-user-access-event", "select * from AccessEvent(loggInCommand=true)").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println(newData[0].get("clientIpAddress") + " Message: " + newData[0].get("message")
                            + " attempted to log in at " + newData[0].get("errorTimeStamp"));
        });

        new EPAdapter().execute("create-last-3-event-window",
                "@public create window LastEvents.win:length(200) as AccessEvent");

        new EPAdapter().execute("fill-window", "insert into LastEvents select * from AccessEvent(loggInCommand=true)");

        new EPAdapter().execute("count-consecutive-failures", "@public insert into ConsecutiveFailureCount " +
                "select clientIpAddress, errorTimeStamp, count(*) as counter from LastEvents group by clientIpAddress");
                
        new EPAdapter().execute("get-alert", "insert into Alert " +
            "select * from ConsecutiveFailureCount(counter % 3 = 0)").
            addListener((newData, __, ___, ____) -> {
                System.out.println("Alert: By " + newData[0].get("errorTimeStamp") + " " + newData[0].get("clientIpAddress")
                        + " has many consecutive failed attempts!");
        });
        // AccessEvent newEvent = getAnEvent();
        // String lastTimeStamp = newEvent.getErrorTimeStamp();
        // while (true) {
        //     newEvent = getAnEvent();
        //     if (newEvent.getErrorTimeStamp() == lastTimeStamp) continue;
        //     lastTimeStamp = newEvent.getErrorTimeStamp();
        //     EPAdapter.runtime.getEventService().sendEventBean(newEvent, "AccessEvent");
        // }

        System.out.println("Read file [" + fileErrorlog + "]");
        int numberOfRecoredLog = 0;
        while (true) {
            ArrayList<AccessEvent> httpLogEvents = null;
            try {
                httpLogEvents = getAnEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int numberOfCurrentLog = httpLogEvents.size();
            if (numberOfRecoredLog < numberOfCurrentLog) {
                for (int i = numberOfRecoredLog; i < numberOfCurrentLog; i++) {
                    EPAdapter.runtime.getEventService().sendEventBean(httpLogEvents.get(i), "AccessEvent");
                }
                numberOfRecoredLog = numberOfCurrentLog;
            }
        }
    }

    static ArrayList<AccessEvent> getAnEvent() throws IOException {
        File file = new File(fileErrorlog);
        FileInputStream fis = null;
        BufferedReader reader = null;
        String lineinput = "";
        ArrayList<AccessEvent> result  = new ArrayList<>();
        try {
            fis = new FileInputStream (file);
            reader = new BufferedReader (new InputStreamReader (fis));

            while ((lineinput = reader.readLine ())!= null) {
                result.add(new AccessEvent(lineinput));
                // newEvent.processErrorLine (lineinput);
                // EPAdapter.runtime.getEventService().sendEventBean(newEvent, "AccessEvent");  
            }
            
        } catch (FileNotFoundException e) {
            System.out.println ("File [" + fileErrorlog + "] does not exist");
        } 
        return result;
    }
}
