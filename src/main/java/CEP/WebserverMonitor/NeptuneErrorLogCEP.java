package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

/**
 * Facade class to set up the CEP Engine to analyze the events recorded by the Webserver
 * @author Khuong Lu, Thien Hoang
 */
public class NeptuneErrorLogCEP {
    private static int[] bruteForce_period = {10, 10};
    private static int[] dictAttack_period = {10, 10};
    private static int[] userBaseScan_period = {10, 10};
    private static int[] bruteForce_threshold = {3, 5};
    private static int[] dictAttack_threshold = {3, 5};
    private static int[] userBaseScan_threshold = {3, 5};

    /**
     * Set up the event streams in the CEP Engine with EPL Statement and some listeners
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static void setup() throws EPCompileException, EPDeployException {
        new EPAdapter().execute("select * from NEL_Event").
                addListener( (newData, __, ___, ____) -> {
                    DashboardAdapter.writeToTable(newData[0], 2);
                });

        setup_BruteForce("LowPriority", bruteForce_period[0], bruteForce_threshold[0]);
        setup_BruteForce("HighPriority", bruteForce_period[1], bruteForce_threshold[1]);
        setup_DictionaryAttack("LowPriority", dictAttack_period[0], dictAttack_threshold[0]);
        setup_DictionaryAttack("HighPriority", dictAttack_period[1], dictAttack_threshold[1]);
        setup_UserBaseScan("LowPriority", userBaseScan_period[0], userBaseScan_threshold[0]);
        setup_UserBaseScan("HighPriority", userBaseScan_period[1], userBaseScan_threshold[1]);
        setup_SuccessChangePassword();

        EPAdapter.quickExecute("@public insert into UserHacked_Alert " +
                "select A.username as username " +
                "from pattern[every A=BruteForce_Alert_HighPriority " +
                "-> B=SuccessChangePassword_Event(username=A.username)]");

        new EPAdapter().execute("select * from BruteForce_Alert_LowPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertLow("Too many failed login attempts on " + data[0].get("username"));
                });

        new EPAdapter().execute("select * from BruteForce_Alert_HighPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertHigh("There seems to be a brute-force attack on " + data[0].get("username"));
                });

        new EPAdapter().execute("select * from UserBaseScan_Alert_HighPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertHigh(data[0].get("clientAddress") + " may be trying to crawl the user base of your platform.");
                });

        new EPAdapter().execute("select * from UserBaseScan_Alert_LowPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertLow(data[0].get("clientAddress") + " failed to register too many times (accounts existed).");
                });

        new EPAdapter().execute("select * from UserHacked_Alert").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertHigh(data[0].get("username") + " may have been hacked because I detected a brute-force attack earlier and a successful password change.");
                });

        new EPAdapter().execute("select * from DictionaryAttack_Alert_LowPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertLow("Too many failed login attempts associated with password = " + data[0].get("password"));
                });

        new EPAdapter().execute("select * from DictionaryAttack_Alert_HighPriority").
                addListener((data, __, ___, ____) -> {
                    DashboardAdapter.alertHigh("Someone is trying to scan the accounts with password = " + data[0].get("password"));
                });
    }

    static final String failedLoginPattern = "Failed login for username % and password (md5 hashed) %";
    static final String failedRegisterPattern = "Failed to register for %. Account already exists.";
    static final String successPasswordChangePattern = "Successfully changed password for %.";

    private static void setup_BruteForce(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String filtered = "FailedLogin_Event_ByUsername_" + id;
        String latest = "FailedLogin_Latest_ByUsername_" + id;
        String alert = "BruteForce_Alert_" + id;
        EPAdapter.quickExecute("@public insert into "+filtered+
                        " select MessageParser.parseFailedLogin(message,1) as username," +
                        " MessageParser.parseFailedLogin(message,2) as password" +
                        " from NEL_Event(message like '"+failedLoginPattern+"')",

                "@public create window "+latest+".win:time("+period+") as "+filtered,
                "insert into "+latest+" select * from "+filtered,

                "@public insert into "+alert+" select username from "+latest+" group by username " +
                        "having count(distinct password) >= "+threshold,

                "on "+alert+" as A delete from "+latest+" as B where A.username=B.username");
    }

    private static void setup_DictionaryAttack(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String filtered = "FailedLogin_Event_ByPassword_" + id;
        String latest = "FailedLogin_Latest_ByPassword_" + id;
        String alert = "DictionaryAttack_Alert_" + id;
        EPAdapter.quickExecute("@public insert into "+filtered+
                        " select MessageParser.parseFailedLogin(message,1) as username," +
                        " MessageParser.parseFailedLogin(message,2) as password" +
                        " from NEL_Event(message like '"+failedLoginPattern+"')",

                "@public create window "+latest+".win:time("+period+") as "+filtered,
                "insert into "+latest+" select * from "+filtered,

                "@public insert into "+alert+" select password from "+latest+" group by password " +
                        "having count(distinct username) >= "+threshold,

                "on "+alert+" as A delete from "+latest+" as B where A.password=B.password");
    }

    private static void setup_UserBaseScan(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String filtered = "FailedRegister_Event__" + id;
        String latest = "FailedRegister_Latest__" + id;
        String alert = "UserBaseScan_Alert_" + id;
        EPAdapter.quickExecute("@public insert into "+filtered+
                        " select MessageParser.parseFailedRegister(message) as username," +
                        " clientAddress" +
                        " from NEL_Event(message like '"+failedRegisterPattern+"')",

                "@public create window "+latest+".win:time("+period+") as "+filtered,
                "insert into "+latest+" select * from "+filtered,

                "@public insert into "+alert+" select clientAddress from "+latest+" group by clientAddress " +
                        "having count(distinct username) >= "+threshold,

                "on "+alert+" as A delete from "+latest+" as B where A.clientAddress=B.clientAddress");
    }

    private static void setup_SuccessChangePassword() throws EPCompileException, EPDeployException {
        String filtered = "SuccessChangePassword_Event";
        EPAdapter.quickExecute("@public insert into "+filtered+
                " select MessageParser.parseSuccessChangePassword(message) as username," +
                " clientAddress" +
                " from NEL_Event(message like '"+successPasswordChangePattern+"')");
    }

    /**
     * Get the current periods after which old events (used to detect brute force) will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getBruteForce_period() {
        return bruteForce_period;
    }

    /**
     * Set the new periods after which old events (used to detect brute force) will expire
     * @param bruteForce_period [period_lowPriority, period_highPriority]
     */
    public static void setBruteForce_period(int[] bruteForce_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.bruteForce_period = bruteForce_period;
    }

    /**
     * Get the current periods after which old events (used to detect dictionary attack) will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getDictAttack_period() {
        return dictAttack_period;
    }

    /**
     * Set the new periods after which old events (used to detect dictionary attack) will expire
     * @param dictAttack_period [period_lowPriority, period_highPriority]
     */
    public static void setDictAttack_period(int[] dictAttack_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.dictAttack_period = dictAttack_period;
    }

    /**
     * Get the current periods after which old events (used to detect user base scanning) will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getUserBaseScan_period() {
        return userBaseScan_period;
    }

    /**
     * Set the new periods after which old events (used to detect user base scan) will expire
     * @param userBaseScan_period [period_lowPriority, period_highPriority]
     */
    public static void setUserBaseScan_period(int[] userBaseScan_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.userBaseScan_period = userBaseScan_period;
    }

    /**
     * Get the current thresholds (number of different passwords) over which a brute-force alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getBruteForce_threshold() {
        return bruteForce_threshold;
    }

    /**
     * Set the new thresholds (number of different passwords) over which a brute-force alert will be raised
     * @param bruteForce_threshold [threshold_lowPriority, threshold_highPriority]
     */
    public static void setBruteForce_threshold(int[] bruteForce_threshold) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.bruteForce_threshold = bruteForce_threshold;
    }

    /**
     * Get the current thresholds (number of different usernames) over which a dictionary attack alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getDictAttack_threshold() {
        return dictAttack_threshold;
    }

    /**
     * Set the new thresholds (number of different usernames) over which a dictionary attack alert will be raised
     * @param dictAttack_threshold [threshold_lowPriority, threshold_highPriority]
     */
    public static void setDictAttack_threshold(int[] dictAttack_threshold) {
        NeptuneErrorLogCEP.dictAttack_threshold = dictAttack_threshold;
    }

    /**
     * Get the current thresholds (number of different usernames) over which a user base scan alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getUserBaseScan_threshold() {
        return userBaseScan_threshold;
    }

    /**
     * Set the new thresholds (number of different usernames) over which a user base scan alert will be raised
     * @param userBaseScan_threshold [threshold_lowPriority, threshold_highPriority]
     */
    public static void setUserBaseScan_threshold(int[] userBaseScan_threshold) {
        NeptuneErrorLogCEP.userBaseScan_threshold = userBaseScan_threshold;
    }
}
