//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.07.05 a las 07:54:46 PM PET 
//


package pe.com.amsac.tramite.pide.soap.tramite.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para CargoTramite complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CargoTramite"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vrucentrem" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vrucentrec" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vcuo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vcuoref" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="vnumregstd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vanioregstd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dfecregstd" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="vuniorgstd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vusuregstd" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bcarstd" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element name="vobs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cflgest" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="vdesanxstdrec" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CargoTramite", propOrder = {
    "vrucentrem",
    "vrucentrec",
    "vcuo",
    "vcuoref",
    "vnumregstd",
    "vanioregstd",
    "dfecregstd",
    "vuniorgstd",
    "vusuregstd",
    "bcarstd",
    "vobs",
    "cflgest",
    "vdesanxstdrec"
})
public class CargoTramite {

    @XmlElement(required = true)
    protected String vrucentrem;
    @XmlElement(required = true)
    protected String vrucentrec;
    @XmlElement(required = true)
    protected String vcuo;
    protected String vcuoref;
    @XmlElement(required = true)
    protected String vnumregstd;
    @XmlElement(required = true)
    protected String vanioregstd;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dfecregstd;
    @XmlElement(required = true)
    protected String vuniorgstd;
    @XmlElement(required = true)
    protected String vusuregstd;
    @XmlElement(required = true)
    protected byte[] bcarstd;
    protected String vobs;
    @XmlElement(required = true)
    protected String cflgest;
    protected String vdesanxstdrec;

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

    /**
     * Obtiene el valor de la propiedad vcuoref.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVcuoref() {
        return vcuoref;
    }

    /**
     * Define el valor de la propiedad vcuoref.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVcuoref(String value) {
        this.vcuoref = value;
    }

    /**
     * Obtiene el valor de la propiedad vnumregstd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVnumregstd() {
        return vnumregstd;
    }

    /**
     * Define el valor de la propiedad vnumregstd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVnumregstd(String value) {
        this.vnumregstd = value;
    }

    /**
     * Obtiene el valor de la propiedad vanioregstd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVanioregstd() {
        return vanioregstd;
    }

    /**
     * Define el valor de la propiedad vanioregstd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVanioregstd(String value) {
        this.vanioregstd = value;
    }

    /**
     * Obtiene el valor de la propiedad dfecregstd.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDfecregstd() {
        return dfecregstd;
    }

    /**
     * Define el valor de la propiedad dfecregstd.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDfecregstd(XMLGregorianCalendar value) {
        this.dfecregstd = value;
    }

    /**
     * Obtiene el valor de la propiedad vuniorgstd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVuniorgstd() {
        return vuniorgstd;
    }

    /**
     * Define el valor de la propiedad vuniorgstd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVuniorgstd(String value) {
        this.vuniorgstd = value;
    }

    /**
     * Obtiene el valor de la propiedad vusuregstd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVusuregstd() {
        return vusuregstd;
    }

    /**
     * Define el valor de la propiedad vusuregstd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVusuregstd(String value) {
        this.vusuregstd = value;
    }

    /**
     * Obtiene el valor de la propiedad bcarstd.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBcarstd() {
        return bcarstd;
    }

    /**
     * Define el valor de la propiedad bcarstd.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBcarstd(byte[] value) {
        this.bcarstd = value;
    }

    /**
     * Obtiene el valor de la propiedad vobs.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVobs() {
        return vobs;
    }

    /**
     * Define el valor de la propiedad vobs.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVobs(String value) {
        this.vobs = value;
    }

    /**
     * Obtiene el valor de la propiedad cflgest.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCflgest() {
        return cflgest;
    }

    /**
     * Define el valor de la propiedad cflgest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCflgest(String value) {
        this.cflgest = value;
    }

    /**
     * Obtiene el valor de la propiedad vdesanxstdrec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVdesanxstdrec() {
        return vdesanxstdrec;
    }

    /**
     * Define el valor de la propiedad vdesanxstdrec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVdesanxstdrec(String value) {
        this.vdesanxstdrec = value;
    }

}
