package pe.com.amsac.tramite.api.util;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseCRUDService<T extends BaseEntity, ID extends Serializable> {
	/**
	 * 
	 * Metodo que guarda una entidad y retorna una instancia del objeto para futuras
	 * operaciones.
	 *
	 * @param {@literal entity} no puede ser null.
	 * @return la entidad guardada.
	 * 
	 */
	T save(@NonNull T entity) throws Exception;

	/**
	 * Registra todas las entidades.
	 *
	 * @param las entidades no deben ser {@literal null}.
	 * @return las entidades guardadas, nunca serán {@literal null}. El listado retornado
	 *         {@literal Iterable}debe tener el mismo tamaño del listado
	 *         {@literal Iterable} pasado como argumento.
	 *         
	 */
	List<T> saveAll(@NonNull List<T> entities) throws Exception;

	/**
	 * Actualiza la entidad enviada.
	 *
	 * @param entidad que será actualizada.
	 *         
	 */
	void update(@NonNull T entity) throws Exception;
	
	/**
	 * Actualiza el listado de entidades.
	 *
	 * @param entidades que será actualizadas.
	 *         
	 */
	void updateAll(@NonNull List<T> entities) throws Exception;
	
	
	/**
	 * Elimina la entidad envidad como parametro.
	 *
	 * @param entity que no debe ser {@literal null}.
	 * 
	 */
	void delete(T entity) throws Exception;

	/**
	 * Elimina las entidades enviadas.
	 *
	 * @param entities no deben ser {@literal null} tampoco deben tener elementos {@literal null}
	 * 
	 */
	void deleteAll(List<T> entities) throws Exception;

	/**
	 * Retorna una entidad por el ID.
	 *
	 * @param id no puede ser {@literal null}.
	 * @return la entidad con el id solicitado o {@literal Optional#empty()} si no
	 *         es encontrado.
	 * 
	 */
	Optional<T> findById(@NonNull ID id) throws Exception;

	/**
	 * Retorna todas las entidades de tipo {@code T} con los IDs enviados. El orden
	 * de los elementos no es garantizado que sea en el mismo de los IDs como
	 * parametro.
	 *
	 * @param ids no debe ser o contener algun valor {@literal null}.
	 * @return garantizado de no ser {@literal null}. La cantidad de entidades
	 *         retornadas podrá ser igual o menos de los {@literal ids} enviados.
	 * 
	 */
	List<T> findAllById(List<ID> ids) throws Exception;

	
	/**
	 * Retorna todas las entidades de tipo {@code T} con los parametros enviados.
	 *
	 * @param selectClause sentencia select en lenguaje hql.
	 * @param whereClause sentencia where.
	 * @param groupByClause sentencia groupby.
	 * @param orderByClause sentencia order by.
	 * @param params parametros para la consulta.
	 * @param cacheable valor booleano qeu indica si la sentencia va a ser cacheable.
	 * @param pageNumber numero de la pagina para la consulta.
	 * @param pageSize tamaño elementos por pagina.
	 * @return listado de entidades que cumplen con los filtros enviados.
	 * 
	 */
	/*
	public List<Object[]> executeNativeQuery(String queryString, Map<String, Object> namedParams, boolean cacheable,
			int pageNumber, int pageSize) throws InternalErrorException;
	*/
	/**
	 * Tamaño de elementos de acuerdo a los filtros enviados.
	 *
	 * @param selectClause sentencia select en lenguaje hql.
	 * @param whereClause sentencia where.
	 * @param groupByClause sentencia groupby.
	 * @param params parametros para la consulta.
	 * @return cantidad de registros que cumplen con los filtros.
	 * 
	 */
	/*
	Integer getRecordCount(String selectClause, String whereClause, String groupByClause,
			Map<String, Object> params) throws InternalErrorException;
	*/
	/**
	 * Metodo que permite ejecutar querys nativos.
	 *
	 * @param selectClause sentencia select en lenguaje hql.
	 * @param params parametros para la consulta.
	 * @param returnProperties campos que tendra el mapa de respuesta.
	 * @param cacheable valor booleano que indica si la sentencia va a ser cacheable.
	 * @param pageNumber numero de la pagina para la consulta.
	 * @param pageSize tamaño elementos por pagina.
	 * @return listado de mapas que contiene la informción encontrada con los filtros.
	 * 
	 */
	/*
	List<Map<String, Object>> executeNativeQuery(String selectClause, Map<String, Object> params,
			String[] returnProperties, boolean cacheable, int pageNumber, int pageSize) throws InternalErrorException;
	*/
	/**
	 * Metodo que permite ejecutar querys nativos.
	 *
	 * @param parameters parametros para la consulta.
	 * @param orderBy campos que seran parte del orderby.
	 * @param groupByClause campos que seran parte del groupby.
	 * @param pageNumber numero de la pagina para la consulta.
	 * @param pageSize tamaño elementos por pagina.
	 * @return listado entidades que cumplen con los filtros enviados.
	 * 
	 */
	/*
	List<T> findByParams(Map<String, Object> parameters, String orderByClause, String groupByClause,
			int pageNumber, int pageSize) throws InternalErrorException;
	*/
	
	//Integer obtenerCantidadTotal(Map<String, Object> parameters);
	
	List<T> findAll(Sort sort) throws Exception;
	
	/**
	 * Elimina la por Id de la entidad.
	 *
	 * @param id que no debe ser {@literal null}.
	 * 
	 */
	void deleteById(ID id) throws ServiceException, Exception;
	
}
