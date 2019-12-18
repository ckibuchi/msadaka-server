package msadaka.controllers.apis;

import msadaka.beans.PostBean;
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
    public String giveToChurch(@RequestBody PostBean bean) {

        try {
            HashMap<String, String> map = bean.getMap();

            return churchService.stkPush(Double.parseDouble(map.get("amount")), map.get("msisdn"), Long.parseLong(map.get("churchID")), map.get("reference"));

        } catch (Exception e) {
            return "{'status':'error','message':'" + e.getMessage() + "'}";
        }

    }
}
