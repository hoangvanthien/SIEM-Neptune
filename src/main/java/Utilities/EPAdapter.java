package Utilities;

import CEP.PortScanDetector.*;
import CEP.WebserverMonitor.*;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.Module;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

import java.io.File;
import java.io.IOException;

/**
 * Adapter class that helps executing EPL statements with less effort
 * @author Thien Hoang
 */
public class EPAdapter {

    /**
     * Prepare the environment
     * That includes compiler, configuration, runtime, and arguments.
     */
    public static void setup() {
        compiler = EPCompilerProvider.getCompiler();
        configuration = new Configuration();
        configuration.getCommon().addEventType("NEL_Event", NeptuneErrorLogEvent.class);
        configuration.getCommon().addEventType("AAL_Event", ApacheAccessLogEvent.class);
        configuration.getCommon().addEventType("TCPPacket_Event", TCPPacketEvent.class);
        configuration.getCommon().addImport("Utilities.MessageParser");

        configuration.getRuntime().getLogging().setEnableExecutionDebug(false);
        configuration.getRuntime().getLogging().setEnableTimerDebug(false);
        runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        arguments = new CompilerArguments(configuration);
        arguments.getPath().add(runtime.getRuntimePath());
    }

    /**
     * Destroy the environment
     * That includes compiler, configuration, runtime, and arguments.
     */
    public static void destroy() {
        if (runtime != null) runtime.destroy();
        compiler = null;
        configuration = null;
        runtime = null;
        arguments = null;
    }

    /**
     * Constructor
     * Start creating the environment iff it does not exist
     */
    public EPAdapter() {
        if (compiler != null && configuration != null && runtime != null) return;
        setup();
    }

    private EPAdapter execute(String name, String statement) throws EPCompileException, EPDeployException {
        EPCompiled epCompiled = compiler.compile("@name('"+ name +"') " + statement, arguments);
        arguments.getPath().add(epCompiled);
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);
        this.statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), name);
        return this;
    }

    /**
     * Execute an EPL statement in the shared environment
     * @param statement the EPL statement
     * @return an instance of the adapter, which you can use to attach a listener with addListener
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public EPAdapter execute(String statement) throws EPCompileException, EPDeployException {
        return execute("Neptune"+(int)(Math.random()*100000), statement);
    }

    /**
     * Fire-and-forget execution
     * Execute an EPL statement to which you do not intend to attach any listener
     * @param statements the EPL statement
     */
    public static void quickExecute(String... statements) throws EPCompileException, EPDeployException {
        for (String statement : statements) {
            new EPAdapter().execute("Neptune"+(int)(Math.random()*100000), statement);
        }
    }

    /**
     * Execute all statements in a file in the shared environment
     * With this method, you will not be able to attach listener to any of the statements in the file.
     * @param filename the relative path to the file with respect to the resources folder
     * @throws IOException
     * @throws ParseException
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static void executeFile(String filename) throws IOException, ParseException, EPCompileException, EPDeployException {
        new EPAdapter();
        Module module = compiler.readModule(filename, EPAdapter.class.getClassLoader());
        EPCompiled epCompiled = compiler.compile(module, arguments);
        arguments.getPath().add(epCompiled);
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);
    }

    /**
     * Attach a listener to the statement belong to this instance.
     * Normally you would call this immediately after executing a statement.
     * For example: new EPAdapter().execute("SELECT * FROM TCPPacketEvent").addListener(...)
     * @param listener preferably a lambda function with 4 parameters
     */
    public void addListener(UpdateListener listener) {
        this.statement.addListener(listener);
    }

    /**
     * Send a POJO to an event stream in CEP
     * @param event the POJO
     * @param eventType the name of the event stream set up in CEP
     * @param <EventType> the Java-type of the object
     */
    public static <EventType> void sendEvent(EventType event, String eventType) {
        runtime.getEventService().sendEventBean(event, eventType);
    }

    private EPStatement statement;
    /**
     * Singleton compiler
     */
    public static EPCompiler compiler;
    /**
     * Singleton configuration
     */
    public static Configuration configuration;
    /**
     * Singleton arguments
     */
    public static CompilerArguments arguments;
    /**
     * Singleton runtime
     */
    public static EPRuntime runtime;
}
