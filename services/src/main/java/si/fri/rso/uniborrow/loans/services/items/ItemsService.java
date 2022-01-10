package si.fri.rso.uniborrow.loans.services.items;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ItemsService {
    //Inject
    //@DiscoverService(value = "uniborrow-items-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget = ClientBuilder.newClient().target("http://items:8080");

    public boolean checkItemAvailable(Integer itemId) {
        Response response = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        if (response.getStatus() != 404) {
            Item item = response.readEntity(Item.class);
            return item.getStatus().equals("Available");
        }
        return false;
    }

    public boolean markItemOnLoan(Integer itemId) {
        Response response = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        Item item = response.readEntity(Item.class);
        item.setStatus("Unavailable");
        response = webTarget.path("v1/items").path(itemId.toString()).request(MediaType.APPLICATION_JSON).buildPut(Entity.entity(item, MediaType.APPLICATION_JSON)).invoke();
        return response.getStatus() != 404;
    }
}

