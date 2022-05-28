package POC;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class PostLogs {

    private final static Logger log = Logger.getLogger(PostLogs.class);
    final static String Access_id = System.getenv("LM_ACCESS_ID");
    final static String Access_key = System.getenv("LM_ACCESS_KEY");
    final static String httpVerp = "POST";
    final static String resourcePath = "/log/ingest";
    final static String company = System.getenv("COMPANY");

    public  void retry(JSONArray logArray,int retryCount) throws IOException {

        if(retryCount <3) {
            log.debug(String.format("Retrying Count = %s ",retryCount));
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            long epoch = (new Date().getTime());

            String requestVars = httpVerp + epoch + logArray.toString() + resourcePath;

            final HmacUtils hmacHelper = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, Access_key);
            final Hex hexHelper = new Hex();
            final byte[] raw = hmacHelper.hmac(requestVars);
            final byte[] hex = hexHelper.encode(raw);
            final String signature = Base64.encodeBase64String(hex);
            String url = "https://" + company + "" + resourcePath;

            final String auth = "v1 " + Access_id + ":" + signature + ":" + epoch;
            HttpPost request = new HttpPost(url);
            StringEntity se = new StringEntity(logArray.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", auth);
            //request.addHeader("Content-Encoding","gzip");
            request.setEntity(se);
            CloseableHttpResponse response = httpClient.execute(request);
            log.debug(String.format("Response = %s",response.getStatusLine().toString()));
            String statusCode = response.getStatusLine().toString().split(" ")[1];

            if (statusCode.equals("400") || statusCode.equals("429") || statusCode.equals("500") || statusCode.equals("404")) {
                retry(logArray, ++retryCount);
            }
        }


    }

    public void post() throws IOException {
        int retryCount=0;
        log.debug("Getting logs for ingestion");
        LmLogsQueue lmLogsQueue = new LmLogsQueue();
        JSONArray logArray = null;

        try {
            logArray = lmLogsQueue.getLogsforLMlogs();
            log.debug(String.format("Returned eventArray size is %s",logArray.length()));
            log.debug(String.format("Event array received %s",logArray.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (logArray.length() > 0) {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            long epoch = (new Date().getTime());

            String requestVars = httpVerp + epoch + logArray.toString() + resourcePath;

            final HmacUtils hmacHelper = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, Access_key);
            final Hex hexHelper = new Hex();
            final byte[] raw = hmacHelper.hmac(requestVars);
            final byte[] hex = hexHelper.encode(raw);
            final String signature = Base64.encodeBase64String(hex);
            String url = "https://" + company + "" + resourcePath;

            final String auth = "v1 " + Access_id + ":" + signature + ":" + epoch;
            try {
                HttpPost request = new HttpPost(url);
                StringEntity se = new StringEntity(logArray.toString());
                request.addHeader("content-type", "application/json");
                request.addHeader("Authorization", auth);
                request.setEntity(se);
                log.debug(String.format("Attempting post on url %s",url));
                CloseableHttpResponse response = httpClient.execute(request);
                log.debug(String.format("Response = %s",response.getStatusLine().toString()));
                String statusCode =response.getStatusLine().toString().split(" ")[1];
                String response_message=StatusCodes.status_messages.get(statusCode);
                log.debug(String.format("Response message is %s " ,response_message));
                if(statusCode.equals("400") || statusCode.equals("429") || statusCode.equals("500") || statusCode.equals("404"))
                {
                    retry(logArray,retryCount);
                }


            } catch (Exception ex) {
                log.debug(String.format("Exception occured %s" ,ex.getMessage()));

            } finally {
                httpClient.close();
            }

        }
    }
}
