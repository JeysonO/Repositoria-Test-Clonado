package pe.com.amsac.tramite.api.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleAmsac {

    @Autowired
    private Environment env;

    @Autowired
    private TramiteDerivacionService tramiteDerivacionService;

    //@Scheduled(cron = "0 0 12 ? * * ")
    //@Scheduled(fixedDelayString = "${fixedDelay.evaluar-tramite-derivacion.milliseconds}")
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

}
