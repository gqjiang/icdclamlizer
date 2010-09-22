package edu.mayo.informatics.icd.claml;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.Border;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.*;


import edu.stanford.smi.protegex.owl.model.*;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ICDClaMLPanel extends JPanel 
                            implements ActionListener{
	
	//private KnowledgeBase kb;
	
    private OWLModel owlModel;
    private ICDContentModel icdContentModel;
    private Collection icdCategories = new ArrayList();
    private ICDClaMLLinearizationHierarchicalStructure model;
    
	private JPanel leftPanel;
	private JPanel rightPanel;
	
	private JSplitPane mainPane;
	
	private JLabel inputLabel;
	private JTextField jtfInput;
	
	private JLabel outputLabel;
	private JTextField jtfOutput;
	
	private JComboBox jcbLinearTypes;

	
	private JButton btnGenerateClaML;
	
	private JProgressBar progressBar;
	
    JLabel statusField = new JLabel("Not started yet!", JLabel.CENTER);
    SwingWorker worker;


	Map mapGeneratedCodes = new HashMap(); 

	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	

	
	final String[] linearTypes = {"Morbidity", "Mortality", "PrimaryCare", "Research", 
			"Speciality_Adaptation_Mental_Health", "Specialty_Adaptation_Dermatology", 
			"Specialty_Adaptation_Musculoskeletal", "Specialty_Adaptation_Neurology", 
			"Specialty_Adaptation_Paediatrics"};	
	

	public ICDClaMLPanel(OWLModel kb){

        owlModel = kb;
        //icdContentModel = new ICDContentModel(owlModel);
		
		this.initUI();

	}
	
	private void initUI(){
		leftPanel = new JPanel(new BorderLayout());
		rightPanel = new JPanel(new BorderLayout());
				
		
		//input pprj file path
		JPanel inputPanel = new JPanel();
		inputLabel = new JLabel("Umbrella pprj: ");
		jtfInput = new JTextField(40);
		jtfInput.setText("C:\\icdcontent\\icd_umbrella.pprj");
		inputPanel.add(inputLabel);
		inputPanel.add(jtfInput);
		
		
		//output claml xml file path
		JPanel outputPanel = new JPanel();
		outputLabel = new JLabel("ClaML output: ");
		jtfOutput = new JTextField(40);
		jtfOutput.setText("C:\\icdcontent\\icd11alpha_claml_2010916v1.0.xml");
		outputPanel.add(outputLabel);
		outputPanel.add(jtfOutput);

		    				
        //linearization types
		JPanel typePanel = new JPanel();
		JLabel typeLabel = new JLabel("Linearization Type: ");
		jcbLinearTypes = new JComboBox(this.linearTypes);
		jcbLinearTypes.addActionListener(this);
		typePanel.add(typeLabel);
		typePanel.add(jcbLinearTypes);
		

		// button generate claml
		JPanel btnPanel = new JPanel();
		btnGenerateClaML = new JButton("Generate ClaML...");
		btnGenerateClaML.addActionListener(startListener);

		

        
		btnPanel.add(btnGenerateClaML);

		
		JPanel progressPanel = new JPanel(new GridLayout(2,1));
	    progressBar = new JProgressBar(0, 100);
	    progressBar.setValue(0);
	    progressBar.setStringPainted(true);


	    
        progressPanel.add(progressBar);
        progressPanel.add(statusField);
        
		
		JPanel leftCorePanel = new JPanel(new GridLayout(5,1));
		leftCorePanel.add(inputPanel);
		leftCorePanel.add(outputPanel);
		leftCorePanel.add(typePanel);
		leftCorePanel.add(btnPanel);
		leftCorePanel.add(progressPanel);
		
		leftPanel.add(leftCorePanel, BorderLayout.NORTH);
	
		//rightPanel.add(progressPanel, BorderLayout.NORTH);
        
		scrollPane = new JScrollPane();
		textArea = new JTextArea();
		scrollPane.getViewport().add(textArea);
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		
		JLabel versionLabel = new JLabel(" This is ICD ClaMLizer version 0.1 - released at September 16, 2010.");
		JPanel versionPanel = new JPanel();
		versionPanel.add(versionLabel);
        JLabel instructionLabel = new JLabel(" See instruction at the website https://sites.google.com/site/icdclaml/");
		JPanel instructionPanel = new JPanel();
		instructionPanel.add(instructionLabel);
        JPanel rightUpperPanel = new JPanel(new GridLayout(2,1));
		rightUpperPanel.add(versionPanel);
		rightUpperPanel.add(instructionPanel);
		
		rightPanel.add(rightUpperPanel, BorderLayout.NORTH);
       
        
        
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				                  true,
				                  leftPanel,
				                  rightPanel);
		mainPane.setOneTouchExpandable(true);
		
		this.setLayout(new BorderLayout());
		this.add(mainPane, BorderLayout.CENTER);
		
	}
	
    public void actionPerformed(ActionEvent event){
    	
  
    }
	

	
    public Object generateClaML(){
    	
    	try{
    		textArea.setText(""); //clear
    		
    		Date startedDate = new Date();
    		long startedTime = startedDate.getTime();
    		
    		textArea.append("Started at " + startedDate.toString() + "\n");

    		String linearType = this.getSelectedLinearType();
    	
    		String pprjFileName = this.getUmbrellaProjectName();
    		Project prj = Project.loadProjectFromFile(pprjFileName, new ArrayList());
    		OWLModel localowlModel = (OWLModel) prj.getKnowledgeBase();
    		icdContentModel = new ICDContentModel(localowlModel);
    	
    		this.updateStatus(1);
    		if (Thread.interrupted()) {
    			throw new InterruptedException();
    		}	
    		textArea.append("Code generation has been started...\n");
    	
    		icdCategories = icdContentModel.getICDCategories();    	
    		this.updateStatus(5);
        
        
    		model = new ICDClaMLLinearizationHierarchicalStructure(icdContentModel, linearType);
    		model.initialize(icdCategories);
    		model.generateCodes(); 
    		mapGeneratedCodes = model.getGeneretadCodes();
    		
    		this.updateStatus(50);
        	
    		textArea.append("Code generation has been completed!\n");
    		
    		this.updateStatus(51);

    		textArea.append("ClaML rendering has been started...\n");
    		
    		//render xml file
        	ICDClaMLXMLRender render = new ICDClaMLXMLRender();
        	String claml = render.processingClaMLXML(icdContentModel, icdCategories, mapGeneratedCodes, model);
        	this.updateStatus(90);
    		textArea.append("ClaML rendering has been completed..\n");
    		
        	
        	String fileName = this.getClaMLOutputFileName();
        	this.saveClaMLXMLFile(fileName, claml);
        	
        	textArea.append("A ClaML xml file has been successfully saved at " + fileName + "\n");		

            
        	//ended time
            Date endedDate = new Date();
            long endedTime = endedDate.getTime();
            long elapsedTime = endedTime - startedTime;
            
        	textArea.append("Ended at " + endedDate.toString() + "\n");
        	textArea.append("Elapsed time: " + elapsedTime + " milliseconds\n");
        	   	
        	this.updateStatus(100);	   		
    	
    	}catch(InterruptedException e){
            updateStatus(0);
            return "Interrupted";  // SwingWorker.get() returns this
        	
        }
        return "Processing Completed!";
    	
    }	
    
    private void saveClaMLXMLFile(String fileName, String text){
    	FileOutputStream fos = null;
    	BufferedWriter bw = null;
    	try{
    		
    		fos = new FileOutputStream(fileName);
    		
    		bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
    		bw.write(text);
    		bw.close();
    		
    	}catch(IOException io){
    		io.printStackTrace();
    	}
    }
	
	// return umbrella project name from widget
	private String getUmbrellaProjectName(){		
		return jtfInput.getText();		
	}
	
	// return claml output file name from widget
	private String getClaMLOutputFileName(){		
		return jtfOutput.getText();
	}
	
	
	// return linear type from widget
	private String getSelectedLinearType(){	
		return (String)jcbLinearTypes.getSelectedItem();
	}
	

	   /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with 
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     */
    private void updateStatus(final int i) {
        Runnable doSetProgressBarValue = new Runnable() {
            public void run() {
                progressBar.setValue(i);
                
                if(i >= 50){
            		statusField.setText("ClaML Rendering...");
                }
                
            }
        };
        SwingUtilities.invokeLater(doSetProgressBarValue);
    }

	
    /**
     * This method represents the application code that we'd like to 
     * run on a separate thread.  It simulates slowly computing 
     * a value, in this case just a string 'All Done'.  It updates the 
     * progress bar every half second to remind the user that
     * we're still busy.
     * 
     * For testing purpose only
     * 
     */
    private Object doWork() {
        try {
            for(int i = 0; i < 100; i++) {
                updateStatus(i);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                Thread.sleep(500);
            }
        }
        catch (InterruptedException e) {
            updateStatus(0);
            return "Interrupted";  // SwingWorker.get() returns this
        }
        return "All Done";         // or this
    }
	
	
    /**
     * This action listener, called by the "Start" button, effectively 
     * forks the thread that does the work.
     */
    private ActionListener startListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            btnGenerateClaML.setEnabled(false);
            //interruptButton.setEnabled(true);
            statusField.setText("Code Generating...");

            /* Invoking start() on the SwingWorker causes a new Thread
             * to be created that will call construct(), and then
             * finished().  Note that finished() is called even if
             * the worker is interrupted because we catch the
             * InterruptedException in doWork().
             */
            worker = new SwingWorker() {
                public Object construct() {
                    return generateClaML();
                }
                public void finished() {
                    btnGenerateClaML.setEnabled(true);
                    //interruptButton.setEnabled(false);
                    statusField.setText(get().toString());
                }
            };
            worker.start();
           
        }
    };

    /**
     * This action listener, called by the "Cancel" button, interrupts
     * the worker thread which is running this.doWork().  Note that
     * the doWork() method handles InterruptedExceptions cleanly.
     */
    private ActionListener interruptListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            //interruptButton.setEnabled(false);
            worker.interrupt();
            //startButton.setEnabled(true);
        }
    };
	
}