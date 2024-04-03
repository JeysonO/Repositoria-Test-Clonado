package pe.com.amsac.tramite.api.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface CustomRepository<T extends BaseEntity, ID extends Serializable> {

    public List<T> findByParams(Map<String, Object> parameters, String orderByClause, String groupByClause,
                                int pageNumber, int pageSize) throws InternalErrorException;

    public Integer getRecordCount(String selectClause, String whereClause, String groupByClause,
                                  Map<String, Object> params) throws InternalErrorException;

    public List<Map<String, Object>> executeNativeQuery(String selectClause, Map<String, Object> params,
                                                        String[] returnProperties, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException;

    public List<Object[]> executeNativeQuery(String queryString, Map<String, Object> namedParams, boolean cacheable,
                                             int pageNumber, int pageSize) throws InternalErrorException;
}
