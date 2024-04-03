package pe.com.amsac.tramite.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJPARepository<T extends BaseEntity, ID extends Serializable>
        implements CustomRepository<T, ID> {

    private static final Logger logger = LogManager.getLogger(CustomJPARepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public String buildWhereClause(Map<String, Object> parameters) throws InternalErrorException {
        return "";
    }

    public String buildOrderByClause(String orderBy) throws InternalErrorException {
        return "";
    }

    public List<T> findByParams(Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
                                int pageSize) throws InternalErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    protected List<T> findByQuery(String selectClause, String whereClause, String groupByClause, String orderByClause,
                                  Map<String, Object> params, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException {
        logger.info(":: findByQuery :: Initializing find operation...");
        if (BeanUtils.isNullOrEmpty(selectClause)) {
            throw new InternalErrorException("Invalid select clause!");
        }
        String queryString = selectClause + (!BeanUtils.isNullOrEmpty(whereClause) ? " where " + whereClause : "")
                + (!BeanUtils.isNullOrEmpty(groupByClause) ? " group by " + groupByClause : "")
                + (!BeanUtils.isNullOrEmpty(orderByClause) ? " order by " + orderByClause : "");

        logger.info(":: findByQuery :: Initializing find operation...");
        return (List<T>) executeQuery(queryString, params, cacheable, pageNumber, pageSize);
    }

    public Integer getRecordCount(String selectClause, String whereClause, String groupByClause,
                                  Map<String, Object> params) throws InternalErrorException {
        logger.info(":: getRecordCount :: Initializing find operation...");
        if (!selectClause.startsWith("select count(")) {
            throw new InternalErrorException("Invalid query statement!");
        }
        String queryString = selectClause + (!BeanUtils.isNullOrEmpty(whereClause) ? " where " + whereClause : "")
                + (!BeanUtils.isNullOrEmpty(groupByClause) ? " group by " + groupByClause : "");

        List<Long> result = (List<Long>) executeQuery(queryString, params, false, 0, 0);
        if ((result == null) || (result.size() == 0)) {
            return 0;
        }
        logger.info(":: getRecordCount :: Initializing find operation...");
        return ((Long) result.get(0)).intValue();
    }

    public List<Map<String, Object>> executeNativeQuery(String selectClause, Map<String, Object> params,
                                                        String[] returnProperties, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException {
        logger.info(":: executeNativeQuery :: Initializing execute operation...");
        List<Map<String, Object>> returnList = null;
        try {
            List<Object[]> list = executeNativeQuery(selectClause, params, cacheable, pageNumber, pageSize);
            if ((list != null) && (list.size() > 0)) {
                returnList = new ArrayList();
            }
            for (Object[] object : list) {
                Map<String, Object> returnElement = new HashMap();
                int i = 0;
                for (String property : returnProperties) {
                    returnElement.put(property, object[(i++)]);
                }
                returnList.add(returnElement);
            }
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
        logger.info(":: executeNativeQuery :: Initializing execute operation...");
        return returnList;
    }

    protected List<?> executeQuery(String queryString, Map<String, Object> namedParams, boolean cacheable, int pageNumber,
                                   int pageSize) throws InternalErrorException {
        logger.info(":: executeQuery :: queryString=" + queryString + ", namedParams=" + namedParams + ", cacheable="
                + cacheable + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize);

        Query query = this.entityManager.createQuery(queryString);
        String regex = ":\\w+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(queryString);
        List<String> nombreParametros = new ArrayList<String>();
        while (matcher.find()) {
            nombreParametros.add(matcher.group().substring(1));
        }

        if (namedParams != null) {
            for(String parametro : nombreParametros) {
                for (String paramName : namedParams.keySet()) {
                    if(parametro.equals(paramName)) {
                        query.setParameter(paramName, namedParams.get(paramName));
                    }
                }
            }

        }
        if (cacheable) {
            query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
        }
        if (pageNumber > 0) {
            int startIndex = (pageNumber - 1) * pageSize;
            query.setFirstResult(startIndex);
        }
        if (pageSize > 0) {
            int maxResult = pageSize;
            query.setMaxResults(maxResult);
        }
        try {
            return query.getResultList();
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    public List<Object[]> executeNativeQuery(String queryString, Map<String, Object> namedParams, boolean cacheable,
                                             int pageNumber, int pageSize) throws InternalErrorException {
        logger.info(":: executeNativeQuery :: Initializing execute operation...");
        Query query = this.entityManager.createNativeQuery(queryString);

        String regex = ":\\w+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(queryString);
        List<String> nombreParametros = new ArrayList<String>();
        while (matcher.find()) {
            nombreParametros.add(matcher.group().substring(1));
        }

        if (namedParams != null) {
            for(String parametro : nombreParametros) {
                for (String paramName : namedParams.keySet()) {
                    if(parametro.equals(paramName)) {
                        query.setParameter(paramName, namedParams.get(paramName));
                    }
                }
            }

        }
		/*
		if (namedParams != null) {
			for (String paramName : namedParams.keySet()) {
				query.setParameter(paramName, namedParams.get(paramName));
			}
		}
		*/
        if (cacheable) {
            query.setHint("org.hibernate.cacheable", Boolean.valueOf(true));
        }
        if (pageNumber > 0) {
            int startIndex = (pageNumber - 1) * pageSize;
            query.setFirstResult(startIndex);
        }
        if (pageSize > 0) {
            int maxResult = pageSize;
            query.setMaxResults(maxResult);
        }
        logger.info(":: executeNativeQuery :: Initializing execute operation...");
        return query.getResultList();
    }

}
