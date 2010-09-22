package edu.mayo.informatics.icd.claml;



import edu.mayo.bmi.guoqian.claml.ClaML;
import edu.mayo.bmi.guoqian.claml.ClassKind;
import edu.mayo.bmi.guoqian.claml.ClassKinds;
import edu.mayo.bmi.guoqian.claml.Identifier;
import edu.mayo.bmi.guoqian.claml.Label;
import edu.mayo.bmi.guoqian.claml.Meta;
import edu.mayo.bmi.guoqian.claml.ObjectFactory;
import edu.mayo.bmi.guoqian.claml.Reference;
import edu.mayo.bmi.guoqian.claml.Rubric;
import edu.mayo.bmi.guoqian.claml.RubricKind;
import edu.mayo.bmi.guoqian.claml.RubricKinds;
import edu.mayo.bmi.guoqian.claml.SubClass;
import edu.mayo.bmi.guoqian.claml.SuperClass;
import edu.mayo.bmi.guoqian.claml.Title;
import edu.mayo.bmi.guoqian.claml.UsageKind;
import edu.mayo.bmi.guoqian.claml.UsageKinds;

import java.util.*;
import java.io.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

//first implementation of new lexwiki templates
public class ICDClaMLXMLExporter {

  private int rubricIndex = 1000000;

  private OWLModel owlModel;
  private ICDContentModel icdContentModel;
  
  private Map mapGeneratedCodes = new HashMap();
  private Collection colCodesNotAssigned = new ArrayList();
  private Collection colTempCodesNotAssigned = new ArrayList();
  
  private Collection colCategoriesXII = new ArrayList();
  
  private Collection noSortingLabelCategories = new ArrayList();
  
  private Collection colAllExistingICDCodes = new ArrayList();
  
  private Map mapAllExistingICDCodes = new HashMap();
  
  private ICDClaMLLinearizationHierarchicalStructure model;
  
  private String[] missingCodes = {"OEAE", "LDDD", "BACFCB", "GDJBE", "GDJBEA"};
  
  
	public ICDClaMLXMLExporter() {
        Project prj = Project.loadProjectFromFile("c:\\icdcontent\\icd_umbrella.pprj", new ArrayList());
        owlModel = (OWLModel) prj.getKnowledgeBase();
        icdContentModel = new ICDContentModel(owlModel);         
	}




    
    private Collection getICDChapters(){
    	Collection ret = new ArrayList();
    	
    	ret = icdContentModel.getICDCategoryClass().getSubclasses(false);
    	   	
    	return ret;
    	
    }
    
    
    //if having multiple parents
    private boolean hasMultipleParents(RDFSNamedClass category){
    	
    	boolean ret = false;
    	
    	Collection superclses = category.getSuperclasses(false);
    	if(superclses.size() > 1){
    		ret = true;
    	}
    	
    	return ret;
    	
    }
    
    private Collection getSiblingClassesWithCodeTBD(RDFSNamedClass icdCategories){
    	
    	Collection ret = new ArrayList();
    	Collection superclses = icdCategories.getSuperclasses(false);
    	for(Iterator it = superclses.iterator(); it.hasNext();){
    		RDFSNamedClass parent = (RDFSNamedClass) it.next();
    		Collection subclses = parent.getSubclasses(false);
    		for(Iterator it1 = subclses.iterator(); it1.hasNext();){
    			RDFSNamedClass child = (RDFSNamedClass) it1.next();
    			String childCode = this.getClassCode(child);
    			if(childCode.equals("TBD")){
    				ret.add(child);
    				//System.out.println("childCode:" + childCode);
    			    
    			}
    		}
    		break;
    		
    	} 	
    	
    	return ret;
    	
    }
    
    
    private Object[] getSortedSubclassCodes(RDFSNamedClass category){
    	
    	Collection subclsCodes = new ArrayList();
    	Object[] ret = null;
    	Collection subclses = category.getSubclasses(false);
    	for(Iterator it = subclses.iterator(); it.hasNext();){
    		RDFSNamedClass subcls = (RDFSNamedClass) it.next();
    		String subclsCode = this.getClassCode(subcls);
    		subclsCodes.add(subclsCode);
    	}
    	
    	ret = subclsCodes.toArray();
    	
    	Arrays.sort(ret);
    	
    	for(int i = 0; i < ret.length; i++){
    		System.out.println((String)ret[i]);
    	}
    	
    	return ret;
    }
    
    
    //get one sibling code
    private String getOneSiblingCode(RDFSNamedClass icdCategory){
    	
    	String retCode = "TBD";
    	
    	String code = this.getClassCode(icdCategory);
    	
    	Collection superclses = icdCategory.getSuperclasses(false);
    	for(Iterator it = superclses.iterator(); it.hasNext();){
    		
    		RDFSNamedClass parent = (RDFSNamedClass) it.next();
    		Collection subclses = parent.getSubclasses(false);
    		for(Iterator it1 = subclses.iterator(); it1.hasNext();){
    			RDFSNamedClass subcls = (RDFSNamedClass) it1.next();
    			String subclsCode = this.getClassCode(subcls);
    			if(!subclsCode.equals("TBD")){
    				retCode = subclsCode;
    				break;
    			}
    		}    		
    	}
    	
    	
    	return retCode;
    	
    	
    }
    
    //get one parent code
    private String getOneParentCode(RDFSNamedClass icdCategory){
    	
    	String retCode = "TBD";
    	Collection superclses = icdCategory.getSuperclasses(false);
    	for(Iterator it = superclses.iterator(); it.hasNext();){
    		RDFSNamedClass parent = (RDFSNamedClass) it.next();
    		String parentCode = this.getClassCode(parent);
    		if(!parentCode.equals("TBD")){
    			retCode = parentCode;
    			break;
    		}
    	}
    	

    	return retCode;
    	
    }
    
    private String getClassCode(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	String term = (String) icdCategory.getPropertyValue(icdContentModel.getIcdCodeProperty());
    	
    	
    	if(term != null)
    		ret = term;
    	else{
    		ret = "TBD";
    		
    		//String generatedCode = (String)this.mapGeneratedCodes.get(icdCategory);
    		//if(generatedCode != null){
    		//	ret = generatedCode;
    		//}
    		
    		//if(ret.equals("TBD")){
    		//	System.out.println("No code generated for:" + icdCategory.getBrowserText());
    		//}
    	}
    	  	
    	return ret;
    	
    }
    
    private Collection getAllExistingICDCodes(Collection icdCategories){
    	
    	Collection ret = new ArrayList();
    	
    	for(Iterator it = icdCategories.iterator(); it.hasNext();){
    		RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
    		String code = this.getClassCode(icdCategory);
    		if(!code.equals("TBD")){
    			if(!ret.contains(code)){
    				ret.add(code);
    			}
    			
    			if(!this.mapAllExistingICDCodes.containsKey(code)){
    				this.mapAllExistingICDCodes.put(code, icdCategory);
    			}
    			
    		}
    	}
    	
    	
    	return ret;
    	
    }
    
    //get generated codes for a original icd code
    private String getGeneratedCodeForICDCategoryCode(String code){
    	RDFSNamedClass icdCategory = (RDFSNamedClass) this.mapAllExistingICDCodes.get(code);
    	return (String) this.mapGeneratedCodes.get(icdCategory);
    }
    
    private String getClassICatId(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	String term = icdCategory.getName();
    	
   
    	if(term != null){
    		ret = term;
    		//System.out.println("id available for:" + ret);
    	
    	}else{
    		//System.out.println("no id available for:" + icdCategory.getBrowserText());
    		ret = "TBD";
    		
    		//String generatedCode = (String)this.mapGeneratedCodes.get(icdCategory);
    		//if(generatedCode != null){
    		//	ret = generatedCode;
    		//}
    		
    		//if(ret.equals("TBD")){
    		//	System.out.println("No code generated for:" + icdCategory.getBrowserText());
    		//}
    	}
    	  	
    	return ret;
    	
    }   
    
    private String getClassKind(RDFSNamedClass icdCategory){
    	   	
    	String ret = "category";
    	String term = (String) icdCategory.getPropertyValue(icdContentModel.getKindProperty());
    	if(term != null)
    		
    		if(term.equals("modifiedcategory"))
    			ret = "category";
    		else
    			ret = term;
    	else{
    	    Collection parents = icdCategory.getSuperclasses(false);
    	    for(Iterator it = parents.iterator(); it.hasNext();){
    	    	RDFSNamedClass parent = (RDFSNamedClass) it.next();
    	    	String pkind = (String) parent.getPropertyValue(icdContentModel.getKindProperty());
    	    	if(pkind != null){
    	    		if(pkind.equals("chapter")){
    	    			ret = "block";
    	    		}else{
    	    			ret = "category";
    	    		}
    	    	}
    	    	
    	    }

    	}
    	
    	return ret;
    	
    }   
    
    private String[] tbdcodes = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
    		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
    		"W", "X", "Y", "Z","A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2", "I2",
    		"J2", "K2", "L2", "M2", "N2", "O2", "P2", "Q2", "R2", "S2", "T2", "U2", "V2",
    		"W2", "X2", "Y2", "Z2","A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3", "I3",
    		"J3", "K3", "L3", "M3", "N3", "O3", "P3", "Q3", "R3", "S3", "T3", "U3", "V3",
    		"W3", "X3", "Y3", "Z3"};
    
    private String[] chaptercodes = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII",
    		"IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII",
    		"XIX", "XX", "XXI", "XXII"};
    
    private Map generateTBDCodes(Collection icdCategories){
    	Map ret = new HashMap();
    	for(Iterator it = icdCategories.iterator(); it.hasNext();){
    		RDFSNamedClass category = (RDFSNamedClass) it.next();
    		String code = this.getClassCode(category);
    		
    		if(code.equals("TBD")){
    			
    			Collection superclses = category.getSuperclasses(false);
    			if(superclses != null){
    				int index = 1;
    				for(Iterator it1 = superclses.iterator(); it1.hasNext();){
    					RDFSNamedClass supercls = (RDFSNamedClass) it1.next();
    					String supercode = this.getClassCode(supercls);
                            //subcode = supercode + tbdcodes[index-1];

                        System.out.println(code + "|" + supercode + "|" + category.getBrowserText() + "|" + supercls.getBrowserText());
                       
                            index++;
    				}
    			}
    		} 
    	}

    	
    	
    	
    	return ret;
    	
    }
    
    private Collection getAllSubCategories(RDFSNamedClass icdCategory){
    	
    	Collection ret = icdCategory.getSubclasses(true);
    	
    	return ret;
    	
    	
    }
    
    
    private void generateIndependentCode(Collection icdTopNodes){
    	
    	for(Iterator it = icdTopNodes.iterator(); it.hasNext();){
    		
    		RDFSNamedClass icdChapter = (RDFSNamedClass) it.next();
    		String code = this.getClassCode(icdChapter);
    		
    	}
    	
    }
    

    
    //generate codes for category without an ICD code
    private void generateCodes(Collection icdCategories){
    	
    	Map mapCategoryLetter = new HashMap();
    	
    	for(Iterator it = icdCategories.iterator(); it.hasNext();){
    		RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
    		
    		String code = this.getClassCode(icdCategory);
    		//System.out.println(code + "|" + icdCategory.getBrowserText());
    		
    		if(code.equals("TBD")){
    			
    			//get all sibling classes with code tbd
    			Collection siblingclses = this.getSiblingClassesWithCodeTBD(icdCategory);
    			if(siblingclses != null){
    				int index = 1;
    				
    				Map mapUsercodeCategory = new HashMap();
    				Collection userCodes = new ArrayList();
  				
    				//loop once to get all others assigned
    				for(Iterator it11 = siblingclses.iterator(); it11.hasNext();){
    					
    					RDFSNamedClass siblingcls = (RDFSNamedClass) it11.next();
    				
    					
    					
    					//if(siblingcls.getBrowserText().indexOf("LA10z0") >= 0 ||
    						//siblingcls.getBrowserText().indexOf("LD32") >= 0){
    					//System.out.println("sibling:" + siblingcls.getBrowserText());
    					//}
    					
    					//get user assigned code
    					//String usercode = this.getUserAssignedCodeForLabel(siblingcls);
    					String usercode = siblingcls.getBrowserText();
    					//String usercode = this.getSortingLabelForSubclass(siblingcls); // introducing sorting label
    					String sortingLabel = this.getSortingLabelForSubclass(siblingcls);
    					usercode = sortingLabel + usercode;
    					
    					if(!userCodes.contains(usercode))
    						userCodes.add(usercode);
    					if(!mapUsercodeCategory.containsKey(usercode))
    						mapUsercodeCategory.put(usercode, siblingcls);
    					
    				}
                            //subcode = supercode + tbdcodes[index-1];

                        //System.out.println(code + "|" + supercode + "|" + category.getBrowserText() + "|" + supercls.getBrowserText());
                       
                            //index++;
    			
    				
    				//sorting usercodes
    				Object[] arrayUserCodes = userCodes.toArray();
    				Arrays.sort(arrayUserCodes);
    				for(int i = 0; i < arrayUserCodes.length; i++){
    					String userCode = (String) arrayUserCodes[i];
    					RDFSNamedClass theCategory = (RDFSNamedClass) mapUsercodeCategory.get(userCode);
    					String letter = tbdcodes[i];

    					this.assignCodeToCategory(theCategory, letter);
    					
    					if(!mapCategoryLetter.containsKey(theCategory)){
    						mapCategoryLetter.put(theCategory, letter);
    						//System.out.println(theCategory.getBrowserText() + "|" + letter);	
    					}
    					
    				}
    				
    			}

    		} 
    	}
    	
    	for(int h = 0; h < 20; h++){
    	
    	System.out.println("Num. Codes Not Assigned:" + colCodesNotAssigned.size());
    	for(Iterator it2 = colCodesNotAssigned.iterator(); it2.hasNext();){
    		RDFSNamedClass codeNotAssigned = (RDFSNamedClass) it2.next();
    		String parentCode = this.getParentCode(codeNotAssigned);
    		if(parentCode != null){
    			String letter = (String) mapCategoryLetter.get(codeNotAssigned);
    			
    			String gCode = parentCode + "." + letter;
    			
    			if(this.colAllExistingICDCodes.contains(gCode)){
    				gCode += "_" + letter; //uniqueness checking
    			}
    			
    			
    			if(parentCode.startsWith("Block")){
    				//String pcode = "Block" + this.getOrderNumber(letter);
    				String ppcode = (String) this.mapBlockSubcategoryCodes.get(parentCode);
    				
    				// generate codes by top-down approach for block subcategories
    				this.generatedCodeForBlock(codeNotAssigned, ppcode);
    				
    				//gCode = ppcode + letter;
    				//if(ppcode.equals("LM") || ppcode.equals("LD"))
    				//System.out.println(parentCode + "|" + ppcode + "|"  + gCode + "|" + letter);
    				
    			}else{
    			
    			
    			if(!mapGeneratedCodes.containsKey(codeNotAssigned)){
    				if(!mapGeneratedCodes.containsValue(gCode)){
    					mapGeneratedCodes.put(codeNotAssigned, gCode);
    				}else{
    					mapGeneratedCodes.put(codeNotAssigned, gCode+letter);

    				}
    			}
    			}
    			
    			//System.out.println("generatedCode:" + gCode + "|" + codeNotAssigned.getBrowserText());
    		}
    	}
    		colCodesNotAssigned = colTempCodesNotAssigned;
    		
    		if(colCodesNotAssigned.size() < 1) {
    			//System.out.println("loop num:" + h);
    			break;
    		}
    		colTempCodesNotAssigned = new ArrayList();
    		
    	}

    	
    }
    
    //generate block subcategory codes by top down approach
    private void generatedCodeForBlock(RDFSNamedClass child, String ppCode){
    	
    	RDFSNamedClass parent = this.getParentClass(child);
    	Collection userCodes = new ArrayList();
    	Map mapUserCodeCategory = new HashMap();
    	Collection subclses = parent.getSubclasses(false);
    	for(Iterator it = subclses.iterator(); it.hasNext();){
    		RDFSNamedClass subcls = (RDFSNamedClass) it.next();
    		String subclsCode = this.getClassCode(subcls);
    		
    		if(subclsCode.equals("TBD")){
    		
    		String userCode = subcls.getBrowserText();
    		//String userCode = this.getSortingLabelForSubclass(subcls); 
			String sortingLabel = this.getSortingLabelForSubclass(subcls);
			userCode = sortingLabel + userCode;
    			
    		if(!userCodes.contains(userCode)){
    			userCodes.add(userCode);
    		}
    		if(!mapUserCodeCategory.containsKey(userCode)){
    			mapUserCodeCategory.put(userCode,subcls);
    		}
    		}
    		
    		
    	}
    	
    	
    	
    	
		//sorting usercodes
		Object[] arrayUserCodes = userCodes.toArray();
		Arrays.sort(arrayUserCodes);
		for(int i = 0; i < arrayUserCodes.length; i++){
			String letter = this.tbdcodes[i];
			String userCode = (String)arrayUserCodes[i];
			String gCode = ppCode + letter;
			
			if(this.colAllExistingICDCodes.contains(gCode)){
				gCode += "_" + letter; //uniqueness checking
			}
			
			RDFSNamedClass theCategory = (RDFSNamedClass)mapUserCodeCategory.get(userCode);
			if(!mapGeneratedCodes.containsKey(theCategory)){
				if(!mapGeneratedCodes.containsValue(gCode)){
					mapGeneratedCodes.put(theCategory, gCode);
				}else{
					mapGeneratedCodes.put(theCategory, gCode+letter);					
				}
				//System.out.println(parent.getBrowserText() + "|" + theCategory.getBrowserText() + "|" + gCode);
			}
			
			
			
		}
    	
    	
    }
    
    
    private String getParentCode(RDFSNamedClass icdCategory){
    	String ret = null;
    	Collection parents = icdCategory.getSuperclasses(false);
    	for(Iterator it = parents.iterator(); it.hasNext();){
    		RDFSNamedClass parent = (RDFSNamedClass) it.next();
    		//System.out.println("parent"  + parent.getBrowserText() + "|" 
    		//		+ this.getUserAssignedCodeForLabel(parent));
    		
    		
    		if(mapGeneratedCodes.containsKey(parent)){
    			ret = (String)mapGeneratedCodes.get(parent);
    			break;
    		}else{
    			if(!colTempCodesNotAssigned.contains(icdCategory))
    				colTempCodesNotAssigned.add(icdCategory);
    		}
    	}
    	
    	return ret;
    	
    }

    //get Parent Category
    private RDFSNamedClass getParentClass(RDFSNamedClass icdCategory){
    	RDFSNamedClass ret = null;
    	Collection parents = icdCategory.getSuperclasses(false);
    	for(Iterator it = parents.iterator(); it.hasNext();){
    		RDFSNamedClass parent = (RDFSNamedClass) it.next();
    		//System.out.println("parent"  + parent.getBrowserText() + "|" 
    		//		+ this.getUserAssignedCodeForLabel(parent));
    		
    		
    		if(mapGeneratedCodes.containsKey(parent)){
    			//ret = (String)mapGeneratedCodes.get(parent);
    			ret = parent;
    			break;
    		}else{
    			if(!colTempCodesNotAssigned.contains(icdCategory))
    				colTempCodesNotAssigned.add(icdCategory);
    		}
    	}
    	
    	return ret;
    	
    }    
    
    
    
    private Map mapBlockSubcategoryCodes = new HashMap();
    
    private void assignCodeToCategory(RDFSNamedClass icdCategory, String letter){
    	String tempCode = this.getOneSiblingCode(icdCategory);
    	if(!this.colCategoriesXII.contains(icdCategory) && !tempCode.equals("TBD")){
    		tempCode = tempCode.substring(0, tempCode.length()-1) + letter;

    		if(this.colAllExistingICDCodes.contains(tempCode)){
				tempCode += "_" + letter; //uniqueness checking
			}
    		   		
    		
    		if(!mapGeneratedCodes.containsKey(icdCategory)){
    			if(!mapGeneratedCodes.containsValue(tempCode)){
    				mapGeneratedCodes.put(icdCategory, tempCode);
    			}else{
    				mapGeneratedCodes.put(icdCategory, tempCode + letter);
    				
    			}
    			//System.out.println("CodeAssigned|" + icdCategory.getBrowserText() + "|" + tempCode);
    		}
    	}else{
    		String tempParentCode = this.getOneParentCode(icdCategory);
    		
			if(this.colAllExistingICDCodes.contains(tempParentCode)){
				tempParentCode += "_" + letter; //uniqueness checking
			} 
    		
    		if(!tempParentCode.equals("TBD")){
    			
    			//for XII chapter
    			if(tempParentCode.equals("XII")){
    				tempParentCode = "Block" + this.getOrderNumber(letter);
    				
    				//register the subcategory code for blocks
    				if(!mapBlockSubcategoryCodes.containsKey(tempParentCode)){
    					mapBlockSubcategoryCodes.put(tempParentCode, "L" + letter);
    					//System.out.println(tempParentCode + "|" + "L" + letter + "|");    					
    				}
    				
    				
    				String subcode = "L" + letter;
    				
    				//if(subcode.equals("LMC") || subcode.equals("LDG"))
    					//System.out.println(tempParentCode + "|" + subcode + "|");
    			}else{
    				

    				
    				tempParentCode = tempParentCode + letter;
    			}
    			
        		if(!mapGeneratedCodes.containsKey(icdCategory)){
        			if(!mapGeneratedCodes.containsValue(tempParentCode)){
        				
        				mapGeneratedCodes.put(icdCategory, tempParentCode);
        			}else{
        				mapGeneratedCodes.put(icdCategory, tempParentCode+letter);
        				
        			}
        			//if(tempParentCode.equals("LMC") || tempParentCode.equals("LDG"))
        			//System.out.println("CodeAssigned|" + icdCategory.getBrowserText() + "|" + tempParentCode);

        		}else{
        			//System.out.println("CodeNotAssigned|" + icdCategory.getBrowserText());
        			
        		}
    			
    		}else{
    			if(!colCodesNotAssigned.contains(icdCategory))
    				colCodesNotAssigned.add(icdCategory);
    			//System.out.println("CodeNotAssigned|" + icdCategory.getBrowserText());
    		}
    	}
    	
    }
    
     
    private String getOrderNumber(String letter){
    	
    	String ret = "";
    	
    	for(int i = 0; i < tbdcodes.length; i++){
    		if(tbdcodes[i].equals(letter)){
    			
    			if(i < 9){
    				
    				int order = i + 1;
    				ret = "0" + order + "";
    				
    			}else{
    				int order = i + 1;
    				ret = "" + order + "";
    				
    			}
    			
    		}
    		
    	}
    	
    	
    	return ret;
    	
    }
    
    private void displayMultipleParents(Collection icdCategories){
    	
    	for(Iterator it = icdCategories.iterator(); it.hasNext();){
    		RDFSNamedClass category = (RDFSNamedClass) it.next();
    		String code = this.getClassCode(category);
    		if(this.hasMultipleParents(category)){
    			Collection superclses = category.getSuperclasses(false);
    			if(superclses != null){
    				int index = 1;
    				for(Iterator it1 = superclses.iterator(); it1.hasNext();){
    					RDFSNamedClass supercls = (RDFSNamedClass) it1.next();
    					String supercode = this.getClassCode(supercls);
                            //subcode = supercode + tbdcodes[index-1];

                        System.out.println(index + "|" + code + "|" + category.getBrowserText() + "|" + supercode + "|" + supercls.getBrowserText());
                       
                            index++;
    				}
    			}    			
    		}
    		
    	}
    }
    
   private String findSuperCode(RDFSNamedClass icdCategory){
	   
	   String ret = "TBD";
	   Collection superclses = icdCategory.getSuperclasses(false);
	   for(Iterator it = superclses.iterator(); it.hasNext();){
			RDFSNamedClass supercls = (RDFSNamedClass) it.next();
			String supercode = this.getClassCode(supercls);
			String code = this.getClassCode(icdCategory);
			String uSuperCode = supercode;
			String uCode = code;

			if(!uCode.equals("TBD")){
				if(uCode.indexOf(uSuperCode) >= 0){
					ret = uSuperCode;
				}
			}else{
				
				//uCode = this.getUserAssignedCodeForLabel(icdCategory);
				uCode = this.getSortingLabel(icdCategory);
				//if(uCode != null){
				if(supercode.equals("TBD")){
					//uSuperCode = this.getUserAssignedCodeForLabel(supercls);
					uSuperCode = this.getSortingLabel(supercls);
				}
				if(uCode.indexOf(uSuperCode) >= 0){
					//ret = uSuperCode;
					ret = (String) this.mapGeneratedCodes.get(supercls);
				}
				
			}
		   
		   
	   }
	   
	   if(ret == null){
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
   private String getLinearizationParentForType(RDFSNamedClass icdCategory, String type){
	   String ret = null;
	   if(this.hasMultipleParent(icdCategory)){ //if having multiple parent
	   

       Collection<RDFResource> linearizationSpecs = icdContentModel.getLinearizationSpecifications(icdCategory);
       for (RDFResource linearizationSpec : linearizationSpecs) {
           RDFResource linearization = (RDFResource) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationViewProperty());
           RDFSNamedClass linearizationParent = (RDFSNamedClass) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationParentProperty());
           Boolean isIncludedInLinearization = (Boolean) linearizationSpec.getPropertyValue(icdContentModel.getIsIncludedInLinearizationProperty());
           
           if(linearizationParent != null){
           
             if(linearization.getBrowserText().equals(type)){
        	   String code = this.getClassCode(linearizationParent);
        	   if(code.equals("TBD")){
        		   
        		   code = (String)this.mapGeneratedCodes.get(linearizationParent); 
        		   ret = code;
        		   break;
        	   }else{
        		   ret = code;
        		   break;
        	   }
            }
           }else{ //no linear parent specified
    		   RDFSNamedClass parent = this.getRandomSingleParent(icdCategory); // get random single parent
    		   String code = this.getClassCode(parent);
    		   if(code.equals("TBD")){
    			   
    			   //if tbd, then get generated code
    			   ret = (String) this.mapGeneratedCodes.get(parent);
    			   
    			   
    		   }else{
    			   ret = code;
    			   
    		   }
    		          	   
        	   
           }

       }	   
	   }else{ //having single parent
		   

		   RDFSNamedClass parent = this.getSingleParent(icdCategory);
		   String code = this.getClassCode(parent);
		   if(code.equals("TBD")){
			   
			   //if tbd, then get generated code
			   ret = (String) this.mapGeneratedCodes.get(parent);
			   
			   
		   }else{
			   ret = code;
			   
		   }
		   
	 		   
		   
	   }
	   
	   
	   return ret;	   
   }
   
    
   /*
    * is Included in a specific linearization
    */
   private boolean isIncludedInLinearizationForType(RDFSNamedClass icdCategory, String type){
	   boolean ret = false;
       Collection<RDFResource> linearizationSpecs = icdContentModel.getLinearizationSpecifications(icdCategory);
       for (RDFResource linearizationSpec : linearizationSpecs) {
           RDFResource linearization = (RDFResource) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationViewProperty());
           RDFSNamedClass linearizationParent = (RDFSNamedClass) linearizationSpec.getPropertyValue(icdContentModel.getLinearizationParentProperty());
           Boolean isIncludedInLinearization = (Boolean) linearizationSpec.getPropertyValue(icdContentModel.getIsIncludedInLinearizationProperty());
           
           if(linearization.getBrowserText().equals(type)){
        	   if(isIncludedInLinearization != null)
        		   ret = isIncludedInLinearization.booleanValue();
           //should deal with null situtation later
        	   break;
           }

       }	   
	   
	   
	   return ret;	   
   }   
   
   
    private Collection getSuperclassCodes(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection superclses = icdCategory.getSuperclasses(false);
    	if(superclses != null){
    		if(superclses.size() == 1){
    		for(Iterator it = superclses.iterator(); it.hasNext();){
    			RDFSNamedClass supercls = (RDFSNamedClass) it.next();
    			String code = this.getClassCode(supercls);
    			if(code.equals("TBD")){
    				String generatedCode = (String) this.mapGeneratedCodes.get(supercls);
    				if(generatedCode != null){
    					code = generatedCode;
    				}
    			}
    			ret.add(code);    		
    		}
    		}else{ //if multiple parents
    			
        		for(Iterator it = superclses.iterator(); it.hasNext();){
        			RDFSNamedClass supercls = (RDFSNamedClass) it.next();
        			String supercode = this.getClassCode(supercls);
        			String code = this.getClassCode(icdCategory);
        			String uSuperCode = supercode;
        			String uCode = code;

        			if(code.equals("TBD")){
        				//uCode = this.getUserAssignedCodeForLabel(icdCategory);
        				uCode = this.getSortingLabel(icdCategory);
        			}
        			
        			//if(code.equals("A51.0"))
        				//System.out.println(code + "|" + supercode + "|" + uSuperCode + "|" + uCode);
        			
        			
        			
        			String retSuperCode = this.findSuperCode(icdCategory);
        			//if(retSuperCode != null){
        			if(!retSuperCode.equals("TBD")){
        				ret.add(retSuperCode);
        				break;
        			}else{
        				if(!supercode.equals("TBD")){
        					ret.add(supercode);
        					break;
        				}else{

                			
                				String generatedCode = (String) this.mapGeneratedCodes.get(supercls);
                				if(generatedCode != null){
                					supercode = generatedCode;
                				}
                				//uSuperCode = this.getUserAssignedCodeForLabel(supercls);
                			if(!supercode.equals("TBD") && ret.size() < 1){      					
        					    ret.add(supercode);
        					    break;
                			}
        					//System.out.println("multiple parents:" + icdCategory.getBrowserText() + "|" + supercode);
        				}
        				
        				
        			}
        			//}//if not null
        			
        		}
    			
    		}
    	}
    	
    	return ret;
    }
    
    private Collection getSubclassCodes(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection subclses = icdCategory.getSubclasses(false);
    	if(subclses != null){
    		for(Iterator it = subclses.iterator(); it.hasNext();){
    			RDFSNamedClass subcls = (RDFSNamedClass) it.next();
    			String code = this.getClassCode(subcls);
    			if(code.equals("TBD")){
    				String generatedCode = (String) this.mapGeneratedCodes.get(subcls);
    				if(generatedCode != null){
    					code = generatedCode;
    				}
    			}
    			if(this.hasMultipleParent(subcls)){
    				if(this.isLinearizationParent(subcls, icdCategory)){
    					ret.add(code);
    				}
    			}else{
    				ret.add(code);
    			}
    		}
    	}
    	
    	return ret;    
    }

    //get sorted subclasses using sorting label information
    private Object[] getSubclassCodesSortingLabels(RDFSNamedClass icdCategory){
    	
    	Map mapLabelCode = new HashMap();
    	Collection colSortingLabels = new ArrayList();
    	
    	Collection subclses = icdCategory.getSubclasses(false);
    	if(subclses != null){
    		for(Iterator it = subclses.iterator(); it.hasNext();){
    			RDFSNamedClass subcls = (RDFSNamedClass) it.next();
    			String sortingLabel = this.getSortingLabelForSubclass(subcls);
    			String code = this.getClassCode(subcls);
    			if(code.equals("TBD")){
    				String generatedCode = (String) this.mapGeneratedCodes.get(subcls);
    				if(generatedCode != null){
    					code = generatedCode;
    				}
    			}
    			if(this.hasMultipleParent(subcls)){
    				if(this.isLinearizationParent(subcls, icdCategory)){
    					//ret.add(code);
    					mapLabelCode.put(sortingLabel, code);
    					colSortingLabels.add(sortingLabel);
    				}
    			}else{
    				//ret.add(code);
    				mapLabelCode.put(sortingLabel, code);
    				colSortingLabels.add(sortingLabel);
    			}
    		}
    	}
    	
    	Object[] arraySortingLabels = colSortingLabels.toArray();
		Arrays.sort(arraySortingLabels);
		Object[] ret = new Object[arraySortingLabels.length];
		for(int i = 0; i < arraySortingLabels.length; i++){
			String sLabel = (String) arraySortingLabels[i];
			String sCode = (String) mapLabelCode.get(sLabel);
			ret[i] = sCode;
			
			//if(sCode.equals("TBD"))
			    System.out.println("sortingLabel:" + sLabel + "|Code:" + sCode);
		}
    	
    	return ret;    
    }    
    
    private String getClassCodeForClass(RDFSNamedClass icdCategory){
    	
    	//get original code, if not availabel it is "TBD"
    	String classCode = getClassCode(icdCategory);
    	
    	//if the code is "TBD", then find it from generated codes
    	if(classCode.equals("TBD")){
    		String generatedCode = (String)this.mapGeneratedCodes.get(icdCategory);
    		if(generatedCode != null){
    			classCode = generatedCode;
    		}else{// in case, no code generated
    			System.out.println("Code not generated for:" + icdCategory.getBrowserText());
    		}
    	}  
    	
    	return classCode;
    }
    
    private boolean isLinearizationParent(RDFSNamedClass child, RDFSNamedClass parent){
    	
    	boolean ret = false;
    	
    	Collection linearParents = this.getSuperclassCodes(child);
    	String curParent = this.getClassCodeForClass(parent);
    	if(linearParents.contains(curParent)){
    		ret = true;
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
    
    // get a random parent
    
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
    
    private Collection getSynonyms(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection synonyms = icdContentModel.getTerms(icdCategory, icdContentModel.getSynonymProperty());
    	if(synonyms != null){
    		for(Iterator it = synonyms.iterator(); it.hasNext();){
    			RDFResource synonym = (RDFResource) it.next();
    			ret.add(synonym.getPropertyValue(icdContentModel.getSynonymProperty()));
    		
    		}
    	}
    	
    	return ret;    
    	
    }
            

    private String getClassPreferredLabel(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel.getIcdTitleProperty());
    	if(term != null)
    		ret = term.getBrowserText();
    	else
    		ret = "TBD";
    	  	
    	return ret;
    	
    } 
    
    private String getClassPreferredLabelRubricID(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel.getIcdTitleProperty());
    	if(term != null){
    		ret = (String) term.getPropertyValue(icdContentModel.getIdProperty());
    	}else{
    		ret = "TBD";
    	    System.out.println("preferred term id:TBD" + icdCategory.getBrowserText());
    	}
    	  	
    	return ret;
    	
    }     
    
    //for tbd codes
    private String getUserAssignedCodeForLabel(RDFSNamedClass icdCategory){
    	
    	String ret = "TBD";
    	
    	String label = icdCategory.getBrowserText();
    	//System.out.println("label:" + label);
    	if(!label.equals("TBD")){
    		//label = label.substring(1, 8);
    		//int index = label.indexOf("\\s");
    		//label = label.substring(0, index-1).trim();
    		label = label.replaceAll("'", "");
    		label = label.replaceAll(" ", "_");
    		label = label.substring(2);
    		int index = label.indexOf("_");
    		label = label.substring(0, index);
    		ret = label;
    		//System.out.println("usercode:" + ret);
    		
    	}
    	 	
    	return ret;
    	
    }

    //for tbd codes
    private String getUserAssignedLabel(RDFSNamedClass icdCategory){
    	
    	String ret = "TBD";
    	
    	String label = icdCategory.getBrowserText();
    	//System.out.println("label:" + label);
    	if(!label.equals("TBD")){
    		//label = label.substring(1, 8);
    		//int index = label.indexOf("\\s");
    		//label = label.substring(0, index-1).trim();
    		label = label.replaceAll("'", "");
    		label = label.replaceAll(" ", "_");
    		label = label.substring(2);
    		
    		//System.out.println("label1:" + label);
    		
    		int index = label.indexOf("_");
    		
    		label = label.substring(index+1);
    		
        	//System.out.println("label2:" + label);    	
   		
    		label = label.replaceAll("_", " ");
    		//System.out.println("usercode:" + ret);
    		
    		ret = label;
    		
    	}
    	
    	//System.out.println("label:" + ret);    	
    	   	
    	return ret;
    	
    }    
    
    private String getClassDefinition(RDFSNamedClass icdCategory){
    	
    	String ret = "TBD";
    	RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel.getDefinitionProperty());
    	if(term != null){
    		//ret = term.getBrowserText();
    		String definition = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    		if(definition != null) ret = definition;
    		//System.out.println("definition id:" + definition);
    	}else{
    		ret = "TBD";
    	}
    	  	
    	return ret;
    	
    }    

    private String getClassDefinitionRubricID(RDFSNamedClass icdCategory){
    	
    	String ret = "";
    	RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel.getDefinitionProperty());
    	if(term != null){
    		ret = (String) term.getPropertyValue(icdContentModel.getIdProperty());
    		//System.out.println("definition id:" + term.getURI());
    	}else
    		ret = "TBD";
    	  	
    	return ret;
    	
    }    
    
    private Collection getClassDefinitions(RDFSNamedClass icdCategory){
    	
    	//Collection ret = new ArrayList();
    	
    	Collection definitions = icdContentModel.getTerms(icdCategory, icdContentModel.getDefinitionProperty());
    	
    	/*
    	if(pdefinitions != null){
    		for(Iterator it = pdefinitions.iterator(); it.hasNext();){
    			RDFResource pdefinition = (RDFResource) it.next();
    			ret.add(pdefinition.getPropertyValue(icdContentModel.getPrefilledDefinitionProperty()));
    		
    		}
    	}
    	*/
    	
    	return definitions;    
    	
    }    
    
    private Collection getPrefilledDefinitions(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection pdefinitions = icdContentModel.getTerms(icdCategory, icdContentModel.getPrefilledDefinitionProperty());
    	if(pdefinitions != null){
    		for(Iterator it = pdefinitions.iterator(); it.hasNext();){
    			RDFResource pdefinition = (RDFResource) it.next();
    			ret.add(pdefinition.getPropertyValue(icdContentModel.getPrefilledDefinitionProperty()));
    		
    		}
    	}
    	
    	return ret;    
    	
    }
        
    
    private Collection getInclusionTerms(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection inclusions = icdContentModel.getTerms(icdCategory, icdContentModel.getInclusionProperty());
    	/*
    	if(inclusions != null){
    		for(Iterator it = inclusions.iterator(); it.hasNext();){
    			RDFResource inclusion = (RDFResource) it.next();
    			ret.add(inclusion.getPropertyValue(icdContentModel.getInclusionProperty()));
    		
    		}
    	}
    	*/
    	
    	
    	return inclusions;        
    	
    }

    private String getTermRubricID(RDFResource term){
    	
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getIdProperty());
    	}
    	
    	return ret;        
    	
    }  

    private Collection getTermClaMLReferences(RDFResource term){
    	
        Collection<RDFResource> clamlRefs = term.getPropertyValues(icdContentModel.getClamlReferencesProperty());
 
    	
    	return clamlRefs;        
    	
    }      
    
    private String getDefinitionTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }
    
    private String getInclusionTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }
    
    
    private Collection getExclusionTerms(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection exclusions = icdContentModel.getTerms(icdCategory, icdContentModel.getExclusionProperty());
    	/*
    	if(exclusions != null){
    		for(Iterator it = exclusions.iterator(); it.hasNext();){
    			RDFResource exclusion = (RDFResource) it.next();
    			ret.add(exclusion.getPropertyValue(icdContentModel.getExclusionProperty()));
    		
    		}
    	}
    	*/
    	
    	return exclusions;     
    	
    }

    private String getExclusionTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }    
    
    private Collection getNoteTerms(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection notes = icdContentModel.getTerms(icdCategory, icdContentModel.getNoteProperty());
    	
    	/*
    	if(notes != null){
    		for(Iterator it = notes.iterator(); it.hasNext();){
    			RDFResource note = (RDFResource) it.next();
    			ret.add(note.getPropertyValue(icdContentModel.getNoteProperty()));
    		
    		}
    	}
    	*/
    	
    	return notes;         
    }
    
        
    
    private String getNoteTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }  
    
    
    private Collection getCodingHintTerms(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection notes = icdContentModel.getTerms(icdCategory, icdContentModel.getCodingHintProperty());
    	
    	/*
    	if(notes != null){
    		for(Iterator it = notes.iterator(); it.hasNext();){
    			RDFResource note = (RDFResource) it.next();
    			ret.add(note.getPropertyValue(icdContentModel.getNoteProperty()));
    		
    		}
    	}
    	*/
    	
    	return notes;         
    }
    
    private String getCodingHintTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }  
    
    
    private Collection getICDRefCodes(RDFSNamedClass icdCategory){
    	
    	Collection ret = new ArrayList();
    	
    	Collection notes = icdContentModel.getTerms(icdCategory, icdContentModel.getIcdCodeProperty());
    	
    	/*
    	if(notes != null){
    		for(Iterator it = notes.iterator(); it.hasNext();){
    			RDFResource note = (RDFResource) it.next();
    			ret.add(note.getPropertyValue(icdContentModel.getNoteProperty()));
    		
    		}
    	}
    	*/
    	
    	return notes;         
    }
    
    private String getICDRefCodeTermText(RDFResource term){
    	String ret = "TBD";
    	

    	if(term != null){
            ret = (String) term.getPropertyValue(icdContentModel.getLabelProperty());
    	}
    	
    	return ret;      	
    }     
    
    private String getCodeFromLabel(String label){
    	//label = label.replaceAll(" ", "_");
    	int index = label.indexOf("-");
    	label = label.substring(0, index);
    	if(label.endsWith(".")){
    		label = label.substring(0, label.length()-1);
    	}
    	
    	return label;
    	
    }
    
    public String getSortingLabel(RDFSNamedClass icdCategory){
    	
        //RDFSNamedClass category = icdContentModel.getICDCategory("http://who.int/icd#Class_1547");
        //System.out.println("Displayed as in the tree: " + category.getBrowserText());
        String ret = "TBD";
        String slabel = (String)icdCategory.getPropertyValue(icdContentModel.getSortingLabelProperty());
        if(slabel != null){
        	ret = slabel;
        }else{
        	//System.out.println("null sorting label: " + icdCategory.getBrowserText());
        	if(!noSortingLabelCategories.contains(icdCategory.getBrowserText()))
        		noSortingLabelCategories.add(icdCategory.getBrowserText());
        }
        
        return ret;
        
    }

    public String getSortingLabelForSubclass(RDFSNamedClass icdCategory){
    	
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
    
    
    public void generateCodesForLinearType(Collection icdCategories){
    	this.model = 
    		new ICDClaMLLinearizationHierarchicalStructure(this.icdContentModel, "Morbidity");
    	model.initialize(icdCategories);
    	model.generateCodes();
    	this.mapGeneratedCodes = model.getGeneretadCodes();
    	
    	
    	//testing missing code
		for(int i = 0; i < this.missingCodes.length; i++){
			
			String missingCode = this.missingCodes[i];
			RDFSNamedClass missingCategory = this.getRDFSNamedClassByLinearCode(missingCode);
			
			
		}
    	
    }
    
    public RDFSNamedClass getRDFSNamedClassByLinearCode(String linearCode){
    	
         RDFSNamedClass ret = null;
         
         for(Iterator it = this.mapGeneratedCodes.keySet().iterator(); it.hasNext();){
        	 
        	 RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
        	 String code = (String) this.mapGeneratedCodes.get(icdCategory);
        	 if(code.equals(linearCode)){
        		 
        		 ret = icdCategory;
 
     			System.out.println("Category found: " + icdCategory.getBrowserText() + "|" + linearCode);
        		 
        		 break;
        		 
        	 }
        	 
        	 
         }
         
			System.out.println("Category not found for:" + linearCode);
         
         return ret;
    	
    	
    	
    }
    
	public void processingClaMLXML(){
		
		System.out.println("starting..." + new Date());		

  	  /**
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
	    * 
	    */      		
		String linearizationType = "Morbidity";
		
		String output = "C:\\icdcontent\\icd11alpha_claml_2010907v1.0.xml";	
		try {
			//RDFSNamedClass icdCategoryXII = icdContentModel.getICDCategory("http://who.int/icd#XII");
			
			Collection<RDFSNamedClass> icdCategories = icdContentModel.getICDCategories();
			
			
			this.colAllExistingICDCodes = this.getAllExistingICDCodes(icdCategories);
			
			System.out.println("getAllExistingICDCodes() done!");
			
			//this.colCategoriesXII = this.getAllSubCategories(icdCategoryXII);
			
			
			//this.generateCodes(icdCategories);
			this.generateCodesForLinearType(icdCategories); //new approach for code generation
			
			System.out.println("generatedCodesForLinearType() done!");
			
			
			//this.generateTBDCodes(icdCategories);
			//this.displayMultipleParents(icdCategories);

			//RDFSNamedClass icdCategoryXIII = icdContentModel.getICDCategory("http://who.int/icd#XIII");
			
			//Collection icdCategories = icdCategoryXII.getSubclasses(true);
			//this.colCategoriesXII = icdCategories;
			//this.generateCodes(icdCategories);
			
			//this.getSortedSubclassCodes(icdContentModel.getICDCategoryClass());
			
			FileOutputStream fos = new FileOutputStream(output);
			ObjectFactory factory = new ObjectFactory();
			
          ClaML claml = factory.createClaML();
          claml.setVersion("2.0.0");
          
          Title title = factory.createTitle();
          title.setDate("2010-06-01");
          title.setName("ICD-11-alpha");
          title.setVersion("June 2010");
          
          claml.setTitle(title);
          
          Identifier identifier = factory.createIdentifier();
          identifier.setAuthority("WHO");
          identifier.setUid("id-to-be-added-later");
          claml.getIdentifier().add(identifier);
          
          Meta meta = factory.createMeta();
          meta.setName("lang");
          meta.setValue("en");
          claml.getMeta().add(meta);

          Meta metaTop = factory.createMeta();
          metaTop.setName("TopLevelSort");
          metaTop.setValue("I II III IV V VI VII VIII IX X XI XII XIII XIV XV XVI XVII XVIII XIX XX XXI XXII");
          claml.getMeta().add(metaTop);  
          
          
          ClassKinds clsKinds = factory.createClassKinds();
		  ClassKind cKind1 = factory.createClassKind();
		  cKind1.setName("chapter");
		  clsKinds.getClassKind().add(cKind1);
		  ClassKind cKind2 = factory.createClassKind();
		  cKind2.setName("block");
		  clsKinds.getClassKind().add(cKind2);
		  ClassKind cKind3 = factory.createClassKind();
		  cKind3.setName("category");
		  clsKinds.getClassKind().add(cKind3);
		  //ClassKind cKind4 = factory.createClassKind();
		  //cKind4.setName("modifiedcategory");
		  //clsKinds.getClassKind().add(cKind4);

		  claml.setClassKinds(clsKinds);
          
          /*
          UsageKinds useKinds = factory.createUsageKinds();
          UsageKind useKind = factory.createUsageKind();
          useKind.setMark("!");
          useKind.setName("optional");
          useKinds.getUsageKind().add(useKind);
          claml.setUsageKinds(useKinds);
          */
          RubricKinds rubKinds = factory.createRubricKinds();
         
          //preferred
          RubricKind rKind1 = factory.createRubricKind();
          rKind1.setInherited("false");
          rKind1.setName(icdContentModel.getPreferredProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind1);
          
          //synonym property
          /*
          RubricKind rKind1_1 = factory.createRubricKind();
          rKind1_1.setInherited("false");
          rKind1_1.setName(icdContentModel.getSynonymProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind1_1);
          */
          
          //definition property
          RubricKind rKind1_2 = factory.createRubricKind();
          rKind1_2.setInherited("false");
          rKind1_2.setName(icdContentModel.getDefinitionProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind1_2);
          
          //prefilled definition property
          /*
          RubricKind rKind1_3 = factory.createRubricKind();
          rKind1_3.setInherited("false");
          rKind1_3.setName(icdContentModel.getPrefilledDefinitionProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind1_3);
          */
          
          //inclusion
          RubricKind rKind2 = factory.createRubricKind();
          rKind2.setInherited("false");
          rKind2.setName(icdContentModel.getInclusionProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind2);
          
          //exclusion
          RubricKind rKind3 = factory.createRubricKind();
          rKind3.setInherited("false");
          rKind3.setName(icdContentModel.getExclusionProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind3);
          
          //note property
          RubricKind rKind5 = factory.createRubricKind();
          rKind5.setInherited("false");
          rKind5.setName(icdContentModel.getNoteProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind5);
          
          //MorbidityParent property
          /*
          RubricKind rKind6 = factory.createRubricKind();
          rKind6.setInherited("false");
          rKind6.setName(icdContentModel.getMorbidityParentProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind6);
          */
          
          //coding hint property
          RubricKind rKind7 = factory.createRubricKind();
          rKind7.setInherited("false");
          rKind7.setName(icdContentModel.getCodingHintProperty().getBrowserText());
          rubKinds.getRubricKind().add(rKind7);
          

          
          //icd Reference property
          //RubricKind rKind9 = factory.createRubricKind();
          //rKind9.setInherited("false");
          //rKind9.setName(icdContentModel.getIcdRefProperty().getBrowserText());
          //rubKinds.getRubricKind().add(rKind9);
          
        
          //set rubric kinds
          claml.setRubricKinds(rubKinds);
          
          
          
          
          //iterating icd categories
          
          int index = 1;
          for(Iterator it = icdCategories.iterator(); it.hasNext();){
        	  
        	  RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
        	  
        	  if(this.mapGeneratedCodes.containsKey(icdCategory)){ //only those categories with codes
 
  	  
        	  //is included?
        	  //if(this.isIncludedInLinearizationForType(icdCategory, linearizationType)){
        	  
        	  
              //RDFSNamedClass icdCategory = icdContentModel.getICDCategory("http://who.int/icd#I82.0");
              //System.out.println("Displayed as in the tree: " + icdCategory.getBrowserText());
        	  
        	    //System.out.println(index++ + "|" + icdCategory.getBrowserText());
        	  
            	edu.mayo.bmi.guoqian.claml.Class cls = factory.createClass();

            	/*
            	//get original code, if not availabel it is "TBD"
            	String classCode = getClassCode(icdCategory);
            	
            	//if the code is "TBD", then find it from generated codes
            	if(classCode.equals("TBD")){
            		String generatedCode = (String)this.mapGeneratedCodes.get(icdCategory);
            		if(generatedCode != null){
            			classCode = generatedCode;
            		}else{// in case, no code generated
            			System.out.println("Code not generated for:" + icdCategory.getBrowserText());
            		}
            	}
            	*/
            	
            	String classCode = (String) this.mapGeneratedCodes.get(icdCategory);
            	
            	cls.setCode(classCode);
            	
            	
            	String cKindName = getClassKind(icdCategory);
            	
      			ClassKind cKind = factory.createClassKind();
      			cKind.setName(cKindName);
      			cls.setKind(cKind);
      			
      			
      			//meta icat id
                Meta metaICatId = factory.createMeta();
                metaICatId.setName("iCatID");
                metaICatId.setValue(icdCategory.getName());
                cls.getMeta().add(metaICatId);

      			
/*      			
      			//superclass codes     
                if(!cKindName.equals("chapter")){// chapter is the root
                	Collection superclassCodes = getSuperclassCodes(icdCategory);
      			
                	if(superclassCodes != null){

                		for(Iterator it1 = superclassCodes.iterator(); it1.hasNext();){    				
                			String superclassCode = (String) it1.next();
                			SuperClass superclass = factory.createSuperClass();
                			superclass.setCode(superclassCode);
                			cls.getSuperClass().add(superclass);
          		
                		}	
      				
                	}
                }

*/                
                //linearization parent code
 

                if(!cKindName.equals("chapter")){// chapter is the root
                	
                	//String linearizationParent = this.getLinearizationParentForType(icdCategory, linearizationType);
                	String linearizationParent = model.getLinearParentCode(icdCategory); //20100613
                	
                	//RDFSNamedClass realLinearParent = this.getRealLinearizationParent(icdCategory, linearizationParent);
                	//String superclassCode = (String)this.mapGeneratedCodes.get(realLinearParent);
                   	//System.out.println(icdCategory.getBrowserText() + "|linear parent: " + linearizationParent);
                	
                	SuperClass superclass = factory.createSuperClass();
                	superclass.setCode(linearizationParent);
                	cls.getSuperClass().add(superclass); 
 
                }  
                



      			//subclass codes sorted with sortingLabel
          		//Object[] arrayCodes = getSubclassCodesSortingLabels(icdCategory);
                Object[] arrayCodes = model.getLinearSubclassCodes(icdCategory); //20100613
          		if(arrayCodes != null){
          			for(int it2 = 0; it2 < arrayCodes.length; it2 ++){
          			//for(Iterator it2 = subclassCodes.iterator(); it2.hasNext();){
          				String subclassCode = (String) arrayCodes[it2];
          				//System.out.println(code + "|" + subclassCode);
          				SubClass subclass = factory.createSubClass();
          				subclass.setCode(subclassCode);
          				cls.getSubClass().add(subclass);
          			}        
          		}
               
/*                
      			//subclass codes
          		Collection subclassCodes = getSubclassCodes(icdCategory);
          		if(subclassCodes != null){
          			Object[] arrayCodes = subclassCodes.toArray();
          			Arrays.sort(arrayCodes);
          			for(int it2 = 0; it2 < arrayCodes.length; it2 ++){
          			//for(Iterator it2 = subclassCodes.iterator(); it2.hasNext();){
          				String subclassCode = (String) arrayCodes[it2];
          				if(subclassCode.equals("TBD"))
          				   System.out.println(icdCategory.getBrowserText() + "|" + subclassCode);
          				SubClass subclass = factory.createSubClass();
          				subclass.setCode(subclassCode);
          				cls.getSubClass().add(subclass);
          			}        
          		}
*/            	
        	  
          		//set preferred label and language
          		

          		String sortingLabel = this.getSortingLabel(icdCategory);
              	String label = this.getClassPreferredLabel(icdCategory);
              	label = label.replaceAll("'", "");
          		String clsCode = getClassCode(icdCategory);           	
            	if(clsCode.equals("TBD")){
            		//label = this.getUserAssignedLabel(icdCategory);
            		label += "[" + sortingLabel + "]";
            	             	
            		Rubric rubricLabel = factory.createRubric();
            		//rubricLabel.setId("id-to-be-added-later");
            		ClassKind cKindPreferred = factory.createClassKind();
            		cKindPreferred.setName("preferred");
            		rubricLabel.setKind(cKindPreferred);

            		Label clsLabel = factory.createLabel();
            		clsLabel.setLang("en");
            		clsLabel.setSpace("default");
            		clsLabel.getContent().add(label);
            		rubricLabel.getLabel().add(clsLabel);
            		cls.getRubric().add(rubricLabel);
            	}else{
            		
            		label += "{" + clsCode + "}"; //add ICD10 code in label
            		label += "[" + sortingLabel + "]";            		
            		Rubric rubricLabel = factory.createRubric();
            		String rubricId = this.getClassPreferredLabelRubricID(icdCategory);
            		rubricLabel.setId(rubricId);
            		ClassKind cKindPreferred = factory.createClassKind();
            		cKindPreferred.setName("preferred");
            		rubricLabel.setKind(cKindPreferred);

            		Label clsLabel = factory.createLabel();
            		clsLabel.setLang("en");
            		clsLabel.setSpace("default");
            		clsLabel.getContent().add(label);

            		
/*
      			try{
      				
      				Collection<RDFResource> clamlRefCodes = this.getTermClaMLReferences(icdContentModel.getTerm(icdCategory, icdContentModel.getIcdTitleProperty()));
      				
      				if(clamlRefCodes != null){
      					for (RDFResource clamlRefCode : clamlRefCodes) {

      						
      	      				Reference refCode = factory.createReference();
      	      				refCode.setClazz("in brackets");      					
      						refCode.setContent((String)clamlRefCode.getPropertyValue(icdContentModel.getTextProperty()));
      						//String refCodeText = ((RDFResource)clamlRefCode.getPropertyValue(icdContentModel.getIcdRefProperty())).getBrowserText();
      						
      						String thisCode = (String)clamlRefCode.getPropertyValue(icdContentModel.getTextProperty());
      						if(thisCode.indexOf("-") >= 0){
      							thisCode = this.getCodeFromLabel(thisCode);
      						}
      						
      						
      						
      						refCode.setCode(thisCode);
      						clsLabel.getContent().add(refCode);          					
      					
      						}
      				}            		
            	}catch(Exception e){
  					e.printStackTrace();
  	        	    System.out.println("preferred" + "|" + icdCategory.getBrowserText());
  	        	    
      				Collection clamlRefCodes = this.getTermClaMLReferences(icdContentModel.getTerm(icdCategory, icdContentModel.getIcdTitleProperty()));
      				
      				if(clamlRefCodes != null){
      					for (Iterator itref = clamlRefCodes.iterator(); itref.hasNext();){
      						String clamlRefCode = (String) itref.next();
      					

      						
      	      				Reference refCode = factory.createReference();
      	      				refCode.setClazz("in brackets");      					
      						refCode.setContent(clamlRefCode);
      						//String refCodeText = ((RDFResource)clamlRefCode.getPropertyValue(icdContentModel.getIcdRefProperty())).getBrowserText();
      						
      						String thisCode = clamlRefCode;
      						if(thisCode.indexOf("-") >= 0){
      							thisCode = this.getCodeFromLabel(thisCode);
      						}
      						refCode.setCode(thisCode);
      						clsLabel.getContent().add(refCode);          					
      					
      						}
      				}   	        	    
  	        	    
  	        	    
                    continue;
  				}
            	
*/            	
            		rubricLabel.getLabel().add(clsLabel);
            	            		
            		
            		
            		
            		cls.getRubric().add(rubricLabel);            		
            	}

  				//synonyms
  				
            	/*
  				Collection colSynonyms = this.getSynonyms(icdCategory);
  				if(colSynonyms != null){
  					for(Iterator itpd = colSynonyms.iterator(); itpd.hasNext();){
  					
  						String synonym = (String) itpd.next();
  						if(synonym != null && synonym.length() > 0){
  						Rubric rubricSynonym = factory.createRubric();
  						rubricSynonym.setId("id-to-be-added-later-" + rubricIndex++);
  						ClassKind cKindSynonym = factory.createClassKind();
  						cKindSynonym.setName(icdContentModel.getSynonymProperty().getBrowserText());
  						rubricSynonym.setKind(cKindSynonym);
  						Label labelSynonym = factory.createLabel();
  						labelSynonym.setLang("en");
  						labelSynonym.setSpace("default");
  						labelSynonym.getContent().add(synonym);
  						rubricSynonym.getLabel().add(labelSynonym);
  						cls.getRubric().add(rubricSynonym);
  						}
  					
  					}  
  				}
  				*/
          		
          		
          		//definition
            	
            	Collection definitions = this.getClassDefinitions(icdCategory);
            	if(definitions != null){
            	
            		for(Iterator itdef = definitions.iterator(); itdef.hasNext();){
            			RDFResource defTerm = (RDFResource) itdef.next();
            			
            			String defText = this.getDefinitionTermText(defTerm);
            			String defRubricID = this.getTermRubricID(defTerm);
            			
            			if(definitions.size() > 1){
            				//System.out.println(classCode + "|" + defRubricID + "|" + defText);
            			}
            			
            			//if(defText != null){
            			
                  		Rubric rubricDefinition = factory.createRubric();
                  		rubricDefinition.setId(defRubricID);
                  		ClassKind cKindDefinition = factory.createClassKind();
                  		cKindDefinition.setName(icdContentModel.getDefinitionProperty().getBrowserText());
                  		rubricDefinition.setKind(cKindDefinition);
                  		Label labelDefinition = factory.createLabel();
          				labelDefinition.setLang("en");
          				labelDefinition.setSpace("default");
          				labelDefinition.getContent().add(defText);
          				rubricDefinition.getLabel().add(labelDefinition);
          				cls.getRubric().add(rubricDefinition);   
            			//}// not null
            		}


          		}
          		

  				//prefilled definitions
  				/*
  				Collection colPrefilledDefinitions = this.getPrefilledDefinitions(icdCategory);
  				if(colPrefilledDefinitions != null){
  					for(Iterator itpd = colPrefilledDefinitions.iterator(); itpd.hasNext();){
  					
  						String pdefinition = (String) itpd.next();
  						if(pdefinition != null){
  						Rubric rubricpDefinition = factory.createRubric();
  						rubricpDefinition.setId("id-to-be-added-later-" + rubricIndex++);
  						ClassKind cKindpDefinition = factory.createClassKind();
  						cKindpDefinition.setName(icdContentModel.getPrefilledDefinitionProperty().getBrowserText());
  						rubricpDefinition.setKind(cKindpDefinition);
  						Label labelpDefinition = factory.createLabel();
  						labelpDefinition.setLang("en");
  						labelpDefinition.setSpace("default");
  						labelpDefinition.getContent().add(definition);
  						rubricpDefinition.getLabel().add(labelpDefinition);
  						cls.getRubric().add(rubricpDefinition);
  						}
  					
  					}
  				}
  				*/
          		
             	  
          		//inclusion terms
          		Collection colInclusions = this.getInclusionTerms(icdCategory);
          		
          		Collection colSortedInclusions = new ArrayList();
          		Map mapSortedInclusions = new HashMap();

          		if(colInclusions != null){
          			for(Iterator it3 = colInclusions.iterator(); it3.hasNext();){
          				RDFResource inclusion = (RDFResource) it3.next();
          				
                        String inclusionLabel = this.getInclusionTermText(inclusion);
                        String inclusionId = this.getTermRubricID(inclusion);
                        
                        String inclusionLabelId = inclusionLabel + "|" + inclusionId;
                        colSortedInclusions.add(inclusionLabelId);
                        mapSortedInclusions.put(inclusionLabelId, inclusion);
          			}
          			
          			    //alphabetically sort the rubrics
                        Object[] arraySortedInclusions = colSortedInclusions.toArray();
                        List listSortedInclusions = Arrays.asList(arraySortedInclusions);
                        Collections.sort(listSortedInclusions, String.CASE_INSENSITIVE_ORDER);
                        for(ListIterator li0 = listSortedInclusions.listIterator(); li0.hasNext();){
                        	String sortedInclusionLabelId = (String)li0.next();
                        	String[] labelId = sortedInclusionLabelId.split("\\|");
                        	RDFResource sortedInclusion = (RDFResource)mapSortedInclusions.get(sortedInclusionLabelId);
          				
                        	Rubric rubricInclusion = factory.createRubric();
              				if(!labelId[1].equals("null")) //if null, do nothing
              					rubricInclusion.setId(labelId[1]);
                        	ClassKind cKindInclusion = factory.createClassKind();
                        	cKindInclusion.setName("inclusion");
                        	rubricInclusion.setKind(cKindInclusion);
                        	Label labelInclusion = factory.createLabel();
                        	labelInclusion.setLang("en");
                        	labelInclusion.setSpace("default");
                        	labelInclusion.getContent().add(labelId[0]);
                        	Collection<RDFResource> clamlRefCodes = this.getTermClaMLReferences(sortedInclusion);

                     try{
          				if(clamlRefCodes != null){
          					for (RDFResource clamlRefCode : clamlRefCodes) {

          						Reference refCode = factory.createReference();
                  				refCode.setClazz("in brackets");
          						String thisCode = (String)clamlRefCode.getPropertyValue(icdContentModel.getTextProperty());
          						if(thisCode.indexOf("-") >= 0){
          							thisCode = this.getCodeFromLabel(thisCode);
          						}
          						
          						String thisGeneratedCode = this.getGeneratedCodeForICDCategoryCode(thisCode);
          					    if(thisGeneratedCode == null){
          					    	thisGeneratedCode = thisCode;
          					    }
                  				
                  				
                  				
          						refCode.setContent(thisGeneratedCode);
          						//String refCodeText = ((RDFResource)clamlRefCode.getPropertyValue(icdContentModel.getIcdRefProperty())).getBrowserText();
          						
          						refCode.setCode(thisGeneratedCode);
          						labelInclusion.getContent().add(refCode);          					
          					
          						}
          				}
          			  }catch(Exception e){
          					e.printStackTrace();
          	        	    System.out.println("inclusion" + "|" + icdCategory.getBrowserText());
                            continue;
          				}
          				
          				
          				rubricInclusion.getLabel().add(labelInclusion);
          				cls.getRubric().add(rubricInclusion);
          			

          			}
          		}          		

          		//exclusion terms
          		Collection colExclusions = this.getExclusionTerms(icdCategory);

          		Collection<String> colSortedExclusions = new ArrayList();
          		Map<String, RDFResource> mapSortedExclusions = new HashMap();
          		
          		
          		
          		if(colExclusions != null){
          			for(Iterator it4 = colExclusions.iterator(); it4.hasNext();){
          				RDFResource exclusion = (RDFResource) it4.next();
                        String exclusionLabel = this.getExclusionTermText(exclusion);
                        String exclusionId = this.getTermRubricID(exclusion);
                        //Collection<RDFResource> clamlRefCodes = this.getTermClaMLReferences(exclusion);
                        
                        String exclusionLabelId = exclusionLabel + "|" + exclusionId;
                        colSortedExclusions.add(exclusionLabelId);
                        mapSortedExclusions.put(exclusionLabelId, exclusion);
                        

          			}
                        //alphabetically sort the rubrics
                        Object[] arraySortedExclusions = colSortedExclusions.toArray();
                        List listSortedExclusions = Arrays.asList(arraySortedExclusions);
                        Collections.sort(listSortedExclusions, String.CASE_INSENSITIVE_ORDER);
                        for(ListIterator li1 = listSortedExclusions.listIterator(); li1.hasNext();){
                        //for(int jj = 0; jj < arraySortedExclusions.length; jj++){
                        	String sortedExclusionLabelId = (String)li1.next();
                        
                        	
                        	String[] labelId = sortedExclusionLabelId.split("\\|");
                        	RDFResource sortedExclusion = (RDFResource)mapSortedExclusions.get(sortedExclusionLabelId);
          				
          				//System.out.println(icdCategory.getBrowserText() + "|" + sortedExclusionLabelId);
                        	
          				Rubric rubricInclusion = factory.createRubric();
          				if(!labelId[1].equals("null")) //if null, do nothing
          					rubricInclusion.setId(labelId[1]);
          				ClassKind cKindInclusion = factory.createClassKind();
          				cKindInclusion.setName("exclusion");
          				rubricInclusion.setKind(cKindInclusion);
          				Label labelInclusion = factory.createLabel();
          				labelInclusion.setLang("en");
          				labelInclusion.setSpace("default");
          				labelInclusion.getContent().add(labelId[0]);
 
          				
          				Collection<RDFResource> clamlRefCodes = this.getTermClaMLReferences(sortedExclusion);
          				
          				
          				//if(sortedExclusion.getPropertyValues(icdContentModel.getClamlReferencesProperty()) != null){
          				//Collection<RDFResource> clamlRefCodes = sortedExclusion.getPropertyValues(icdContentModel.getClamlReferencesProperty());
          				
          				try{
          				if(clamlRefCodes != null){
          					
          					
          					for (RDFResource clamlRefCode : clamlRefCodes) {
          					
                  				Reference refCode = factory.createReference();
                  				refCode.setClazz("in brackets");
                  				
          						String thisCode = (String)clamlRefCode.getPropertyValue(icdContentModel.getTextProperty());
          						if(thisCode.indexOf("-") >= 0){
          							thisCode = this.getCodeFromLabel(thisCode);
          						}
          						
          						String thisGeneratedCode = this.getGeneratedCodeForICDCategoryCode(thisCode);
          					    if(thisGeneratedCode == null){
          					    	thisGeneratedCode = thisCode;
          					    }
                  				
                  				
          						refCode.setContent(thisGeneratedCode);
          						//String refCodeText = ((RDFResource)clamlRefCode.getPropertyValue(icdContentModel.getIcdRefProperty())).getBrowserText();

          						
          						refCode.setCode(thisGeneratedCode);
          						labelInclusion.getContent().add(refCode);          					
          					
          						}
          				}
          				}catch(Exception e){
          					e.printStackTrace();
          	        	    System.out.println("exclusion" + "|" + icdCategory.getBrowserText());
          					continue;
          				}
          				
          				//}
          				

          				rubricInclusion.getLabel().add(labelInclusion);
          				
          				cls.getRubric().add(rubricInclusion);

          			}
          		}  
          		
          		//notes term
          		Collection colNotes = this.getNoteTerms(icdCategory);
          		if(colNotes != null){
          			for(Iterator it6 = colNotes.iterator(); it6.hasNext();){
          				RDFResource note = (RDFResource) it6.next();

                        String noteLabel = this.getNoteTermText(note);
                        String noteId = this.getTermRubricID(note);
          				
          				Rubric rubricNote = factory.createRubric();
          				rubricNote.setId(noteId);
          				ClassKind cKindNote = factory.createClassKind();
          				cKindNote.setName("note");
          				rubricNote.setKind(cKindNote);
          				Label labelNote = factory.createLabel();
          				labelNote.setLang("en");
          				labelNote.setSpace("default");
          				labelNote.getContent().add(noteLabel);
          				rubricNote.getLabel().add(labelNote);
          				cls.getRubric().add(rubricNote);
          				
          			
          			}
          		
          		}
          		
          		//codinghint term
          		Collection colCodingHints = this.getCodingHintTerms(icdCategory);
          		if(colCodingHints != null){
          			for(Iterator it7 = colCodingHints.iterator(); it7.hasNext();){
          				RDFResource note = (RDFResource) it7.next();

                        String noteLabel = this.getCodingHintTermText(note);
                        String noteId = this.getTermRubricID(note);
          				
          				Rubric rubricNote = factory.createRubric();
          				rubricNote.setId(noteId);
          				ClassKind cKindNote = factory.createClassKind();
          				cKindNote.setName(icdContentModel.getCodingHintProperty().getBrowserText());
          				rubricNote.setKind(cKindNote);
          				Label labelNote = factory.createLabel();
          				labelNote.setLang("en");
          				labelNote.setSpace("default");
          				labelNote.getContent().add(noteLabel);
          				rubricNote.getLabel().add(labelNote);
          				cls.getRubric().add(rubricNote);
          				
          			
          			}
          		
          		}

          		
          		claml.getClazz().add(cls);      
        	  }else{
        		  
          	    //System.out.println("categorywithoutcode:|" + icdCategory.getBrowserText());
          	    
          	    //String[] missingCodes = {"OEAE", "LDDD", "BACFCB", "GDJBE", "GDJBEA"};
          	    
          	    
        		  
        	  }
        	  
        	  
        	  //}//isIncluded?
          }
        	  
          
          
			JAXBContext jaxbContext = JAXBContext.newInstance("edu.mayo.bmi.guoqian.claml");
			Marshaller marshaller = jaxbContext.createMarshaller();
	
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal( claml, fos );
			
			System.out.println("Num. Of categories without sorting label: " + noSortingLabelCategories.size());
			System.out.println("ending..." + new Date());		
         
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	


	public static void main(String[] args){
		ICDClaMLXMLExporter model = new ICDClaMLXMLExporter();

		model.processingClaMLXML();
		
		
	}


}
