//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.09.10 a las 01:10:47 AM PET 
//


package pe.com.amsac.tramite.pide.soap.entidad.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getListaEntidad complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getListaEntidad"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sidcatent" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getListaEntidad", propOrder = {
    "sidcatent"
})
@XmlRootElement(name = "getListaEntidad")
public class GetListaEntidad {

    protected int sidcatent;

    /**
     * Obtiene el valor de la propiedad sidcatent.
     * 
     */
    public int getSidcatent() {
        return sidcatent;
    }

    /**
     * Define el valor de la propiedad sidcatent.
     * 
     */
    public void setSidcatent(int value) {
        this.sidcatent = value;
    }

}
