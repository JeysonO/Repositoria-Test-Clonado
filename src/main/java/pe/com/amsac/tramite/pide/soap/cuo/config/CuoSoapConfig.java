package pe.com.amsac.tramite.pide.soap.cuo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPCUOConnector;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPConnector;

@Configuration
public class CuoSoapConfig {

    @Autowired
    private Environment env;

    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this is the package name specified in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("pe.com.amsac.tramite.pide.soap.cuo.request");
        return marshaller;
    }

    @Bean
    public SOAPCUOConnector soapConnector(Jaxb2Marshaller marshaller) {
        SOAPCUOConnector client = new SOAPCUOConnector();
        //client.setDefaultUri("http://161.132.150.248/wsentidad/Entidad");
        //client.setDefaultUri("https://ws3.pide.gob.pe/services/PcmCuo");
        client.setDefaultUri(env.getProperty("app.url.cuoServer"));
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
