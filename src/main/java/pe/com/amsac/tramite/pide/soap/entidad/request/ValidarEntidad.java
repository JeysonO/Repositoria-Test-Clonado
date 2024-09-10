//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.09.10 a las 01:10:47 AM PET 
//


package pe.com.amsac.tramite.pide.soap.entidad.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para validarEntidad complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="validarEntidad"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vrucent" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validarEntidad", propOrder = {
    "vrucent"
})
public class ValidarEntidad {

    @XmlElement(required = true)
    protected String vrucent;

    /**
     * Obtiene el valor de la propiedad vrucent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrucent() {
        return vrucent;
    }

    /**
     * Define el valor de la propiedad vrucent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrucent(String value) {
        this.vrucent = value;
    }

}
