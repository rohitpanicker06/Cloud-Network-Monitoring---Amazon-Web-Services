package POC;

import java.util.HashMap;
// class for getting status messages.
public class StatusCodes {
    public static HashMap<String, String> status_messages = new HashMap<>();
    public StatusCodes() {
        status_messages.put("202","Accepted.");
        status_messages.put("207","Multi-Status.Some events are rejected");
        status_messages.put("400","Bad request.");
        status_messages.put("401","Unauthorized.");
        status_messages.put("402","Payment Required");
        status_messages.put("403","Forbidden");
        status_messages.put("413","Payload Too Large");
        status_messages.put("429","Too Many Requests");
        status_messages.put("500","Server Error.");
        status_messages.put("404","Bad Url");
    }
}

