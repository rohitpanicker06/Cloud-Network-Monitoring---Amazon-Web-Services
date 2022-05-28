package POC;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.IOException;

public class Main {

    static int PollingCount =1;
    private static final String FILE_PATH = "src/main/resources/config.properties";
    public static GetS3Object getS3Object = new GetS3Object();
    public static LmLogsQueue lmLogsQueue = new LmLogsQueue(true);
    public static PostLogs postLogs = new PostLogs();
    public static StatusCodes statusCodes = new StatusCodes();

    static ConfigurationChangeListner listner = new ConfigurationChangeListner(FILE_PATH);

    private final static Logger log = Logger.getLogger(Main.class);

    //Setting log level through property file.
    public static void setLogLevel() throws InterruptedException {

   String logLevel = ApplicationConfiguration.getInstance().getConfiguration("LogLevel");
    if ("DEBUG".equalsIgnoreCase(logLevel)) {
      LogManager.getRootLogger().setLevel(Level.DEBUG);
    }else if ("INFO".equalsIgnoreCase(logLevel)) {
      LogManager.getRootLogger().setLevel(Level.INFO);
    }else if("WARN".equalsIgnoreCase(logLevel)){
      LogManager.getRootLogger().setLevel(Level.WARN);
    }else if("ERROR".equalsIgnoreCase(logLevel)){
      LogManager.getRootLogger().setLevel(Level.ERROR);
    }else if("FATAL".equalsIgnoreCase(logLevel)){
      LogManager.getRootLogger().setLevel(Level.FATAL);
    }
  }

    public static void main(String[] args) throws InterruptedException, IOException {

        //starting a new thread for Property File Watcher.
      try {
        new Thread(listner).start();
      } catch(Exception e){
        e.printStackTrace();
      }
      //Sleeping for 1 second in order to get Properties Loaded and for setting Log level.
        Thread.sleep(1000);

      //Creating new thread for periodic polling of LogEvents Queue and sending them to  Logs.
      ScheduledExecutorService executor =
          Executors.newSingleThreadScheduledExecutor();
      Runnable periodicTask = new Runnable() {
        public void run() {
          try {
            postLogs.post();
          } catch (IOException e) {
            log.debug(e.getMessage());
          }
        }
      };
      executor.scheduleAtFixedRate(periodicTask, 0, 5, TimeUnit.SECONDS); //setting time period interval for periodic execution*/

        //Polling for s3 Bucket
        while (true) {
            log.info(String.format("Polling Count %s",PollingCount++));
            getS3Object.getFilteredObjects(ApplicationConfiguration.getInstance().getConfiguration("Bucket"),ApplicationConfiguration.getInstance().getConfiguration("Directory"));
            Thread.sleep(10000);

            }
    }
}



































//for getting account ID of IAM USer,
       /*StsClient stsClient = StsClient.builder().region(Region.AP_SOUTH_1).build();
       GetCallerIdentityResponse info = stsClient.getCallerIdentity();
       String accountInfo =info.account();
       System.out.println(accountInfo);*/

