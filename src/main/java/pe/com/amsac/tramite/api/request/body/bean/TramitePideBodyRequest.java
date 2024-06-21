package pe.com.amsac.tramite.api.request.body.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TramitePideBodyRequest {

	private String id;

	//Remitente
	private String dependenciaRemitenteId;
	private String dependenciaRemitenteNombre;
	private String dependenciaRemitenteSigla;
	private String usuarioRemitenteNombre;
	private String usuarioRemitenteId;
	private String cargoRemitenteId;

	//Documento
	private String numeroDocumento;
	private LocalDate fechaDocumento;
	private String tipoDocumentoPideId;
	private String asunto;
	private String numeroFolios;

	//Destino
	private String entidadDestinoId;
	private String nombreEntidadDestino;
	private String rucEntidadDestino;
	private String unidadOrganicaDestino;
	private String nombreDestinatario;
	private String cargoDestinatario;

	private String origenDocumento; //INTERNO
	private String origen; //INTEROPERABILIDAD

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoPideId(){return tipoDocumentoPideId;}

	//Entidad externa
	@Mapping("entidadExterna.razonSocial")
	public String getNombreEntidadDestino(){return nombreEntidadDestino;}

	@Mapping("entidadExterna.cargo")
	public String getCargoDestinatario(){return cargoDestinatario;}

	@Mapping("entidadExterna.nombre")
	public String getNombreDestinatario(){return nombreDestinatario;}

	@Mapping("entidadExterna.unidadOrganicaDestino")
	public String getUnidadOrganicaDestino(){return unidadOrganicaDestino;}

	@Mapping("entidadExterna.rucEntidadDestino")
	public String getRucEntidadDestino(){return rucEntidadDestino;}

	@Mapping("entidadPide.id")
	public String getEntidadDestinoId(){return entidadDestinoId;}

	@Mapping("folio")
	public String getNumeroFolios(){return numeroFolios;}

	@Mapping("dependenciaRemitente.id")
	public String getDependenciaRemitenteId(){return dependenciaRemitenteId;}

	@Mapping("cargoRemitente.id")
	public String getCargoRemitenteId(){return cargoRemitenteId;}

	@Mapping("usuarioRemitente.id")
	public String getUsuarioRemitenteId(){return usuarioRemitenteId;}
}
