package edu.mayo.informatics.icd.claml;

import java.util.*;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ICDClaMLLinearizationHierarchicalStructure {
	
	private ICDContentModel icdContentModel = null;
	private Collection icdCategories = new ArrayList();
	private Collection icdChapterNodes = new ArrayList();
	private Map mapGeneratedCodes = new HashMap();
	private Map mapRegisterLinearParent = new HashMap();
	
	private Map mapLinearParent = new HashMap();
	private Map mapLinearChildren = new HashMap();
	
	private int progressIndex = 0;

    private String[] chaptercodes = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII",
    		"IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII",
    		"XIX", "XX", "XXI", "XXII"};
    
    private String[] tbdcodes = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
    		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
    		"W", "X", "Y", "Z","A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2", "I2",
    		"J2", "K2", "L2", "M2", "N2", "O2", "P2", "Q2", "R2", "S2", "T2", "U2", "V2",
    		"W2", "X2", "Y2", "Z2","A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3", "I3",
    		"J3", "K3", "L3", "M3", "N3", "O3", "P3", "Q3", "R3", "S3", "T3", "U3", "V3",
    		"W3", "X3", "Y3", "Z3","A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4", "I4",
    		"J4", "K4", "L4", "M4", "N4", "O4", "P4", "Q4", "R4", "S4", "T4", "U4", "V4",
    		"W4", "X4", "Y4", "Z4","A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5", "I5",
    		"J5", "K5", "L5", "M5", "N5", "O5", "P5", "Q5", "R5", "S5", "T5", "U5", "V5",
    		"W5", "X5", "Y5", "Z5","A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6", "I6",
    		"J6", "K6", "L6", "M6", "N6", "O6", "P6", "Q6", "R6", "S6", "T6", "U6", "V6",
    		"W6", "X6", "Y6", "Z6"};
    
	private String[] missingCodes = {"OEAE", "LDDD", "BACFCB", "GDJBE", "GDJBEA"};
    
    private String linearType = "Morbidity";
	
	public ICDClaMLLinearizationHierarchicalStructure(ICDContentModel icdContentModel, String linearType){
		this.icdContentModel = icdContentModel;
		//this.linearType = linearType;
		//this.initializeTest();
	}

	public void initializeTest(){
		this.icdCategories = icdContentModel.getICDCategories();
		//this.icdCategories = icdCategories;
		this.icdChapterNodes = icdContentModel.getICDCategoryClass().getSubclasses(false);
	}	
	
	public void initialize(Collection icdCategories){
		//this.icdCategories = icdContentModel.getICDCategories();
		this.icdCategories = icdCategories;
		this.icdChapterNodes = icdContentModel.getICDCategoryClass().getSubclasses(false);
	}
	
	public int getProgressIndex(){
		
		return this.progressIndex;
		
	}
	
	public void generateCodes(){
	    int index = 0;
	    
	    try{
	    
		for(Iterator it = icdChapterNodes.iterator(); it.hasNext(); ){
			RDFSNamedClass icdChaptetNode = (RDFSNamedClass) it.next();
			String chapterCode = this.getRDFSClassCode(icdChaptetNode);
			
			if(!chapterCode.equals("TBD")){
			
				String assignedChapterCode = this.getAssignedChapterCode(chapterCode);
			
				if(!mapGeneratedCodes.containsKey(icdChaptetNode)){
					mapGeneratedCodes.put(icdChaptetNode, assignedChapterCode);
				
				}
			
				//generate codes by different linear type
				this.generateCodesFromSeed(icdChaptetNode, assignedChapterCode, linearType);

				index ++;
				
			}
			
			//Thread.sleep(500);
			//int pIndex = (index/icdChapterNodes.size())*200;
			//this.progressIndex = pIndex;
			//if(index > 0){
				//break;
			//}
			
			
		}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		

		
	}
	
	public Map getGeneretadCodes(){
		//System.out.println("Codes generated: " +  mapGeneratedCodes.size());
		return this.mapGeneratedCodes;
	}
	
	public Object[] getLinearSubclassCodes(RDFSNamedClass icdCategory){
		return (Object[]) this.mapLinearChildren.get(icdCategory);
	}
	
	
	public String getLinearParentCode(RDFSNamedClass icdCategory){		
		RDFSNamedClass linearParent = this.getLinearizationParentForType(icdCategory, linearType);	
		return (String)this.mapGeneratedCodes.get(linearParent);
		
	}
	
	
	//recursively generate codes
	private void generateCodesFromSeed(RDFSNamedClass icdCategory, String seedCode, String linearType){
		
		Collection subclasses = icdCategory.getSubclasses(false);
		Collection linearSubclasses = new ArrayList();
		for(Iterator it = subclasses.iterator(); it.hasNext();){
			RDFSNamedClass subclass = (RDFSNamedClass) it.next();
			if(this.isLinearizationParent(subclass, icdCategory, linearType)){
				linearSubclasses.add(subclass);
			}else{
				
				//System.out.println("Is not linear parent:|" + icdCategory.getBrowserText() + "|" + icdCategory.getURI() + "|subclass:" + subclass.getBrowserText() + "|" + subclass.getURI());
				
			}
			
		}
		this.generatedSubclassCodesWithSortingLabels(icdCategory, linearSubclasses, seedCode);
		
		
		//recursive
		for(Iterator it1 = linearSubclasses.iterator(); it1.hasNext();){
			
			RDFSNamedClass linearSubcls = (RDFSNamedClass) it1.next();
			String seedSubcode = (String) this.mapGeneratedCodes.get(linearSubcls);
			generateCodesFromSeed(linearSubcls, seedSubcode, linearType);
		}	
				
	}
	
    //generate subclass codes using sorting label information
    private void generatedSubclassCodesWithSortingLabels(RDFSNamedClass icdCategory, Collection subclses, String seedCode){
    	
    	Map mapLabels = new HashMap();
    	Collection colSortingLabels = new ArrayList();
    	for(Iterator it = subclses.iterator(); it.hasNext();){
    		RDFSNamedClass subcls = (RDFSNamedClass) it.next();
    		//It seems that sorting label may duplicate, so we added additional text to keep it unique
    		String sortingLabel = this.getSortingLabelForSubclass(subcls) + subcls.getBrowserText(); 
    		colSortingLabels.add(sortingLabel);
    		mapLabels.put(sortingLabel, subcls);

    	}
    
        Object[] arraySortedCodes = new Object[colSortingLabels.size()];	
    	Object[] arraySortingLabels = colSortingLabels.toArray();
		Arrays.sort(arraySortingLabels);
		
		
		int arraySize = arraySortingLabels.length;
		if(arraySize > 26){
			System.out.println("too many|" + icdCategory.getBrowserText() + "|" + arraySize);
			
		}		
		for(int i = 0; i < arraySortingLabels.length; i++){
			
            if(i > 154){
            	System.out.println("size>154, check please!");
            	break;
            }
			
			String slabel = (String) arraySortingLabels[i];
			String scode = seedCode + this.tbdcodes[i];
			

			
			arraySortedCodes[i] = scode;
			RDFSNamedClass ssubcls = (RDFSNamedClass)mapLabels.get(slabel);
			
			//debug
			//if(this.isMissingCodes(scode)){
			//	System.out.println("Got the missing code:" + scode + "|" + ssubcls.getBrowserText());
			//	this.mapGeneratedCodes.put(ssubcls, scode);
			//}			
			
			
			//need debug, why ssubcls is the same
			if(!mapGeneratedCodes.containsKey(ssubcls)){
				this.mapGeneratedCodes.put(ssubcls, scode);
				//System.out.println(scode);
				//debug
				if(this.isMissingCodes(scode)){
					System.out.println("Not the missing code:" + scode + "|" + ssubcls.getBrowserText() + "|" + ssubcls.getURI());
					
				}					
				
			}else{
				
				String dupCode = (String) this.mapGeneratedCodes.get(ssubcls);
				
				System.out.println("duplicate code:" + dupCode + "|missing code:" + scode + "|" + ssubcls.getBrowserText());
				
				
			}
			
		} 
		
		this.mapLinearChildren.put(icdCategory, arraySortedCodes);
    }   
    
    private boolean isMissingCodes(String code){
    	
    	boolean ret = false;
    	
    	for(int i = 0; i < this.missingCodes.length; i++){
    		
    		if(missingCodes[i].equals(code)){
    			ret = true;
    			break;
    		}
    		
    	}
    	
    	return ret;
    	
    }
    
    
    private String getSortingLabelForSubclass(RDFSNamedClass icdCategory){
    	
        //RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#Class_1547");
        //System.out.println("Displayed as in the tree: " + category.getBrowserText());
        String ret = "";
        String slabel = (String)icdCategory.getPropertyValue(icdContentModel.getSortingLabelProperty());
        if(slabel != null){
        	ret = slabel;
        }else{
        	ret = icdCategory.getBrowserText();
        }
        
        return ret;
        
    }       
	
    private String getRDFSClassCode(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	String term = (String) icdCategory.getPropertyValue(icdContentModel.getIcdCodeProperty());
    	    	
    	if(term != null)
    		ret = term;
    	else{
    		ret = "TBD";
    	}
    	  	
    	return ret;
    	
    }	

    /**
     *  get linearization parent for a specific type
     * 
     * @param icdCategory
     * @param type
     * Linearization: Mortality;
     * Linearization: PrimaryCare;
     * Linearization: Research; 
     * Linearization: Speciality_Adaptation_Mental_Health; 
     * Linearization: Specialty_Adaptation_Dermatology; 
     * Linearization: Specialty_Adaptation_Musculoskeletal; 
     * Linearization: Specialty_Adaptation_Neurology; 
     * Linearization: Specialty_Adaptation_Paediatrics; 
     * Linearization: Morbidity;
     * @return
     * 
     */
    private RDFSNamedClass getLinearizationParentForType(RDFSNamedClass icdCategory, String type){
 	   RDFSNamedClass ret = null;
 	   if(this.hasMultipleParent(icdCategory)){ //if having multiple parent
 	   

        Collection<RDFResource> linearizationSpecs = icdContentModel.getLinearizationSpecifications(icdCategory);
        for (RDFResource linearizationSpec : linearizationSpecs) {
            RDFResource linearization = (RDFResource) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationViewProperty());
            RDFSNamedClass linearizationParent = (RDFSNamedClass) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationParentProperty());
            Boolean isIncludedInLinearization = (Boolean) linearizationSpec.getPropertyValue(icdContentModel.getIsIncludedInLinearizationProperty());

            if(linearization.getBrowserText().equals(type)){//only for a specific type
            
            if(linearizationParent != null){

        		Collection linearParentSubclasses = linearizationParent.getSubclasses(false); // check if linear parent is real
        		if(!linearParentSubclasses.contains(icdCategory)){
        			RDFSNamedClass randomLinearParent = this.getRandomSingleParent(icdCategory);
        			ret = randomLinearParent;
        			
        			//System.out.println("not real linear parent:|" + icdCategory.getURI() + "|" + randomLinearParent.getURI());
        			break;
        		}else{            	
            	
                	ret = linearizationParent;
                	break;
        		}
            	
            }else{ //no linear parent specified
     		   RDFSNamedClass parent = this.getRandomSingleParent(icdCategory); // get random single parent
     		   if(!mapRegisterLinearParent.containsKey(icdCategory)){
     			   mapRegisterLinearParent.put(icdCategory, parent);
     			   //System.out.println("icdCategory:" + icdCategory.getBrowserText() + "|linearParent(random):" + parent.getBrowserText() );
     		   }
     			   ret = parent;
     			   break;
         	   
            }
            }

        }	   
 	   }else{ //having single parent
 		   

 		   RDFSNamedClass parent = this.getSingleParent(icdCategory);
 		   
 		   ret = parent;

 		   
		   
 	   }
 	   
 	   
 	   return ret;	   
    }
        
    //get parent calss if it is a single parent
    private RDFSNamedClass getSingleParent(RDFSNamedClass icdCategory){
    	RDFSNamedClass ret = null;
    	
    	Collection parents = icdCategory.getSuperclasses(false);
    	if(parents.size() == 1){
    		for(Iterator it = parents.iterator(); it.hasNext();){
    			RDFSNamedClass parent = (RDFSNamedClass) it.next();
    			ret = parent;
    			break;
    		}
    		
    	}
    	
    	return ret;
    }
    
    // get a random parent (first one)
    
    private RDFSNamedClass getRandomSingleParent(RDFSNamedClass icdCategory){
    	RDFSNamedClass ret = null;
    	
    	Collection parents = icdCategory.getSuperclasses(false);
    		for(Iterator it = parents.iterator(); it.hasNext();){
    			RDFSNamedClass parent = (RDFSNamedClass) it.next();
    			ret = parent;
    			break;
    		}
    	
    	return ret;
    }       
    
    private boolean isLinearizationParent(RDFSNamedClass child, RDFSNamedClass parent, String type){
    	
    	boolean ret = false;
    	
    	RDFSNamedClass linearParent = this.getLinearizationParentForType(child, type);
    	if(linearParent.equals(parent)){
    		ret = true;
    	}else{
    		
    		//System.out.println("LinearParent|" + linearParent.getBrowserText() + "|" + linearParent.getURI());
    		//System.out.println("CurrentParent|" + parent.getBrowserText() + "|" + parent.getURI());
    	}
    	
    	return ret;
    	
    }
    
    private boolean hasMultipleParent(RDFSNamedClass icdCategory){
    	
    	boolean ret = false;
    	
    	Collection parents = icdCategory.getSuperclasses(false);
    	if(parents.size() > 1)
    		ret = true;
    	
    	return ret;
    	
    }
        
    
    
	private int getChapaterIndex(String code){
		int ret = 0;
		
		for(int i = 0; i < chaptercodes.length; i++){
			if(chaptercodes[i].equals(code)){
				ret = i;
				break;
			}
			
		}
		
		return ret;
		
	}
	
	
	private String getAssignedChapterCode(String code){
		int index = this.getChapaterIndex(code);
		
		return tbdcodes[index];
		
	}
	
	public static void main(String[] args){
        Project prj = Project.loadProjectFromFile("c:\\icdcontent\\icd_umbrella.pprj", new ArrayList());
        OWLModel owlModel = (OWLModel) prj.getKnowledgeBase();
        ICDContentModel icdContentModel = new ICDContentModel(owlModel); 
        ICDClaMLLinearizationHierarchicalStructure model = new ICDClaMLLinearizationHierarchicalStructure(icdContentModel, "Morbidity");
        model.generateCodes();
        model.getGeneretadCodes();
	}
	

}
