package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.KeycloakProperties;
import pe.com.amsac.tramite.api.request.bean.LoginRequest;
import pe.com.amsac.tramite.api.response.bean.AuthenticationKeycloakResponse;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.service.FirmaDocumentoService;

import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RestController
@RequestMapping("/firma-callback")
public class FirmaDigitalCallbackController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KeycloakProperties keycloakProperties;

	@Autowired
	private FirmaDocumentoService firmaDocumentoService;

    @PostMapping("/url-back-file/{filename}")
    public ResponseEntity<CommonResponse> recepcionarArchivoFirmado(@PathVariable("filename") String filename, @RequestBody byte[] archivoFirmado) throws Exception {

		log.info(">> recepcionarArchivoFirmado");

		firmaDocumentoService.recepcionarFileDocumento(filename, archivoFirmado);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

    }

	@PostMapping("/url-back-log/{filename}")
	public ResponseEntity<CommonResponse> recepcionarLogArchivoFirmado(@PathVariable("filename") String filename, @RequestBody String archivoLog) throws Exception {

		log.info(">> recepcionarLogArchivoFirmado: "+filename);
		log.info(">> archivoLog: "+archivoLog);

		firmaDocumentoService.recepcionarLogDocumento(filename, archivoLog);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

	}

}
