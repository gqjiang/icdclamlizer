package edu.mayo.informatics.icd.claml;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.FileField;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseSourcesEditor;


public class ICDClaMLExportPanel extends JPanel {

    private FileField umbrellaProjectFileField;
    private FileField clamlOutputFileFiled;
	private JComboBox jcbLinearTypes;
	private JLabel labelLinearType;
	
	final String[] linearTypes = {"Morbidity", "Mortality", "PrimaryCare", "Research", 
			"Speciality_Adaptation_Mental_Health", "Specialty_Adaptation_Dermatology", 
			"Specialty_Adaptation_Musculoskeletal", "Specialty_Adaptation_Neurology", 
			"Specialty_Adaptation_Paediatrics"};	

    public ICDClaMLExportPanel() {
    	umbrellaProjectFileField = new FileField("ICD Umbrella pprj file name",
                                     null, ".pprj",
                                     "Protege Project files");

    	clamlOutputFileFiled = new FileField("ICD ClaML output file name",
                null, ".xml",
                "ICD ClaML output (xml) files");
    	
    	JPanel panelLinearType = new JPanel();
    	labelLinearType = new JLabel("Linearization type: ");
        jcbLinearTypes = new JComboBox(linearTypes);
        panelLinearType.add(labelLinearType);
        panelLinearType.add(jcbLinearTypes);
        
        setLayout(new BorderLayout(8, 8));
        //add(BorderLayout.NORTH, umbrellaProjectFileField);
        add(BorderLayout.CENTER, clamlOutputFileFiled);
        add(BorderLayout.SOUTH, panelLinearType);
    }


    public String getUmbrellaProjectFilePath() {
        String path = umbrellaProjectFileField.getPath();

        // make sure the filename has an extension
        File file = new File(path);
        String filename = file.getName();
        int extIndex = filename.indexOf('.');
        if (extIndex < 0){
            path = umbrellaProjectFileField.getPath() + "." + "pprj";
        }
        return path;
    }

    public String getClaMLOutputFilePath() {
        String path = clamlOutputFileFiled.getPath();

        // make sure the filename has an extension
        File file = new File(path);
        String filename = file.getName();
        int extIndex = filename.indexOf('.');
        if (extIndex < 0){
            path = clamlOutputFileFiled.getPath() + "." + "xml";
        }
        return path;
    }    
    
    public String getLinearType() {
    	return (String)jcbLinearTypes.getSelectedItem();
    }

	
	
}
