package pe.com.amsac.tramite.api.request.body.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramitePIDERecepcionBodyRequest {

	private String id;

	//Datos de configuracion del tramite
	private String mensajeTramite;
	private String avisoConfidencial;
	private String codigoEtica;

	//Datos de registro
	private String tramitePrioridadId;
	private String dependenciaDestinoId;

	//Datos del documento
	private LocalDate fechaDocumento;
	private String tipoDocumentoId;
	private String origenDocumento; //INTERNO, EXTERNO, PIDE
	private String numeroDocumento;
	private String siglas;
	private String formaRecepcionId;
	private String folio;
	private String asunto;
	private String atencion;
	private String deseaFactura;
	private String nombreProyecto;
	private String contratoOrden;

	//Origen del documento
	private String origen; //INTERNO, EXTERNO
	private String tipoOrigen; //si es interno entonces va DOCUMENTO_PERSONAL.

	//Datos de Entidad Interno
	private String dependenciaId;
	private String cargoInternoId;
	private String usuarioId;

	//Datos Entidad externa
	private String razonSocial;
	private String cargo;
	private String nombres;
	private String telefono;
	private String anexo;
	private String celular;
	private String email;
	private String direccion;
	private String unidadOrganicaRemitente;
	private String rucEntidadRemitente;

	//Id tramite referencia
	private String idTramiteRelacionado;

	private boolean validarTramiteRelacionado;

	private String cuo;

	//Pin para firmar
	//private String pinFirma;
	private String dependenciaInternaDestinoTramitePide;

	//Datos para firmar
	private DatosFirmaDocumentoRequest datosFirmaDocumentoRequest;

	@Mapping("dependenciaDestino.id")
	public String getDependenciaDestinoId(){return dependenciaDestinoId;}

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoId(){return tipoDocumentoId;}

	//Entidad Interna
	@Mapping("entidadInterna.dependencia.id")
	public String getDependenciaId(){return dependenciaId;}

	@Mapping("entidadInterna.cargo.id")
	public String getCargoInternoId(){return cargoInternoId;}

	@Mapping("entidadInterna.usuario.id")
	public String getUsuarioId(){return usuarioId;}

	//Entidad externa
	@Mapping("entidadExterna.razonSocial")
	public String getRazonSocial(){return razonSocial;}

	@Mapping("entidadExterna.cargo")
	public String getCargo(){return cargo;}

	@Mapping("entidadExterna.nombre")
	public String getNombres(){return nombres;}

	@Mapping("entidadExterna.telefono")
	public String getTelefono(){return telefono;}

	@Mapping("entidadExterna.anexo")
	public String getAnexo(){return anexo;}

	@Mapping("entidadExterna.celular")
	public String getCelular(){return celular;}

	@Mapping("entidadExterna.email")
	public String getEmail(){return email;}

	@Mapping("entidadExterna.direccion")
	public String getDireccion(){return direccion;}

	@Mapping("entidadExterna.unidadOrganicaRemitente")
	public String getUnidadOrganicaRemitente(){return unidadOrganicaRemitente;}

	@Mapping("entidadExterna.rucEntidadRemitente")
	public String getRucEntidadRemitente(){return rucEntidadRemitente;}

	//Otros datos de tramite
	@Mapping("formaRecepcion.id")
	public String getFormaRecepcionId(){return formaRecepcionId;}

	@Mapping("tramitePrioridad.id")
	public String getTramitePrioridadId(){return tramitePrioridadId;}
	
}
