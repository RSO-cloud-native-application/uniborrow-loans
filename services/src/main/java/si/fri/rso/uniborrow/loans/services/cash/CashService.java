package si.fri.rso.uniborrow.loans.services.cash;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CashService {
    private Logger log = Logger.getLogger(CashService.class.getName());

    @Inject
    @DiscoverService(value = "uniborrow-cash-service", environment = "dev", version = "1.0.0")
    private URL serviceUrl;

    public void sendCashFromToAsync(Float amount, Integer fromUserId, Integer toUserId) {
        CashClient cashClient = RestClientBuilder.newBuilder().baseUrl(serviceUrl).build(CashClient.class);
        cashClient.sendCashAsync(fromUserId, toUserId, amount, "EUR")
                .whenComplete((p, e) -> log.log(Level.INFO, "Transaction " + p.toString() + " was succesfull."))
                .exceptionally(e -> {
                    log.log(Level.WARNING, "Transaction was not succesfull.");
                    return null;
                });
    }

    public float getUserCash(Integer userId) {
        CashClient cashClient = RestClientBuilder.newBuilder().baseUrl(serviceUrl).build(CashClient.class);
        CashInfo getCash;
        try {
            getCash = cashClient.getCashByUserId(userId, "EUR");
            if (getCash == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return getCash.getCurrentCash();
    }

}
