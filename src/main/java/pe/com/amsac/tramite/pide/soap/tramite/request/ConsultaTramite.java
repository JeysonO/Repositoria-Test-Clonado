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
 * <p>Clase Java para ConsultaTramite complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaTramite"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vrucentrem" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vrucentrec" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vcuo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaTramite", propOrder = {
    "vrucentrem",
    "vrucentrec",
    "vcuo"
})
public class ConsultaTramite {

    @XmlElement(required = true)
    protected String vrucentrem;
    @XmlElement(required = true)
    protected String vrucentrec;
    @XmlElement(required = true)
    protected String vcuo;

    /**
     * Obtiene el valor de la propiedad vrucentrem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrucentrem() {
        return vrucentrem;
    }

    /**
     * Define el valor de la propiedad vrucentrem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrucentrem(String value) {
        this.vrucentrem = value;
    }

    /**
     * Obtiene el valor de la propiedad vrucentrec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrucentrec() {
        return vrucentrec;
    }

    /**
     * Define el valor de la propiedad vrucentrec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrucentrec(String value) {
        this.vrucentrec = value;
    }

    /**
     * Obtiene el valor de la propiedad vcuo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVcuo() {
        return vcuo;
    }

    /**
     * Define el valor de la propiedad vcuo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVcuo(String value) {
        this.vcuo = value;
    }

}
