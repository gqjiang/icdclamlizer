package edu.mayo.informatics.icd.claml;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.io.*;

import edu.mayo.bmi.guoqian.claml.ClassKind;
import edu.mayo.bmi.guoqian.claml.Label;
import edu.mayo.bmi.guoqian.claml.Reference;
import edu.mayo.bmi.guoqian.claml.Rubric;
import edu.stanford.bmir.icd.claml.ICDContentModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ICDClaMLXMLRender {

	public String getClaMLHeader() {
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<ClaML version=\"2.0.0\">\n");
		sb.append("    <Meta value=\"en\" name=\"lang\"/>\n");
		sb.append("    <Meta value=\"I II III IV V VI VII VIII IX X XI XII XIII XIV XV XVI XVII XVIII XIX XX XXI XXII\" name=\"TopLevelSort\"/>\n");
		sb.append("    <Identifier uid=\"id-to-be-added-later\" authority=\"WHO\"/>\n");
		sb.append("    <Title version=\"September 2010\" name=\"ICD-11-alpha\" date=\"2010-09-16\"/>\n");
		sb.append("    <ClassKinds>\n");
		sb.append("        <ClassKind name=\"chapter\"/>\n");
		sb.append("        <ClassKind name=\"block\"/>\n");
		sb.append("        <ClassKind name=\"category\"/>\n");
		sb.append("    </ClassKinds>\n");
		sb.append("    <RubricKinds>\n");
		sb.append("        <RubricKind name=\"preferred\" inherited=\"false\"/>\n");
		sb.append("        <RubricKind name=\"definition\" inherited=\"false\"/>\n");
		sb.append("        <RubricKind name=\"inclusion\" inherited=\"false\"/>\n");
		sb.append("        <RubricKind name=\"exclusion\" inherited=\"false\"/>\n");
		sb.append("        <RubricKind name=\"note\" inherited=\"false\"/>\n");
		sb.append("        <RubricKind name=\"codingHint\" inherited=\"false\"/>\n");
		sb.append("    </RubricKinds>\n");
		return sb.toString();
	}

	public String getClaMLTrailer() {
		return "</ClaML>\n";
	}

	public String getClaMLClassHeader(String kind, String code) {
		StringBuffer sb = new StringBuffer();
		sb.append("    <Class kind=\"");
		sb.append(kind + "\" code=\"");
		sb.append(code + "\">\n");
		return sb.toString();
	}

	public String getClaMLClassTrailer() {
		return "    </Class>\n";
	}

	public String getClaMLMeta(String value, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append("        <Meta value=\"");
		sb.append(this.forXML(value) + "\" name=\"");
		sb.append(this.forXML(name) + "\"/>\n");
		return sb.toString();
	}

	public String getClaMLSuperClass(String code) {
		StringBuffer sb = new StringBuffer();
		sb.append("        <SuperClass code=\"");
		sb.append(code + "\"/>\n");
		return sb.toString();
	}

	public String getClaMLSubClass(String code) {
		StringBuffer sb = new StringBuffer();
		sb.append("        <SubClass code=\"");
		sb.append(code + "\"/>\n");
		return sb.toString();
	}

	public String getClaMLPreferredLabel(String label) {
		StringBuffer sb = new StringBuffer();

		sb.append("        <Rubric kind=\"preferred\">\n");
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">");
		sb.append(this.forXML(label) + "</Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	public String getClaMLPreferredLabel(String id, String label) {
		StringBuffer sb = new StringBuffer();

		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"preferred\">\n");
		}else{
			sb.append("        <Rubric kind=\"preferred\" id=\"");
			sb.append(id + "\">\n");			
		}
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">");
		sb.append(this.forXML(label) + "</Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	public String getClaMLDefinition(String id, String label) {
		StringBuffer sb = new StringBuffer();

		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"definition\">\n");
		}else{
			sb.append("        <Rubric kind=\"definition\" id=\"");
			sb.append(id + "\">\n");			
		}
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">");
		sb.append(this.forXML(label) + "</Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	
	public String getClaMLInclusionLabel(String id, String label) {
		StringBuffer sb = new StringBuffer();
		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"inclusion\">\n");
		}else{
			sb.append("        <Rubric kind=\"inclusion\" id=\"");
			sb.append(id + "\">\n");
		}
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">");
		sb.append(this.forXML(label) + "</Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	public String getClaMLInclusionLabel(String id, String label, String references) {
		StringBuffer sb = new StringBuffer();
		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"inclusion\">\n");
		}else{
			sb.append("        <Rubric kind=\"inclusion\" id=\"");
			sb.append(id + "\">\n");
		}
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">" + this.forXML(label) + "\n");
		sb.append(references);
		sb.append("            </Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}
	
	
	public String getClaMLReferenceCode(String code) {
		StringBuffer sb = new StringBuffer();
		sb.append("                <Reference code=\"");
		sb.append(code + "\" class=\"in brackets\">");
		sb.append(code + "</Reference>\n");
		return sb.toString();
	}
		
	
	public String getClaMLExclusionLabel(String id, String label) {
		StringBuffer sb = new StringBuffer();

		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"exclusion\">\n");
		}else{
			sb.append("        <Rubric kind=\"exclusion\" id=\"");
			sb.append(id + "\">\n");
		}
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">");
		sb.append(this.forXML(label) + "</Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	public String getClaMLExclusionLabel(String id, String label, String references) {
		StringBuffer sb = new StringBuffer();

		if(id == null || id.equals("null")){
			sb.append("        <Rubric kind=\"exclusion\">\n");
		}else{
			sb.append("        <Rubric kind=\"exclusion\" id=\"");
			sb.append(id + "\">\n");
		}		
		sb.append("            <Label xml:space=\"default\" xml:lang=\"en\">" + this.forXML(label) + "\n");
		sb.append(references);
		sb.append("            </Label>\n");
		sb.append("        </Rubric>\n");
		return sb.toString();

	}

	public String processingClaMLXML(ICDContentModel icdContentModel,
			Collection icdCategories, Map mapGeneratedCodes,
			ICDClaMLLinearizationHierarchicalStructure model) {

		StringBuffer sb = new StringBuffer();

		Collection allExistingICDCodes = this.getAllExistingICDCodes(
				icdCategories, icdContentModel);

		sb.append(this.getClaMLHeader());
		int index = 0;
		for (Iterator it = icdCategories.iterator(); it.hasNext();) {
			RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
			// only those categories having generated codes
			if (mapGeneratedCodes.containsKey(icdCategory)) {
				String classCode = (String) mapGeneratedCodes.get(icdCategory);
				String classKind = getClassKind(icdCategory, icdContentModel);

				// Class Header
				sb.append(this.getClaMLClassHeader(classKind, classCode));

				// Meta for iCatID
				String metaICatId = "iCatID";
				String metaICatIdName = icdCategory.getName();
				sb.append(this.getClaMLMeta(metaICatIdName, metaICatId));

				// SuperClass
				if (!classKind.equals("chapter")) {// chapter is the root
					String linearizationParent = model
							.getLinearParentCode(icdCategory);
					sb.append(this.getClaMLSuperClass(linearizationParent));

				}

				// SubClasses
				Object[] arrayCodes = model.getLinearSubclassCodes(icdCategory);
				if (arrayCodes != null) {
					for (int it2 = 0; it2 < arrayCodes.length; it2++) {
						String subclassCode = (String) arrayCodes[it2];
						sb.append(this.getClaMLSubClass(subclassCode));

					}
				}

				// Preferred label
				String sortingLabel = this.getSortingLabel(icdCategory,
						icdContentModel);
				String label = this.getClassPreferredLabel(icdCategory,
						icdContentModel);
				label = label.replaceAll("'", "");
				String clsCode = getClassCode(icdCategory, icdContentModel);
				if (clsCode.equals("TBD")) {
					// label = this.getUserAssignedLabel(icdCategory);
					label += "[" + sortingLabel + "]";
					sb.append(this.getClaMLPreferredLabel(label));
				} else {

					label += "{" + clsCode + "}"; // add ICD10 code in label
					label += "[" + sortingLabel + "]";
					String rubricId = this.getClassPreferredLabelRubricID(
							icdCategory, icdContentModel);
					sb.append(this.getClaMLPreferredLabel(rubricId, label));
				}

				// Definitions
				Collection definitions = this.getClassDefinitions(icdCategory,
						icdContentModel);
				if (definitions != null) {

					for (Iterator itdef = definitions.iterator(); itdef
							.hasNext();) {
						RDFResource defTerm = (RDFResource) itdef.next();

						String defText = this.getDefinitionTermText(defTerm,
								icdContentModel);
						String defRubricID = this.getTermRubricID(defTerm,
								icdContentModel);
						if (defText != null) {
							sb.append(this.getClaMLDefinition(defRubricID, defText));
						}
					}
				}

				// Inclusion
				Collection colInclusions = this.getInclusionTerms(icdCategory,
						icdContentModel);

				Collection colSortedInclusions = new ArrayList();
				Map mapSortedInclusions = new HashMap();

				if (colInclusions != null) {
					for (Iterator it3 = colInclusions.iterator(); it3.hasNext();) {
						RDFResource inclusion = (RDFResource) it3.next();

						String inclusionLabel = this.getInclusionTermText(
								inclusion, icdContentModel);
						String inclusionId = this.getTermRubricID(inclusion,
								icdContentModel);

						String inclusionLabelId = inclusionLabel + "|"
								+ inclusionId;
						colSortedInclusions.add(inclusionLabelId);
						mapSortedInclusions.put(inclusionLabelId, inclusion);
					}

					// alphabetically sort the rubrics
					Object[] arraySortedInclusions = colSortedInclusions.toArray();
					List listSortedInclusions = Arrays.asList(arraySortedInclusions);
					Collections.sort(listSortedInclusions,String.CASE_INSENSITIVE_ORDER);
					for (ListIterator li0 = listSortedInclusions.listIterator(); li0.hasNext();) {
						String sortedInclusionLabelId = (String) li0.next();
						String[] labelId = sortedInclusionLabelId.split("\\|");
						RDFResource sortedInclusion = (RDFResource) mapSortedInclusions
								.get(sortedInclusionLabelId);

						Collection<RDFResource> clamlRefCodes = this.getTermClaMLReferences(sortedInclusion, icdContentModel);
						
							
						StringBuffer refCodeInclusion = new StringBuffer();; 

						try {
							if (clamlRefCodes != null) {
								for (RDFResource clamlRefCode : clamlRefCodes) {

									String thisCode = (String) clamlRefCode
											.getPropertyValue(icdContentModel
													.getTextProperty());
									if (thisCode.indexOf("-") >= 0) {
										thisCode = this
												.getCodeFromLabel(thisCode);
									}

									String thisGeneratedCode = this
											.getGeneratedCodeForICDCategoryCode(
													thisCode, mapGeneratedCodes);
									if (thisGeneratedCode == null) {
										thisGeneratedCode = thisCode;
									}

									refCodeInclusion.append(this.getClaMLReferenceCode(thisGeneratedCode));
									
									//sb.append(this.getClaMLInclusionLabel(labelId[1],labelId[0], thisGeneratedCode));

								}
							} 
							
						} catch (Exception e) {
							//e.printStackTrace();
							continue;
						}
						
						
						if(refCodeInclusion.toString().length() < 1){
							sb.append(this.getClaMLInclusionLabel(labelId[1], labelId[0]));
						}else{
							
							sb.append(this.getClaMLInclusionLabel(labelId[1],
									labelId[0], refCodeInclusion.toString()));
						}
						

					}
				}

				// exclusion terms
				Collection colExclusions = this.getExclusionTerms(icdCategory,
						icdContentModel);

				Collection<String> colSortedExclusions = new ArrayList();
				Map<String, RDFResource> mapSortedExclusions = new HashMap();

				if (colExclusions != null) {
					for (Iterator it4 = colExclusions.iterator(); it4.hasNext();) {
						RDFResource exclusion = (RDFResource) it4.next();
						String exclusionLabel = this.getExclusionTermText(
								exclusion, icdContentModel);
						String exclusionId = this.getTermRubricID(exclusion,
								icdContentModel);

						String exclusionLabelId = exclusionLabel + "|"
								+ exclusionId;
						colSortedExclusions.add(exclusionLabelId);
						mapSortedExclusions.put(exclusionLabelId, exclusion);

					}
					// alphabetically sort the rubrics
					Object[] arraySortedExclusions = colSortedExclusions
							.toArray();
					List listSortedExclusions = Arrays
							.asList(arraySortedExclusions);
					Collections.sort(listSortedExclusions,
							String.CASE_INSENSITIVE_ORDER);
					for (ListIterator li1 = listSortedExclusions.listIterator(); li1
							.hasNext();) {
						// for(int jj = 0; jj < arraySortedExclusions.length;
						// jj++){
						String sortedExclusionLabelId = (String) li1.next();

						String[] labelId = sortedExclusionLabelId.split("\\|");
						RDFResource sortedExclusion = (RDFResource) mapSortedExclusions
								.get(sortedExclusionLabelId);

						Collection<RDFResource> clamlRefCodes = this
								.getTermClaMLReferences(sortedExclusion,
										icdContentModel);

						StringBuffer refCodeExclusion = new StringBuffer();

						try {
							if (clamlRefCodes != null) {

								for (RDFResource clamlRefCode : clamlRefCodes) {

									String thisCode = (String) clamlRefCode
											.getPropertyValue(icdContentModel
													.getTextProperty());
									if (thisCode.indexOf("-") >= 0) {
										thisCode = this
												.getCodeFromLabel(thisCode);
									}

									String thisGeneratedCode = this
											.getGeneratedCodeForICDCategoryCode(
													thisCode, mapGeneratedCodes);
									if (thisGeneratedCode == null) {
										thisGeneratedCode = thisCode;
									}
                                    
									refCodeExclusion.append(this.getClaMLReferenceCode(thisGeneratedCode));

								}
							}
							
						} catch (Exception e) {
							//e.printStackTrace();

							continue;
						}
						
						if(refCodeExclusion.toString().length() < 1){
							sb.append(this.getClaMLExclusionLabel(labelId[1], labelId[0]));
						}else{
							sb.append(this.getClaMLExclusionLabel(labelId[1], labelId[0], refCodeExclusion.toString()));
						}

					}
				}

				// Class Trailer
				sb.append(this.getClaMLClassTrailer());
			}

		}

		sb.append(this.getClaMLTrailer());
		return sb.toString();

	}

	private String getCodeFromLabel(String label) {
		// label = label.replaceAll(" ", "_");
		int index = label.indexOf("-");
		label = label.substring(0, index);
		if (label.endsWith(".")) {
			label = label.substring(0, label.length() - 1);
		}

		return label;

	}

	private Map mapAllExistingICDCodes = new HashMap();

	private Collection getAllExistingICDCodes(Collection icdCategories,
			ICDContentModel icdContentModel) {

		Collection ret = new ArrayList();

		for (Iterator it = icdCategories.iterator(); it.hasNext();) {
			RDFSNamedClass icdCategory = (RDFSNamedClass) it.next();
			String code = this.getClassCode(icdCategory, icdContentModel);
			if (!code.equals("TBD")) {
				if (!ret.contains(code)) {
					ret.add(code);
				}

				if (!this.mapAllExistingICDCodes.containsKey(code)) {
					this.mapAllExistingICDCodes.put(code, icdCategory);
				}

			}
		}

		return ret;

	}

	// get generated codes for a original icd code
	private String getGeneratedCodeForICDCategoryCode(String code,
			Map mapGeneratedCodes) {
		RDFSNamedClass icdCategory = (RDFSNamedClass) this.mapAllExistingICDCodes
				.get(code);
		return (String) mapGeneratedCodes.get(icdCategory);
	}

	private Collection getTermClaMLReferences(RDFResource term,
			ICDContentModel icdContentModel) {

		Collection<RDFResource> clamlRefs = term.getPropertyValues(icdContentModel.getClamlReferencesProperty());
		return clamlRefs;

	}

	private Collection getInclusionTerms(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		Collection inclusions = icdContentModel.getTerms(icdCategory,
				icdContentModel.getInclusionProperty());

		return inclusions;

	}

	private String getInclusionTermText(RDFResource term,
			ICDContentModel icdContentModel) {
		String ret = "TBD";

		if (term != null) {
			ret = (String) term.getPropertyValue(icdContentModel
					.getLabelProperty());
		}

		return ret;
	}

	private Collection getExclusionTerms(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		Collection exclusions = icdContentModel.getTerms(icdCategory,
				icdContentModel.getExclusionProperty());

		return exclusions;

	}

	private String getExclusionTermText(RDFResource term,
			ICDContentModel icdContentModel) {
		String ret = "TBD";

		if (term != null) {
			ret = (String) term.getPropertyValue(icdContentModel
					.getLabelProperty());
		}

		return ret;
	}

	private String getDefinitionTermText(RDFResource term,
			ICDContentModel icdContentModel) {
		String ret = "TBD";
		if (term != null) {
			ret = (String) term.getPropertyValue(icdContentModel
					.getLabelProperty());
		}

		return ret;
	}

	private String getTermRubricID(RDFResource term,
			ICDContentModel icdContentModel) {
		String ret = "TBD";
		if (term != null) {
			ret = (String) term.getPropertyValue(icdContentModel
					.getIdProperty());
		}
		return ret;
	}

	private Collection getClassDefinitions(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		Collection definitions = icdContentModel.getTerms(icdCategory,
				icdContentModel.getDefinitionProperty());

		return definitions;

	}

	private String getClassPreferredLabelRubricID(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		String ret = "";
		RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel
				.getIcdTitleProperty());
		if (term != null) {
			ret = (String) term.getPropertyValue(icdContentModel
					.getIdProperty());
		} else {
			ret = "TBD";
		}

		return ret;

	}

	private String getClassCode(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		String ret = "TBD";
		String term = (String) icdCategory.getPropertyValue(icdContentModel
				.getIcdCodeProperty());

		if (term != null)
			ret = term;

		return ret;

	}

	private String getClassPreferredLabel(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		String ret = "";
		RDFResource term = icdContentModel.getTerm(icdCategory, icdContentModel
				.getIcdTitleProperty());
		if (term != null)
			ret = term.getBrowserText();
		else
			ret = "TBD";

		return ret;

	}

	private String getSortingLabel(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		String ret = "TBD";
		String slabel = (String) icdCategory.getPropertyValue(icdContentModel
				.getSortingLabelProperty());
		if (slabel != null) {
			ret = slabel;
		}

		return ret;

	}

	private String getClassKind(RDFSNamedClass icdCategory,
			ICDContentModel icdContentModel) {

		String ret = "category";
		String term = (String) icdCategory.getPropertyValue(icdContentModel
				.getKindProperty());
		if (term != null)

			if (term.equals("modifiedcategory"))
				ret = "category";
			else
				ret = term;
		else {
			Collection parents = icdCategory.getSuperclasses(false);
			for (Iterator it = parents.iterator(); it.hasNext();) {
				RDFSNamedClass parent = (RDFSNamedClass) it.next();
				String pkind = (String) parent.getPropertyValue(icdContentModel
						.getKindProperty());
				if (pkind != null) {
					if (pkind.equals("chapter")) {
						ret = "block";
					} else {
						ret = "category";
					}
				}

			}

		}

		return ret;

	}
	
	  private String forXML(String aText){
		    final StringBuilder result = new StringBuilder();
		    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
		    char character =  iterator.current();
		    while (character != CharacterIterator.DONE ){
		      if (character == '<') {
		        result.append("&lt;");
		      }
		      else if (character == '>') {
		        result.append("&gt;");
		      }
		      else if (character == '\"') {
		        result.append("&quot;");
		      }
		      else if (character == '\'') {
		        result.append("&#039;");
		      }
		      else if (character == '&') {
		         result.append("&amp;");
		      }
		      else {
		        //the char is not a special one
		        //add it to the result as is
		        result.append(character);
		      }
		      character = iterator.next();
		    }
		    return result.toString();
		  }
	

}
