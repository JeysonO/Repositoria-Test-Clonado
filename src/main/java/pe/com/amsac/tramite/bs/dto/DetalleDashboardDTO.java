package pe.com.amsac.tramite.bs.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DetalleDashboardDTO {

    private Integer tramite;
    private Date fechaInicio;
    private String asunto;
    private String estado;
    private String dependenciaRemitente;
    private String usuarioRemitente;
    private String dependenciaReceptor;
    private String usuarioReceptor;
    private String tipoDocumento;
    private String prioridad;
    private Integer diasFueraPlazo;
    private String idTramite;
    private String idTramiteDerivacion;
    private String tipoTramite;
    private String tramiteDependencia;
}
