package POC;

//Class for holding LmLogs in a queue.
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class LmLogsQueue {

    private final static Logger log = Logger.getLogger(LmLogsQueue.class);
    public static BlockingDeque<JSONObject> logsQ;
    public static int maxsize =  7500000; //setting max size to 7.5MB  (Size mentioned in Bytes)


    LmLogsQueue(){}
    LmLogsQueue(boolean t){
        logsQ = new LinkedBlockingDeque<>();
    }

    //getting logs from Queue.
    public  JSONArray getLogsforLMlogs() throws UnsupportedEncodingException {
        log.info("Inside getlogsforLmLogs , attempting to get Logs stored in queue");
        int size;
        JSONArray logs = new JSONArray();

        while(!logsQ.isEmpty())
        {
            for(int i=0;i<500&&!logsQ.isEmpty();i++){

                logs.put(logsQ.getFirst());
                logsQ.pop();

            }
            if(size(logs)>=maxsize){
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
