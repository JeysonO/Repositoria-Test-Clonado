package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tramite_entidad_interna")
public class EntidadInterna extends BaseEntity {

    @Id
    @Column(name = "id_tramite_entidad_interna")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dependencia", referencedColumnName = "id_dependencia")
    private Dependencia dependencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo", referencedColumnName = "id_cargo")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Override
    public Serializable getEntityId() {
        return getId();
    }
}
