public class Main{
    public static void main(String[] args){
        String logline = "127.0.0.1 - - [12/Oct/2020:11:57:53 +0700] \"GET / HTTP/1.1\" 401 728 ";

        AccessEvent A = new AccessEvent();
        System.out.println(A.parseLogLine(logline));
    }


}