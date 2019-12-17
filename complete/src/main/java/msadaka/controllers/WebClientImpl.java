package msadaka.controllers;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public class WebClientImpl implements WebClient {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private static String bearer = "";

    // private final HttpClient Client = new DefaultHttpClient();
    private static String Content;
    private String ACTION;
    JSONObject data = null;//new JSONObject();
    private String Error = null;


    public String mpesaendpoint, authurl, app_secret, app_key;


    int sizeData = 0;


    @Override
    public String stkpushrequest(String mpesaendpoint, String data) {

        try {
            bearer = getBearer();
            System.out.println("bearer:" + "" + bearer);
            RequestBody body = RequestBody.create(JSON, data);
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Bearer " + bearer)
                    .addHeader("Content-Type", "application/json")
                    .url(mpesaendpoint)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            Content = response.body().string();

        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("ERROR" + e.getMessage());
        }

        return Content;
    }

    @Override
    public String getBearer() {

        return ScheduledTasks.bearer;
         }

}
