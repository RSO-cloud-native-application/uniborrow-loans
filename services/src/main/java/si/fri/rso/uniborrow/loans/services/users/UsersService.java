package si.fri.rso.uniborrow.loans.services.users;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class UsersService {

    @Inject
    @DiscoverService(value = "uniborrow-users", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;

    public boolean checkUserExists(String userId) {
        webTarget.path("/v1/users").request(MediaType.APPLICATION_JSON);
        return true;
    }
}
