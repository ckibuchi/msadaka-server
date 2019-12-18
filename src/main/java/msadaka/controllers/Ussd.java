package msadaka.controllers;

import msadaka.enums.PaymentMethod;
import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class Ussd {


    Functions fn = new Functions();
    public static JSONObject data = new JSONObject();

    @Value("${mpesa.passkey}")
    private String mpesapasskey;

    @Value("${mpesa.callbackurl}")
    private String callbackurl;

    @Value("${mpesa.shortcode}")
    private String shortcode;

    @Value("${nairobi.rate}")
    private String nairobirate;

    @Value("${mombasa.rate}")
    private String mombasarate;

    @Value("${mpesa.endpoint}")
    String mpesaendpoint;

    @Value("${mpesa.authurl}")
    String authurl;

    @Value("${mpesa.appsecret}")
    String app_secret;

    @Value("${mpesa.appkey}")
    String app_key;


    @PostMapping("/ussd")
    public String ussd(@RequestParam(name = "sessionId", required = false) String sessionId, @RequestParam(name = "serviceCode", required = false) String serviceCode, @RequestParam(name = "phoneNumber", required = false) String phoneNumber, @RequestParam(name = "text", required = false) String text) {
        String response = "";
        String balance = "--.--";
        String parkingareas = "--";
        System.out.println("text is |" + text + "|");
        System.out.println("phoneNumber is |" + phoneNumber + "|");
        if (text.equalsIgnoreCase("")) {

            // This is the first request. Note how we start the response with CON
            response = "CON Please make a choice \n";
            response += "1. Pay for Parking \n";
            response += "2. Check My Parking";

        } else if (text.trim().equalsIgnoreCase("1")) {
            // Business logic for first level response
            response = "CON Choose County \n";
            response += "1. Nairobi \n";
            response += "2. Mombasa";

        } else if (text.trim().equalsIgnoreCase("2")) {

            // Business logic for first level response
            Date utilDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            List<Payment> payments = paymentRepository.findPaymentsByMsisdnAndPaymentDateAndStatus(phoneNumber, sqlDate, "COMPLETED");
            String parkings = "Dear Customer,\n" +
                    "You have paid for below parkings:\n";
            if (payments.isEmpty()) {
                parkings = "Dear Customer,\n" +
                        "We are sorry there are no payments found for you today.";
            } else {
                int i = 1;
                for (Payment payment : payments) {
                    // parkings+=i+". "+payment.getCounty()+" "+payment.getSubCounty()+" "+ payment.getCarRegNo()+"\n";
                    i += 1;

                }
            }

            // This is a terminal request. Note how we start the response with END
            response = "END " + parkings;
        } else if (text.trim().equalsIgnoreCase("1*1")) {

            // This is a second level response where the user selected 1 in the first instance
            parkingareas = "1. CBD\n" +
                    "2. WESTLANDS";
            // This is a terminal request. Note how we start the response with END
            response = "CON Please choose area\n" + parkingareas;
        } else if (text.trim().equalsIgnoreCase("1*2")) {

            // This is a second level response where the user selected 1 in the first instance
            parkingareas = "1. KILIFI\n" +
                    "2. MTAWAPA";
            // This is a terminal request. Note how we start the response with END
            response = "CON Please choose area\n" + parkingareas;

        } else if (text.trim().equalsIgnoreCase("1*1*1") || text.trim().equalsIgnoreCase("1*2*1") || text.trim().equalsIgnoreCase("1*2*2") || text.trim().equalsIgnoreCase("1*1*2")) {

            // This is a terminal request. Note how we start the response with END
            response = "CON Enter Car Reg No.\n";


        } else {
            System.out.println("array length: " + text.split("\\*").length);
            String[] request = text.split("\\*");
            if (request.length == 4) {
                String county = "";
                String amount = "";
                String subcounty = "";
                if (request[1].equalsIgnoreCase("1")) {
                    county = "NRB";
                    amount = nairobirate;
                    if (request[2].equalsIgnoreCase("1")) {
                        subcounty = "CBD";
                    }
                    if (request[2].equalsIgnoreCase("2")) {
                        subcounty = "WESTLANDS";
                    }
                }
                if (request[1].equalsIgnoreCase("2")) {
                    amount = mombasarate;
                    county = "MBSA";
                    if (request[2].equalsIgnoreCase("1")) {
                        subcounty = "KILIFI";
                    }
                    if (request[2].equalsIgnoreCase("2")) {
                        subcounty = "MTWAPA";
                    }
                }

                String regno = request[3];
                System.out.println("regno before: " + regno);
                regno = regno.replaceAll("[^a-zA-Z0-9]", "").trim();
                System.out.println("regno After: " + regno);
                /*System.out.println("Mombasa Rate  "+mombasarate);
                System.out.println("Nai Rate  "+nairobirate);
                System.out.println("amount  "+amount);*/
                Date utilDate = new Date();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                System.out.println("regno:" + regno + " county: " + county + " subcounty: " + subcounty + " sqlDate: " + sqlDate);

                List<Payment> payments = new ArrayList<>();//= paymentRepository.findPaymentsByCarRegNoAndCountyAndSubCountyAndPaymentDateAndStatus(regno,county,subcounty,sqlDate,"COMPLETED");
                System.out.println("payments: " + payments);
                System.out.println(" payments.isEmpty(): " + payments.isEmpty());
                System.out.println("payments.size(): " + payments.size());

                if (payments.isEmpty()) {
                    String stkpushresp = stkPush(Double.parseDouble(amount), phoneNumber, regno, county, subcounty);
                    response = "END " + stkpushresp;
                } else {
                    response = "END Sorry, You have already paid for " + regno + " in " + subcounty + ",  " + county;
                }
            } else {

                response = "END Sorry, we did not understand your request";

            }

        }

        System.out.println(response);
// Print the response onto the page so that our gateway can read it
        return response;

        //return plainTextResponseEntity(response);


    }

    public String stkPush(Double the_amount, String msisdn, String regNo, String county, String subcounty) {
        String amount = new String("" + String.format("%.0f", the_amount));

        Date now = new Date();

        String timestamp = new String(Functions.sdf.format(now)).replaceAll("-", "");
        Payment payment = new Payment();
        try {

            payment.setPaymentMethod(PaymentMethod.MPESA);
            payment.setAmount(amount);
            payment.setStartTime(timestamp);
            payment.setMsisdn(msisdn);
            payment = paymentRepository.save(payment);
        } catch (Exception e) {

            e.printStackTrace();
        }

        data = fn.prepareLNMRequest(msisdn, amount, regNo, timestamp, shortcode, shortcode, mpesapasskey, callbackurl);
        System.out.println("Request " + data.toString());
        System.out.println("authurl " + authurl);

        String result = webClient.stkpushrequest(mpesaendpoint, data.toString());

        System.out.println("LNM REQ RES" + result);

        JSONObject results = new JSONObject(result);

        if (results.has("fault")) {
            return "Sorry, an error occured. Try again";
        } else if (results.has("errorMessage")) {
            if (results.has("errorCode")) {
                if (results.getString("errorCode").equalsIgnoreCase("404.001.03")) {
                    try {

                        Date now2 = new Date();

                        String timestamp2 = new String(Functions.sdf.format(now2)).replaceAll("-", "");

                        payment.setDesc1("Sorry, auth error occured. Try again");
                        payment.setError_code1(results.getString("errorCode"));
                        paymentRepository.save(payment);
                        return "Sorry, auth error occured. Try again";

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            return results.getString("errorMessage");

        } else if (results.has("ResponseCode")) {
            if (results.has("CustomerMessage")) {
                payment.setDesc1(results.getString("CustomerMessage"));
                payment.setError_code1(results.getString("ResponseCode"));
                payment.setRefID(results.getString("CheckoutRequestID"));
                paymentRepository.save(payment);
                return results.getString("CustomerMessage");
            } else {
                payment.setDesc1("Unknown response");
                payment.setError_code1("4001");
                paymentRepository.save(payment);
                return "Unknown response";
            }

        } else {
            payment.setDesc1("Unknown Error");
            payment.setError_code1("4001");
            paymentRepository.save(payment);
            return "Unknown Error";
        }
    }


    public String plainTextResponseEntity(String response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
        return new ResponseEntity(response, httpHeaders, HttpStatus.OK).toString();
    }

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    WebClient webClient;


}
