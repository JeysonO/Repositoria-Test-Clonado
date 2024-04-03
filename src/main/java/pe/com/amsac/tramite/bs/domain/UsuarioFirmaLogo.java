package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
//@Document(collection = "usuario_firma_logo")
@Entity
@Table(name = "usuario_firma_logo")
@AttributeOverrides(value = {
        @AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
        @AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
        @AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class UsuarioFirmaLogo extends BaseAuditableEntity<String> {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    @Column(name = "id_usuario_firma_logo")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    //@DBRef
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_firma", referencedColumnName = "id_usuario_firma")
    private UsuarioFirma usuarioFirma;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "size")
    private String size;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "es_favorito")
    private boolean esFavorito;

    @Override
    public Serializable getEntityId() {
        return getId();
    }


}
