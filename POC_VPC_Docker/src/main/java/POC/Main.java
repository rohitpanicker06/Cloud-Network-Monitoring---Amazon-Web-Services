package POC;

import org.apache.log4j.Logger;
import java.io.IOException;

public class Main {

    static int PollingCount =1;
    public static GetS3Object getS3Object = new GetS3Object();
    public static LmLogsQueue lmLogsQueue = new LmLogsQueue(true);
    public static PostLogs postLogs = new PostLogs();
    public static StatusCodes statusCodes = new StatusCodes();

    private final static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {

             while (true) {
                log.info(String.format("Polling Count %s",PollingCount++));
                getS3Object.getFilteredObjects(System.getenv("bucketName"),System.getenv("directory"));
                postLogs.post();
               Thread.sleep(10000);
            }
    }
}



































//for getting account ID of IAM USer,
       /*StsClient stsClient = StsClient.builder().region(Region.AP_SOUTH_1).build();
       GetCallerIdentityResponse info = stsClient.getCallerIdentity();
       String accountInfo =info.account();
       System.out.println(accountInfo);*/

