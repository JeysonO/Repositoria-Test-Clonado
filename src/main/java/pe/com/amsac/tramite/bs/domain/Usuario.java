package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
//@Entity
//@Table(name = "usuario")
//@Document(collection = "usuario")
@Entity
@Table(name = "usuario")
@AttributeOverrides(value = {
        @AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
        @AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
        @AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class Usuario extends BaseAuditableEntity<String> {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "ape_paterno")
    private String apePaterno;

    @Column(name = "ape_materno")
    private String apeMaterno;

    @Column(name = "email")
    private String email;

    @Column(name = "estado")
    private String estado;

    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    @Column(name = "token_activacion")
    private String tokenActivacion;

    @Transient
    private String nombreCompleto;

    //@DBRef
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Persona persona;

    @Override
    public Serializable getEntityId() {
        return getId();
    }

    public String getNombreCompleto(){
        return getNombre().concat(" ").concat(getApePaterno()).concat(" ").concat(StringUtils.isBlank(getApeMaterno())?"":getApeMaterno());
    }

}
