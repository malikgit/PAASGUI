//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.16 at 11:55:29 AM IST 
//


package com.getusroi.paas.application.jaxb;

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
 *       &lt;attribute name="transport" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mailHost" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="smtpport" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="smtpsslenable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="authenticate" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="starttlsenable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="authUser" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="authPassword" type="{http://www.w3.org/2001/XMLSchema}string" />
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
public class MailServerConfig
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "transport")
    protected String transport;
    @XmlAttribute(name = "mailHost")
    protected String mailHost;
    @XmlAttribute(name = "smtpport")
    protected Short smtpport;
    @XmlAttribute(name = "smtpsslenable")
    protected Boolean smtpsslenable;
    @XmlAttribute(name = "authenticate")
    protected Boolean authenticate;
    @XmlAttribute(name = "starttlsenable")
    protected Boolean starttlsenable;
    @XmlAttribute(name = "authUser")
    protected String authUser;
    @XmlAttribute(name = "authPassword")
    protected String authPassword;

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
     * Gets the value of the transport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Sets the value of the transport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransport(String value) {
        this.transport = value;
    }

    /**
     * Gets the value of the mailHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailHost() {
        return mailHost;
    }

    /**
     * Sets the value of the mailHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailHost(String value) {
        this.mailHost = value;
    }

    /**
     * Gets the value of the smtpport property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSmtpport() {
        return smtpport;
    }

    /**
     * Sets the value of the smtpport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSmtpport(Short value) {
        this.smtpport = value;
    }

    /**
     * Gets the value of the smtpsslenable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSmtpsslenable() {
        return smtpsslenable;
    }

    /**
     * Sets the value of the smtpsslenable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSmtpsslenable(Boolean value) {
        this.smtpsslenable = value;
    }

    /**
     * Gets the value of the authenticate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAuthenticate() {
        return authenticate;
    }

    /**
     * Sets the value of the authenticate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAuthenticate(Boolean value) {
        this.authenticate = value;
    }

    /**
     * Gets the value of the starttlsenable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isStarttlsenable() {
        return starttlsenable;
    }

    /**
     * Sets the value of the starttlsenable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStarttlsenable(Boolean value) {
        this.starttlsenable = value;
    }

    /**
     * Gets the value of the authUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthUser() {
        return authUser;
    }

    /**
     * Sets the value of the authUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthUser(String value) {
        this.authUser = value;
    }

    /**
     * Gets the value of the authPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthPassword() {
        return authPassword;
    }

    /**
     * Sets the value of the authPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthPassword(String value) {
        this.authPassword = value;
    }

}
