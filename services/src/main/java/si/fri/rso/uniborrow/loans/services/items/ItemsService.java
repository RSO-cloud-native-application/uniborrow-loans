package si.fri.rso.uniborrow.loans.services.items;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ItemsService {
    @Inject
    @DiscoverService(value = "uniborrow-items-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;

    public boolean checkItemExists(Integer itemId) {
        Response userCheck = webTarget.path("/v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        return userCheck.getStatus() != 404;
    }

    public boolean markItemOnLoan(Integer itemId) {
        return true;
    }
}

