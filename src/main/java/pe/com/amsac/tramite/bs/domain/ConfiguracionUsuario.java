package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "configuracion_usuario")
public class ConfiguracionUsuario extends BaseEntity {

    private static final long serialVersionUID = 7857201376677339392L;

    @Id
    @Column(name = "id_configuracion_usuario")
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Column(name = "enviar_alerta_tramite_pendiente")
    private String enviarAlertaTramitePendiente;

    @Column(name = "estado")
    private String estado;

    @Override
    public Serializable getEntityId() {
        return getId();
    }


}
