package itti.com.pl.eda.tactics.ontology.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import itti.com.pl.eda.tactics.data.model.Ontology;
import itti.com.pl.eda.tactics.exception.OntologyAssemblyLayerException;
import itti.com.pl.eda.tactics.exception.OntologyAssemblyLayerException.OntologyAssemblyLayerExceptionType;


/**
 * class used for file-level ontology operations
 * @author marcin
 *
 */
public class OntologyAssemblyLayer {

	/**
	 * collection of already loaded into-memory objects
	 */
	private static Map<OntologyType, JenaOWLModel> owlModels = new HashMap<OntologyType, JenaOWLModel>();

	/**
	 * collection of Ontology objects (list of in-memory objects)
	 */
	private static Map<OntologyType, Ontology> ontologies = new HashMap<OntologyType, Ontology>();


	/**
	 * returns an Jena OWL Model from repository
	 * @param ontology Ontology object
	 * @return Jena Ontology Model of ontology
	 * @throws OntologyAssemblyLayerException
	 */
	public JenaOWLModel getOntologyModel(Ontology ontology) throws OntologyAssemblyLayerException{
		if(!owlModels.containsKey(ontology.getOntologyType())){
			loadOntologyFromRepository(ontology);
		}
		return owlModels.get(ontology.getOntologyType());
	}


	/**
	 * returns an instance of Ontology object
	 * @param ontologyType type of an object, that is expected
	 * @return Ontology object if already loaded
	 * @throws OntologyAssemblyLayerException
	 */
	public Ontology getOntology(OntologyType ontologyType) throws OntologyAssemblyLayerException {

		if(ontologyType == null){
			throw new OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType.EmptyType);

		}else if(!ontologies.containsKey(ontologyType)){
			throw new OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType.WiredName, ontologyType.toString());
		}

		return ontologies.get(ontologyType);
	}


	private void loadOntologyFromRepository(Ontology ontology) throws OntologyAssemblyLayerException{

		if(ontology.getOntologyLocation() == null){
			throw new OntologyAssemblyLayerException(
					OntologyAssemblyLayerExceptionType.DatabaseException, 
					"Failed to load ontlogy database information for ontologyId = " + ontology.getId());
		}

		String ontologyLocation = ontology.getOntologyLocation();

		JenaOWLModel model = loadOntology(ontologyLocation);

		owlModels.put(ontology.getOntologyType(), model);
		ontologies.put(ontology.getOntologyType(), ontology);
	}


	private static JenaOWLModel loadOntology(String ontologyFileName) throws OntologyAssemblyLayerException{

		JenaOWLModel model = null;

//		try {
//			model = ProtegeOWL.createJenaOWLModelFromInputStream(new FileInputStream(ontologyFileName));
//		} catch (Exception e) {
//
//			model = null;

			model = ProtegeOWL.createJenaOWLModel();
			URI uri = URIUtilities.createURI("file:///" + ontologyFileName);
			String dirRepo = ontologyFileName.substring(0, ontologyFileName.lastIndexOf('/') + 1) + "imports/";
			File fileRepo = new File(dirRepo);
			model.getRepositoryManager().addProjectRepository(
					new LocalFolderRepository(fileRepo));
			try{
				model.load(uri, FileUtils.langXMLAbbrev);
			}catch (Exception ex) {
				throw new OntologyAssemblyLayerException(
				OntologyAssemblyLayerExceptionType.LoadException, ex.getMessage());
			}
//		}
		return model;
	}


	/**
	 * Saves an (updated/modified) ontology model in the Ontology Repository
	 * @param model JenaOWLObject ontology model
	 * @param ontology Ontology object (specifies ontology location)
	 * @return true, if process ends with success, false otherwise
	 * @throws OntologyAssemblyLayerException
	 */
	@SuppressWarnings("deprecation")
	public boolean saveOntology(JenaOWLModel model, Ontology ontology) throws OntologyAssemblyLayerException{

		try{
			model.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
			model.getNamespaceManager().setPrefix(SWRLNames.SWRLX_NAMESPACE, SWRLNames.SWRLX_PREFIX);
//			modelQos.getNamespaceManager().setPrefix(SWRLNames.
//					xmlns:assert="http://www.owl-ontologies.com/assert.owl#"

			List<String> errList = new ArrayList<String>();
			
//			URI absoluteURI = new URI(ontology.getOntologyLocation());

//			OutputStream fos = new FileOutputStream("c:/wacho.owl");
			String location = ontology.getOntologyLocation();
			//TODO: temporary
			location = location.substring(0, location.lastIndexOf('.')) + '2' + 
				location.substring(location.lastIndexOf('.'));
			File f = new File(location);
			if(!f.exists()){
				f.createNewFile();
			}
			OutputStream fos = new FileOutputStream(f, false);
			model.save(fos, "RDF/XML-ABBREV", errList);
			fos.flush();
			fos.close();
//			model.save(absoluteURI, "RDF/XML-ABBREV", errList);

			if(!errList.isEmpty()){
				throw new OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType.SaveException, errList);
			}

		}catch(Exception e){
			throw new OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType.SaveException, e.toString());
		}
		return true;
	}
}