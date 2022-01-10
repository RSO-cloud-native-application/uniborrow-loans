package si.fri.rso.uniborrow.loans.services.users;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class UsersService {

    @Inject
    @DiscoverService(value = "uniborrow-users-service", version = "1.0.0", environment = "dev")
    private WebTarget webTarget;

    @Timeout(value = 3, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "checkUserExistsFallback")
    @Retry(maxRetries = 3)
    public boolean checkUserExists(Integer userId) {
        Response userCheck = webTarget.path("/v1/users").path(userId.toString()).request(MediaType.APPLICATION_JSON).buildGet().invoke();
        return userCheck.getStatus() != 404;
    }

    public boolean checkUserExistsFallback(Integer userId) {
        return false;
    }
}
