package itti.com.pl.eda.tactics.ontology.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;


/**
 * ontology reasoner wrapper
 * reasoner - third party element which is able to query and manipulate ontology
 * @author marcin
 *
 */
public class OntologyReasoner {

	private OWLModel model = null;
	private ProtegeOWLReasoner reasoner = null;


	/**
	 * default constructor
	 * @param owlModel owl ontology model
	 */
	public OntologyReasoner(OWLModel owlModel){
		this.model = owlModel;
	}


	/**
	 * returns a list of instances belonging to the class specified by its name
	 * @param className name of the ontology class
	 * @return list of class instances
	 */
	@SuppressWarnings("unchecked")
	public List<String> getInstancesNames(String className){

		if(reasoner == null){
			connectReasoner();
		}

		List<String> resultList = null;

		if(reasoner != null && reasoner.isConnected()){

			Collection<SimpleInstance> instancesList = null;

			Collection<OWLNamedClass> includeCollection = new HashSet<OWLNamedClass>();
			OWLNamedClass parentClass = model.getOWLNamedClass(className);
			includeCollection.add(parentClass);

			OWLClass intersectionClass = model.createOWLIntersectionClass(includeCollection);

			try {
				instancesList = reasoner.getIndividualsBelongingToClass(intersectionClass, null);
			}
			catch(DIGReasonerException e) {
				instancesList = null;
			}

			if(instancesList != null){

				resultList = new ArrayList<String>();
				Iterator<SimpleInstance> instancesIterartor = instancesList.iterator();
				while(instancesIterartor.hasNext()){
					resultList.add(instancesIterartor.next().getName());
				}
			}
		}
		return resultList;
	}


	private void connectReasoner(){

		reasoner = ReasonerManager.getInstance().getReasoner(model);
//		reasoner.setURL(Settings.REASONER_URL);
		if(reasoner.isConnected()){
			try {
				reasoner.classifyTaxonomy(null);
			}catch (Exception e) {
				e.printStackTrace();
				reasoner = null;
			}
		}
	}
}
