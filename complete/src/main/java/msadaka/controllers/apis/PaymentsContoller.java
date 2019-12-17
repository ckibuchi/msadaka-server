package msadaka.controllers.apis;

import com.google.gson.Gson;
import msadaka.beans.Maharagwe;
import msadaka.beans.PostBean;
import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.utils.Functions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsContoller {
    private Logger logger = LoggerFactory.getLogger(PaymentsContoller.class);

    @PostMapping("/getparkings")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getparkingsbyRegandDate(@RequestParam(name = "carRegNo", required = true) String carRegNo) {
        List<JSONObject> response = new ArrayList<JSONObject>();
        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        List<Payment> payments = new ArrayList<>();// paymentRepository.findPaymentsByCarRegNoAndPaymentDate(carRegNo.replaceAll("[^a-zA-Z0-9]", "").trim(),sqlDate);
        if (!payments.isEmpty()) {
            Gson gson = new Gson();
            for (Payment payment : payments) {
                String jsonString = gson.toJson(payment);
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

    @RequestMapping("/search-payments")
    //@Produces({MediaType.APPLICATION_JSON})
    public String searchPayments(@RequestParam(name = "carRegNo", required = false, defaultValue = "") String carRegNo,
                                 @RequestParam(name = "county", required = false, defaultValue = "") String county,
                                 @RequestParam(name = "subCounty", required = false, defaultValue = "") String subCounty,
                                 @RequestParam(name = "dateFrom", required = false, defaultValue = "") String dateFrom,
                                 @RequestParam(name = "dateTo", required = false, defaultValue = "") String dateTo) {


        List<JSONObject> response = new ArrayList<JSONObject>();
        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        String carRegno2 = carRegNo.replaceAll("[^a-zA-Z0-9]", "").trim();
        List<Payment> payments = null;

        if (carRegno2.isEmpty() && county.isEmpty() && subCounty.isEmpty() && dateFrom.isEmpty() && dateTo.isEmpty()) {
            logger.info("No filters.... Show all transactions for today ");
            payments = new ArrayList<>();
            ;// paymentRepository.findPaymentsByPaymentDate(sqlDate);
        } else if (dateFrom.isEmpty() || dateTo.isEmpty()) {
            logger.info("No Dates...");
            payments = new ArrayList<>();//=  paymentRepository.findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDate(carRegno2,county,subCounty,sqlDate);
        } else {
            try {
                java.util.Date datef = Functions.sdf1.parse(dateFrom);
                java.sql.Date sqlDateFrom = new java.sql.Date(datef.getTime());

                java.util.Date datet = Functions.sdf1.parse(dateTo);
                java.sql.Date sqlDateTo = new java.sql.Date(datet.getTime());
                payments = new ArrayList<>();//=  paymentRepository.findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDateGreaterThanEqualAndPaymentDateIsLessThanEqual(carRegno2,county,subCounty,sqlDateFrom,sqlDateTo);
                logger.info("dateFrom >>" + dateFrom + " sqlDateFrom >> " + sqlDateFrom);
                logger.info("sqlDateTo >> " + sqlDateTo + " sqlDateTo >> " + sqlDateTo);
            } catch (Exception e) {
                logger.info("Date conversion Failed Badly!!!!!!!!!!!!!!!");
                e.printStackTrace();

                payments = new ArrayList<>();// paymentRepository.findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDate(carRegno2,county,subCounty,sqlDate);


            }


        }


        if (!payments.isEmpty()) {
            Gson gson = new Gson();
            for (Payment payment : payments) {
                String jsonString = gson.toJson(payment);
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
        //   return Response.ok(response.toString()).header("Access-Control-Allow-Origin", "*").header(  "Access-Control-Allow-Headers","Authorization").build();

    }

    @PostMapping("/getTodaysTotal")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getTodaysTotal() {
        String response = "0.00";
        try {

            Date utilDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            response = "" + paymentRepository.getTotalToday(sqlDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    @PostMapping("/getTodaysTotalForEmail")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getTodaysTotalForEmail(@RequestBody PostBean bean) {
        String response = "0.00";
        try {
            HashMap<String, String> map = bean.getMap();
            Date utilDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            response = "" + paymentRepository.getTotalTodayForEmail(map.get("userEmail"), sqlDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    @PostMapping("/getForeverTotalForEmail")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getForeverTotalForEmail(@RequestBody PostBean bean) {
        String response = "0.00";
        try {
            HashMap<String, String> map = bean.getMap();
            response = "" + paymentRepository.getForeverTotalForEmail(map.get("userEmail"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    @PostMapping("/getThisMonthTotalForEmail")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getThisMonthTotalForEmail(@RequestBody PostBean bean) {
        String response = "0.00";
        try {

            HashMap<String, String> map = bean.getMap();
            logger.info("First day of the Month ", "" + LocalDate.now()
                    .with(TemporalAdjusters.firstDayOfMonth()));

            logger.info("Last day of the Month ", "" + LocalDate.now()
                    .with(TemporalAdjusters.lastDayOfMonth()));

            java.sql.Date firstDate = java.sql.Date.valueOf(LocalDate.now()
                    .with(TemporalAdjusters.firstDayOfMonth()));

            java.sql.Date lastDate = java.sql.Date.valueOf(LocalDate.now()
                    .with(TemporalAdjusters.lastDayOfMonth()));

            response = "" + paymentRepository.getThisMonthTotalForEmail(map.get("userEmail"), firstDate, lastDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }


    @PostMapping("/getTodaysCountForEmail")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getTodaysCountForEmail(@RequestBody PostBean bean) {
        String response = "0";
        try {
            HashMap<String, String> map = bean.getMap();
            Date utilDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            logger.info("UserEmail>> ", map.get("userEmail"));
            logger.info("sqlDate>> ", sqlDate);
            response = "" + paymentRepository.getCountTodayForEmail(map.get("userEmail"), sqlDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    @PostMapping("/getTodaysCount")
    //@Produces({MediaType.APPLICATION_JSON})
    public String getTodaysCount() {
        String response = "0";
        try {
            Date utilDate = new Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            response = "" + paymentRepository.countByPaymentDateAndStatusIgnoreCaseContaining(sqlDate, "COMPLETED");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }


    @Autowired
    PaymentRepository paymentRepository;


}
