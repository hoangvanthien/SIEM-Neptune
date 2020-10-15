import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestCountSuccessfulIP {

    public static void Count(String record) {

        final String regex = "^(\\S+) (\\S+) (\\S+) " + "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+)" + " (\\S+)\\s*(\\S+)?\\s*\" (\\d{3}) (\\S+)";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(record);

        HashMap<String, Integer> countIP = new HashMap<String, Integer>();
        while (matcher.find()) {

            String IP = matcher.group(1);
            String Response = matcher.group(8);
            int response = Integer.parseInt(Response);

            if (response == 200) {
                if (countIP.containsKey(IP)) {
                    countIP.put(IP, countIP.get(IP) + 1);
                } else {
                    countIP.put(IP, 1);
                }
            }
        }

        for (Map.Entry entry : countIP.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        final String log = "127.0.0.1 - - [12/Oct/2020:11:57:53 +0700] \"GET / HTTP/1.1\" 401 728 " + "" +
                "127.0.0.1 - odin [12/Oct/2020:11:57:58 +0700] \"GET / HTTP/1.1\" 200 3477"+"" +
                "127.0.0.1 - - [12/Oct/2020:11:57:59 +0700] \"GET /icons/ubuntu-logo.png HTTP/1.1\" 200 3623"+
                "127.0.0.1 - odin [12/Oct/2020:11:57:59 +0700] \"GET /favicon.ico HTTP/1.1\" 404 487"+
                "";
        Count(log);
    }
}