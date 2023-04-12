package pe.com.amsac.tramite.api.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        DatosToken datosToken = (DatosToken)authentication.getPrincipal();
        //UserDetails usuarioPrincipal = (UserDetails)authentication.getPrincipal();
        return Optional.of(datosToken.getIdUser());
    }
}
