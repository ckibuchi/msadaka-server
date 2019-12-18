package msadaka.controllers;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


@Repository
@Component
public interface WebClient {
    String stkpushrequest(String mpesaendpoint, String data);

    String getBearer();
}
