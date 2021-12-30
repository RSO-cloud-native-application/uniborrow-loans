package si.fri.rso.uniborrow.loans.services.users;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@ApplicationScoped
public class UsersService {

    private Logger log = Logger.getLogger(UsersService.class.getName());

    @Inject
    @DiscoverService(value = "uniborrow-users-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget = ClientBuilder.newClient().target("http://uniborrow-users:8080");

    public boolean checkUserExists(Integer userId) {
        Response userCheck = webTarget.path("/v1/users").path(userId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        return userCheck.getStatus() != 404;
    }
}
