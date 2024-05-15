package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tramite_entidad_externa")
public class EntidadExterna extends BaseEntity {

    @Id
    @Column(name = "id_tramite_entidad_externa")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "anexo")
    private String anexo;

    @Column(name = "celular")
    private String celular;

    @Column(name = "email")
    private String email;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "tramite_id")
    private String tramiteId;

    @Override
    public Serializable getEntityId() {
        return getId();
    }
}
