package si.fri.rso.uniborrow.loans.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.uniborrow.loans.services.config.AdministrationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class HealthChecker implements HealthCheck {

    @Inject
    private AdministrationProperties adminProperties;

    @Override
    public HealthCheckResponse call() {
        if (adminProperties.getBroken()) {
            return HealthCheckResponse.down(HealthChecker.class.getSimpleName());
        } else {
            return HealthCheckResponse.up(HealthChecker.class.getSimpleName());
        }
    }
}
