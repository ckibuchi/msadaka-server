package msadaka.controllers.mpesa;

import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import msadaka.services.MpesaService;
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

    @Autowired
    MpesaService mpesaService;

    @PostMapping("/api/v1/mpesa-call-back")
    public ResponseEntity mpesacallback(@RequestBody String data) {
        mpesaService.processCallBack(data);
        return ResponseEntity.ok(HttpStatus.OK);

    }

}
