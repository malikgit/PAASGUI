//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.16 at 11:55:29 AM IST 
//


package com.getusroi.paas.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="subnetName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cidr" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class Subnet
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "subnetName")
    protected String subnetName;
    @XmlAttribute(name = "cidr")
    protected String cidr;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the subnetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetName() {
        return subnetName;
    }

    /**
     * Sets the value of the subnetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetName(String value) {
        this.subnetName = value;
    }

    /**
     * Gets the value of the cidr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * Sets the value of the cidr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidr(String value) {
        this.cidr = value;
    }

}
