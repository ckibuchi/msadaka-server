package msadaka.controllers.apis;

import com.google.gson.Gson;
import msadaka.beans.PostBean;
import msadaka.controllers.WebClient;
import msadaka.enums.PayBillStatus;
import msadaka.enums.PaymentMethod;
import msadaka.models.Church;
import msadaka.models.Payment;
import msadaka.repositories.ChurchRepository;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/churches")
public class ChurchesController {
    Functions fn = new Functions();
    public static JSONObject data = new JSONObject();

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


    private Logger logger = LoggerFactory.getLogger(ChurchesController.class);


    @PostMapping("/createChurch")
    public String createChurch(@RequestBody PostBean bean) {

        HashMap<String, String> map = bean.getMap();
        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {
            Church newchurch = new Church();
            if (!map.get("id").isEmpty()) {
                newchurch = churchRepository.findChurchById(Long.parseLong(map.get("id")));
                if (newchurch == null) {
                    return "{'status':'error','message':'Church with " + map.get("id") + " is not found'}";
                }

            }

            newchurch.setName(map.get("name"));
            newchurch.setAltName(map.get("altName"));
            newchurch.setPayBill(map.get("payBill"));
            newchurch.setUserEmail(map.get("userEmail"));
            newchurch.setShortCode(map.get("shortCode"));
            newchurch.setMpesaAppkey(map.get("mpesaAppkey"));
            newchurch.setMpesaAppsecret(map.get("mpesaAppsecret"));
            newchurch.setMpesaPasskey(map.get("mpesaPasskey"));

            newchurch.setCreationDate(sqlDate);
            newchurch.setStatus(PayBillStatus.ACTIVE);
            newchurch = churchRepository.save(newchurch);
            return "{'status':'success','message':'" + newchurch.toString() + "'}";

            // return "{'status':'success','message':'Completed Successfully'}";
        } catch (Exception e) {
            return "{'status':'error','message':'" + e.getMessage() + "'}";
        }


    }


    @PostMapping("/deleteChurch")
    public String deleteChurch(@RequestBody PostBean bean) {

        HashMap<String, String> map = bean.getMap();

        Church church = churchRepository.findChurchById(Long.parseLong(map.get("id")));

        churchRepository.delete(church);
        return "{'status':'success','message':'PayBill Deleted!'}";

    }


    @PostMapping("/getPayBills")
    //@Produces({MediaType.APPLICATION_JSON})
    public String findChurchesByUseremail(@RequestParam(name = "userEmail", required = true) String userEmail) {
        List<JSONObject> response = new ArrayList<JSONObject>();
        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        List<Church> churches = churchRepository.findChurchesByUserEmail(userEmail);
        if (!churches.isEmpty()) {
            Gson gson = new Gson();
            for (Church church : churches) {
                String jsonString = gson.toJson(church);
                try {
                    JSONObject paymentobject = new JSONObject(jsonString);
                    response.add(paymentobject);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response.toString();

    }

    @PostMapping("/giveToChurch")
    public String giveToChurch(@RequestBody PostBean bean) {

        try {
            HashMap<String, String> map = bean.getMap();
            //String msisdn,int churchID,String reference
            String resp = stkPush(Double.parseDouble(map.get("amount")), map.get("msisdn"), Long.parseLong(map.get("churchID")), map.get("reference"));

            return resp;

        } catch (Exception e) {
            return "{'status':'error','message':'" + e.getMessage() + "'}";
        }

    }

    public String stkPush(Double the_amount, String msisdn, Long churchID, String reference) {
        String amount = new String("" + String.format("%.0f", the_amount));

        Date now = new Date();

        String timestamp = new String(Functions.sdf.format(now)).replaceAll("-", "");
        Payment payment = new Payment();
        try {
            // Church church=churchRepository.findChurchByIdAndStatus(churchID,PayBillStatus.ACTIVE);
            logger.info("Searching for church with ID: " + churchID);
            Church church = churchRepository.findChurchById(churchID);
            if (church != null) {
                logger.info("Found  a church wirh ID: " + churchID);

                if (church.getStatus() != PayBillStatus.ACTIVE) {
                    return "{\"status\":\"error\",\"message\":\"Sorry, The paybill is pending renewal\"}";
                }


                payment.setPaymentMethod(PaymentMethod.MPESA);
                payment.setAmount(amount);
                payment.setChurch(church);
                payment.setStartTime(timestamp);
                payment.setMsisdn(msisdn);
                payment.setReference(reference);
                logger.info("Preparing request");
                data = fn.prepareLNMRequest(msisdn, amount, reference, timestamp, church.getShortCode(), church.getPayBill(), church.getMpesaPasskey(), callbackurl);
                logger.info("Request prepared.. now calling STKPush");
                //WebClient client=new WebClient( mpesaendpoint, authurl, church.getMpesaAppsecret(), church.getMpesaAppkey());
                logger.info("Calling..... ");
                String result = webClient.stkpushrequest(mpesaendpoint, data.toString());
                logger.info("LNM REQ RES" + result);
                logger.info("Called StkPush.. Saving payment now");
                payment = paymentRepository.save(payment);
                logger.info("Payment Saved..");

                JSONObject results = new JSONObject(result);

                if (results.has("fault")) {
                    //return "Sorry, an error occured. Try again";
                    Date now2 = new Date();

                    String timestamp2 = new String(Functions.sdf.format(now2)).replaceAll("-", "");

                    payment.setDesc1("Sorry, auth error occured. Try again");
                    payment.setError_code1(results.getString("errorCode"));
                    payment.setDesc2("Sorry, auth error occured. Try again");
                    payment.setError_code2(results.getString("errorCode"));
                    payment.setStatus("FAILED");
                    paymentRepository.save(payment);
                    return "{\"status\":\"error\",\"message\":\"Sorry, an error occured. Try again\"}";
                } else if (results.has("errorMessage")) {
                    if (results.has("errorCode")) {
                        if (results.getString("errorCode").equalsIgnoreCase("404.001.03")) {
                            try {

                                Date now2 = new Date();

                                String timestamp2 = new String(Functions.sdf.format(now2)).replaceAll("-", "");

                                payment.setDesc1("Sorry, auth error occured. Try again");
                                payment.setError_code1(results.getString("errorCode"));
                                payment.setDesc2(results.getString("CustomerMessage"));
                                payment.setError_code2(results.getString("ResponseCode"));
                                payment.setStatus("FAILED");
                                paymentRepository.save(payment);
                                return "{\"status\":\"error\",\"message\":\"Sorry, an error occured. Try again\"}";

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                    //  return results.getString("errorMessage");
                    return "{\"status\":\"error\",\"message\":\"" + results.getString("errorMessage") + "\"}";

                } else if (results.has("ResponseCode")) {
                    if (results.has("CustomerMessage")) {


                        if (results.getString("ResponseCode").equalsIgnoreCase("0")) {
                            payment.setDesc1(results.getString("CustomerMessage"));
                            payment.setError_code1(results.getString("ResponseCode"));
                            payment.setRefID(results.getString("CheckoutRequestID"));
                            paymentRepository.save(payment);
                            String mess = results.getString("CustomerMessage");
                            return "{\"status\":\"success\",\"message\":\"" + mess + "\"}";
                        } else {

                            String mess = results.getString("CustomerMessage");
                            payment.setDesc1(results.getString("CustomerMessage"));
                            payment.setError_code1(results.getString("ResponseCode"));
                            payment.setDesc2(results.getString("CustomerMessage"));
                            payment.setError_code2(results.getString("ResponseCode"));
                            payment.setStatus("FAILED");
                            payment.setRefID(results.getString("CheckoutRequestID"));
                            paymentRepository.save(payment);

                            return "{\"status\":\"success\",\"message\":\"" + mess + "\"}";

                        }
                    } else {
                        payment.setDesc1("Unknown response");
                        payment.setError_code1("4001");
                        payment.setDesc2("Unknown response");
                        payment.setError_code2("4001");
                        payment.setStatus("FAILED");
                        paymentRepository.save(payment);
                        return "{\"status\":\"success\",\"message\":\"Unknown response\"}";

                    }

                } else {
                    payment.setDesc1("Unknown Error");
                    payment.setError_code1("4001");
                    payment.setDesc2("Unknown response");
                    payment.setError_code2("4001");
                    payment.setStatus("FAILED");
                    paymentRepository.save(payment);
                    return "{\"status\":\"success\",\"message\":\"Unknown ERROR\"}";
                }

            } else {
                //return "Sorry, This Church is currently unavailable";
                return "{\"status\":\"success\",\"message\":\"Sorry, This Church is currently unavailable\"}";

            }
        } catch (Exception e) {

            e.printStackTrace();
            //   return "Sorry, a fatal error occured";
            return "{\"status\":\"success\",\"message\":\"Sorry, a fatal error occured\"}";
        }


    }


    @Autowired
    ChurchRepository churchRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    WebClient webClient;
}
