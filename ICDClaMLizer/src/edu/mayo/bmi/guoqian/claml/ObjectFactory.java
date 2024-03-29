//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.06.25 at 10:11:38 AM CDT 
//


package edu.mayo.bmi.guoqian.claml;

import javax.xml.bind.annotation.XmlRegistry;
import edu.mayo.bmi.guoqian.claml.Author;
import edu.mayo.bmi.guoqian.claml.Authors;
import edu.mayo.bmi.guoqian.claml.Caption;
import edu.mayo.bmi.guoqian.claml.Cell;
import edu.mayo.bmi.guoqian.claml.ClaML;
import edu.mayo.bmi.guoqian.claml.Class;
import edu.mayo.bmi.guoqian.claml.ClassKind;
import edu.mayo.bmi.guoqian.claml.ClassKinds;
import edu.mayo.bmi.guoqian.claml.Display;
import edu.mayo.bmi.guoqian.claml.ExcludeModifier;
import edu.mayo.bmi.guoqian.claml.Fragment;
import edu.mayo.bmi.guoqian.claml.History;
import edu.mayo.bmi.guoqian.claml.Identifier;
import edu.mayo.bmi.guoqian.claml.Include;
import edu.mayo.bmi.guoqian.claml.IncludeDescendants;
import edu.mayo.bmi.guoqian.claml.Label;
import edu.mayo.bmi.guoqian.claml.List;
import edu.mayo.bmi.guoqian.claml.ListItem;
import edu.mayo.bmi.guoqian.claml.Meta;
import edu.mayo.bmi.guoqian.claml.ModifiedBy;
import edu.mayo.bmi.guoqian.claml.Modifier;
import edu.mayo.bmi.guoqian.claml.ModifierClass;
import edu.mayo.bmi.guoqian.claml.ObjectFactory;
import edu.mayo.bmi.guoqian.claml.Para;
import edu.mayo.bmi.guoqian.claml.Reference;
import edu.mayo.bmi.guoqian.claml.Row;
import edu.mayo.bmi.guoqian.claml.Rubric;
import edu.mayo.bmi.guoqian.claml.RubricKind;
import edu.mayo.bmi.guoqian.claml.RubricKinds;
import edu.mayo.bmi.guoqian.claml.SubClass;
import edu.mayo.bmi.guoqian.claml.SuperClass;
import edu.mayo.bmi.guoqian.claml.TBody;
import edu.mayo.bmi.guoqian.claml.TFoot;
import edu.mayo.bmi.guoqian.claml.THead;
import edu.mayo.bmi.guoqian.claml.Table;
import edu.mayo.bmi.guoqian.claml.Term;
import edu.mayo.bmi.guoqian.claml.Title;
import edu.mayo.bmi.guoqian.claml.UsageKind;
import edu.mayo.bmi.guoqian.claml.UsageKinds;
import edu.mayo.bmi.guoqian.claml.ValidModifierClass;
import edu.mayo.bmi.guoqian.claml.Variant;
import edu.mayo.bmi.guoqian.claml.Variants;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.mayo.bmi.guoqian.claml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.mayo.bmi.guoqian.claml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ClassKinds }
     * 
     */
    public ClassKinds createClassKinds() {
        return new ClassKinds();
    }

    /**
     * Create an instance of {@link Title }
     * 
     */
    public Title createTitle() {
        return new Title();
    }

    /**
     * Create an instance of {@link ClaML }
     * 
     */
    public ClaML createClaML() {
        return new ClaML();
    }

    /**
     * Create an instance of {@link Display }
     * 
     */
    public Display createDisplay() {
        return new Display();
    }

    /**
     * Create an instance of {@link History }
     * 
     */
    public History createHistory() {
        return new History();
    }

    /**
     * Create an instance of {@link RubricKind }
     * 
     */
    public RubricKind createRubricKind() {
        return new RubricKind();
    }

    /**
     * Create an instance of {@link Table }
     * 
     */
    public Table createTable() {
        return new Table();
    }

    /**
     * Create an instance of {@link Fragment }
     * 
     */
    public Fragment createFragment() {
        return new Fragment();
    }

    /**
     * Create an instance of {@link ValidModifierClass }
     * 
     */
    public ValidModifierClass createValidModifierClass() {
        return new ValidModifierClass();
    }

    /**
     * Create an instance of {@link ClassKind }
     * 
     */
    public ClassKind createClassKind() {
        return new ClassKind();
    }

    /**
     * Create an instance of {@link THead }
     * 
     */
    public THead createTHead() {
        return new THead();
    }

    /**
     * Create an instance of {@link Cell }
     * 
     */
    public Cell createCell() {
        return new Cell();
    }

    /**
     * Create an instance of {@link Meta }
     * 
     */
    public Meta createMeta() {
        return new Meta();
    }

    /**
     * Create an instance of {@link TBody }
     * 
     */
    public TBody createTBody() {
        return new TBody();
    }

    /**
     * Create an instance of {@link TFoot }
     * 
     */
    public TFoot createTFoot() {
        return new TFoot();
    }

    /**
     * Create an instance of {@link ListItem }
     * 
     */
    public ListItem createListItem() {
        return new ListItem();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link UsageKinds }
     * 
     */
    public UsageKinds createUsageKinds() {
        return new UsageKinds();
    }

    /**
     * Create an instance of {@link SubClass }
     * 
     */
    public SubClass createSubClass() {
        return new SubClass();
    }

    /**
     * Create an instance of {@link Term }
     * 
     */
    public Term createTerm() {
        return new Term();
    }

    /**
     * Create an instance of {@link Include }
     * 
     */
    public Include createInclude() {
        return new Include();
    }

    /**
     * Create an instance of {@link RubricKinds }
     * 
     */
    public RubricKinds createRubricKinds() {
        return new RubricKinds();
    }

    /**
     * Create an instance of {@link Rubric }
     * 
     */
    public Rubric createRubric() {
        return new Rubric();
    }

    /**
     * Create an instance of {@link ModifierClass }
     * 
     */
    public ModifierClass createModifierClass() {
        return new ModifierClass();
    }

    /**
     * Create an instance of {@link ModifiedBy }
     * 
     */
    public ModifiedBy createModifiedBy() {
        return new ModifiedBy();
    }

    /**
     * Create an instance of {@link Label }
     * 
     */
    public Label createLabel() {
        return new Label();
    }

    /**
     * Create an instance of {@link Para }
     * 
     */
    public Para createPara() {
        return new Para();
    }

    /**
     * Create an instance of {@link UsageKind }
     * 
     */
    public UsageKind createUsageKind() {
        return new UsageKind();
    }

    /**
     * Create an instance of {@link IncludeDescendants }
     * 
     */
    public IncludeDescendants createIncludeDescendants() {
        return new IncludeDescendants();
    }

    /**
     * Create an instance of {@link Author }
     * 
     */
    public Author createAuthor() {
        return new Author();
    }

    /**
     * Create an instance of {@link Authors }
     * 
     */
    public Authors createAuthors() {
        return new Authors();
    }

    /**
     * Create an instance of {@link ExcludeModifier }
     * 
     */
    public ExcludeModifier createExcludeModifier() {
        return new ExcludeModifier();
    }

    /**
     * Create an instance of {@link SuperClass }
     * 
     */
    public SuperClass createSuperClass() {
        return new SuperClass();
    }

    /**
     * Create an instance of {@link Modifier }
     * 
     */
    public Modifier createModifier() {
        return new Modifier();
    }

    /**
     * Create an instance of {@link Variants }
     * 
     */
    public Variants createVariants() {
        return new Variants();
    }

    /**
     * Create an instance of {@link List }
     * 
     */
    public List createList() {
        return new List();
    }

    /**
     * Create an instance of {@link Variant }
     * 
     */
    public Variant createVariant() {
        return new Variant();
    }

    /**
     * Create an instance of {@link Class }
     * 
     */
    public Class createClass() {
        return new Class();
    }

    /**
     * Create an instance of {@link Identifier }
     * 
     */
    public Identifier createIdentifier() {
        return new Identifier();
    }

    /**
     * Create an instance of {@link Caption }
     * 
     */
    public Caption createCaption() {
        return new Caption();
    }

    /**
     * Create an instance of {@link Row }
     * 
     */
    public Row createRow() {
        return new Row();
    }

}
