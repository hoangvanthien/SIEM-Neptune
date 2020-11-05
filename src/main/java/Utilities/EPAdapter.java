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

public class EPAdapter {

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

    public static void destroy() {
        if (runtime != null) runtime.destroy();
        compiler = null;
        configuration = null;
        runtime = null;
        arguments = null;
    }

    public EPAdapter() {
        if (compiler != null && configuration != null && runtime != null) return;
        setup();
    }

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
     * @param statements
     */
    public static void quickExecute(String... statements) throws EPCompileException, EPDeployException {
        for (String statement : statements) {
            new EPAdapter().execute("Neptune"+(int)(Math.random()*100000), statement);
        }
    }

    public static void executeFile(String filename) throws IOException, ParseException, EPCompileException, EPDeployException {
        new EPAdapter();
        Module module = compiler.readModule(filename, EPAdapter.class.getClassLoader());
        EPCompiled epCompiled = compiler.compile(module, arguments);
        arguments.getPath().add(epCompiled);
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);
    }

    public void addListener(UpdateListener listener) {
        this.statement.addListener(listener);
    }

    public static <EventType> void sendEvent(EventType event, String eventType) {
        runtime.getEventService().sendEventBean(event, eventType);
    }

    private EPStatement statement;
    public static EPCompiler compiler;
    public static Configuration configuration;
    public static CompilerArguments arguments;
    public static EPRuntime runtime;
}
