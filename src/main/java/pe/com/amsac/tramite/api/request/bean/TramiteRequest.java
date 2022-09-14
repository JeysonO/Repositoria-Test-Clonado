package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class TramiteRequest {

	private String createdByUser;
	private String estado;
	private String id;

}
