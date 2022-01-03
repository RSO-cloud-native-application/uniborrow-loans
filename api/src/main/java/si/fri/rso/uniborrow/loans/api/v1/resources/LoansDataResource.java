package si.fri.rso.uniborrow.loans.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
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
    public Response getAllLoans() {
        List<LoanEntity> loanEntities = loansDataProviderBean.getLoansFilter(uriInfo);
        return Response.status(Response.Status.OK).entity(loanEntities).build();
    }

    @GET
    @Path("/{loanId}")
    public Response getLoan(@PathParam("loanId") Integer loanId) {
        LoanEntity loanEntity = loansDataProviderBean.getLoan(loanId);
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @POST
    @Path("/{loanId}/accept")
    public Response acceptLoan(@PathParam("loanId") Integer loanId) {
        LoanEntity loan = loansDataProviderBean.getLoan(loanId);
        if (loan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        float receivingCash = cashService.getUserCash(loan.getToId());
        if (receivingCash > loan.getPrice()) {
            boolean cashSent = cashService.sendCashFromTo(loan.getPrice(), loan.getFromId(), loan.getToId());
            if (cashSent) {
                itemsService.markItemOnLoan(loan.getItemId());
                LoanEntity acceptedLoan = loansDataProviderBean.acceptLoan(loanId);
                return Response.status(Response.Status.OK).entity(acceptedLoan).build();
            } else {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("CASH WAS NOT SENT.").build();
            }
        } else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("NOT ENOUGH CASH").build();
        }
    }

    @POST
    @Path("/{loanId}/reject")
    public Response rejectLoan(@PathParam("loanId") Integer loanId) {
        LoanEntity rejectedLoan = loansDataProviderBean.rejectLoan(loanId);
        if (rejectedLoan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(rejectedLoan).build();
    }

    @POST
    @Path("/propose")
    public Response proposeLoan(LoanEntity loanEntity) {
        loanEntity.setAcceptedState(AcceptedState.PENDING);
        if (loanEntity.getFromId() == null || loanEntity.getStartTime() == null ||
                loanEntity.getItemId() == null || loanEntity.getToId() == null || loanEntity.getEndTime() == null ||
                !usersService.checkUserExists(loanEntity.getFromId()) || !usersService.checkUserExists(loanEntity.getToId()) || !itemsService.checkItemAvailable(loanEntity.getItemId())
                || loanEntity.getEndTime().isBefore(loanEntity.getStartTime())
        ) {
            return Response.status(Response.Status.BAD_REQUEST).entity(loanEntity).build();
        } else {
            loanEntity = loansDataProviderBean.createLoan(loanEntity);
        }
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @PATCH
    @Path("{loanId}")
    public Response patchLoan(@PathParam("loanId") Integer loanId,
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
    public Response deleteLoan(@PathParam("loanId") Integer loanId) {
        boolean deleted = loansDataProviderBean.deleteLoan(loanId);
        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}