package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.List;

@Data
public class WrapperDocumentoAdjuntoMigracionBodyRequest {

	private List<DocumentoAdjuntoMigracionBodyRequest> documentosAdjuntos;
}
