package pe.com.amsac.tramite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.TareasComplementariasMigracionRequest;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.CategoriaEntidad;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.service.CategoriaEntidadService;
import pe.com.amsac.tramite.bs.service.TramiteCommandHandlerService;
import pe.com.amsac.tramite.bs.service.TramiteService;
import pe.com.amsac.tramite.bs.util.EstadoTramiteConstant;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/categorias-entidad")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class CategoriaEntidadController {
	private static final Logger LOGGER = LogManager.getLogger(CategoriaEntidadController.class);

	@Autowired
	private CategoriaEntidadService tramiteService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerCategoriasEntidad() throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			List<CategoriaEntidad> categoriaEntidadList = tramiteService.obtenerCategoriaEntidadActivo();

			List<CategoriaEntidadResponse> obtenerCategoriaEntidadActivoList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(categoriaEntidadList))
				categoriaEntidadList.forEach(x -> obtenerCategoriaEntidadActivoList.add(mapper.map(x,CategoriaEntidadResponse.class)));

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerCategoriaEntidadActivoList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


}
