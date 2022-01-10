package si.fri.rso.uniborrow.loans.services.cash;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CashService {
    private Logger log = Logger.getLogger(CashService.class.getName());

    @Inject
    @DiscoverService(value = "uniborrow-cash-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;

    public void sendCashFromToAsync(Float amount, Integer fromUserId, Integer toUserId) {
        CompletionStage<Response> sendCash = webTarget.path("/v1/cash")
                .path(fromUserId.toString())
                .path("send")
                .path(toUserId.toString())
                .queryParam("amount", amount)
                .queryParam("currency", "EUR")
                .request(MediaType.APPLICATION_JSON)
                .rx()
                .post(null);
        sendCash.whenComplete((r, t) -> {
            if (t != null) {
                log.log(Level.WARNING, "Sending cash went wrong.");
            } else {
                log.log(Level.INFO, "Sent cash successfully.");
            }
        });
    }

    public float getUserCash(Integer userId) {
        CashInfo getCash;
        try {
            getCash = webTarget.path("/v1/cash")
                    .path(userId.toString())
                    .queryParam("currency", "EUR")
                    .request(MediaType.APPLICATION_JSON).get(CashInfo.class);
            if (getCash == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return getCash.getCurrentCash();
    }

}
