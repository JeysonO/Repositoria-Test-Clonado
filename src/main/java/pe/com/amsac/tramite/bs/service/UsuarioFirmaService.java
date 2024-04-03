package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.UsuarioFirmaRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.response.bean.UsuarioDTOResponse;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaJPARepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class UsuarioFirmaService {

	@Autowired
	private Mapper mapper;

	@Autowired
	private UsuarioFirmaJPARepository usuarioFirmaJPARepository;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	@Autowired
	private Environment env;

	/*
	@Autowired
	private MongoTemplate mongoTemplate;
	*/

	public UsuarioFirma obtenerUsuarioFirmaByUsuarioId(String usuarioId) throws Exception {

		List<UsuarioFirma> usuarioFirmaList = usuarioFirmaJPARepository.obtenerUsuarioFirmaByUsuarioId(usuarioId);

		if(CollectionUtils.isEmpty(usuarioFirmaList)){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","Usuario no tiene registro habilitado para firmar"));
			throw new ServiceException(mensajes);
		}

		return usuarioFirmaList.get(0);

	}

	public UsuarioFirma registrarUsuarioFirma(UsuarioFirmaBodyRequest usuarioFirmaBodyrequest) throws Exception {

		//Se hacen validaciones de los datos del usuario a registrar
		List<Mensaje> mensajesError = validarDatosUsuarioFirma(usuarioFirmaBodyrequest);
		if(!org.apache.commons.collections.CollectionUtils.isEmpty(mensajesError)){
			throw new ServiceException(mensajesError);
		}

		UsuarioFirma usuarioFirma = mapper.map(usuarioFirmaBodyrequest,UsuarioFirma.class);
		usuarioFirmaJPARepository.save(usuarioFirma);
		return usuarioFirma;

	}

	public List<UsuarioFirma> obtenerUsuarioFirma(UsuarioFirmaRequest usuarioFirmaRequest) throws Exception {

		/*
		Query andQuery = new Query();
		List<Criteria> orExpression =  new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Criteria criteriaOr = null;
		Criteria criteriaGlobal = new Criteria();
		Criteria andCriteria = new Criteria();
		List<UsuarioFirma> usuarioFirmaList = new ArrayList<>();

		Criteria expression = new Criteria();
		expression.and("estado").is("A");

		if(!StringUtils.isBlank(usuarioFirmaRequest.getNombre())){
			List<String> usuariosIds = obtenerUsuarioId(usuarioFirmaRequest.getNombre());
			if(!CollectionUtils.isEmpty(usuariosIds)){
				for (String usuarioId : usuariosIds) {
					Criteria expressionTMP = new Criteria();
					expressionTMP.and("usuario.id").is(usuarioId);
					orExpression.add(expressionTMP);
				}
				criteriaOr = orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()]));
			}else{
				return usuarioFirmaList;
			}

		}

		Criteria criteriaAnd = andCriteria.andOperator(expression);

		if(criteriaOr!=null)
			criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd,criteriaOr);
		else
			criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd);
		andQuery.addCriteria(criteriaGlobal);

		if(usuarioFirmaRequest.getPageNumber()>=0 && usuarioFirmaRequest.getPageSize()>0){
			Pageable pageable = PageRequest.of(usuarioFirmaRequest.getPageNumber(), usuarioFirmaRequest.getPageSize());
			andQuery.with(pageable);
		}

		usuarioFirmaList = mongoTemplate.find(andQuery, UsuarioFirma.class);

		return usuarioFirmaList; //usuarioFirmaMongoRepository.findByEstado("A");
		*/

		return usuarioFirmaJPARepository.obtenerUsuarioFirmaByNombreUsuario(usuarioFirmaRequest.getNombre(), PageRequest.of(usuarioFirmaRequest.getPageNumber(), usuarioFirmaRequest.getPageSize()));

	}

	public void eliminarUsuarioFirmaById(String usuarioFirmaId) throws Exception {

		//Obtenemos los logos para este usuario firma, con la finalidad de eliminar cada registro
		List<UsuarioFirmaLogo> usuarioFirmaLogoList = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaId);
		if(!CollectionUtils.isEmpty(usuarioFirmaLogoList)){
			for(UsuarioFirmaLogo usuarioFirmaLogo:usuarioFirmaLogoList){
				usuarioFirmaLogoService.eliminarUsuarioFirmaLogoById(usuarioFirmaLogo.getId());
			}
		}

		usuarioFirmaJPARepository.deleteById(usuarioFirmaId);

	}

	public List<Mensaje> validarDatosUsuarioFirma(UsuarioFirmaBodyRequest usuarioFirmaBodyrequest){

		List<Mensaje> mensajes = new ArrayList<>();
		if(!CollectionUtils.isEmpty(usuarioFirmaJPARepository.obtenerUsuarioFirmaByUsuarioId(usuarioFirmaBodyrequest.getUsuarioId()))){
			mensajes.add(new Mensaje("E001","ERROR","Ya existe otro registro de firma para el usuario ingresado"));
		}

		return mensajes;

	}

	public UsuarioFirma obtenerUsuarioFirmaByUsuario() throws Exception {

		String usuarioId = securityHelper.obtenerUserIdSession();

		List<UsuarioFirma> usuarioFirmaList = usuarioFirmaJPARepository.obtenerUsuarioFirmaByUsuarioId(usuarioId);

		if(CollectionUtils.isEmpty(usuarioFirmaList)){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","Usuario no tiene registro habilitado para firmar"));
			throw new ServiceException(mensajes);
		}

		return usuarioFirmaList.get(0);

	}

	public List<String> obtenerUsuarioId(String nombre){

		List<String> usuarioIdList = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios?nombre="+nombre;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		//List<UsuarioResponse> usuarioResponseList = new ArrayList<>();//mapper.map((List<HashMap<Object, Object>>)response.getBody().getData(),List.class);
		List listaObject = (ArrayList)response.getBody().getData();
		if(!CollectionUtils.isEmpty(listaObject)){
			listaObject.stream().forEach(x -> usuarioIdList.add(mapper.map((LinkedHashMap)x, UsuarioDTOResponse.class).getId()));
		}

		return usuarioIdList;

	}

	public int totalRegistros(UsuarioFirmaRequest usuarioFirmaRequest) throws Exception {

		/*
		Query andQuery = new Query();
		List<Criteria> andExpression =  new ArrayList<>();
		List<Criteria> listOrCriteria =  new ArrayList<>();
		List<Criteria> orExpression =  new ArrayList<>();
		Criteria orCriteria = new Criteria();
		Criteria criteriaOr = null;
		Criteria criteriaGlobal = new Criteria();
		Criteria andCriteria = new Criteria();

		Criteria expression = new Criteria();
		expression.and("estado").is("A");

		if(!StringUtils.isBlank(usuarioFirmaRequest.getNombre())){
			List<String> usuariosIds = obtenerUsuarioId(usuarioFirmaRequest.getNombre());
			if(!CollectionUtils.isEmpty(usuariosIds)){
				for (String usuarioId : usuariosIds) {
					Criteria expressionTMP = new Criteria();
					expressionTMP.and("usuario.id").is(usuarioId);
					orExpression.add(expressionTMP);
				}
			}
			criteriaOr = orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()]));
		}

		Criteria criteriaAnd = andCriteria.andOperator(expression);

		criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd,criteriaOr);
		andQuery.addCriteria(criteriaGlobal);

		if(usuarioFirmaRequest.getPageNumber()>=0 && usuarioFirmaRequest.getPageSize()>0){
			Pageable pageable = PageRequest.of(usuarioFirmaRequest.getPageNumber(), usuarioFirmaRequest.getPageSize());
			andQuery.with(pageable);
		}

		long cantidadRegistro = mongoTemplate.count(andQuery, UsuarioFirma.class);

		return (int)cantidadRegistro;
		*/

		List<UsuarioFirma> usuarioFirmaList = usuarioFirmaJPARepository.obtenerUsuarioFirmaByNombreUsuario(usuarioFirmaRequest.getNombre(), PageRequest.of(usuarioFirmaRequest.getPageNumber(), usuarioFirmaRequest.getPageSize()));

		return CollectionUtils.isEmpty(usuarioFirmaList)?0:usuarioFirmaList.size();

	}

	public UsuarioFirma actualizarUsuarioFirma(UsuarioFirmaBodyRequest usuarioFirmaBodyrequest) throws Exception {

		UsuarioFirma usuarioFirma = usuarioFirmaJPARepository.findById(usuarioFirmaBodyrequest.getId()).get();
		usuarioFirma.setPasswordServicioFirma(usuarioFirmaBodyrequest.getPasswordServicioFirma());
		usuarioFirma.setUsernameServicioFirma(usuarioFirmaBodyrequest.getUsernameServicioFirma());
		usuarioFirma.setSiglaFirma(usuarioFirmaBodyrequest.getSiglaFirma());
		usuarioFirmaJPARepository.save(usuarioFirma);
		return usuarioFirma;

	}

}
