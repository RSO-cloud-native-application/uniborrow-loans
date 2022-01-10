package si.fri.rso.uniborrow.loans.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.uniborrow.loans.models.entities.AcceptedState;
import si.fri.rso.uniborrow.loans.models.entities.LoanEntity;
import si.fri.rso.uniborrow.loans.services.beans.LoansDataProviderBean;
import si.fri.rso.uniborrow.loans.services.cash.CashService;
import si.fri.rso.uniborrow.loans.services.items.ItemsService;
import si.fri.rso.uniborrow.loans.services.users.UsersService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Log
@ApplicationScoped
@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoansDataResource {

    @Inject
    private UsersService usersService;

    @Inject
    private ItemsService itemsService;

    @Inject
    private CashService cashService;

    @Inject
    private LoansDataProviderBean loansDataProviderBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Operation(description = "Get loans by filter, or all.", summary = "Get loans by filter, or all.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Loans that fit the filter.",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class, type = SchemaType.ARRAY))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No loans found."
            )
    })
    public Response getAllLoans() {
        List<LoanEntity> loanEntities = loansDataProviderBean.getLoansFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(loanEntities).build();
    }

    @GET
    @Path("/{loanId}")
    @Operation(description = "Get loan by id.", summary = "Get loan by id.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Loan by id.",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Loan with id not found."
            )
    })
    public Response getLoan(@Parameter(
            description = "Loan id.",
            required = true
    ) @PathParam("loanId") Integer loanId) {
        LoanEntity loanEntity = loansDataProviderBean.getLoan(loanId);
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @POST
    @Path("/{loanId}/accept")
    @Operation(description = "Accept loan with id.", summary = "Accept loan with id.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Accepted loan",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            ),
            @APIResponse(
                    responseCode = "417",
                    description = "Problems with sending cash."
            ),
            @APIResponse(
                    responseCode = "406",
                    description = "User does not have enough cash."
            )
    })
    public Response acceptLoan(@Parameter(
            description = "Loan id.",
            required = true
    ) @PathParam("loanId") Integer loanId) {
        LoanEntity loan = loansDataProviderBean.getLoan(loanId);
        if (loan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        float receivingCash = cashService.getUserCash(loan.getToId());
        if (receivingCash > loan.getPrice()) {
            cashService.sendCashFromToAsync(loan.getPrice(), loan.getFromId(), loan.getToId());
            itemsService.markItemOnLoanAsync(loan.getItemId());
            LoanEntity acceptedLoan = loansDataProviderBean.acceptLoan(loanId);
            return Response.status(Response.Status.OK).entity(acceptedLoan).build();
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("NOT ENOUGH CASH").build();
        }
    }

    @POST
    @Path("/{loanId}/reject")
    @Operation(description = "Reject loan with id.", summary = "Reject loan with id.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Rejected loan",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Loan not found."
            )
    })
    public Response rejectLoan(@Parameter(
            description = "Loan id.",
            required = true
    ) @PathParam("loanId") Integer loanId) {
        LoanEntity rejectedLoan = loansDataProviderBean.rejectLoan(loanId);
        if (rejectedLoan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(rejectedLoan).build();
    }

    @POST
    @Path("/propose")
    @Operation(description = "Propose new loan.", summary = "Propose new loan.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Proposed loan",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problems with request body."
            )
    })
    public Response proposeLoan(
            @RequestBody(
                    description = "Proposed loan info.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            )
                    LoanEntity loanEntity) {
        loanEntity.setAcceptedState(AcceptedState.PENDING);
        if (loanEntity.getFromId() == null || loanEntity.getStartTime() == null ||
                loanEntity.getItemId() == null || loanEntity.getToId() == null || loanEntity.getEndTime() == null ||
                !usersService.checkUserExists(loanEntity.getFromId()) || !usersService.checkUserExists(loanEntity.getToId()) || loanEntity.getEndTime().isBefore(loanEntity.getStartTime())
        ) {
            return Response.status(Response.Status.BAD_REQUEST).entity(loanEntity).build();
        } else {
            loanEntity = loansDataProviderBean.createLoan(loanEntity);
        }
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @PATCH
    @Path("{loanId}")
    @Operation(description = "Patch a loan.", summary = "Patch a loan.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Patched loan",
                    content = @Content(schema = @Schema(implementation = LoanEntity.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Problems with request body."
            )
    })
    public Response patchLoan(@Parameter(
            description = "Loan id.",
            required = true
    ) @PathParam("loanId") Integer loanId,
                              @RequestBody(
                                      description = "Changed loan info.",
                                      required = true,
                                      content = @Content(schema = @Schema(implementation = LoanEntity.class))
                              )
                                      LoanEntity loanEntity) {
        if (loanEntity.getFromId() != null && !usersService.checkUserExists(loanEntity.getFromId()) ||
                (loanEntity.getToId() != null && !usersService.checkUserExists(loanEntity.getToId())) || !itemsService.checkItemAvailable(loanEntity.getItemId())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        loanEntity = loansDataProviderBean.patchLoan(loanId, loanEntity);
        if (loanEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{loanId}")
    @Operation(description = "Delete a loan.", summary = "Delete a loan.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Loan deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Loan not found."
            )
    })
    public Response deleteLoan(@Parameter(
            description = "Loan id.",
            required = true
    ) @PathParam("loanId") Integer loanId) {
        boolean deleted = loansDataProviderBean.deleteLoan(loanId);
        if (deleted) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}