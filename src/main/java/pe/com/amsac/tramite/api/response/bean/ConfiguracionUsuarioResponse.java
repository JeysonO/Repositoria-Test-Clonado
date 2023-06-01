package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfiguracionUsuarioResponse {


    private String id;
    private UsuarioResponse usuario;
    private String enviarAlertaTramitePendiente;
    private String estado;

}
