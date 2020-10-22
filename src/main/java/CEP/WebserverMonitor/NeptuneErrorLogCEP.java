package CEP.WebserverMonitor;

import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.lang.reflect.InvocationTargetException;

public class NeptuneErrorLogCEP {
    static int alertPeriod, threshold;
    public static void setup(int _alertPeriod, int _threshold) throws EPCompileException, EPDeployException, NoSuchFieldException, IllegalAccessException {
        alertPeriod = _alertPeriod;
        threshold = _threshold;
        new EPAdapter().execute("select * from NEL_Event").
                addListener( (newData, __, ___, ____) -> {
                    System.out.println("[" + newData[0].get("timeFormatted") + "] " +
                            newData[0].get("clientAddress") + " " + newData[0].get("message"));
                });

        setupEventStream(FailedLoginEvent.class);
        setupEventStream(FailedRegisterDuplicateEvent.class);
        setupEventStream(SuccessChangePasswordEvent.class);

        EPAdapter.quickExecute(
                "@public insert into NEL_FailedLoginEventByUsername_Count select current_timestamp() as timestamp, username, count(*) as counter from NEL_FailedLoginEvent_Latest group by username",
                "@public insert into NEL_FailedLoginEventByPassword_Count select current_timestamp() as timestamp, password, count(*) as counter from NEL_FailedLoginEvent_Latest group by password",
                "@public insert into NEL_FailedRegisterDuplicateEvent_Count select current_timestamp() as timestamp, clientAddress, count(*) as counter from NEL_FailedRegisterDuplicateEvent_Latest group by clientAddress",
                "@public insert into NEL_BruteForceEvent select * from NEL_FailedLoginEventByUsername_Count(counter >= " + threshold + ")",
                "@public insert into NEL_SinglePasswordHackEvent select * from NEL_FailedLoginEventByPassword_Count(counter >= " + threshold + ")",
                "@public insert into NEL_UserBaseScanEvent select * from NEL_FailedRegisterDuplicateEvent_Count(counter >= " + threshold + ")",
                "@public insert into NEL_UserHackedEvent select current_timestamp() as timestamp, A.username as username from pattern[A=NEL_BruteForceEvent -> B=NEL_SuccessChangePasswordEvent(username=A.username)]"
        );

        new EPAdapter().execute("select * from NEL_BruteForceEvent").
                addListener((data, __, ___, ____) -> {
                    System.out.println("[" + Misc.formatTime((long)data[0].get("timestamp")) + "] ALERT: There seems to be a brute-force attack on " + data[0].get("username"));
                });

        new EPAdapter().execute("select * from NEL_UserBaseScanEvent").
                addListener((data, __, ___, ____) -> {
                    System.out.println("[" + Misc.formatTime((long)data[0].get("timestamp")) + "] ALERT: " + data[0].get("clientAddress") + " may be trying to crawl the user base of your platform.");
                });

        new EPAdapter().execute("select * from NEL_UserHackedEvent").
                addListener((data, __, ___, ____) -> {
                    System.out.println("[" + Misc.formatTime((long)data[0].get("timestamp")) + "] ALERT: " + data[0].get("username") + " may have been hacked because I detected a brute-force attack earlier and a successful password change.");
                });

        new EPAdapter().execute("select * from NEL_SinglePasswordHackEvent").
                addListener((data, __, ___, ____) -> {
                    System.out.println("[" + Misc.formatTime((long)data[0].get("timestamp")) + "] ALERT: Someone is trying to scan for accounts having password as " + data[0].get("password"));
                });
    }

    static <T> void setupEventStream(Class<T> klass) throws EPCompileException, EPDeployException, NoSuchFieldException, IllegalAccessException {
        new EPAdapter().execute("NEL-catch-"+klass.getSimpleName(), "select * from NEL_Event(message like '" + klass.getDeclaredField("REGEXP_LIKE").get(null) + "')").
                addListener( (newData, __, ___, ____) -> {
                    try {
                        T event = klass.getDeclaredConstructor(EventBean.class).newInstance(newData[0]);
                        Monitor.sendEvent(event, "NEL_"+klass.getSimpleName());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
        EPAdapter.quickExecute(
                "@public create window NEL_" + klass.getSimpleName() + "_Latest.win:time("+alertPeriod+") as NEL_"+klass.getSimpleName(),
                "insert into NEL_"+klass.getSimpleName()+"_Latest select * from NEL_"+klass.getSimpleName()
        );
    }
}
