package si.fri.rso.uniborrow.loans.services.items;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ItemsService {

    private Logger log = Logger.getLogger(ItemsService.class.getName());
    //@Inject
    //@DiscoverService(value = "uniborrow-items-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget = ClientBuilder.newClient().target("http://items:8080/");

    public boolean checkItemAvailable(Integer itemId) {
        Response response = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        if (response.getStatus() != 404) {
            Item item = response.readEntity(Item.class);
            return item.getStatus().equals("Available");
        }
        return false;
    }

    public void markItemOnLoanAsync(Integer itemId) {
        Response response = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        Item item = response.readEntity(Item.class);
        item.setStatus("Unavailable");
        CompletionStage<Response> asyncResponse = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON)
                .rx()
                .put(Entity.entity(item, MediaType.APPLICATION_JSON));
        asyncResponse.whenComplete((r, t) -> {
            if (t != null) {
                log.log(Level.WARNING, "Marking item on loan went wrong.");
            } else {
                log.log(Level.WARNING, "Marking item on loan was successful.");
            }
        });
    }
}

