package pe.com.amsac.tramite.pide.soap.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import pe.com.amsac.tramite.pide.soap.cuo.request.GetCUO;
import pe.com.amsac.tramite.pide.soap.cuo.request.GetCUOEntidad;
import pe.com.amsac.tramite.pide.soap.cuo.request.GetCUOEntidadResponse;
import pe.com.amsac.tramite.pide.soap.cuo.request.GetCUOResponse;
import pe.com.amsac.tramite.pide.soap.entidad.request.GetListaEntidad;
import pe.com.amsac.tramite.pide.soap.entidad.request.GetListaEntidadResponse;
import pe.com.amsac.tramite.pide.soap.tramite.request.RecepcionarTramiteResponse;
import pe.com.amsac.tramite.pide.soap.tramite.request.RecepcionarTramiteResponseResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

@Slf4j
@Configuration
public class SOAPEntidadConnector extends WebServiceGatewaySupport {

    public Object callWebService(JAXBElement jaxbElement){
        /*
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(3000);
        setMessageSender(new ClientHttpRequestMessageSender(requestFactory));
        */
        return getWebServiceTemplate().marshalSendAndReceive(jaxbElement);

    }

    /*
    public GetListaEntidadResponse callWebService(GetListaEntidad getListaEntidad) throws JsonProcessingException, JAXBException {

        log.info(new ObjectMapper().writeValueAsString(getListaEntidad));

        JAXBContext jaxbContext = JAXBContext.newInstance(GetListaEntidad.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(getListaEntidad, sw);
        String xmlString = sw.toString();

        //StreamSource source = new StreamSource(new StringReader(new ObjectMapper().writeValueAsString(recepcionarTramiteResponse)));
        StreamSource source = new StreamSource(new StringReader(xmlString));
        StreamResult result = new StreamResult(System.out);
        getWebServiceTemplate().sendSourceAndReceiveToResult(source,result);

        return null;

    }

    public GetCUOResponse callWebService(GetCUO getCUO){

        GetCUOResponse response = (GetCUOResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getCUO,new SoapActionCallback("getCUO"));
        return response;

    }
    */

    public GetListaEntidadResponse callWebService(GetListaEntidad getListaEntidad){

        GetListaEntidadResponse response = (GetListaEntidadResponse) getWebServiceTemplate()
                .marshalSendAndReceive(getListaEntidad, new SoapActionCallback("getListaEntidad"));
        return response;

    }

}
