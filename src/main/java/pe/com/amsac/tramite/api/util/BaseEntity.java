package pe.com.amsac.tramite.api.util;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@MappedSuperclass
//@EntityListeners({ ValidatorEntityListener.class })
//@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

	@Transient
	private Long txByUserId;
	@Transient
	private AuditEntity auditEntity;

	public abstract Serializable getEntityId();

	public Long getTxByUserId() {
		return txByUserId;
	}

	public void setTxByUserId(Long txByUserId) {
		this.txByUserId = txByUserId;
	}

	public AuditEntity getAuditEntity() {
		return auditEntity;
	}

	public void setAuditEntity(AuditEntity auditEntity) {
		this.auditEntity = auditEntity;
	}

}
