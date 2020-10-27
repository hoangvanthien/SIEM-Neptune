package Utilities;

import CEP.PortScanDetector.*;
import CEP.WebserverMonitor.ApacheAccessLogEvent;
import CEP.WebserverMonitor.NeptuneErrorLogEvent;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

public class EPAdapter {

    public static void setup() {
        compiler = EPCompilerProvider.getCompiler();
        configuration = new Configuration();
        configuration.getCommon().addEventType("AEL_Event", NeptuneErrorLogEvent.class);
        configuration.getCommon().addEventType("AAL_Event", ApacheAccessLogEvent.class);
        configuration.getCommon().addEventType("TCPPacket", TCPPacket.class);
        configuration.getCommon().addEventType("VerticalPortScanAlert", VerticalPortScanEvent.class);
        configuration.getCommon().addEventType("HorizontalPortScanAlert", HorizontalPortScanEvent.class);
        configuration.getCommon().addEventType("BlockPortScanAlert", BlockPortScanEvent.class);

        configuration.getRuntime().getLogging().setEnableExecutionDebug(false);
        configuration.getRuntime().getLogging().setEnableTimerDebug(false);
        runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        arguments = new CompilerArguments(configuration);
        arguments.getPath().add(runtime.getRuntimePath());
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

    public void addListener(UpdateListener listener) {
        this.statement.addListener(listener);
    }

    private EPStatement statement;
    public static EPCompiler compiler;
    public static Configuration configuration;
    public static CompilerArguments arguments;
    public static EPRuntime runtime;
}
