package pe.com.amsac.tramite.api.util;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Interface generica para operaciones customizadas, para un tipo especifico de
 * objeto <T>
 * 
 * @author Edgard Alvino
 * 
 */

@NoRepositoryBean
public interface AmsacMongoRepository<T extends BaseEntity, ID extends Serializable> extends MongoRepository<T, ID> {
	
}
