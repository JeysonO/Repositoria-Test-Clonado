package pe.com.amsac.tramite.api.request.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentoAdjuntoPideRequest {

	@NotBlank
	private String cuo;

	@NotBlank
	private String nombreArchivo;

}
