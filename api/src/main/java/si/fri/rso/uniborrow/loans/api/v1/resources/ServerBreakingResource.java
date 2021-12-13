package si.fri.rso.uniborrow.loans.api.v1.resources;

import si.fri.rso.uniborrow.loans.services.config.AdministrationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/break")
public class ServerBreakingResource {
    @Inject
    private AdministrationProperties administrationProperties;

    @GET
    public String breakServer() {
        administrationProperties.setBroken(true);
        return "YOU HAVE BROKEN THE SERVER. WHY WOULD YOU DO SOMETHING LIKE THAT?";
    }
}
