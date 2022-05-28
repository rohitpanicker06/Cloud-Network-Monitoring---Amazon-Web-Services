package POC;
// Class for getting each log entrie in log files and transforming them into JSON Objects for further procesing.
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;


import java.net.UnknownHostException;


public class CreatingEvents {

    private final static Logger log = Logger.getLogger(CreatingEvents.class);

    /*
    @Param
    eventLog= Single line or log entry in a log file.
     */
    public void createEvent(String eventLog) throws UnknownHostException {
        log.debug("Creating Log event");
        String cidr = ApplicationConfiguration.getInstance().getConfiguration("VPC_CIDR") ;
        String[] info = eventLog.split(" ");
        String scraddr = info[3];
        String dstaddr = info[4];
        String auto_logs_ip= "" ;

        SubnetUtils subnet = new SubnetUtils(cidr);
        SubnetUtils.SubnetInfo subnetInfo = subnet.getInfo();
        if(MyisInRange(subnetInfo , scraddr) == true)
            auto_logs_ip=scraddr;
        else if(MyisInRange(subnetInfo,dstaddr) == true)
            auto_logs_ip = dstaddr;
        //creating JSONEvent
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",eventLog);
        jsonObject.put("_resourceId",new JSONObject().put("logs.ip",auto_logs_ip));
        LmLogsQueue.logsQ.add(jsonObject);
        log.debug("Logevent added to queue.");
    }

    //function to check if the src/destination-address mentioned inside the log entries belong to the provided CIDR.
    private boolean MyisInRange(SubnetUtils.SubnetInfo info, String Addr )
    {
        int address = info.asInteger( Addr );
        int low = info.asInteger( info.getLowAddress() );
        int high = info.asInteger( info.getHighAddress() );
        return low <= address && address <= high;
    }
}
