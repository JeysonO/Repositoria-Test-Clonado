package pe.com.amsac.tramite.api.util;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity<U> extends BaseEntity {

	@CreatedDate
	@Temporal(TIMESTAMP)
	private Date createdDate;
	
	@CreatedBy
	private U createdByUser;
	
	@LastModifiedDate
    @Temporal(TIMESTAMP)
	private Date lastModifiedDate;
	
	@LastModifiedBy
	private U lastModifiedByUser;

	public abstract Serializable getEntityId();

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public U getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(U createdByUser) {
		this.createdByUser = createdByUser;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public U getLastModifiedByUser() {
		return lastModifiedByUser;
	}

	public void setLastModifiedByUser(U lastModifiedByUser) {
		this.lastModifiedByUser = lastModifiedByUser;
	}
	

}
