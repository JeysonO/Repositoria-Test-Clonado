package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Document(collection = "usuario_firma")
@AttributeOverrides(value = {
        @AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
        @AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
        @AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})
public class UsuarioFirma extends BaseAuditableEntity<String> {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    private String id;
    private String usuario;
    private String usernameServicioFirma;
    private String passwordServicioFirma;
    private String rutaImagenFirma;
    private String size;
    private String estado;

    @Override
    public Serializable getEntityId() {
        return getId();
    }


}
