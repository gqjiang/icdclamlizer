package edu.mayo.informatics.icd.claml;

import java.awt.*;
import javax.swing.*;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.widget.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ICDClaMLTab extends AbstractTabWidget {
	
	private ICDClaMLPanel clamlPanel;
	//private OntologyExportToWikiForProtegePanel protegePanel;
	
	private JTabbedPane tabbedPane;
	
	
	public ICDClaMLTab(){
	
		
	}
	
	public void initialize(){
		this.intiUI();
	}
	
	public void intiUI(){
		setLabel("ICD ClaMLizer");
		//KnowledgeBase kb = getKnowledgeBase();
		
		OWLModel kb = (OWLModel) getKnowledgeBase();
				
		tabbedPane = new JTabbedPane();

		clamlPanel = new ICDClaMLPanel(kb);		
		tabbedPane.add("ClaMLizer", clamlPanel);
		
		//protegePanel = new OntologyExportToWikiForProtegePanel(kb);
		//tabbedPane.add("Protege Format", protegePanel);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		edu.stanford.smi.protege.Application.main(args);

	}

}

