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
public class ItemServiceHealthCheck implements HealthCheck {

    @Inject
    @DiscoverService(value = "uniborrow-items-service", version = "1.0.0", environment = "dev")
    private Optional<URL> itemsService;

    @Override
    public HealthCheckResponse call() {
        return itemsService.isPresent()
                ? HealthCheckResponse.up(ItemServiceHealthCheck.class.getSimpleName())
                : HealthCheckResponse.down(ItemServiceHealthCheck.class.getSimpleName());
    }
}

