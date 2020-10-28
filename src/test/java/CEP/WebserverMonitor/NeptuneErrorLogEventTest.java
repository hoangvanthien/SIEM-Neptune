package CEP.WebserverMonitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NeptuneErrorLogEventTest {

    @Test
    void parseTest() throws Exception {
        NeptuneErrorLogEvent event = new NeptuneErrorLogEvent("[Thu Oct 15 23:43:24.866561 2020] [php7:notice] " +
                "[pid 847] [client 192.168.56.1:54578] Neptune: Unauthorized access to /special/code01542.php. " +
                "User has not logged in., referer: http://192.168.56.101/special/");
        assertEquals(event.getTimestamp(), 1602780204866L);
        assertEquals(event.getTimeFormatted(), "15/Oct/2020 23:43:24");
    }

}