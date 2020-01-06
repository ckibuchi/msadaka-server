package msadaka.controllers.apis;

import com.google.gson.Gson;
import msadaka.beans.PostBean;
import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.services.PaymentService;
import msadaka.utils.Functions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsContoller {
    private Logger logger = LoggerFactory.getLogger(PaymentsContoller.class);

    @Autowired
    PaymentService paymentService;

    @PostMapping("/getparkings")
    //@Produces({MediaType.APPLICATION_JSON})
    public List<Payment> getparkingsbyRegandDate(@RequestParam(name = "carRegNo", required = true) String carRegNo) {

        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        List<Payment> payments = Collections.emptyList();// paymentRepository.findPaymentsByCarRegNoAndPaymentDate(carRegNo.replaceAll("[^a-zA-Z0-9]", "").trim(), sqlDate);

        return payments;

    }

    @RequestMapping("/search-payments")
    //@Produces({MediaType.APPLICATION_JSON})
    public List<Payment> searchPayments(@RequestParam(name = "carRegNo", required = false, defaultValue = "") String carRegNo,
                                        @RequestParam(name = "county", required = false, defaultValue = "") String county,
                                        @RequestParam(name = "subCounty", required = false, defaultValue = "") String subCounty,
                                        @RequestParam(name = "dateFrom", required = false, defaultValue = "") String dateFrom,
                                        @RequestParam(name = "dateTo", required = false, defaultValue = "") String dateTo) {


        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        String carRegno2 = carRegNo.replaceAll("[^a-zA-Z0-9]", "").trim();
        List<Payment> payments = null;

        return getPayments(county, subCounty, dateFrom, dateTo, carRegno2);
    }

    private List<Payment> getPayments(@RequestParam(name = "county", required = false, defaultValue = "") String county, @RequestParam(name = "subCounty", required = false, defaultValue = "") String subCounty, @RequestParam(name = "dateFrom", required = false, defaultValue = "") String dateFrom, @RequestParam(name = "dateTo", required = false, defaultValue = "") String dateTo, String carRegno2) {
        List<Payment> payments = Collections.emptyList();
        if (isEmpty(carRegno2) && isEmpty(county) && isEmpty(subCounty) && isEmpty(dateFrom) && isEmpty(dateTo)) {
            logger.info("No filters.... Show all transactions for today ");
            payments = new ArrayList<>();
            ;// paymentRepository.findPaymentsByPaymentDate(sqlDate);
        } else if (isEmpty(dateFrom) || isEmpty(dateTo)) {
            logger.info("No Dates...");
            payments = Collections.emptyList(); //paymentRepository.findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDate(carRegno2, county,subCounty, new Date());
        } else {
            try {
                Date datef = Functions.sdf1.parse(dateFrom);
                java.sql.Date sqlDateFrom = new java.sql.Date(datef.getTime());

                Date datet = Functions.sdf1.parse(dateTo);
                java.sql.Date sqlDateTo = new java.sql.Date(datet.getTime());
                payments = Collections.emptyList();// paymentRepository.findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDateGreaterThanEqualAndPaymentDateIsLessThanEqual(carRegno2, county, subCounty, sqlDateFrom, sqlDateTo);
                logger.info("dateFrom >>" + dateFrom + " sqlDateFrom >> " + sqlDateFrom);
                logger.info("sqlDateTo >> " + sqlDateTo + " sqlDateTo >> " + sqlDateTo);
            } catch (Exception e) {
                logger.info("Date conversion Failed Badly!!!!!!!!!!!!!!!");
                e.printStackTrace();

            }
        }
        return payments;
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

    @GetMapping("/getChurchPayments/{Id}")
    public List<Payment> findPaymentsByChurchId(@PathVariable long Id)
    {
        return  paymentService.findPaymentsByChurchId(Id);
    }


    @Autowired
    PaymentRepository paymentRepository;


}
