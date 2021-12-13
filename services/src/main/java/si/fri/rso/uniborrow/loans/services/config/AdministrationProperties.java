package si.fri.rso.uniborrow.loans.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;

@Data
@ConfigBundle("administration")
@ApplicationScoped
public class AdministrationProperties {

    @ConfigValue(watch = true)
    private Boolean maintenanceMode;

    private Boolean broken = false;
}
