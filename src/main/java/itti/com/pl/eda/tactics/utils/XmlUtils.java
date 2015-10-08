package itti.com.pl.eda.tactics.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import itti.com.pl.eda.tactics.exception.ConfigException;
import itti.com.pl.eda.tactics.exception.ConfigException.ConfigExceptionType;


/**
 * tool class for working with xmls
 * @author marcin
 *
 */
public class XmlUtils {

	private static Logger logger = Logger.getLogger(XmlUtils.class);

	/**
	 * parses xml configuration file into map of property-value pairs
	 * @param configFileName location of the config file
	 * @return map of property-value pairs, or null if process fails
	 * @throws ConfigException
	 */
	public static Map<String, String> parseProperiesDocument(String configFileName) throws ConfigException{

		Map<String, String> properties = new HashMap<String, String>();

		try {

			logger.info("Parsing document: " + configFileName);

			Document doc = getXmlDocument(configFileName);

			NodeList propertiesList = doc.getElementsByTagName("property");

			logger.info("Getting properties list");

			if(propertiesList != null){

				for(int i=0 ; i<propertiesList.getLength() ; i++){

					Node propertyNode = propertiesList.item(i);

					Node attrName = propertyNode.getAttributes().getNamedItem("name");
					Node attrValue = propertyNode.getAttributes().getNamedItem("value");
	
					if(attrName == null || attrValue == null){
						if(attrName == null){
							throw new ConfigException(
									ConfigExceptionType.MissingAttributeException, "name");
						}else{
							throw new ConfigException(
									ConfigExceptionType.MissingAttributeException, "value");
						}
					}else{

						String propName = attrName.getNodeValue();
						String propValue = attrValue.getNodeValue();

						logger.info("Loaded property: " + propName + "=" + propValue);

						if(StringUtils.isNullOrEmpty(propName) || StringUtils.isNullOrEmpty(propValue)){

							if(propName == null){
								throw new ConfigException(
										ConfigExceptionType.EmptyAttributeException, "name");
							}else{
								throw new ConfigException(
										ConfigExceptionType.EmptyAttributeException, "value (parameter " + propName + ")");
							}

						}else{
							properties.put(propName.trim(), propValue.trim());
						}
					}
				}
			}else{
				logger.info("There were no property entries in loaded file");
			}

		} catch (ParserConfigurationException e) {
			throw new ConfigException(
					ConfigExceptionType.ParseException, e.toString());
		} catch (SAXException e) {
			throw new ConfigException(
					ConfigExceptionType.ParseException, e.toString());
		} catch (IOException e) {
			throw new ConfigException(
					ConfigExceptionType.FileNotFoundException, e.toString());
		}

		return properties;
	}


	/**
	 * returns DOM instance of the file-stored xml document
	 * @param fileName name of the xml file
	 * @return DOM instance
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getXmlDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {

		Document doc = null;

		logger.info("Parsing XML document");

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.parse(fileName);

		logger.info("Document parsed");

		return doc;
	}
}
