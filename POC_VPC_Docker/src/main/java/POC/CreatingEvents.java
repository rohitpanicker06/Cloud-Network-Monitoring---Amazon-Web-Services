package POC;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;


import java.net.UnknownHostException;


public class CreatingEvents {

    private final static Logger log = Logger.getLogger(CreatingEvents.class);

    public void createEvent(String eventLog) throws UnknownHostException {

        log.info("Creating Log event");
        String cidr = System.getenv("VPC_CIDR");
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

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",eventLog);
        jsonObject.put("_resourceId",new JSONObject().put("logs.ip",auto_logs_ip));
        LmLogsQueue.logsQ.add(jsonObject);
        log.info("Logevent added to queue.");


    }

    private boolean MyisInRange(SubnetUtils.SubnetInfo info, String Addr )
    {
        int address = info.asInteger( Addr );
        int low = info.asInteger( info.getLowAddress() );
        int high = info.asInteger( info.getHighAddress() );
        return low <= address && address <= high;
    }
}
