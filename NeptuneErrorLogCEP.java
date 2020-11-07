package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
/**
 * Read and parse the Neptune Error Log file
 * @author Hoang Van Thien
 */
public class NeptuneErrorLogCEP {
    private static int[] failedLoginByUsername_period = {10, 10};
    private static int[] failedLoginByPassword_period = {10, 10};
    private static int[] failedRegister_period = {10, 10};
    private static int[] failedLoginByUsername_threshold = {3, 5};
    private static int[] failedLoginByPassword_threshold = {3, 5};
    private static int[] failedRegister_threshold = {3, 5};

    /**
     * add listener and setup priority from low to high in range
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     * @throws NoSuchFieldException Indicate that the method doesn't have a field of a specified name.
     * @throws IllegalAccessException Indicate that the method does not have access to specified field
     */
    public static void setup() throws EPCompileException, EPDeployException, NoSuchFieldException, IllegalAccessException {
        new EPAdapter().execute("select * from NEL_Event").
                addListener( (newData, __, ___, ____) -> {
                    DashboardAdapter.writeToTable(newData[0], 2);
                });

        setup_BruteForce("LowPriority", failedLoginByUsername_period[0], failedLoginByUsername_threshold[0]);
        setup_BruteForce("HighPriority", failedLoginByUsername_period[1], failedLoginByUsername_threshold[1]);
        setup_DictionaryAttack("LowPriority", failedLoginByPassword_period[0], failedLoginByPassword_threshold[0]);
        setup_DictionaryAttack("HighPriority", failedLoginByPassword_period[1], failedLoginByPassword_threshold[1]);
        setup_UserBaseScan("LowPriority", failedRegister_period[0], failedRegister_threshold[0]);
        setup_UserBaseScan("HighPriority", failedRegister_period[1], failedRegister_threshold[1]);
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

    /**
     * EPL statement for Brute Force
     * @param id user's ip address
     * @param period time interval condition for consecutive failed login
     * @param threshold the maximum of consecutive failed login by username
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
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

    /**
     * EPL statement for Dictionary attack
     * @param id user's ip address
     * @param period time interval condition for consecutive failed login
     * @param threshold the maximum of consecutive failed login by password
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
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

    /**
     * EPL statement for failed register
     * @param id user's ip address
     * @param period time interval condition for consecutive failed register
     * @param threshold the maximum of consecutive failed register account
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
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

    /**
     * EPL statement for successfully change password
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
    private static void setup_SuccessChangePassword() throws EPCompileException, EPDeployException {
        String filtered = "SuccessChangePassword_Event";
        EPAdapter.quickExecute("@public insert into "+filtered+
                " select MessageParser.parseSuccessChangePassword(message) as username," +
                " clientAddress" +
                " from NEL_Event(message like '"+successPasswordChangePattern+"')");
    }
    /**
     * return the time interval condition for brute force
     * @return the value of time interval condition
     */
    public static int[] getFailedLoginByUsername_period() {
        return failedLoginByUsername_period;
    }
    /**
     * set the time interval condition for brute force
     * @param failedLoginByUsername_period instance for period condition
     */
    public static void setFailedLoginByUsername_period(int[] failedLoginByUsername_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.failedLoginByUsername_period = failedLoginByUsername_period;
    }
    /**
     * return the time interval condition for dictionary attack
     * @return the value of time interval condition
     */
    public static int[] getFailedLoginByPassword_period() {
        return failedLoginByPassword_period;
    }
    /**
     * set the time interval condition for dictionary attack
     * @param failedLoginByPassword_period instance for period condition
     */
    public static void setFailedLoginByPassword_period(int[] failedLoginByPassword_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.failedLoginByPassword_period = failedLoginByPassword_period;
    }
    /**
     * return the time interval condition for failed register
     * @return the value of time interval condition
     */
    public static int[] getFailedRegister_period() {
        return failedRegister_period;
    }
    /**
     * set the time interval condition for failed register
     * @param failedRegister_period instance for period condition
     */
    public static void setFailedRegister_period(int[] failedRegister_period) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.failedRegister_period = failedRegister_period;
    }
    /**
     * return the threshold for brute force
     * @return the value of threshold
     */
    public static int[] getFailedLoginByUsername_threshold() {
        return failedLoginByUsername_threshold;
    }
    /**
     * set the threshold for consecutive failed login by username
     * @param failedLoginByUsername_threshold instance for threshold
     */
    public static void setFailedLoginByUsername_threshold(int[] failedLoginByUsername_threshold) {
        EPAdapter.destroy();
        NeptuneErrorLogCEP.failedLoginByUsername_threshold = failedLoginByUsername_threshold;
    }
    /**
     * return the threshold for dictionary attack
     * @return the value of threshold
     */
    public static int[] getFailedLoginByPassword_threshold() {
        return failedLoginByPassword_threshold;
    }
    /**
     * set the threshold for consecutive failed login by password
     * @param failedLoginByPassword_threshold instance for threshold
     */
    public static void setFailedLoginByPassword_threshold(int[] failedLoginByPassword_threshold) {
        NeptuneErrorLogCEP.failedLoginByPassword_threshold = failedLoginByPassword_threshold;
    }
    /**
     * return the threshold for failed register
     * @return the value of threshold
     */
    public static int[] getFailedRegister_threshold() {
        return failedRegister_threshold;
    }
    /**
     * set the threshold for consecutive failed register
     * @param failedRegister_threshold instance for threshold
     */
    public static void setFailedRegister_threshold(int[] failedRegister_threshold) {
        NeptuneErrorLogCEP.failedRegister_threshold = failedRegister_threshold;
    }
}
