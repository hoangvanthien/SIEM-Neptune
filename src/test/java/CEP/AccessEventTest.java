package CEP;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessEventTest {

    @Test
    void parseAccessLogLine() {
        AccessEvent accessEvent = new AccessEvent("192.168.56.1 - - [14/Oct/2020:18:27:31 +0700] \"GET /login.php " +
                "HTTP/1.1\" 200 691 \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/86.0.4240.75 Safari/537.36 Edg/86.0.622.38\"");
        assertEquals(accessEvent.toString(), "192.168.56.1 - [14/Oct/2020 18:27:31] \"GET /login.php HTTP/1.1\" 200");
        assertEquals(accessEvent.getTimestamp(), 1602674851000L);
    }
}