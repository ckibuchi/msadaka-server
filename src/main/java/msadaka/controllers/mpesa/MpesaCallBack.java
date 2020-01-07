package msadaka.controllers.mpesa;

import msadaka.services.MpesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mpesa")
public class MpesaCallBack {

    @Autowired
    MpesaService mpesaService;

    @PostMapping("/callback")
    @ResponseBody
    public String mpesacallback(@RequestBody String mpesaQueryResponse) {
        mpesaService.processCallBack(mpesaQueryResponse);
        return "OK";


    }

}
