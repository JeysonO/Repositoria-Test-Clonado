package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    @Column(name = "id_tramite")
    private String tramiteId;

    @Column(name = "uo_remitente")
    private String unidadOrganicaRemitente;

    @Column(name = "ruc_remitente")
    private String rucEntidadRemitente;

    //Se agrega para pide despacho
    @Column(name = "uo_destino")
    private String unidadOrganicaDestino;

    @Column(name = "ruc_destino")
    private String rucEntidadDestino;

    @Column(name = "num_reg_std")
    private String numeroRegistroStd;

    @Column(name = "anio_reg_std")
    private String anioRegistroStd;

    @Column(name = "fec_reg_std")
    private Date fechaRegistroStd;

    @Column(name = "uo_reg_std")
    private String uoRegistroStd;

    @Column(name = "usu_reg_std")
    private String usuRegistroStd;

    @Column(name = "obs_std")
    private String obsStd;

    @Override
    public Serializable getEntityId() {
        return getId();
    }
}
