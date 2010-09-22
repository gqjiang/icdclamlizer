package edu.stanford.bmir.icd.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ICD_API_Example {

    private static OWLModel owlModel;
    private static ICDContentModel icdContentModel;

    public static void main(String[] args) {
        Project prj = Project.loadProjectFromFile("C:\\icdcontent\\icd_umbrella.pprj", new ArrayList());
        owlModel = (OWLModel) prj.getKnowledgeBase();
        icdContentModel = new ICDContentModel(owlModel);

        //getICDcategories(); //takes around 90 secs to get the result back, that is why it is commented out
        //getCategoryDetails();
        //getChildren();
        //getParent();
        //getClamlRef();
        //getLinearizationInfo();
        
        getChapterNodes();
        
       // getSortingLabel();
        
        //linearizationParentTesting();    //testing invalid linearParent    
    }
    
    public static void getChapterNodes(){
    	Collection chapterNodes = new ArrayList();
    	chapterNodes = icdContentModel.getICDCategoryClass().getSubclasses(false);
		for(Iterator it = chapterNodes.iterator(); it.hasNext(); ){
			RDFSNamedClass icdChaptetNode = (RDFSNamedClass) it.next();
			String chapterCode = getRDFSClassCode(icdChaptetNode);
			
			if(chapterCode.equals("TBD")){
				Collection directSubclses = icdChaptetNode.getSubclasses(false);
				Collection indirectSubclses = icdChaptetNode.getSubclasses(true);
				System.out.println("chapterCode:" + chapterCode + "|" + icdChaptetNode.getBrowserText());
				System.out.println("NumDirect:" + directSubclses.size() + "|NumIndirect" + indirectSubclses.size());
				
				
			}
			
		}
		
    	
    }

    private static String getRDFSClassCode(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	String term = (String) icdCategory.getPropertyValue(icdContentModel.getIcdCodeProperty());
    	    	
    	if(term != null)
    		ret = term;
    	else{
    		ret = "TBD";
    	}
    	  	
    	return ret;
    	
    }	    
    
    public static void getICDcategories() {
        long t0 = System.currentTimeMillis();
        Collection<RDFSNamedClass> icdCategories = icdContentModel.getICDCategories();
        System.out.println("ICD Categories count: " + icdCategories.size() + " in time: " + ((System.currentTimeMillis() - t0)/1000) + " sec");
    }

    public static void getCategoryDetails() {
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#Class_2751");
        System.out.println("Displayed as in the tree: " + category.getBrowserText());

        RDFResource defTerm = icdContentModel.getTerm(category, icdContentModel.getDefinitionProperty());
        if (defTerm != null) {
            String definition = (String) defTerm.getPropertyValue(icdContentModel.getLabelProperty());

            String rubricId = (String) defTerm.getPropertyValue(icdContentModel.getIdProperty());
            System.out.println("Definition rubric id: " + rubricId);
            System.out.println("Definition: " + definition);

        }

        Collection<RDFResource> prefilledDefsTerm = icdContentModel.getTerms(category, icdContentModel.getPrefilledDefinitionProperty());
        System.out.println("\nPrefilled defintion terms: " + prefilledDefsTerm);
    }

    public static void getChildren() {
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#H34.2");
        Collection children =  icdContentModel.getChildren(category);
        for(Iterator it = children.iterator(); it.hasNext();){
        	RDFSNamedClass child = (RDFSNamedClass) it.next();
            System.out.println("Children of " + category.getURI() + " : " + child.getBrowserText() + " : " + child.getName());
                    	
        }
   }
    
    public static void getParent() {
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#H34.2");
        System.out.println("Parent of " + category.getBrowserText() + " : " + category.getSuperclasses(false));
    }
    
    
    public static void getClamlRef() {
    	
    	//A51.0 / R75
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#H34.2");
        System.out.println("\n" + category.getBrowserText());
        Collection<RDFResource> exclusionTerms = category.getPropertyValues(icdContentModel.getExclusionProperty());
        for (RDFResource exclusionTerm : exclusionTerms) {
            System.out.println("\tExclusion: " + exclusionTerm.getBrowserText());
            Collection<RDFResource> clamlRefs = exclusionTerm.getPropertyValues(icdContentModel.getClamlReferencesProperty());
            if (clamlRefs != null) {
                for (RDFResource clamlRef : clamlRefs) {
                    System.out.println("\t\tClaml Reference: " + clamlRef.getBrowserText());
                    System.out.println("\t\tClaml text: " + clamlRef.getPropertyValue(icdContentModel.getTextProperty()));
                    System.out.println("\t\tClaml usage: " + clamlRef.getPropertyValue(icdContentModel.getUsageProperty()));
                    System.out.println("\t\tClaml category ref in ICD: " + ((RDFResource)clamlRef.getPropertyValue(icdContentModel.getIcdRefProperty())).getBrowserText());
                }
            }
        }
    }

    public static void getLinearizationInfo() {
    	
    	//A65-A69
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#H34.2");
        System.out.println("\n" + category.getBrowserText());

       Collection<RDFResource> linearizationSpecs = icdContentModel.getLinearizationSpecifications(category);
       for (RDFResource linearizationSpec : linearizationSpecs) {
           RDFResource linearization = (RDFResource) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationViewProperty());
           RDFSNamedClass linearizationParent = (RDFSNamedClass) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationParentProperty());
           Boolean isIncludedInLinearization = (Boolean) linearizationSpec.getPropertyValue(icdContentModel.getIsIncludedInLinearizationProperty());
           String linSortingLabel = (String) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationSortingLabelProperty());
           

           
           
           System.out.println("Linearization: " + linearization.getBrowserText() +
                   "; is included: " + (isIncludedInLinearization == null ? "(not specified)" : isIncludedInLinearization) +
                   "; linearization parent: " + linearizationParent.getBrowserText() +
                   "; linearization sorting label: " + (linSortingLabel == null ? "(not specified)" : linSortingLabel));
       }
    }
    

    
    public static void getSortingLabel(){
    	
        RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#M60");
        System.out.println("Displayed as in the tree: " + category.getBrowserText());
        
        String slabel = (String)category.getPropertyValue(icdContentModel.getSortingLabelProperty());

        //RDFResource defTerm = icdContentModel.getTerm(category, icdContentModel.getSortingLabelProperty());
        if (slabel != null) {
            //String sortingLabel = (String) slabel.getBrowserText();

            //String rubricId = (String) defTerm.getPropertyValue(icdContentModel.getIdProperty());
           // System.out.println("Definition rubric id: " + rubricId);
            System.out.println("SortingLabel: " + slabel);

        }
    	
    }
    
    public static void linearizationParentTesting(){
    	Collection<RDFSNamedClass> icdCategories = icdContentModel.getICDCategories();
        String linearizationType = "Morbidity"; 
        
    	for(Iterator it = icdCategories.iterator(); it.hasNext();){
    		RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
    		
    		if(hasMultipleParents(icdCategory)){
    			RDFSNamedClass linearParent = getLinearizationParentForType(icdCategory, linearizationType);
    			String slabel = (String)icdCategory.getPropertyValue(icdContentModel.getSortingLabelProperty());
    			if(!icdCategories.contains(linearParent)){
    				System.out.println("icdCategory=" + icdCategory.getBrowserText() + "|linearParent=" + linearParent.getBrowserText() + "|soringLabel=" + slabel);
    			}
    		}
    		
    		
    	}
    	
    	System.out.println("Processing is done!");
    	
    }
    	
    	
    
    //if having multiple parents
    private static boolean hasMultipleParents(RDFSNamedClass category){
    	
    	boolean ret = false;
    	
    	Collection superclses = category.getSuperclasses(false);
    	if(superclses.size() > 1){
    		ret = true;
    	}
    	
    	return ret;
    	
    }   	
    	

    private static RDFSNamedClass getLinearizationParentForType(RDFSNamedClass icdCategory, String type){
 	   RDFSNamedClass ret = null;
        Collection<RDFResource> linearizationSpecs = icdContentModel.getLinearizationSpecifications(icdCategory);
        for (RDFResource linearizationSpec : linearizationSpecs) {
            RDFResource linearization = (RDFResource) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationViewProperty());
            RDFSNamedClass linearizationParent = (RDFSNamedClass) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationParentProperty());
            Boolean isIncludedInLinearization = (Boolean) linearizationSpec.getPropertyValue(icdContentModel.getIsIncludedInLinearizationProperty());
            
            if(linearization.getBrowserText().equals(type)){
         	   ret = linearizationParent;
         	   break;
            }

        }	   
 	   
 	   
 	   return ret;	   
    }
}
