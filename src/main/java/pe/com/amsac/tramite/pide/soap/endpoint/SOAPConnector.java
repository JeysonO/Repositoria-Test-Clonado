package pe.com.amsac.tramite.pide.soap.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
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
public class SOAPConnector extends WebServiceGatewaySupport {

    public Object callWebService(String url, JAXBElement jaxbElement){

        //return getWebServiceTemplate().marshalSendAndReceive(url, request);
        //return getWebServiceTemplate().marshalSendAndReceive(url, jaxbElement);
        return getWebServiceTemplate().marshalSendAndReceive(jaxbElement);

    }

    public RecepcionarTramiteResponseResponse callWebService(RecepcionarTramiteResponse recepcionarTramiteResponse) throws JsonProcessingException, JAXBException {

        /*
        RecepcionarTramiteResponseResponse response = (RecepcionarTramiteResponseResponse) getWebServiceTemplate()
                .marshalSendAndReceive(recepcionarTramiteResponse);

         */
        /*
        JAXBElement<RecepcionarTramiteResponseResponse> response = (JAXBElement<RecepcionarTramiteResponseResponse>) getWebServiceTemplate()
                .marshalSendAndReceive(recepcionarTramiteResponse);

        return response.getValue();
         */
        log.info(new ObjectMapper().writeValueAsString(recepcionarTramiteResponse));

        JAXBContext jaxbContext = JAXBContext.newInstance(RecepcionarTramiteResponse.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(recepcionarTramiteResponse, sw);
        String xmlString = sw.toString();

        //StreamSource source = new StreamSource(new StringReader(new ObjectMapper().writeValueAsString(recepcionarTramiteResponse)));
        StreamSource source = new StreamSource(new StringReader(xmlString));
        StreamResult result = new StreamResult(System.out);
        getWebServiceTemplate().sendSourceAndReceiveToResult(source,result);

        return null;

    }

    public RecepcionarTramiteResponseResponse callWebService(JAXBElement jaxbElement){

        RecepcionarTramiteResponseResponse response = (RecepcionarTramiteResponseResponse) getWebServiceTemplate()
                .marshalSendAndReceive(jaxbElement);
        return response;

    }
}
