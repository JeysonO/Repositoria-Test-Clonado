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
 * <p>Clase Java para DocumentoAnexo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DocumentoAnexo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vnomdoc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoAnexo", propOrder = {
    "vnomdoc"
})
public class DocumentoAnexo {

    protected String vnomdoc;

    /**
     * Obtiene el valor de la propiedad vnomdoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVnomdoc() {
        return vnomdoc;
    }

    /**
     * Define el valor de la propiedad vnomdoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVnomdoc(String value) {
        this.vnomdoc = value;
    }

}
