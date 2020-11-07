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
 * setup CEP engine structure and add listener
 * @author Hoang Van Thien
 */
public class EPAdapter {
    /**
     * config and setup EPL compiler
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
     * clean and restart the EPL compiler
     */
    public static void destroy() {
        if (runtime != null) runtime.destroy();
        compiler = null;
        configuration = null;
        runtime = null;
        arguments = null;
    }

    /**
     * start the EPL compiler
     */
    public EPAdapter() {
        if (compiler != null && configuration != null && runtime != null) return;
        setup();
    }

    /**
     * setup EPL structure and Run the EPL compiler
     * @param name the name of EPL statement
     * @param statement instance contain the EPL statements
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
    public EPAdapter execute(String name, String statement) throws EPCompileException, EPDeployException {
        EPCompiled epCompiled = compiler.compile("@name('"+ name +"') " + statement, arguments);
        arguments.getPath().add(epCompiled);
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);
        this.statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), name);
        return this;
    }

    public EPAdapter execute(String statement) throws EPCompileException, EPDeployException {
        return execute("Neptune"+(int)(Math.random()*100000), statement);
    }

    /**
     * Fire-and-forget execution
     * @param statements instance contain EPL statement
     */
    public static void quickExecute(String... statements) throws EPCompileException, EPDeployException {
        for (String statement : statements) {
            new EPAdapter().execute("Neptune"+(int)(Math.random()*100000), statement);
        }
    }

    /**
     *
     * @param filename
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
     * add listener to wait the event
     * @param listener instance to trigger action
     */
    public void addListener(UpdateListener listener) {
        this.statement.addListener(listener);
    }

    /**
     * wrap the events and sed to CEP engine
     * @param event instance to contain events
     * @param eventType the event type of object
     * @param <EventType> the predefined type of event object
     */
    public static <EventType> void sendEvent(EventType event, String eventType) {
        runtime.getEventService().sendEventBean(event, eventType);
    }

    private EPStatement statement;
    public static EPCompiler compiler;
    public static Configuration configuration;
    public static CompilerArguments arguments;
    public static EPRuntime runtime;
}
