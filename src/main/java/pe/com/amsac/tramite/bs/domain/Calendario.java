package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Document(collection = "calendario")
public class Calendario {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String fechaDate; // dd/MM/yyyy

	private String feriado; //S o N

	private Integer fechaNumber; //yyyyMMdd

}
