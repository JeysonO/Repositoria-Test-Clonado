package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.AtencionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;

import java.time.LocalDateTime;
import java.util.*;

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
		parameters.values().removeIf(Objects::isNull);
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<TramiteDerivacion> tramiteList = mongoTemplate.find(andQuery, TramiteDerivacion.class);
		return tramiteList;
	}

	public TramiteDerivacion registrarTramiteDerivacion(TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		TramiteDerivacion tramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		tramiteDerivacion.setEstado("P");
		tramiteDerivacionMongoRepository.save(tramiteDerivacion);
		return tramiteDerivacion;

	}

	public TramiteDerivacion registrarAtencionTramiteDerivacion(AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		TramiteDerivacion atencionTramiteDerivacion = mapper.map(atenciontramiteDerivacionBodyrequest, TramiteDerivacion.class);
		atencionTramiteDerivacion.setFechaFin(new Date());
		atencionTramiteDerivacion.setEstado("A");
		tramiteDerivacionMongoRepository.save(atencionTramiteDerivacion);

		return atencionTramiteDerivacion;
	}

}
