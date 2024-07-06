//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.07.05 a las 07:54:46 PM PET 
//


package pe.com.amsac.tramite.pide.soap.tramite.request;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pe.com.amsac.tramite.pide.soap.tramite.request package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CargoResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "cargoResponse");
    private final static QName _CargoResponseResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "cargoResponseResponse");
    private final static QName _ConsultarTramiteResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "consultarTramiteResponse");
    private final static QName _ConsultarTramiteResponseResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "consultarTramiteResponseResponse");
    private final static QName _GetTipoDocumento_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "getTipoDocumento");
    private final static QName _GetTipoDocumentoResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "getTipoDocumentoResponse");
    private final static QName _RecepcionarTramiteResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "recepcionarTramiteResponse");
    private final static QName _RecepcionarTramiteResponseResponse_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "recepcionarTramiteResponseResponse");
    private final static QName _IOException_QNAME = new QName("http://ws.wsiopidetramite.segdi.gob.pe/", "IOException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pe.com.amsac.tramite.pide.soap.tramite.request
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CargoResponse }
     * 
     */
    public CargoResponse createCargoResponse() {
        return new CargoResponse();
    }

    /**
     * Create an instance of {@link CargoResponseResponse }
     * 
     */
    public CargoResponseResponse createCargoResponseResponse() {
        return new CargoResponseResponse();
    }

    /**
     * Create an instance of {@link ConsultarTramiteResponse }
     * 
     */
    public ConsultarTramiteResponse createConsultarTramiteResponse() {
        return new ConsultarTramiteResponse();
    }

    /**
     * Create an instance of {@link ConsultarTramiteResponseResponse }
     * 
     */
    public ConsultarTramiteResponseResponse createConsultarTramiteResponseResponse() {
        return new ConsultarTramiteResponseResponse();
    }

    /**
     * Create an instance of {@link GetTipoDocumento }
     * 
     */
    public GetTipoDocumento createGetTipoDocumento() {
        return new GetTipoDocumento();
    }

    /**
     * Create an instance of {@link GetTipoDocumentoResponse }
     * 
     */
    public GetTipoDocumentoResponse createGetTipoDocumentoResponse() {
        return new GetTipoDocumentoResponse();
    }

    /**
     * Create an instance of {@link RecepcionarTramiteResponse }
     * 
     */
    public RecepcionarTramiteResponse createRecepcionarTramiteResponse() {
        return new RecepcionarTramiteResponse();
    }

    /**
     * Create an instance of {@link RecepcionarTramiteResponseResponse }
     * 
     */
    public RecepcionarTramiteResponseResponse createRecepcionarTramiteResponseResponse() {
        return new RecepcionarTramiteResponseResponse();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link CargoTramite }
     * 
     */
    public CargoTramite createCargoTramite() {
        return new CargoTramite();
    }

    /**
     * Create an instance of {@link RespuestaCargoTramite }
     * 
     */
    public RespuestaCargoTramite createRespuestaCargoTramite() {
        return new RespuestaCargoTramite();
    }

    /**
     * Create an instance of {@link ConsultaTramite }
     * 
     */
    public ConsultaTramite createConsultaTramite() {
        return new ConsultaTramite();
    }

    /**
     * Create an instance of {@link RespuestaConsultaTramite }
     * 
     */
    public RespuestaConsultaTramite createRespuestaConsultaTramite() {
        return new RespuestaConsultaTramite();
    }

    /**
     * Create an instance of {@link IoTipoDocumentoTramite }
     * 
     */
    public IoTipoDocumentoTramite createIoTipoDocumentoTramite() {
        return new IoTipoDocumentoTramite();
    }

    /**
     * Create an instance of {@link RecepcionTramite }
     * 
     */
    public RecepcionTramite createRecepcionTramite() {
        return new RecepcionTramite();
    }

    /**
     * Create an instance of {@link DocumentoAnexo }
     * 
     */
    public DocumentoAnexo createDocumentoAnexo() {
        return new DocumentoAnexo();
    }

    /**
     * Create an instance of {@link RespuestaTramite }
     * 
     */
    public RespuestaTramite createRespuestaTramite() {
        return new RespuestaTramite();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "cargoResponse")
    public JAXBElement<CargoResponse> createCargoResponse(CargoResponse value) {
        return new JAXBElement<CargoResponse>(_CargoResponse_QNAME, CargoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CargoResponseResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CargoResponseResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "cargoResponseResponse")
    public JAXBElement<CargoResponseResponse> createCargoResponseResponse(CargoResponseResponse value) {
        return new JAXBElement<CargoResponseResponse>(_CargoResponseResponse_QNAME, CargoResponseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarTramiteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarTramiteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "consultarTramiteResponse")
    public JAXBElement<ConsultarTramiteResponse> createConsultarTramiteResponse(ConsultarTramiteResponse value) {
        return new JAXBElement<ConsultarTramiteResponse>(_ConsultarTramiteResponse_QNAME, ConsultarTramiteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarTramiteResponseResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultarTramiteResponseResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "consultarTramiteResponseResponse")
    public JAXBElement<ConsultarTramiteResponseResponse> createConsultarTramiteResponseResponse(ConsultarTramiteResponseResponse value) {
        return new JAXBElement<ConsultarTramiteResponseResponse>(_ConsultarTramiteResponseResponse_QNAME, ConsultarTramiteResponseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTipoDocumento }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetTipoDocumento }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "getTipoDocumento")
    public JAXBElement<GetTipoDocumento> createGetTipoDocumento(GetTipoDocumento value) {
        return new JAXBElement<GetTipoDocumento>(_GetTipoDocumento_QNAME, GetTipoDocumento.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTipoDocumentoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetTipoDocumentoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "getTipoDocumentoResponse")
    public JAXBElement<GetTipoDocumentoResponse> createGetTipoDocumentoResponse(GetTipoDocumentoResponse value) {
        return new JAXBElement<GetTipoDocumentoResponse>(_GetTipoDocumentoResponse_QNAME, GetTipoDocumentoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionarTramiteResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RecepcionarTramiteResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "recepcionarTramiteResponse")
    public JAXBElement<RecepcionarTramiteResponse> createRecepcionarTramiteResponse(RecepcionarTramiteResponse value) {
        return new JAXBElement<RecepcionarTramiteResponse>(_RecepcionarTramiteResponse_QNAME, RecepcionarTramiteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionarTramiteResponseResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RecepcionarTramiteResponseResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "recepcionarTramiteResponseResponse")
    public JAXBElement<RecepcionarTramiteResponseResponse> createRecepcionarTramiteResponseResponse(RecepcionarTramiteResponseResponse value) {
        return new JAXBElement<RecepcionarTramiteResponseResponse>(_RecepcionarTramiteResponseResponse_QNAME, RecepcionarTramiteResponseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.wsiopidetramite.segdi.gob.pe/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

}
