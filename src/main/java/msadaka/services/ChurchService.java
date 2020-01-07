package msadaka.services;

import msadaka.beans.PostBean;
import msadaka.controllers.WebClient;
import msadaka.dto.StkPushResponse;
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

    StkPushResponse response = new StkPushResponse();


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

    public List<Church> listChurchesByUserEmail(String userEmail) {
        return churchRepository.findChurchesByUserEmail(userEmail);

    }

    public List<Church> findAll() {
        return (List<Church>) churchRepository.findAll();
    }

    public List<Church> findAllByName(String name) {
        return churchRepository.findChurchesByNameContainingIgnoreCase(name);
    }

    public StkPushResponse stkPush(Double the_amount, String msisdn, Long churchID, String reference) {
        String amount = String.format("%.0f", the_amount);

        Date now = new Date();

        String timestamp = Functions.sdf.format(now).replaceAll("-", "");
        Payment payment = new Payment();
        try {
            // Church church=churchRepository.findChurchByIdAndStatus(churchID,PayBillStatus.ACTIVE);
            return getStkPushResponse(msisdn, churchID, reference, amount, timestamp, payment);
        } catch (Exception e) {

            e.printStackTrace();
            //   return "Sorry, a fatal error occured";
            response.setStatus("error");
            response.setMessage("Sorry, a fatal error occured");
            return response;
        }


    }

    private StkPushResponse getStkPushResponse(String msisdn, Long churchID, String reference, String amount, String timestamp, Payment payment) throws JSONException {
        Church church = churchRepository.findChurchById(churchID);
        if (church != null) {

            return getStkPushResponse(msisdn, reference, amount, timestamp, payment, church);

        } else {
            response.setStatus("error");
            response.setMessage("Sorry, This Church is currently unavailable");
            return response;
        }
    }

    private StkPushResponse getStkPushResponse(String msisdn, String reference, String amount, String timestamp, Payment payment, Church church) throws JSONException {
        if (church.getStatus() != PayBillStatus.ACTIVE) {
            response.setStatus("error");
            response.setMessage("Sorry, The paybill is pending renewal");
            return response;
        }


        payment.setPaymentMethod(PaymentMethod.MPESA);
        payment.setAmount(amount);
        payment.setChurch(church);
        payment.setStartTime(timestamp);
        payment.setMsisdn(msisdn);
        payment.setReference(reference);
        logger.info("callbackUrl "+callbackUrl);
        data = fn.prepareLNMRequest(msisdn, amount, reference, timestamp, church.getShortCode(), church.getPayBill(), church.getMpesaPasskey(), callbackUrl);

        String result = webClient.stkpushrequest(mpesaEndpoint, data.toString());
        payment = paymentRepository.save(payment);

        JSONObject results = new JSONObject(result);

        return getStkPushResponse(payment, results);
    }

    private StkPushResponse getStkPushResponse(Payment payment, JSONObject results) throws JSONException {
        response.setPaymentId(payment.getId());
        if (results.has("fault")) {
            payment.setDesc1("Sorry, auth error occured. Try again");
            payment.setError_code1(results.getString("errorCode"));
            payment.setDesc2("Sorry, auth error occured. Try again");
            payment.setError_code2(results.getString("errorCode"));
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            response.setStatus("error");
            response.setMessage("Sorry, an error occured. Try again");
            return response;
        } else if (results.has("errorMessage")) {
            if (results.has("errorCode")) {
                if (results.getString("errorCode").equalsIgnoreCase("404.001.03")) {
                    try {
                        payment.setDesc1("Sorry, auth error occured. Try again");
                        payment.setError_code1(results.getString("errorCode"));
                        payment.setDesc2(results.getString("CustomerMessage"));
                        payment.setError_code2(results.getString("ResponseCode"));
                        payment.setStatus("FAILED");
                        paymentRepository.save(payment);
                        response.setStatus("error");
                        response.setMessage("Sorry, an error occured. Try again");
                        return response;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            response.setStatus("error");
            response.setMessage(results.getString("errorMessage"));
            return response;

        } else if (results.has("ResponseCode")) {
            if (results.has("CustomerMessage")) {


                if (results.getString("ResponseCode").equalsIgnoreCase("0")) {
                    payment.setDesc1(results.getString("CustomerMessage"));
                    payment.setError_code1(results.getString("ResponseCode"));
                    payment.setRefID(results.getString("CheckoutRequestID"));
                    paymentRepository.save(payment);
                    String mess = results.getString("CustomerMessage");
                    response.setStatus("success");
                    response.setMessage(mess);
                    return response;
                } else {

                    String mess = results.getString("CustomerMessage");
                    payment.setDesc1(results.getString("CustomerMessage"));
                    payment.setError_code1(results.getString("ResponseCode"));
                    payment.setDesc2(results.getString("CustomerMessage"));
                    payment.setError_code2(results.getString("ResponseCode"));
                    payment.setStatus("FAILED");
                    payment.setRefID(results.getString("CheckoutRequestID"));
                    paymentRepository.save(payment);
                    response.setStatus("error");
                    response.setMessage(mess);
                    return response;

                }
            } else {
                payment.setDesc1("Unknown response");
                payment.setError_code1("4001");
                payment.setDesc2("Unknown response");
                payment.setError_code2("4001");
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                response.setStatus("error");
                response.setMessage("Unknown response from M-PESA");
                return response;

            }

        } else {
            payment.setDesc1("Unknown Error");
            payment.setError_code1("4001");
            payment.setDesc2("Unknown response");
            payment.setError_code2("4001");
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            response.setStatus("error");
            response.setMessage("Unknown response from M-PESA");
            return response;
        }
    }
}
