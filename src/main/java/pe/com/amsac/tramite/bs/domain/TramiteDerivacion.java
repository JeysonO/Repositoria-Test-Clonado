package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "tramite_derivacion")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})

public class TramiteDerivacion extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private int secuencia;

	//private String usuarioInicio;
	@DBRef(db = "amsac-seguridad")
	private Usuario usuarioInicio;

	@DBRef(db = "amsac-seguridad")
	private Dependencia dependenciaUsuarioInicio;

	//private String usuarioFin;
	@DBRef(db = "amsac-seguridad")
	private Usuario usuarioFin;

	@DBRef(db = "amsac-seguridad")
	private Dependencia dependenciaUsuarioFin;

	private String estadoInicio;

	private String estadoFin;

	private String comentarioInicio;

	private String comentarioFin;

	private Date fechaInicio;

	private Date fechaFin;

	private Date fechaMaximaAtencion;

	private String proveidoAtencion;

	private String estado;

	private String forma;

	@DBRef
	private Tramite tramite;

	@DBRef(db = "amsac-seguridad")
	private Cargo cargoUsuarioInicio;

	@DBRef(db = "amsac-seguridad")
	private Cargo cargoUsuarioFin;

	//@Transient
	private String dependenciaNombreUsuarioInicio;

	@Transient
	private String cargoNombreUsuarioInicio;

	//@Transient
	private String dependenciaNombreUsuarioFin;

	@Transient
	private String cargoNombreUsuarioFin;

	//@Transient
	private String usuarioInicioNombreCompleto;

	//@Transient
	private String usuarioFinNombreCompleto;

	//@Transient
	private String usuarioCreacion;

	//@Transient
	private String dependenciaEmpresa;

	//@Transient
	private String emailUsuarioCreacion;

	@Transient
	private int diasFueraPlazo;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

	/*
	public String getUsuarioInicioNombreCompleto(){
		if(usuarioInicio!=null)
			return usuarioInicio.getNombreCompleto();
		else
			return "";
	}

	public String getUsuarioFinNombreCompleto(){
		if(usuarioFin!=null)
			return usuarioFin.getNombreCompleto();
		else
			return "";
	}
	*/

}
