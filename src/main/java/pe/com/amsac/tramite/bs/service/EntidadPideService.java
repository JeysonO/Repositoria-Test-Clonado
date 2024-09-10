package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.CategoriaEntidad;
import pe.com.amsac.tramite.bs.domain.EntidadPide;
import pe.com.amsac.tramite.bs.repository.EntidadPideJPARepository;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPEntidadConnector;
import pe.com.amsac.tramite.pide.soap.entidad.request.EntidadBean;
import pe.com.amsac.tramite.pide.soap.entidad.request.GetListaEntidad;
import pe.com.amsac.tramite.pide.soap.entidad.request.GetListaEntidadResponse;
import pe.com.amsac.tramite.pide.soap.entidad.request.ObjectFactory;
import pe.com.amsac.tramite.pide.soap.tramite.request.RecepcionarTramiteResponseResponse;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EntidadPideService {

	@Autowired
	private EntidadPideJPARepository entidadPideJPARepository;

	@Autowired
	private CategoriaEntidadService categoriaEntidadService;

	@Autowired
	private Environment env;

	@Autowired
	private SOAPEntidadConnector soapEntidadConnector;

	public List<EntidadPide> obtenerEntidadPideByEstado(String estado) throws Exception{

		return entidadPideJPARepository.findByEstado(estado);
	}

	public List<EntidadPide> obtenerEntidadPideByCategoriaEntidad(String idCategoriaEntidad) throws Exception{

		return entidadPideJPARepository.findByCategoriaEntidad_Id(idCategoriaEntidad);
	}

	public void sincronizarEntidadesCatalogo() throws Exception {
		//Se obtiene la lista de categoria
		List<CategoriaEntidad> categoriaEntidadList = categoriaEntidadService.obtenerCategoriaEntidadActivo();

		//Sincronizar las entidades por categoria
		categoriaEntidadList.stream().forEach(x-> {
			try {
				sincronizarCategoria(x);
			} catch (Exception e) {
				log.error("ERROR",e);
			}
		});

	}

	public void sincronizarCategoria(CategoriaEntidad categoriaEntidad) throws Exception {
		//Se obtiene la lista de entidad para la categoria desde Pide
		List<EntidadPide> entidadesByCategoriaFromPide = obtenerEntidadesByCategoriaFromPide(categoriaEntidad);

		//Obtenermos las entidades registradas en la bd para la categoria
		List<EntidadPide> entidadPideList = obtenerEntidadPideByCategoriaEntidad(categoriaEntidad.getId());

		//Se compara las entidades obtenidas por categoria de la BD y se compara con lo que retorna del WS.
		entidadesByCategoriaFromPide.stream().forEach(x->{
			//Buscamos la entidad obtenida de PIDE en la lista de entidades de la bd de amsac
			if(!x.getRuc().equals("0") && !existeEntidadEnLista(entidadPideList,x))
				entidadPideJPARepository.save(x);//Si la entidad no existe en nuestra bd, entonces se agrega.

		});

		//Se obtienen las entidad de la bd para ver si estan en la lista obtenida de de PIDE, sino esta entonces se actualiza como no disponible
		entidadPideList.stream().forEach(x->{
			//Buscamos la entidad obtenida de PIDE en la lista de entidades de la bd de amsac
			if(!existeEntidadEnLista(entidadesByCategoriaFromPide,x)){
				//Si en la lista obtenida de pide, entonces se coloca como inactiva
				x.setEstado("I");
				entidadPideJPARepository.save(x);
			}

		});

	}

	public List<EntidadPide> obtenerEntidadesByCategoriaFromPide(CategoriaEntidad categoriaEntidadPide){
		ObjectFactory objectFactory = new ObjectFactory();
		List<EntidadPide> entidadPideList = new ArrayList<>();

		GetListaEntidad getListaEntidad = new GetListaEntidad();
		getListaEntidad.setSidcatent(Integer.parseInt(categoriaEntidadPide.getCategoriaEntidad()));

		JAXBElement jaxbGetListaEntidad = objectFactory.createGetListaEntidad(getListaEntidad);
		JAXBElement jaxbGetListaEntidadResponse = (JAXBElement) soapEntidadConnector.callWebService(jaxbGetListaEntidad);
		GetListaEntidadResponse getListaEntidadResponse = (GetListaEntidadResponse) jaxbGetListaEntidadResponse.getValue();

		List<EntidadBean> entidadBeanList = getListaEntidadResponse.getReturn();

		entidadBeanList.stream().forEach(x->{
			if(!x.getVrucent().equals("0"))
				entidadPideList.add(transformCategoriaBeanToCategoriaEntidad(x,categoriaEntidadPide));
		});

		return entidadPideList;
	}

	public EntidadPide transformCategoriaBeanToCategoriaEntidad(EntidadBean entidadBean, CategoriaEntidad categoriaEntidad){
		EntidadPide entidadPide = new EntidadPide();
		entidadPide.setCategoriaEntidad(categoriaEntidad);
		entidadPide.setEstado("A");
		entidadPide.setNombre(entidadBean.getVnoment());
		entidadPide.setRuc(entidadBean.getVrucent());
		return entidadPide;
	}

	public boolean existeEntidadEnLista(List<EntidadPide> lista, EntidadPide entidadPide){
		return lista.stream().filter(x->entidadPide.getRuc().equals(x.getRuc())).count()>0;
	}
	
}
