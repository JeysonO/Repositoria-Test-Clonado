//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.09.10 a las 01:10:47 AM PET 
//


package pe.com.amsac.tramite.pide.soap.entidad.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EntidadBean2 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EntidadBean2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sident" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="sidpadent" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="vnoment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vrucent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="lstEntidad" type="{http://ws.wsentidad.segdi.gob.pe/}EntidadBean2" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntidadBean2", propOrder = {
    "sident",
    "sidpadent",
    "vnoment",
    "vrucent",
    "lstEntidad"
})
public class EntidadBean2 {

    protected int sident;
    protected int sidpadent;
    protected String vnoment;
    protected String vrucent;
    @XmlElement(nillable = true)
    protected List<EntidadBean2> lstEntidad;

    /**
     * Obtiene el valor de la propiedad sident.
     * 
     */
    public int getSident() {
        return sident;
    }

    /**
     * Define el valor de la propiedad sident.
     * 
     */
    public void setSident(int value) {
        this.sident = value;
    }

    /**
     * Obtiene el valor de la propiedad sidpadent.
     * 
     */
    public int getSidpadent() {
        return sidpadent;
    }

    /**
     * Define el valor de la propiedad sidpadent.
     * 
     */
    public void setSidpadent(int value) {
        this.sidpadent = value;
    }

    /**
     * Obtiene el valor de la propiedad vnoment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVnoment() {
        return vnoment;
    }

    /**
     * Define el valor de la propiedad vnoment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVnoment(String value) {
        this.vnoment = value;
    }

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

    /**
     * Gets the value of the lstEntidad property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lstEntidad property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLstEntidad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntidadBean2 }
     * 
     * 
     */
    public List<EntidadBean2> getLstEntidad() {
        if (lstEntidad == null) {
            lstEntidad = new ArrayList<EntidadBean2>();
        }
        return this.lstEntidad;
    }

}
