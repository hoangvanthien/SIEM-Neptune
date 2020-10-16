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
        // new EPAdapter().execute("get-user-error-event", "select * from AccessEvent").
        //         addListener( (newData, __, ___, ____) -> {
        //             System.out.println(newData[0].get("userName") + " " + newData[0].get("accepted") + " Code: " + newData[0].get("httpStatusCode")
        //                     + " attempted to log in at " + newData[0].get("timeStamp"));
        // });

        // new EPAdapter().execute("create-last-3-event-window",
        //         "@public create window LastEvents.win:time(10) as AccessEvent");

        // new EPAdapter().execute("fill-window", "insert into LastEvents select * from AccessEvent(accepted=false)");

        // new EPAdapter().execute("count-consecutive-failures", "@public insert into ConsecutiveFailureCount " +
        //         "select userName, timeStamp, httpStatusCode, count(*) as counter from LastEvents group by userName");
                
        // new EPAdapter().execute("get-alert", "insert into Alert " +
        //     "select * from ConsecutiveFailureCount(counter >= 3)").
        //     addListener((newData, __, ___, ____) -> {
        //         System.out.println("Alert: By " + newData[0].get("timeStamp") + " " + newData[0].get("userName")
        //                 + " has many consecutive failed attempts!");
        // });
        new AccessEventStatement(10,3);
        new ErrorEventStatement(10,3);

        System.out.println("Read files [" + fileAccesslog + "]");
        int numberOfRecoredLog = 0;
        int numberOfRecoredErrorLog = 0;
        int alter = 0;
        ArrayList<AccessEvent> httpLogEvents = null;
        ArrayList<ErrorEvent> httpErrorEvents = null;
        while (true) {
            try {
                httpLogEvents = getAnEvent();
                httpErrorEvents = getAnErrorEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (alter % 2 == 0){
                numberOfRecoredLog = sendEvent(httpLogEvents, "AccessEvent", numberOfRecoredLog);
            } else {
                numberOfRecoredErrorLog = sendEvent(httpErrorEvents, "ErrorEvent", numberOfRecoredErrorLog);
            }
            alter += 1;
        }
    }
    static int sendEvent(ArrayList<?> httpEvents, String eventType, int numerOfRecored) {
        int numberOfCurrentLog = httpEvents.size();
        if (numerOfRecored != 0 ){
            if (numerOfRecored < numberOfCurrentLog) {
                for (int i = numerOfRecored; i < numberOfCurrentLog; i++) {
                    EPAdapter.runtime.getEventService().sendEventBean(httpEvents.get(i), eventType);
                }
            }
        }
        numerOfRecored = numberOfCurrentLog;
        return numerOfRecored;
    }
    static ArrayList<AccessEvent> getAnEvent() throws IOException {
        File file = new File(fileAccesslog);
        FileInputStream fis = null;
        BufferedReader reader = null;
        String lineinput = "";
        ArrayList<AccessEvent> result  = new ArrayList<>();
        try {
            fis = new FileInputStream (file);
            reader = new BufferedReader (new InputStreamReader (fis));

            while ((lineinput = reader.readLine ())!= null) {
                result.add(new AccessEvent(lineinput)); 
            }
            
        } catch (FileNotFoundException e) {
            System.out.println ("File [" + fileAccesslog + "] does not exist");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static ArrayList<ErrorEvent> getAnErrorEvent() throws IOException {
        File file = new File(fileErrorlog);
        FileInputStream fis = null;
        BufferedReader reader = null;
        String lineErrorInput = "";
        ArrayList<ErrorEvent> result  = new ArrayList<>();
        try {
            fis = new FileInputStream (file);
            reader = new BufferedReader (new InputStreamReader (fis));

            while ((lineErrorInput = reader.readLine ())!= null) {
                result.add(new ErrorEvent(lineErrorInput)); 
            }
            
        } catch (FileNotFoundException e) {
            System.out.println ("File [" + fileErrorlog + "] does not exist");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
