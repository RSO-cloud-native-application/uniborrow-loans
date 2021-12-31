package si.fri.rso.uniborrow.loans.services.users;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class UsersService {

    @Inject
    @DiscoverService(value = "uniborrow-users-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;

    public boolean checkUserExists(Integer userId) {
        Response userCheck = webTarget.path("/v1/users").path(userId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        return userCheck.getStatus() != 404;
    }
}
