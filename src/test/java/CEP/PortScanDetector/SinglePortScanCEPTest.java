package CEP.PortScanDetector;

import Utilities.EPAdapter;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SinglePortScanCEPTest {

    @Test
    void setup() throws EPCompileException, EPDeployException, ParseException, IOException {
        new EPAdapter();
        SinglePortScanCEP.setup();
    }
}