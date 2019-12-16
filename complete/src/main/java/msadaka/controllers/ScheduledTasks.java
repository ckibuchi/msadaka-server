package msadaka.controllers;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public static String bearer="";

    @Value("${algolia.key}")
    private String key;

    @Value("${algolia.appid}")
    private String appId;

    @Value("${mpesa.passkey}")
    private String mpesapasskey;

    @Value("${mpesa.callbackurl}")
    private String callbackurl;

    @Value("${mpesa.endpoint}")
    String mpesaendpoint;

    @Value("${mpesa.shortcode}")
    private String shortcode;

    @Value("${mpesa.authurl}")
    String authurl;

    @Value("${mpesa.appsecret}")
    String app_secret;

    @Value("${mpesa.appkey}")
    String app_key;

    @Scheduled(fixedDelay  = 1800000)
    public void renewBearer() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        try {


            String appKeySecret = this.app_key + ":" + this.app_secret;
            System.out.println("this.app_key + this.app_secret "+this.app_key + ":" + this.app_secret);
            byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
            String auth= Base64.getEncoder().encodeToString(bytes);

            final OkHttpClient client = new OkHttpClient();
            log.info("this.authurl "+this.authurl);

            Request request = new Request.Builder()
                    .url(this.authurl)
                    .get()
                    .addHeader("Authorization", "Basic " + auth.trim())
                    //.addHeader("cache-control", "no-cache")
                    .build();
            Response response  =    client.newCall(request).execute();
            // Background Code
            String resp=response.body().string();
            log.info("response "+resp);
            JSONObject results=new JSONObject(resp);
            if(results.has("access_token"))
            {
                try{
                    log.info("token: "+results.getString("access_token"));
                    this.bearer=results.getString("access_token");



                }
                catch(Exception e)
                {e.printStackTrace();
                }
            }
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }


    }
}