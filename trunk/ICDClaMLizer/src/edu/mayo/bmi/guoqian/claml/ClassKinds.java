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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import edu.mayo.bmi.guoqian.claml.ClassKind;
import edu.mayo.bmi.guoqian.claml.ClassKinds;


/**
 * <p>Java class for ClassKinds element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="ClassKinds">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element ref="{}ClassKind" maxOccurs="unbounded"/>
 *         &lt;/sequence>
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
    "classKind"
})
@XmlRootElement(name = "ClassKinds")
public class ClassKinds {

    @XmlElement(name = "ClassKind")
    protected List<ClassKind> classKind;

    /**
     * Gets the value of the classKind property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classKind property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassKind().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassKind }
     * 
     * 
     */
    public List<ClassKind> getClassKind() {
        if (classKind == null) {
            classKind = new ArrayList<ClassKind>();
        }
        return this.classKind;
    }

}
