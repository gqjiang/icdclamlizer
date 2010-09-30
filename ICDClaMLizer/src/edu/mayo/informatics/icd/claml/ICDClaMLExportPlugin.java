package edu.mayo.informatics.icd.claml;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;

import javax.swing.*;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.plugin.*;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.jena.JenaFilePanel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

public class ICDClaMLExportPlugin implements ExportPlugin {
    private static final String EXTENSION = ".xml";

    private OWLModel owlModel;
    private ICDContentModel icdContentModel;
    private Collection icdCategories = new ArrayList();
    private ICDClaMLLinearizationHierarchicalStructure model;
	Map mapGeneratedCodes = new HashMap(); 
   
    
    public String getName() {
        return "ClaML";
    }

    public void handleExportRequest(Project project) {
    	
		ICDClaMLExportPanel panel = new ICDClaMLExportPanel();


		int rval = ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(project),
				panel, "ICD ClaML File to Export", ModalDialogFactory.MODE_OK_CANCEL);
		if (rval == ModalDialogFactory.OPTION_OK) {
			//String filePath = panel.getUmbrellaProjectFilePath();
			String outputFilePath = panel.getClaMLOutputFilePath();
			String linearType = panel.getLinearType();
			WaitCursor cursor = new WaitCursor(ProjectManager.getProjectManager().getMainPanel());
			
			
			try {
				//exportProject(project.getKnowledgeBase(), filePath, panel.getUseNativeWriter());
		        File file = new File(outputFilePath);
		        if (file != null) {
		            this.generateClaML(project, file, linearType);
		        }
		    				
				
			}
			catch (Exception ex) {
				Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
				ProtegeUI.getModalDialogFactory().showErrorMessageDialog(panel,
						"Export failed. Please see console for details.\n" + ex);
			}
			finally {
				cursor.hide();
			}
		}
    }

    	
    private void generateClaML(Project project, File file, String linearType){
    	
    	try{

     	
    		owlModel = (OWLModel) project.getKnowledgeBase();
    		icdContentModel = new ICDContentModel(owlModel);
    	   	
    		icdCategories = icdContentModel.getICDCategories();    	

        
        
    		model = new ICDClaMLLinearizationHierarchicalStructure(icdContentModel, linearType);
    		model.initialize(icdCategories);
    		model.generateCodes(); 
    		mapGeneratedCodes = model.getGeneretadCodes();
    		
    		
    		//render xml file
        	ICDClaMLXMLRender render = new ICDClaMLXMLRender();
        	String claml = render.processingClaMLXML(icdContentModel, icdCategories, mapGeneratedCodes, model);
   		
        	
        	this.saveToFile(file, claml);
        	
 	
    	
    	}catch(Exception e){
    		e.printStackTrace();
        }

    	
    }	
    
    

    public void dispose() {
        // do nothing
    }
    
    public static void main(String[] args) {
        Application.main(args);
    }
    
    private void saveToFile(File file, String claml) {
        PrintWriter writer = FileUtilities.createPrintWriter(file, false);
        writer.println(claml);
        writer.close();
    }

}
