package POC;
//Class for Posting logs to LMlogs.
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;

public class PostLogs {

    private final static Logger log = Logger.getLogger(PostLogs.class);

    final static String httpVerp = "POST";
    final static String resourcePath = "/log/ingest";

    // function to retry if initial post fails.
    public void retry(JSONArray logArray, int retryCount) throws IOException {

        if (retryCount < 3) {
            log.debug(String.format("Retrying Count = %s ", retryCount));
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            long epoch = (new Date().getTime());

            String requestVars = httpVerp + epoch + logArray.toString() + resourcePath;

            final HmacUtils hmacHelper = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, ApplicationConfiguration.getInstance().getConfiguration("LM_ACCESS_KEY"));
            final Hex hexHelper = new Hex();
            final byte[] raw = hmacHelper.hmac(requestVars);
            final byte[] hex = hexHelper.encode(raw);
            final String signature = Base64.encodeBase64String(hex);
            String url = "https://" + ApplicationConfiguration.getInstance().getConfiguration("COMPANY") + "" + resourcePath;

            final String auth = "v1 " + ApplicationConfiguration.getInstance().getConfiguration("LM_ACCESS_ID") + ":" + signature + ":" + epoch;
            HttpPost request = new HttpPost(url);
            //Compressing payload before setting it as Http Entity
            byte[] abc = GZipCompressor.compress(logArray.toString().getBytes(Charset.defaultCharset()));
            ByteArrayEntity se = new ByteArrayEntity(abc);
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", auth);
            request.addHeader("Content-Encoding", "gzip");
            request.setEntity(se);
            CloseableHttpResponse response = httpClient.execute(request);
            log.info(String.format("Response = %s", response.getStatusLine().toString()));
            String statusCode = response.getStatusLine().toString().split(" ")[1];
            String response_message = StatusCodes.status_messages.get(statusCode);
            log.info(String.format("Response message is %s ", response_message));
            if (statusCode.equals("400") || statusCode.equals("429") || statusCode.equals("500") || statusCode.equals("404")) {
                log.debug(String.format("Retrying , Status Code = %s , Response message = %s, ",statusCode,response_message));
                log.debug(String.format("Data = %s" , logArray.toString()));
                retry(logArray, ++retryCount);
            }
        }
        else{
            log.debug("Retry count exceeded");
        }


    }

    public void post() throws IOException {
        int retryCount = 0;
        log.info("Getting logs for ingestion");
        LmLogsQueue lmLogsQueue = new LmLogsQueue();
        JSONArray logArray = null;
        try {
            logArray = lmLogsQueue.getLogsforLMlogs();
            log.info(String.format("Returned eventArray size is %s", logArray.length()));
            log.debug(String.format("Event array received %s", logArray.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (logArray.length() > 0) {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            long epoch = (new Date().getTime());
            String requestVars = httpVerp + epoch + logArray.toString() + resourcePath;
            //generating Signature
            final HmacUtils hmacHelper = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, ApplicationConfiguration.getInstance().getConfiguration("LM_ACCESS_KEY"));
            final Hex hexHelper = new Hex();
            final byte[] raw = hmacHelper.hmac(requestVars);
            final byte[] hex = hexHelper.encode(raw);
            final String signature = Base64.encodeBase64String(hex);

            //Building final URL
            String url = "https://" + ApplicationConfiguration.getInstance().getConfiguration("COMPANY") + "" + resourcePath;
            //Building Authorization Header
            final String auth = "v1 " + ApplicationConfiguration.getInstance().getConfiguration("LM_ACCESS_ID") + ":" + signature + ":" + epoch;
            try {
                HttpPost request = new HttpPost(url);
                //Compressing payload before setting it as Http Entity
                byte[] abc = GZipCompressor.compress(logArray.toString().getBytes(Charset.defaultCharset()));
                ByteArrayEntity se = new ByteArrayEntity(abc);
                request.addHeader("content-type", "application/json");
                request.addHeader("Authorization", auth);
                request.addHeader("Content-Encoding", "gzip");
                request.setEntity(se);
                log.info(String.format("Attempting post on url %s", url));
                CloseableHttpResponse response = httpClient.execute(request);
                log.info(String.format("Response = %s", response.getStatusLine().toString()));
                String statusCode = response.getStatusLine().toString().split(" ")[1];
                String response_message = StatusCodes.status_messages.get(statusCode);
                log.debug(String.format("Response message is %s ", response_message));
                if (statusCode.equals("400") || statusCode.equals("429") || statusCode.equals("500") || statusCode.equals("404")) {
                    log.debug(String.format("Retrying , Status Code = %s , Response message = %s, ",statusCode,response_message));
                    log.debug(String.format("Data = %s" , logArray.toString()));
                    retry(logArray, retryCount);
                }
                if(statusCode.equals("401"))
                {
                    log.error("Unauthorized Access");
                }


            } catch (Exception ex) {
                log.debug(String.format("Exception occured %s", ex.getMessage()));

            } finally {
                httpClient.close();
            }

        }
    }
}

