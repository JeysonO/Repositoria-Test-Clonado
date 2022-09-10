package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TramiteDerivacionService {

	@Autowired
	private TramiteDerivacionMongoRepository tramiteDerivacionMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	public List<TramiteDerivacion> buscarTramiteDerivacionParams(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> crearExpresion(expression,key,value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<TramiteDerivacion> tramiteList = mongoTemplate.find(andQuery, TramiteDerivacion.class);
		return tramiteList;
	}

	public TramiteDerivacion registrarTramiteDerivacion(TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		TramiteDerivacion tramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		tramiteDerivacion.setEstado("A");
		tramiteDerivacionMongoRepository.save(tramiteDerivacion);
		return tramiteDerivacion;

	}

	public void crearExpresion(Criteria criterio, String key, Object value){
		if (value!=null){
			criterio.and(key).is(value);
		}
	}
	
}
