package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
//@Document(collection = "usuario_firma")
@Entity
@Table(name = "usuario_firma")
@AttributeOverrides(value = {
        @AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
        @AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
        @AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class UsuarioFirma extends BaseAuditableEntity<String> {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    @Column(name = "id_usuario_firma")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    //@DBRef(db = "amsac-seguridad")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Column(name = "username_servicio_firma")
    private String usernameServicioFirma;

    @Column(name = "password_servicio_firma")
    private String passwordServicioFirma;

    @Column(name = "sigla_firma")
    private String siglaFirma;

    @Column(name = "estado")
    private String estado;

    @Override
    public Serializable getEntityId() {
        return getId();
    }


}
