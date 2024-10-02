package pe.com.amsac.tramite.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.DatosToken;
import pe.com.amsac.tramite.api.request.bean.LoginRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.bs.service.EntidadPideService;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;
import pe.com.amsac.tramite.bs.service.TramiteService;

import java.util.LinkedHashMap;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleAmsac {

    @Autowired
    private Environment env;

    @Autowired
    private TramiteDerivacionService tramiteDerivacionService;

    @Autowired
    private TramiteService tramiteService;

    @Autowired
    private EntidadPideService entidadPideService;

    @Autowired
    private RestTemplate restTemplate;

    //@Scheduled(cron = "0 0 12 * * * ")
    //@Scheduled(fixedDelayString = "${fixedDelay.evaluar-tramite-derivacion.milliseconds}")
    @Scheduled(cron = "${cron.evaluar-vencimiento-tramite-derivacion}", zone = "America/Lima")
    public void scheduleEvaluarEventosTask() throws Exception {
        //TODO: Crear clase Schedule dentro de api (Guiarse de SIOPS) Crear metodo alertar-tramites-fuera-plazo-atencion para invocar la
        // funcion a crear. Ejecutar c/12h
        // fixedDelay:
        // evaluar-tramite-derivacion:
        // milliseconds: 300000
        log.info("<========= INICIO EVALUAR TRAMITES DERIVACION FUERA DE PLAZO =========>");

        try{
            //Invocamos a un servicio para evaluar alguna alerta que se tenga que enviar de los eventos
            tramiteDerivacionService.alertaTramiteFueraPlazoAtencion();

        }catch (Exception e){
            log.error("Error scheduleEvaluarEventosTask",e);
        }

        log.info("<========= FIN EVALUAR TRAMITES DERIVACION FUERA DE PLAZO =========>");
    }

    //@Scheduled(cron = "${cron.enviar-tramite-pendiente-pide}", zone = "America/Lima")
    @Scheduled(fixedDelayString = "${fixedDelay.enviar-tramite-pendiente-pide.milliseconds}")
    public void scheduleEnvioTramitePIDETask() throws Exception {
        log.info("<========= INICIO ENVIAR TRAMITE PENDIENTE PIDE =========>");
        try{
            createAuthentication();
            tramiteService.enviarTramitePendientePide();

        }catch (Exception e){
            log.error("Error scheduleEnvioTramitePIDETask",e);
        }

        log.info("<========= FIN ENVIAR TRAMITE PENDIENTE PIDE =========>");
    }

    @Scheduled(cron = "${cron.obtener-entidad-pide}", zone = "America/Lima")
    public void scheduleObtenerEntidadPideTask() throws Exception {
        log.info("<========= INICIO SINCRONIZAR ENTIDADES PIDE =========>");

        try{
            createAuthentication();
            entidadPideService.sincronizarEntidadesCatalogo();

        }catch (Exception e){
            log.error("Error scheduleObtenerEntidadPideTask",e);
        }

        log.info("<========= FIN SINCRONIZAR ENTIDADES PIDE =========>");
    }

    public void createAuthentication(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication==null) {
            DatosToken datosToken = new DatosToken();
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(env.getProperty("app.credenciales.schedule.username"));
            loginRequest.setPassword(env.getProperty("app.credenciales.schedule.password"));

            String uri = env.getProperty("app.url.seguridad") + "/signin";
            HttpHeaders headers = new HttpHeaders();
            //headers.add("Authorization", String.format("%s %s", "Bearer", jwt));
            //log.info("uri obtener usuario: "+uri);
            //log.info("datosToken: "+new ObjectMapper().writeValueAsString(datosToken));
            HttpEntity entity = new HttpEntity<>(loginRequest, headers);
            ResponseEntity<CommonResponse> responseConsultaUsuario = restTemplate.exchange(uri, HttpMethod.POST,entity, new ParameterizedTypeReference<CommonResponse>() {});
            if(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("token")!=null)
                datosToken.setToken(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("token").toString());

            uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id";
            headers.add("Authorization", String.format("%s %s", "Bearer", datosToken.getToken()));
            //log.info("uri obtener usuario: "+uri);
            //log.info("datosToken: "+new ObjectMapper().writeValueAsString(datosToken));
            entity = new HttpEntity<>(null, headers);
            responseConsultaUsuario = restTemplate.exchange(uri, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
            if(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("id")!=null)
                datosToken.setIdUser(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("id").toString());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(datosToken, null, null);

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

    }

}
