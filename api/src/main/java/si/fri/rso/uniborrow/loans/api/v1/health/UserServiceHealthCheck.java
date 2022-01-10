package si.fri.rso.uniborrow.loans.api.v1.health;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.util.Optional;

@Readiness
@ApplicationScoped
public class UserServiceHealthCheck implements HealthCheck {

    @Inject
    @DiscoverService(value = "uniborrow-users-service", version = "1.0.0", environment = "dev")
    private Optional<URL> usersService;

    @Override
    public HealthCheckResponse call() {
        return usersService.isPresent()
                ? HealthCheckResponse.up(UserServiceHealthCheck.class.getSimpleName())
                : HealthCheckResponse.down(UserServiceHealthCheck.class.getSimpleName());
    }
}

