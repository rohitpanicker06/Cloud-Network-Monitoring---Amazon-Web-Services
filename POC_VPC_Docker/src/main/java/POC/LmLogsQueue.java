package POC;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;

public class LmLogsQueue {

    private final static Logger log = Logger.getLogger(LmLogsQueue.class);
    public static Deque<JSONObject> logsQ;
    public static int maxsize =  8000000;


    LmLogsQueue(){}
    LmLogsQueue(boolean t){
        logsQ = new LinkedList<>();
    }

    public  JSONArray getLogsforLMlogs() throws UnsupportedEncodingException {
        log.info("Inside getlogsforLmLogs , attempting to get Logs stored in queue");
        int size;
        JSONArray logs = new JSONArray();

        while(!logsQ.isEmpty())
        {
            logs.put(logsQ.getFirst());
            if(size(logs)<=maxsize)
            {
                logsQ.pop();
                continue;
            }
            else{
                logs.remove(logs.length()-1);
                break;
            }
        }

        return logs;

    }

    public  int size(JSONArray logsJSON) throws UnsupportedEncodingException {
        String logsJSONString = logsJSON.toString();
        int sizeOf = logsJSONString.getBytes().length;
        return sizeOf;
    }

}
