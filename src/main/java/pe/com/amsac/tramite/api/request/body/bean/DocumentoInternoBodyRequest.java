package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;

@Data
public class DocumentoInternoBodyRequest {

	private String id;
	private String mensajeTramite;
	private String avisoConfidencial;
	private String codigoEtica;
	private String origen;
	private Date fechaDocumento;
	private String tipoOrigen;
	private String numeroDocumento;
	private String siglas;
	private String asunto;

	private String tipoDocumentoId;

	private String dependenciaId;
	private String cargoInternoId;
	private String usuarioId;

	private String razonSocial;
	private String cargo;
	private String nombres;
	private String telefono;
	private String anexo;
	private String celular;
	private String email;
	private String direccion;

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoId(){return tipoDocumentoId;}

	@Mapping("entidadInterna.dependencia.id")
	public String getDependenciaId(){return dependenciaId;}

	@Mapping("entidadInterna.cargo.id")
	public String getCargoInternoId(){return cargoInternoId;}

	@Mapping("entidadInterna.usuario.id")
	public String getUsuarioId(){return usuarioId;}


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

}
