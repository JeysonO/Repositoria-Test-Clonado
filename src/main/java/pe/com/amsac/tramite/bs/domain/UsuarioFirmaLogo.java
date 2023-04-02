package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Document(collection = "usuario_firma_logo")
@AttributeOverrides(value = {
        @AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
        @AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
        @AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})
public class UsuarioFirmaLogo extends BaseAuditableEntity<String> {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    private String id;

    @DBRef
    private UsuarioFirma usuarioFirma;

    private String nombreArchivo;
    private String size;
    private String estado;
    private String descripcion;

    @Override
    public Serializable getEntityId() {
        return getId();
    }


}
