package pe.com.amsac.tramite.api.util;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomAPIAssembler<T extends BaseEntity, R extends Object, Q extends AmsacRequestBean, B extends AmsacBodyRequestBean> {

	@Autowired
	protected Mapper mapper;
	
	private Class<R> responseModel;

	public CustomAPIAssembler(Class<R> responseModel) {
		this.responseModel = responseModel;		
    }
    	
	public R toModel(T entity) {
		R response = (R)mapper.map(entity, responseModel);
		return response;
	}
	
	public Map toMap(Q request) {
		Map<String, Object> mapa = mapper.map(request, Map.class);
		return mapa;
	}
	
	public List<R> toModel(List<T> naveEntitys){
		List<R> responseList = new ArrayList<R>();
		naveEntitys.forEach(entity -> responseList.add(this.toModel(entity)));
		
		return responseList;
	}
	
	public T toEntity(B bodyRequest, Class<T> clase) {
		T entity = mapper.map(bodyRequest, clase);
		return entity;
	} 
	
	public T toEntity(Q request, Class<T> clase) {
		T entity = mapper.map(request, clase);
		return entity;
	} 
	
}
