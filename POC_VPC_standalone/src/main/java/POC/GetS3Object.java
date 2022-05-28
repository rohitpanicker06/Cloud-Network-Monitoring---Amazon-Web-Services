package POC;

//Class for getting s3 log files stored in s3 buckets.


import org.apache.log4j.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

/*@Param
startAfter = marks the last fetched s3 log file , so in the next polling it only gets s3 log file which was created after that.
 */
public class GetS3Object {

    static int i =1;
    static String startAfter = " ";
    GetS3ObjectLog getS3ObjectLog = new GetS3ObjectLog();

    private final static Logger log = Logger.getLogger(GetS3Object.class);

    public void getFilteredObjects(String bucketName,String directory)
    {
        log.debug("Start after = " +startAfter);
        log.info(String.format("Getting S3 Objects List for bucket=%s, and directory=%s",bucketName,directory));
        S3Client s3Client = S3Client.builder().region(Region.AP_SOUTH_1).build();

        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).prefix(directory).startAfter(startAfter).build();
        ListObjectsV2Iterable response = s3Client.listObjectsV2Paginator(request);
        //Automated Pagination {{1000 s3 log files per page}}
        for(ListObjectsV2Response page :response) {
            if (page.keyCount() > 0) {
                page.contents().forEach((S3Object object) -> {
                    log.info(String.format("Log File Count = %s",i++));
                    log.info(String.format("Attempting to get logs for S3 file = %s ",object.key()));
                    getS3ObjectLog.getS3Logs(object.key(),bucketName);
                    startAfter = object.key();


                });
            }else{
                log.info("No new log files found");
                break;
            }
        }


    }

}
