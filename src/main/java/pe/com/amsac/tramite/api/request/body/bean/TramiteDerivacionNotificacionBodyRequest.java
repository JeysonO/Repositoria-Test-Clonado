package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Builder;
import lombok.Data;
import org.dozer.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class TramiteDerivacionNotificacionBodyRequest {

	private String tramiteDerivacionId;
	private String email;
	private String mensaje;
	private MultipartFile file;

}
