package pe.com.amsac.tramite.api.util;

import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCRUDMongoBaseService<T extends BaseEntity, ID extends Serializable> implements BaseCRUDService<T, ID> {

    public abstract AmsacMongoRepository<T, ID> getGenericRepositoryCRUD();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public T save(T entity) throws Exception {
        return getGenericRepositoryCRUD().save(entity);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<T> saveAll(List<T> entities) throws Exception {
        return getGenericRepositoryCRUD().saveAll(entities);
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(T entity) throws Exception {
        getGenericRepositoryCRUD().save(entity);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateAll(List<T> entities) throws Exception {
        for(T entity : entities ) {
            update(entity);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void delete(T entity) throws Exception {
        getGenericRepositoryCRUD().delete(entity);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteAll(List<T> entities) throws Exception {
        getGenericRepositoryCRUD().deleteAll(entities);
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) throws Exception {
        return getGenericRepositoryCRUD().findById(id);
    }

    @Transactional(readOnly = true)
    public List<T> findAllById(List<ID> ids) throws Exception {
        return (List<T>) getGenericRepositoryCRUD().findAllById(ids);
    }

    @Override
    public List<T> findAll(Sort sort) throws Exception {
        return getGenericRepositoryCRUD().findAll(sort);
    }

    @Override
    public void deleteById(ID id) throws ServiceException, Exception {
        getGenericRepositoryCRUD().deleteById(id);
    }



	/*
	@Transactional(readOnly = true)
	public List<T> findByQuery(String selectClause, String whereClause, String groupByClause, String orderByClause,
			Map<String, Object> params, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException {
		return getGenericRepositoryCRUD().findByQuery(selectClause, whereClause, groupByClause, orderByClause, params, cacheable, pageNumber, pageSize);
	}

	@Transactional(readOnly = true)
	public Integer getRecordCount(String selectClause, String whereClause, String groupByClause,
			Map<String, Object> params) throws InternalErrorException {
		return getGenericRepositoryCRUD().getRecordCount(selectClause, whereClause, groupByClause, params);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> executeNativeQuery(String selectClause, Map<String, Object> params,
			String[] returnProperties, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException {
		return getGenericRepositoryCRUD().executeNativeQuery(selectClause, params, returnProperties, cacheable, pageNumber, pageSize);
	}

	@Transactional(readOnly = true)
	public List<T> findByParams(Map<String, Object> parameters, String orderBy, int pageNumber, int pageSize)
			throws InternalErrorException {
		return getGenericRepositoryCRUD().findByParams(parameters, orderBy, pageNumber, pageSize);
	}
	*/
}
