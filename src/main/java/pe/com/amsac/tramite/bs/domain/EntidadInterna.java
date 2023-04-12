package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
@Data
public class EntidadInterna {

    @DBRef(db = "amsac-seguridad")
    private Dependencia dependencia;
    @DBRef(db = "amsac-seguridad")
    private Cargo cargo;
    @DBRef(db = "amsac-seguridad")
    private Usuario usuario;
}
