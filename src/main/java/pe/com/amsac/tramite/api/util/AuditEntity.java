package pe.com.amsac.tramite.api.util;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class AuditEntity implements Serializable{

	 public abstract void setEntityId(Long paramLong);
}
