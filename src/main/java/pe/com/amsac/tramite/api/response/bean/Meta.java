package pe.com.amsac.tramite.api.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Meta {

    @JsonProperty("result")
    private String result;

    @JsonProperty("mensajes")
    private List<Mensaje> mensajes;

    @JsonProperty("atributos")
    private Map atributos;

    public Meta(String result, List<Mensaje> mensajes) {
        this.result = result;
        this.mensajes = mensajes;
    }
}
