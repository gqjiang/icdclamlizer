//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.06.25 at 10:11:38 AM CDT 
//


package edu.mayo.bmi.guoqian.claml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import edu.mayo.bmi.guoqian.claml.History;
import edu.mayo.bmi.guoqian.claml.Meta;
import edu.mayo.bmi.guoqian.claml.Modifier;
import edu.mayo.bmi.guoqian.claml.Rubric;
import edu.mayo.bmi.guoqian.claml.SubClass;


/**
 * <p>Java class for Modifier element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="Modifier">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}Meta" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}SubClass" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}Rubric" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{}History" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;attGroup ref="{}attlist.Modifier"/>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "", propOrder = {
    "meta",
    "subClass",
    "rubric",
    "history"
})
@XmlRootElement(name = "Modifier")
public class Modifier {

    @XmlElement(name = "Meta")
    protected List<Meta> meta;
    @XmlElement(name = "SubClass")
    protected List<SubClass> subClass;
    @XmlElement(name = "Rubric")
    protected List<Rubric> rubric;
    @XmlElement(name = "History")
    protected List<History> history;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String code;
    @XmlAttribute
    @XmlIDREF
    protected List<Object> variants;

    /**
     * Gets the value of the meta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the meta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMeta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Meta }
     * 
     * 
     */
    public List<Meta> getMeta() {
        if (meta == null) {
            meta = new ArrayList<Meta>();
        }
        return this.meta;
    }

    /**
     * Gets the value of the subClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubClass }
     * 
     * 
     */
    public List<SubClass> getSubClass() {
        if (subClass == null) {
            subClass = new ArrayList<SubClass>();
        }
        return this.subClass;
    }

    /**
     * Gets the value of the rubric property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rubric property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRubric().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rubric }
     * 
     * 
     */
    public List<Rubric> getRubric() {
        if (rubric == null) {
            rubric = new ArrayList<Rubric>();
        }
        return this.rubric;
    }

    /**
     * Gets the value of the history property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the history property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link History }
     * 
     * 
     */
    public List<History> getHistory() {
        if (history == null) {
            history = new ArrayList<History>();
        }
        return this.history;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the variants property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variants property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariants().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getVariants() {
        if (variants == null) {
            variants = new ArrayList<Object>();
        }
        return this.variants;
    }

}
