package si.fri.rso.uniborrow.loans.services.items;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ItemsService {

    private Logger log = Logger.getLogger(ItemsService.class.getName());
    @Inject
    @DiscoverService(value = "uniborrow-items-service", environment = "dev", version = "1.0.0")
    private URL serviceUrl;

    public boolean checkItemAvailable(Integer itemId) {
        ItemsClient itemsClient = RestClientBuilder.newBuilder().baseUrl(serviceUrl).build(ItemsClient.class);
        Item item = itemsClient.getItem(itemId);
        return item.getStatus().equals("Available");
    }

    public void markItemOnLoanAsync(Integer itemId) {
        ItemsClient itemsClient = RestClientBuilder.newBuilder().baseUrl(serviceUrl).build(ItemsClient.class);
        Item item = itemsClient.getItem(itemId);
        item.setStatus("Unavailable");
        itemsClient.updateItem(item, item.getItemId()).exceptionally(e -> {
            log.log(Level.WARNING, "Item could not be marked unavailable.");
            return null;
        });
    }
}

