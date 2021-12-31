package si.fri.rso.uniborrow.loans.services.cash;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class CashService {

    @Inject
    @DiscoverService(value = "uniborrow-cash-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;

    public boolean sendCashFromTo(Float amount, Integer fromUserId, Integer toUserId) {
        Response sendCash = webTarget.path("/v1/cash")
                .path(fromUserId.toString())
                .path("send")
                .path(toUserId.toString())
                .queryParam("amount", amount)
                .queryParam("currency", "EUR")
                .request(MediaType.APPLICATION_JSON).post(null);
        return sendCash.getStatus() == 200;
    }

    public float getUserCash(Integer userId) {
        Float getCash = webTarget.path("/v1/cash")
                .path(userId.toString())
                .queryParam("currency", "EUR")
                .request(MediaType.APPLICATION_JSON).get(Float.class);
        if (getCash == null) {
            return 0;
        }
        return getCash;
    }

}
