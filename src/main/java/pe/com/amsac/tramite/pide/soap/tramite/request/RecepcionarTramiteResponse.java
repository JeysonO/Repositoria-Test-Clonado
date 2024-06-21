//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.06.21 a las 12:43:28 AM PET 
//


package pe.com.amsac.tramite.pide.soap.tramite.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para recepcionarTramiteResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="recepcionarTramiteResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="request" type="{http://ws.wsiopidetramite.segdi.gob.pe/}RecepcionTramite"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recepcionarTramiteResponse", propOrder = {
    "request"
})
public class RecepcionarTramiteResponse {

    @XmlElement(required = true)
    protected RecepcionTramite request;

    /**
     * Obtiene el valor de la propiedad request.
     * 
     * @return
     *     possible object is
     *     {@link RecepcionTramite }
     *     
     */
    public RecepcionTramite getRequest() {
        return request;
    }

    /**
     * Define el valor de la propiedad request.
     * 
     * @param value
     *     allowed object is
     *     {@link RecepcionTramite }
     *     
     */
    public void setRequest(RecepcionTramite value) {
        this.request = value;
    }

}
