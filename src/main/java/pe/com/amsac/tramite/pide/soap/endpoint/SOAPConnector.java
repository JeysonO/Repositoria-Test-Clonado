package pe.com.amsac.tramite.pide.soap.endpoint;

import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;

@Configuration
public class SOAPConnector extends WebServiceGatewaySupport {

    public Object callWebService(String url, JAXBElement jaxbElement){

        //return getWebServiceTemplate().marshalSendAndReceive(url, request);
        return getWebServiceTemplate().marshalSendAndReceive(url, jaxbElement);

    }
}
