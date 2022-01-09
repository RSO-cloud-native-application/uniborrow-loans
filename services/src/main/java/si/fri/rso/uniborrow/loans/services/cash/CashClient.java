package si.fri.rso.uniborrow.loans.services.cash;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.concurrent.CompletionStage;

@Path("/cash")
@RegisterRestClient
public interface CashClient {
    @GET
    @Path("/{userId}")
    CashInfo getCashByUserId(@PathParam("userId") Integer cashId, @QueryParam("currency") String currency);

    @POST
    @Path("/{fromUserId}/send/{toUserId}")
    CompletionStage<TransactionEntity> sendCashAsync(@PathParam("fromUserId") Integer fromUserId, @PathParam("toUserId") Integer toUserId,
                                                     @QueryParam("amount") Float amount,
                                                     @QueryParam("currency") String currency);
}
