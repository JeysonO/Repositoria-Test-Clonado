package pe.com.amsac.tramite.api.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityHelper {

    public String obtenerUserIdSession(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        DatosToken datosToken = (DatosToken)authentication.getPrincipal();
        //UserDetails usuarioPrincipal = (UserDetails)authentication.getPrincipal();
        return Optional.of(datosToken.getIdUser()).get();
    }

    public String obtenerDependenciaIdUserSession(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        DatosToken datosToken = (DatosToken)authentication.getPrincipal();
        //UserDetails usuarioPrincipal = (UserDetails)authentication.getPrincipal();
        //return Optional.of(datosToken.getDependenciaId()).get();
        return datosToken.getDependenciaId();
    }

    public String obtenerCargoIdUserSession(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        DatosToken datosToken = (DatosToken)authentication.getPrincipal();
        //UserDetails usuarioPrincipal = (UserDetails)authentication.getPrincipal();
        //return Optional.of(datosToken.getDependenciaId()).get();
        return datosToken.getCargoId();
    }

    public String getTokenCurrentSession(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        DatosToken datosToken = (DatosToken)authentication.getPrincipal();
        return datosToken.getToken();
    }
}
