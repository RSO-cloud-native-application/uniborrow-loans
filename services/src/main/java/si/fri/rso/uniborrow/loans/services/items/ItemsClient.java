package si.fri.rso.uniborrow.loans.services.items;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.CompletionStage;

@Path("/items")
@RegisterRestClient
public interface ItemsClient {
    @GET
    @Path("/{itemId}")
    Item getItem(@PathParam("itemId") Integer itemId);

    @PUT
    @Path("{itemId}")
    CompletionStage<Void> updateItem(Item item, @PathParam("itemId") Integer itemId);

}
