package POC;


import org.apache.log4j.Logger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


public class GetS3ObjectLog {

    Region region = Region.AP_SOUTH_1;
    S3Client s3Client = S3Client.builder().region(region).build();
    CreatingEvents creatingEvents = new CreatingEvents();

    private final static Logger log = Logger.getLogger(GetS3ObjectLog.class);

    public void getS3Logs(String logFileName,String bucketName)
    {
        log.debug(String.format("Getting logs for S3 file = %s ",logFileName));

        int logcount=0;
        ResponseInputStream<GetObjectResponse> s3Object;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName).key(logFileName).build();

        s3Object = s3Client.getObject(getObjectRequest);
        log.info(String.format("Attemping to unzip file %s ",logFileName));
        try {
            GZIPInputStream is = new GZIPInputStream(s3Object);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String strline;

                while ((strline = bufferedReader.readLine()) != null) {
                     logcount++;

                    if(logcount==1) {
                        log.debug(String.format("Skipping log entry %s for log file = %s",strline,logFileName));
                        logcount++;
                        continue;
                    }
                    creatingEvents.createEvent(strline);

                }
                logcount=0;


            is.close();
           log.debug(String.format("File Successfully decompressed = %s",logFileName));

        }catch (IOException e)
        {
            log.debug(String.format("Error occured in unzipping file %s.",logFileName));
            log.warn(String.format("Error occured in unzipping file %s.",logFileName));

        }
    }
}
