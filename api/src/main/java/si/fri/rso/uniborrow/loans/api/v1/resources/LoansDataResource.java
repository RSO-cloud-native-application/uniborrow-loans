package si.fri.rso.uniborrow.loans.api.v1.resources;

import si.fri.rso.uniborrow.loans.models.entities.AcceptedState;
import si.fri.rso.uniborrow.loans.models.entities.LoanEntity;
import si.fri.rso.uniborrow.loans.services.beans.LoansDataProviderBean;
import si.fri.rso.uniborrow.loans.services.users.UsersService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoansDataResource {

    private Logger log = Logger.getLogger(LoansDataResource.class.getName());
    @Inject
    private UsersService usersService;

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
    @Path("/accept")
    public Response acceptLoan(LoanEntity loanEntity) {
        LoanEntity acceptedLoan = loansDataProviderBean.acceptLoan(loanEntity.getId());
        if (acceptedLoan == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @POST
    @Path("/propose")
    public Response proposeLoan(LoanEntity loanEntity) {
        loanEntity.setAcceptedState(AcceptedState.PENDING);
        if (loanEntity.getFromId() == null || loanEntity.getStartTime() == null ||
                loanEntity.getItemId() == null || loanEntity.getToId() == null || loanEntity.getEndTime() == null ||
                !usersService.checkUserExists(loanEntity.getFromId()) || !usersService.checkUserExists(loanEntity.getToId())
        ) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            loanEntity = loansDataProviderBean.createLoan(loanEntity);
        }
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @POST
    public Response createLoan(LoanEntity loanEntity) {
        if (loanEntity.getFromId() == null || loanEntity.getStartTime() == null ||
                loanEntity.getItemId() == null || loanEntity.getToId() == null || loanEntity.getEndTime() == null ||
                !usersService.checkUserExists(loanEntity.getFromId()) || !usersService.checkUserExists(loanEntity.getToId())
        ) {
            return Response.status(Response.Status.BAD_REQUEST).build();
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
                (loanEntity.getToId() != null && !usersService.checkUserExists(loanEntity.getToId()))) {
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