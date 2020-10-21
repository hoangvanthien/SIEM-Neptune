package Utilities;

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

    /**
     * Fire-and-forget execution
     * @param statements
     */
    public static void quickExecute(String... statements) throws EPCompileException, EPDeployException {
        for (String statement : statements) {
            new EPAdapter().execute("Neptune"+(int)(Math.random()*100000), statement);
        }
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
