package si.fri.rso.uniborrow.loans.api.v1.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

import si.fri.rso.uniborrow.loans.models.entities.LoanEntity;
import si.fri.rso.uniborrow.loans.services.beans.LoansDataProviderBean;

@ApplicationScoped
@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoansDataResource {

    private Logger log = Logger.getLogger(LoansDataResource.class.getName());

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
    public Response getLoans(@PathParam("loanId") Integer loanId) {
        LoanEntity loanEntity = loansDataProviderBean.getLoan(loanId);
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @POST
    public Response createLoan(LoanEntity loanEntity) {
        if (loanEntity.getFromId() == null || loanEntity.getStartTime() == null ||
                loanEntity.getItemId() == null || loanEntity.getToId() == null || loanEntity.getEndTime() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            loanEntity = loansDataProviderBean.createLoan(loanEntity);
        }
        return Response.status(Response.Status.OK).entity(loanEntity).build();
    }

    @PUT
    @Path("{loanId}")
    public Response putLoan(@PathParam("loanId") Integer loanId,
                            LoanEntity loanEntity) {
        loanEntity = loansDataProviderBean.putLoan(loanId, loanEntity);
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