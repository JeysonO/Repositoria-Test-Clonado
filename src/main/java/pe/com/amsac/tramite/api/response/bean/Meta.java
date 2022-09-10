package pe.com.amsac.tramite.api.response.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Meta {

    private String result;
    private List<Mensaje> mensajes;

}
