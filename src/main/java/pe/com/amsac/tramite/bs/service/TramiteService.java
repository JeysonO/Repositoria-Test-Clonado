package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TramiteService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	public List<Tramite> buscarTramiteParams(TramiteRequest tramiteRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
			Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		Criteria expression = new Criteria();
		parameters.values().removeIf(Objects::isNull);
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
		return tramiteList;
	}

	public Tramite registrarTramite(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		Tramite tramite = mapper.map(tramiteBodyRequest,Tramite.class);
		tramite.setEstado("A");
		tramiteMongoRepository.save(tramite);
		return tramite;

	}
		
	
}
