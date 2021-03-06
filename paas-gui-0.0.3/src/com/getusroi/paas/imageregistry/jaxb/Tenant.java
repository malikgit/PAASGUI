//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.16 at 08:59:00 PM IST 
//


package com.getusroi.paas.imageregistry.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImageTenantConfigs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ImageRegistry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                             &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "imageTenantConfigs"
})
public class Tenant
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ImageTenantConfigs", required = true)
    protected ImageTenantConfigs imageTenantConfigs;
    @XmlAttribute(name = "id")
    protected int id;

    /**
     * Gets the value of the imageTenantConfigs property.
     * 
     * @return
     *     possible object is
     *     {@link ImageTenantConfigs }
     *     
     */
    public ImageTenantConfigs getImageTenantConfigs() {
        return imageTenantConfigs;
    }

    /**
     * Sets the value of the imageTenantConfigs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImageTenantConfigs }
     *     
     */
    public void setImageTenantConfigs(ImageTenantConfigs value) {
        this.imageTenantConfigs = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setId(int value) {
        this.id = value;
    }

}
