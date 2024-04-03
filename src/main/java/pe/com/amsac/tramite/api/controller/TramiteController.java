package pe.com.amsac.tramite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.TareasComplementariasMigracionRequest;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteMigracionBatchBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteMigracionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteMigracion;
import pe.com.amsac.tramite.bs.service.TramiteService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tramites")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class TramiteController {
	private static final Logger LOGGER = LogManager.getLogger(TramiteController.class);

	@Autowired
	private TramiteService tramiteService;

	@Autowired
	private Mapper mapper;

	@Autowired
	private SecurityHelper securityHelper;


	@GetMapping
	public ResponseEntity<CommonResponse> buscarTramiteParams(@Valid TramiteRequest tramiteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {
			/*
			List<Tramite> listaTramite = tramiteService.buscarTramiteParams(tramiteRequest);
			List<TramiteResponse> obtenerTramiteList =  new ArrayList<>();
			TramiteResponse tramiteResponse = null;
			for (Tramite temp : listaTramite) {
				tramiteResponse = mapper.map(temp, TramiteResponse.class);
				obtenerTramiteList.add(tramiteResponse);
			}
			*/
			List<TramiteResponse> obtenerTramiteList =  tramiteService.buscarTramiteWithParams(tramiteRequest);

			/*
			Map<String, Object> param = new HashMap<>();
			param.put("totalRegistrosFiltros", tramiteService.totalRegistros(tramiteRequest));
			Meta meta = new Meta(EstadoRespuestaConstant.RESULTADO_OK, null);
			meta.setAtributos(param);
			*/

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	//Para busqueda de los tramites iniciados por una persona
	@GetMapping("/tramites-by-usuario-id")
	public ResponseEntity<CommonResponse> buscarTramiteByUsuarioId(@Valid TramiteRequest tramiteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		String usuarioId = securityHelper.obtenerUserIdSession();
		List<Tramite> listaTramite = tramiteService.buscarTramiteParamsByUsuarioId(usuarioId, tramiteRequest);
		List<TramiteResponse> obtenerTramiteList =  new ArrayList<>();
		TramiteResponse tramiteResponse = null;
		for (Tramite temp : listaTramite) {
			tramiteResponse = mapper.map(temp, TramiteResponse.class);
			obtenerTramiteList.add(tramiteResponse);
		}
		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteList).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/exportar-reporte")
	//public ResponseEntity<Resource> downloadFileEscala(@Valid TramiteRequest tramiteRequest, HttpServletResponse response) throws Exception {
	public ResponseEntity<Resource> downloadFileTramite(@Valid TramiteRequest tramiteRequest) throws Exception {

			JasperPrint jasperPrint = null;
			//response.setContentType("application/pdf");
			//response.setHeader("Content-Disposition", String.format("attachment; filename=\"tramite.pdf\""));

			//Directorio donde se guardará una copia fisica
			//final String reportPdf = "C:/Users/sayhu/Downloads/reporteTramite.pdf";
			//File file = File.createTempFile("abc_", ".pdf");
			jasperPrint = tramiteService.exportPdfFile(tramiteRequest);
			//Guardamos en el directorio
			//JasperExportManager.exportReportToPdfFile(jasperPrint, reportPdf);

			byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);
			//File file = File.createTempFile("abc_", ".pdf");
			Resource resource = new ByteArrayResource(reporte);//new UrlResource("file:" + fulFilePath);
			//Enviamos el Stream al Cliente
			//OutputStream out = response.getOutputStream();
			//JasperExportManager.exportReportToPdfStream(jasperPrint, out);

			//Resource resource = JasperExportManager.exportReportToPdfStream(jasperPrint, out);

			//Resource resource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);

			String contentType = "application/pdf";

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tramite-reporte.pdf")
					.body(resource);

	}

	@PostMapping
	public ResponseEntity<CommonResponse> registrarTramites(@Valid @RequestBody TramiteBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			Tramite tramite = tramiteService.registrarTramite(tramiteBodyrequest);

			TramiteResponse tramiteResponse = mapper.map(tramite, TramiteResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/registrar-tramite-completo")
	public ResponseEntity<CommonResponse> registrarTramiteCompleto(@Valid @RequestBody TramiteBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			ObjectMapper objectMapper = new ObjectMapper();
			log.info("Body para registrar tramite:"+objectMapper.writeValueAsString(tramiteBodyrequest));

			Tramite tramite = tramiteService.registrarTramite(tramiteBodyrequest);

			TramiteResponse tramiteResponse = mapper.map(tramite, TramiteResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes(),se.getAtributos())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/enviar-acuse-tramite/{tramiteId}")
	public ResponseEntity<CommonResponse> enviarAcusePorTramiteId(@PathVariable String tramiteId) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {
			tramiteService.enviarAcusePorTramiteId(tramiteId);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes(),se.getAtributos())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	/*
	@PostMapping("/registrar-tramite-migracion")
	public ResponseEntity<CommonResponse> registrarTramiteExternoMigracion(@Valid @RequestBody TramiteMigracionBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteMigracion tramite = tramiteService.registrarTramiteMigracion(tramiteBodyrequest);

			TramiteResponse tramiteResponse = new TramiteResponse(); //mapper.map(tramite, TramiteResponse.class);
			tramiteResponse.setId(tramite.getId());

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes(),se.getAtributos())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
	*/

	@GetMapping("/record-count")
	public ResponseEntity<CommonResponse> cantidadRegistrosBuscarTramiteParams(@Valid TramiteRequest tramiteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			int cantidadRegistros = tramiteService.totalRegistros(tramiteRequest);

			Map<String, Object> param = new HashMap<>();
			param.put("cantidadRegistros", cantidadRegistros);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


	//Para busqueda de los tramites iniciados por una persona
	@GetMapping("/tramites-by-usuario-id/record-count")
	public ResponseEntity<CommonResponse> cantidadRegistrosBuscarTramiteByUsuarioId(@Valid TramiteRequest tramiteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		String usuarioId = securityHelper.obtenerUserIdSession();

		tramiteRequest.setCreatedByUser(usuarioId);
		int cantidadRegistros = tramiteService.totalRegistros(tramiteRequest);

		Map<String, Object> param = new HashMap<>();
		param.put("cantidadRegistros", cantidadRegistros);

		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PutMapping("/actualizar-tramite-migracion")
	public ResponseEntity<CommonResponse> actualizarDependenciaUsuarioCreacionTramite(@Valid @RequestBody TramiteMigracionBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		tramiteService.actualizarDependenciaUsuarioCreacionTramite(tramiteBodyrequest.getId(), tramiteBodyrequest.getDependenciaUsuarioCreacionId());

		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@GetMapping("/indicadores-dashboard-usuario")
	public ResponseEntity<CommonResponse> obtenerIndicadoresDashboardByTokenUsuario() throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		Map<String,Object> mapaDashboardUsuario = tramiteService.obtenerIndicadoresDashboardByTokenUsuario();

		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(mapaDashboardUsuario).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/actividades-complementarias-migracion")
	public ResponseEntity<CommonResponse> ejecutgarActividadesComplementarias(@Valid @RequestBody TareasComplementariasMigracionRequest tareasComplementariasMigracionRequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		Map mapa = tramiteService.ejecutarActividadesComplementariasMigracion(tareasComplementariasMigracionRequest);

		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(mapa).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	/*
	@PostMapping("/registrar-tramite-carga-batch")
	public ResponseEntity<CommonResponse> registrarTramiteCargBatch(@Valid @RequestBody TramiteMigracionBatchBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteMigracion tramite = tramiteService.registrarTramiteCargBatch(tramiteBodyrequest);

			TramiteResponse tramiteResponse = new TramiteResponse(); //mapper.map(tramite, TramiteResponse.class);
			tramiteResponse.setId(tramite.getId());

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes(),se.getAtributos())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
	*/


}
