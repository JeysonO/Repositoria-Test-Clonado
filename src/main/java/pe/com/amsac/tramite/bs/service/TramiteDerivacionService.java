package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.config.DatosToken;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.AtencionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DerivarTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;

import java.util.*;

@Service
public class TramiteDerivacionService {

	@Autowired
	private TramiteDerivacionMongoRepository tramiteDerivacionMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	public List<TramiteDerivacion> obtenerTramiteDerivacionPendientes() throws Exception {
		//Obtener Usuario
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		DatosToken datosToken = (DatosToken)authentication.getPrincipal();

		String idUser =  datosToken.getIdUser();

		Query query = new Query();
		Criteria criteria = Criteria.where("usuarioFin").is(idUser).and("estado").is("P");
		query.addCriteria(criteria);
		List<TramiteDerivacion> tramitePendienteList = mongoTemplate.find(query, TramiteDerivacion.class);

		return tramitePendienteList;
	}

	public TramiteDerivacion obtenerTramiteDerivacionById(String id) throws Exception {
		TramiteDerivacion obtenerTramiteDerivacionById = tramiteDerivacionMongoRepository.findById(id).get();
		return obtenerTramiteDerivacionById;
	}

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

		TramiteDerivacion registrotramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		registrotramiteDerivacion.setEstado("P");
		tramiteDerivacionMongoRepository.save(registrotramiteDerivacion);
		return registrotramiteDerivacion;

	}

	public TramiteDerivacion registrarDerivacionTramite(DerivarTramiteBodyRequest derivartramiteBodyrequest) throws Exception {

		TramiteDerivacion derivacionTramiteActual = tramiteDerivacionMongoRepository.findById(derivartramiteBodyrequest.getId()).get();
		derivacionTramiteActual.setEstadoFin("DERIVADO");
		derivacionTramiteActual.setFechaFin(new Date());
		derivacionTramiteActual.setProveidoAtencion(derivartramiteBodyrequest.getProveidoAtencion());
		derivacionTramiteActual.setComentarioFin(derivartramiteBodyrequest.getComentarioFin());
		derivacionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(derivacionTramiteActual);

		int sec = obtenerSecuencia(derivacionTramiteActual.getId()).get(0).getSecuencia();

		TramiteDerivacionBodyRequest derivacionTramiteBodyRequest = mapper.map(derivacionTramiteActual, TramiteDerivacionBodyRequest.class);
		derivacionTramiteBodyRequest.setSecuencia(sec+1);
		derivacionTramiteBodyRequest.setUsuarioInicio(derivacionTramiteActual.getUsuarioFin());
		derivacionTramiteBodyRequest.setUsuarioFin(derivartramiteBodyrequest.getUsuarioFin());
		derivacionTramiteBodyRequest.setEstadoInicio(derivacionTramiteActual.getEstadoFin());
		derivacionTramiteBodyRequest.setFechaInicio(new Date());
		derivacionTramiteBodyRequest.setFechaMaximaAtencion(derivartramiteBodyrequest.getFechaMaximaAtencion());
		derivacionTramiteBodyRequest.setComentarioInicio(derivacionTramiteActual.getComentarioFin());
		derivacionTramiteBodyRequest.setId(null);
		derivacionTramiteBodyRequest.setEstadoFin(null);
		derivacionTramiteBodyRequest.setFechaFin(null);
		derivacionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(derivacionTramiteBodyRequest);

		return nuevoDerivacionTramite;
	}

	public TramiteDerivacion registrarRecepcionTramiteDerivacion(String id) throws Exception {

		TramiteDerivacion recepcionTramiteActual = tramiteDerivacionMongoRepository.findById(id).get();
		recepcionTramiteActual.setEstadoFin("RECEPCIONADO");
		recepcionTramiteActual.setFechaFin(new Date());
		recepcionTramiteActual.setComentarioFin("Se recepciona tramite para verificacion");
		recepcionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(recepcionTramiteActual);

		int sec = obtenerSecuencia(recepcionTramiteActual.getId()).get(0).getSecuencia();

		TramiteDerivacionBodyRequest recepcionTramiteBodyRequest = mapper.map(recepcionTramiteActual, TramiteDerivacionBodyRequest.class);
		recepcionTramiteBodyRequest.setSecuencia(sec+1);
		recepcionTramiteBodyRequest.setUsuarioInicio(recepcionTramiteActual.getUsuarioFin());
		recepcionTramiteBodyRequest.setUsuarioFin(recepcionTramiteActual.getUsuarioFin());
		recepcionTramiteBodyRequest.setEstadoInicio(recepcionTramiteActual.getEstadoFin());
		recepcionTramiteBodyRequest.setFechaInicio(new Date());
		recepcionTramiteBodyRequest.setComentarioInicio(recepcionTramiteActual.getComentarioFin());
		recepcionTramiteBodyRequest.setId(null);
		recepcionTramiteBodyRequest.setEstadoFin(null);
		recepcionTramiteBodyRequest.setFechaFin(null);
		recepcionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoRecepcionTramite = registrarTramiteDerivacion(recepcionTramiteBodyRequest);

		return nuevoRecepcionTramite;
	}

	public TramiteDerivacion registrarAtencionTramiteDerivacion(AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		TramiteDerivacion atenderTramiteDerivacion = tramiteDerivacionMongoRepository.findById(atenciontramiteDerivacionBodyrequest.getId()).get();
		atenderTramiteDerivacion.setEstadoFin(atenciontramiteDerivacionBodyrequest.getEstadoFin());
		atenderTramiteDerivacion.setFechaFin(new Date());
		atenderTramiteDerivacion.setComentarioFin(atenciontramiteDerivacionBodyrequest.getComentarioFin());
		atenderTramiteDerivacion.setEstado("A");
		tramiteDerivacionMongoRepository.save(atenderTramiteDerivacion);

		return atenderTramiteDerivacion;
	}

	public List<TramiteDerivacion> obtenerSecuencia(String id){
		Query query = new Query();
		Criteria criteria = Criteria.where("id").is(id).and("estado").is("A");
		query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("secuencia")
		));
		List<TramiteDerivacion> tramiteList = mongoTemplate.find(query, TramiteDerivacion.class);
		return tramiteList;
	}
}
