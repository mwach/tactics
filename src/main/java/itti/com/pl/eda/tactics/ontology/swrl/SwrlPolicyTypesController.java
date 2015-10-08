package itti.com.pl.eda.tactics.ontology.swrl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import itti.com.pl.eda.tactics.exception.PolicyTypeException;
import itti.com.pl.eda.tactics.exception.PolicyTypeException.PolicyTypeExceptionType;
import itti.com.pl.eda.tactics.utils.FileUtils;
import itti.com.pl.eda.tactics.utils.XmlUtils;


/**
 * class keeps information about swlr-defined policy types used during policy classification process
 * policy types are stored in policy.types.definition.xml configuration file
 */
public class SwrlPolicyTypesController {

	private Logger logger = Logger.getLogger(SwrlPolicyTypesController.class);

	private List<SwrlPolicyType> swrlPolicyTypes = new ArrayList<SwrlPolicyType>();


	/**
	 * returns a list of swrl-defined policy types
	 * @return list of swrl-defined policy types
	 */
	public List<SwrlPolicyType> getSwrlPolicyTypes(){
		return swrlPolicyTypes;
	}


	/**
	 * loads swrl-defined policy types from configuration file
	 * @param fileName configuration file (policy.types.definition.xml)
	 * @return true, if policy types were loaded succesfully, false otherwise
	 */
	public boolean loadPolicyTypesConfiguration(String fileName){

		logger.info("Reading policy types from file");
		logger.info("File name: " + fileName);

		boolean result = false;

		try{

			boolean fileExists = FileUtils.isDocumentExists(fileName);

			if(fileExists){
	
				logger.info("Parsing file...");
				parseDocument(fileName);

				result = true;

			}else{
				logger.info("Policy configuration file does not exist");
				throw new PolicyTypeException(PolicyTypeExceptionType.FileNotFound, fileName);
			}
		}catch(Exception e){
			logger.warn("Exceptions during loading policy configuration:\n" + e.toString());
		}

		return result;
	}


	private void parseDocument(String fileName) throws Exception{

		try{

			Document doc = XmlUtils.getXmlDocument(fileName);

			NodeList typeNodes = doc.getElementsByTagName("type");

			for (int i=0 ; i<typeNodes.getLength() ; i++) {

				Node typeNode = typeNodes.item(i);

				Node attrName = typeNode.getAttributes().getNamedItem("name");
				Node attrSwrlQuery = typeNode.getAttributes().getNamedItem("swrl_query");
				Node attrDesc = typeNode.getAttributes().getNamedItem("description");

				String name = (attrName != null ? attrName.getNodeValue() : null);
				String swrlQuery = (attrSwrlQuery != null ? attrSwrlQuery.getNodeValue() : null);
				String desc = (attrDesc != null ? attrDesc.getNodeValue() : null);
				
				SwrlPolicyType policyType = new SwrlPolicyType(name, swrlQuery, desc);
				swrlPolicyTypes.add(policyType);
			}

			logger.info("File parsed correctly. Total types: " + typeNodes.getLength());

		}catch (Exception e) {
			logger.warn("Exception during parsing policy types file: " + e.getMessage());
			logger.warn("Process canceled: policy types will NOT be used!");
			swrlPolicyTypes.clear();
			throw e;
		}
	}


}
