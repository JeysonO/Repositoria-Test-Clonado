package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.bean.TipoDocumentoRequest;
import pe.com.amsac.tramite.api.request.body.bean.TipoDocumentoBodyRequest;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoTramite;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoJPARepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TipoDocumentoService {

	@Autowired
	private TipoDocumentoJPARepository tipoDocumentoJPARepository;

	@Autowired
	private Mapper mapper;

	/*
	@Autowired
	private MongoTemplate mongoTemplate;
	*/

	public TipoDocumentoTramite registrarTipoDocumento(TipoDocumentoBodyRequest tipoDocumentoBodyRequest) throws Exception {

		TipoDocumentoTramite tipoDocumento = mapper.map(tipoDocumentoBodyRequest, TipoDocumentoTramite.class);
		tipoDocumento.setEstado("A");
		tipoDocumentoJPARepository.save(tipoDocumento);
		return tipoDocumento;

	}

	public List<TipoDocumentoTramite> findByAllTipoDocumento() throws Exception{
		return tipoDocumentoJPARepository.findByEstado("A");
	}

	public List<TipoDocumentoTramite> obtenerTipoDocumento(TipoDocumentoRequest tipoDocumentoRequest) throws Exception{

		/*
		List<String> ambitos = Arrays.asList("A",tipoDocumentoRequest.getTipoAmbito());
		Query query = new Query();
		//query.addCriteria(Criteria.where("estado").is("A"));
		//query.addCriteria(Criteria.where("tipoAmbito").in(ambitos.toArray()));
		Criteria andCriteria = new Criteria();
		Criteria orCriteria = new Criteria();
		List<Criteria> orExpression = new ArrayList<>();
		Criteria expression = new Criteria();
		expression.and("tipoAmbito").is("A");
		orExpression.add(expression);
		expression = new Criteria();
		expression.and("tipoAmbito").is(tipoDocumentoRequest.getTipoAmbito());
		orExpression.add(expression);

		Criteria criteriaAnd = andCriteria.andOperator(Criteria.where("estado").is("A"));
		Criteria criteriaOr = new Criteria();
		criteriaOr = orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()]));

		//andCriteria.andOperator(Criteria.where("estado").is("A"), orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		//andCriteria.andOperator(criteriaAnd, orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		Criteria criteriaGlobal = new Criteria();
		criteriaGlobal.andOperator(criteriaAnd,criteriaOr);
		query.addCriteria(criteriaGlobal);
		//log.info(query.toString());
		//List<TipoDocumento> users = mongoTemplate.find(query, TipoDocumento.class);
		//return tipoDocumentoMongoRepository.findByEstado("A");
		return mongoTemplate.find(query, TipoDocumento.class);
		*/

		List<String> ambitos = new ArrayList<>();
		ambitos.add("A");
		if(!StringUtils.isBlank(tipoDocumentoRequest.getTipoAmbito()))
			ambitos.add(tipoDocumentoRequest.getTipoAmbito());
		if(!StringUtils.isBlank(tipoDocumentoRequest.getTipoAmbito()) && tipoDocumentoRequest.getTipoAmbito().equals("P")){
			ambitos = new ArrayList<>();
			ambitos.add(tipoDocumentoRequest.getTipoAmbito());
		}

		return tipoDocumentoJPARepository.obtenerTipoDocumentoByAmbito(ambitos);
	}
		
	
}
