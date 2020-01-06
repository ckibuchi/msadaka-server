package msadaka.controllers.apis;

import msadaka.beans.PostBean;
import msadaka.dto.StkPushRequest;
import msadaka.dto.StkPushResponse;
import msadaka.models.Church;
import msadaka.services.ChurchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/churches")
public class ChurchesController {


    private Logger logger = LoggerFactory.getLogger(ChurchesController.class);

    @Autowired
    ChurchService churchService;


    @PostMapping("/createChurch")
    public Church createChurch(@RequestBody Church church)
    {
        return churchService.createChurch(church);
    }


    @PostMapping("/deleteChurch")
    public String deleteChurch(@RequestBody PostBean bean) {
        HashMap<String, String> map = bean.getMap();
        return churchService.deleteChurch(Long.parseLong(map.get("id")));

    }

    @PostMapping("/getPayBills")
    public List<Church> findChurchesByUseremail(@RequestParam(name = "userEmail", required = true) String userEmail) {
        return churchService.listChurchesByUserEmail(userEmail);
    }

    @GetMapping("/query/{name}")
    public List<Church> queryChurches(@PathVariable(name = "name") String name) {
        logger.info("name => "+ name);
        if(name.trim().isEmpty())
            return churchService.findAll();
        return churchService.findAllByName(name);
    }

    @PostMapping("/giveToChurch")
    public StkPushResponse giveToChurch(@RequestBody StkPushRequest stkPushRequest) {

        try {

            return churchService.stkPush(Double.parseDouble(stkPushRequest.getAmount()), stkPushRequest.getMsisdn(), Long.parseLong(stkPushRequest.getChurchId()), stkPushRequest.getReference());

        } catch (Exception e) {
            e.printStackTrace();
           StkPushResponse error= new StkPushResponse();
           error.setStatus("error");
           error.setMessage(e.getMessage());
           return error;
        }

    }
}
