//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.17 at 12:02:02 PM IST 
//


package com.getusroi.paas.vpc.jaxb;

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
 *         &lt;element name="AddVirtualPrivateCloudConfigs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AddVirtualPrivateCloudConfig" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="VPCName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Cidr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Acl" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "addVirtualPrivateCloudConfigs"
})
public class Tenant
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddVirtualPrivateCloudConfigs", required = true)
    protected AddVirtualPrivateCloudConfigs addVirtualPrivateCloudConfigs;
    @XmlAttribute(name = "id")
    protected int id;

    /**
     * Gets the value of the addVirtualPrivateCloudConfigs property.
     * 
     * @return
     *     possible object is
     *     {@link AddVirtualPrivateCloudConfigs }
     *     
     */
    public AddVirtualPrivateCloudConfigs getAddVirtualPrivateCloudConfigs() {
        return addVirtualPrivateCloudConfigs;
    }

    /**
     * Sets the value of the addVirtualPrivateCloudConfigs property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddVirtualPrivateCloudConfigs }
     *     
     */
    public void setAddVirtualPrivateCloudConfigs(AddVirtualPrivateCloudConfigs value) {
        this.addVirtualPrivateCloudConfigs = value;
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