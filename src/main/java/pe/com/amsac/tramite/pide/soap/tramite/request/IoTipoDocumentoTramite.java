//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.07.05 a las 07:54:46 PM PET 
//


package pe.com.amsac.tramite.pide.soap.tramite.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ioTipoDocumentoTramite complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ioTipoDocumentoTramite"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ccodtipdoctra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vnomtipdoctra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ioTipoDocumentoTramite", propOrder = {
    "ccodtipdoctra",
    "vnomtipdoctra"
})
public class IoTipoDocumentoTramite {

    protected String ccodtipdoctra;
    protected String vnomtipdoctra;

    /**
     * Obtiene el valor de la propiedad ccodtipdoctra.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcodtipdoctra() {
        return ccodtipdoctra;
    }

    /**
     * Define el valor de la propiedad ccodtipdoctra.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcodtipdoctra(String value) {
        this.ccodtipdoctra = value;
    }

    /**
     * Obtiene el valor de la propiedad vnomtipdoctra.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVnomtipdoctra() {
        return vnomtipdoctra;
    }

    /**
     * Define el valor de la propiedad vnomtipdoctra.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVnomtipdoctra(String value) {
        this.vnomtipdoctra = value;
    }

}
