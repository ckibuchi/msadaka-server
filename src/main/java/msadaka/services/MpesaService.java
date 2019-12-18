package msadaka.services;

import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MpesaService {
    private Logger logger = LoggerFactory.getLogger(MpesaService.class);
    @Value("${sms.username}")
    private String username;

    @Value("${sms.apikey}")
    private String apikey;

    @Value("${sms.send}")
    private Boolean sendsms;

    Functions fn = new Functions();

    public void processCallBack(String data) {
        System.out.println("data " + data);

        try {
            JSONObject results_1 = new JSONObject(data);
            System.out.println(results_1);
            JSONObject results2 = results_1.getJSONObject("Body");
            JSONObject results = results2.getJSONObject("stkCallback");
            System.out.println(results);
            Date now = new Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());

            String timestamp = new String(Functions.sdf.format(now)).replaceAll("-", "");
            Payment payment = paymentRepository.findPaymentByRefID(results.getString("CheckoutRequestID"));
            //sendSMS
            payment.setDesc2(results.getString("ResultDesc"));
            payment.setError_code2("" + results.getLong("ResultCode"));

            if (results.getLong("ResultCode") == 0)
                payment.setStatus("COMPLETED");
            else
                payment.setStatus("FAILED");

            payment.setEndTime(timestamp);
            payment.setPaymentDate(sqlDate);
            payment = paymentRepository.save(payment);

            if (sendsms)
                fn.sendSMS(payment, username, apikey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    PaymentRepository paymentRepository;

}
