package msadaka.services;

import msadaka.beans.PostBean;
import msadaka.controllers.WebClient;
import msadaka.enums.PayBillStatus;
import msadaka.enums.PaymentMethod;
import msadaka.models.Church;
import msadaka.models.Payment;
import msadaka.repositories.ChurchRepository;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ChurchService {
    @Value("${mpesa.passkey}")
    private String mpesaPassKey;

    @Value("${mpesa.callbackurl}")
    private String callbackUrl;

    @Value("${mpesa.endpoint}")
    String mpesaEndpoint;

    @Value("${mpesa.shortcode}")
    private String shortCode;


    @Value("${mpesa.authurl}")
    String authUrl;

    @Value("${mpesa.appsecret}")
    String app_secret;

    @Value("${mpesa.appkey}")
    String appKey;

    @Autowired
    ChurchRepository churchRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    WebClient webClient;

    private Logger logger = LoggerFactory.getLogger(ChurchService.class);

    Functions fn = new Functions();
    public static JSONObject data = new JSONObject();


    public Church createChurch(Church church) {

        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {
            if (church.getId() != null) {
                church = churchRepository.findChurchById(church.getId());
                if (church == null) {
                    return null;
                }

            }
            church.setCreationDate(Instant.now());
            church.setStatus(PayBillStatus.ACTIVE);
            church = churchRepository.save(church);
            logger.info(church.toString());
            return church;

            // return "{'status':'success','message':'Completed Successfully'}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String deleteChurch(Long id) {


        churchRepository.deleteById(id);
        return "{'status':'success','message':'PayBill Deleted!'}";
    }

    public List<Church> listChurchesByUserEmail(String userEmail)
    {
       return churchRepository.findChurchesByUserEmail(userEmail);

    }

    public List<Church> findAll()
    {
        return (List<Church>) churchRepository.findAll();
    }

    public List<Church> findAllByName(String name)
    {
        return churchRepository.findChurchesByNameContainingIgnoreCase(name);
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
                data = fn.prepareLNMRequest(msisdn, amount, reference, timestamp, church.getShortCode(), church.getPayBill(), church.getMpesaPasskey(), callbackUrl);
                logger.info("Request prepared.. now calling STKPush");
                //WebClient client=new WebClient( mpesaendpoint, authurl, church.getMpesaAppsecret(), church.getMpesaAppkey());
                logger.info("Calling..... ");
                String result = webClient.stkpushrequest(mpesaEndpoint, data.toString());
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
}
