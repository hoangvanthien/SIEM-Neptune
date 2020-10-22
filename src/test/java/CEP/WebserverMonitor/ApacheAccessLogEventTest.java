package CEP.WebserverMonitor;

import CEP.WebserverMonitor.NeptuneErrorLogEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApacheAccessLogEventTest {

    @Test
    void parseTest() throws Exception {
        ApacheAccessLogEvent event = new ApacheAccessLogEvent("192.168.56.1 - - [14/Oct/2020:18:27:31 +0700] \"GET /login.php " +
                "HTTP/1.1\" 200 691 \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/86.0.4240.75 Safari/537.36 Edg/86.0.622.38\"");
        assertEquals(event.getUrl(), "login.php");
        assert(!event.isBadRequest());
        assertEquals(event.getTimestamp(), 1602674851000L);
    }
}