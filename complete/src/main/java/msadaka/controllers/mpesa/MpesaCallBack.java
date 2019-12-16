package msadaka.controllers.mpesa;

import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class MpesaCallBack {

    @Value("${sms.username}")
    private String username;

    @Value("${sms.apikey}")
    private String apikey;

    @Value("${sms.send}")
    private Boolean sendsms;

    Functions fn =new Functions();

    @PostMapping("/mpesa-call-back")
    public ResponseEntity mpesacallback(@RequestBody String data){
        System.out.println("data "+data);

        try{
            JSONObject results_1=new JSONObject(data);
            System.out.println(results_1);
            JSONObject results2=results_1.getJSONObject("Body");
            JSONObject results=results2.getJSONObject("stkCallback");
            System.out.println(results);
            Date now=new Date();
            java.sql.Date sqlDate =new java.sql.Date(now.getTime());

            String  timestamp=new String(Functions.sdf.format(now)).replaceAll("-","");
            Payment payment=paymentRepository.findPaymentByRefID(results.getString("CheckoutRequestID"));
            //sendSMS
            payment.setDesc2(results.getString("ResultDesc"));
            payment.setError_code2(""+results.getLong("ResultCode"));

            if(results.getLong("ResultCode")==0)
            payment.setStatus("COMPLETED");
            else
                payment.setStatus("FAILED");

            payment.setEndTime(timestamp);
            payment.setPaymentDate(sqlDate);
            payment=paymentRepository.save(payment);

            if(sendsms)
            fn.sendSMS(payment,username,apikey);

        }
        catch(Exception e)
        {
         e.printStackTrace();
        }
        return ResponseEntity.ok(HttpStatus.OK);

    }
@Autowired
    PaymentRepository paymentRepository;

}
