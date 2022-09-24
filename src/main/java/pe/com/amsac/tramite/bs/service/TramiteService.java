package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.util.*;

@Service
public class TramiteService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

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
		registrarDerivacion(tramite);
		return tramite;

	}

	public void registrarDerivacion(Tramite tramite) throws Exception {
		//Obtener 1er Usuario de Seguridad-UsuarioCargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuario-cargo/recepcion_mesa_partes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjcnE1emEtZjVJTlNEdWlmXzU4V3BKdGhpS2dHZGE4ZnpqUElOODBSa1Q0In0.eyJleHAiOjE2NjI1MTYyMDIsImlhdCI6MTY2MjUxNTkwMiwianRpIjoiMWFhODBlYTktOTc5OC00ZWUzLTk0ZDktYjFlZDg5MTgyNjYyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgxL3JlYWxtcy9hcHAtcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiOWI1ZjRjNjUtYTgzNy00NWU4LTgwMmUtMjk5OWI0OGE3ZWUwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic2FvbmEtaW50ZWdyYXRvciIsInNlc3Npb25fc3RhdGUiOiI2YjI3MTVhYS0xNmM5LTQ3NTAtYjNjZC05YjY3Zjg0ZjIwZDIiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtYXBwLXJlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsic2FvbmEtaW50ZWdyYXRvciI6eyJyb2xlcyI6WyJpbnRlZ3JhdG9yLXJvbGUiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjZiMjcxNWFhLTE2YzktNDc1MC1iM2NkLTliNjdmODRmMjBkMiIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZ1bmN0ZXN0IGZ1bmN0ZXN0IiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZnVuY3Rlc3Q2IiwiZ2l2ZW5fbmFtZSI6ImZ1bmN0ZXN0IiwiZmFtaWx5X25hbWUiOiJmdW5jdGVzdCIsImVtYWlsIjoibWU2QGdtYWlsLmNvbSJ9.EcJHHAHlLcKS8SBBprIVTWi1LkebZnvTzanKNnnm6Rm9jRlTwLfRfNOQi28ovfPcbb0lXy0GzQo9dDswS8OARdL36Wc3rKXHUaZTvcKXOfLlrtY5nqsEhRXx7W8V6OQBVk9JPg29H3FhetOb8mL1TyqD4rxQUsypAktUr37ZdV8yfaL0wij3uin25gsa_pyJEXNcDY0BJChyy5lsOYBiRCyrvFPcEXJIU2XS8t6TX_prCEEY0cUNgdY_qYdrgDVwmcUiWIqv4QaxX8b6Wnxw-gyLlfjxPQLXuO7bDqloe0egGV6-PGBqX5UDw3QVN9tMzJJIAwFIXupKPm-C7d9WUA"));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

		TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest = new TramiteDerivacionBodyRequest();
		tramiteDerivacionBodyRequest.setSecuencia(1);
		tramiteDerivacionBodyRequest.setUsuarioInicio(tramite.getCreatedByUser());
		tramiteDerivacionBodyRequest.setUsuarioFin(((LinkedHashMap)((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("usuario")).get("id").toString());
		tramiteDerivacionBodyRequest.setEstadoInicio("REGISTRADO");
		tramiteDerivacionBodyRequest.setFechaInicio(tramite.getCreatedDate());
		tramiteDerivacionBodyRequest.setTramiteId(tramite.getId());
		tramiteDerivacionBodyRequest.setComentarioInicio("Se inicia registro del Tramite");
		tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyRequest);
	}
	
}
