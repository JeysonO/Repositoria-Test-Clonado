package pe.com.amsac.tramite.api.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BitallJPARepository<T extends BaseEntity, ID extends Serializable> extends JpaRepository<T, ID> {

}
