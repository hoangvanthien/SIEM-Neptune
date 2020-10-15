public class ErrorEvent {
    private String timeStamp;
    private String clientIpAddress;
    private String message;
    private boolean loggInCommand;
    private boolean sameIpAddress;
    private String lastIpAddress = "";

    public ErrorEvent(String lineInput) throws Exception{

        String[] parseErrorLog = lineInput.split("\\[", 5);

        String[] clientIpAndMess = parseErrorLog[parseErrorLog.length - 1].split("AH");
        
        this.timeStamp = parseErrorLog[1].replace("]", "");
        this.clientIpAddress =  (clientIpAndMess[0].replace("]", "")).contains("client") ? clientIpAndMess[0].replace("]", "").split(" ")[1] : "";
        this.message = clientIpAndMess[1].contains("user") ? "AH" + clientIpAndMess[1] : "";
        this.loggInCommand = !this.message.equals("");
        
        if (this.loggInCommand) {
            String nowIpAddress = this.clientIpAddress;

            this.sameIpAddress = this.lastIpAddress.equals(nowIpAddress) && nowIpAddress != "" ;
            this.lastIpAddress = nowIpAddress;

        }

    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getLoggInCommand() {
        return this.loggInCommand;
    }

    public boolean getSameIpAddress() {
        return this.sameIpAddress;
    }

    public String getLastIpAddress() {
        return this.lastIpAddress;
    }

}
