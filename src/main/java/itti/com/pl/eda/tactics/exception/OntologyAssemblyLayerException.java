package itti.com.pl.eda.tactics.exception;

import java.util.List;


public class OntologyAssemblyLayerException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public static enum OntologyAssemblyLayerExceptionType{
		OntologyAssemblyLayerIsNull, 
		DatabaseException, 
		SaveException, 
		EmptyType, 
		WiredName, 
		LoadException, 
	}

	static{
		
		descriptions.put
				(OntologyAssemblyLayerExceptionType.OntologyAssemblyLayerIsNull.name(), "Ontology assembly layer is NULL");
		descriptions.put
				(OntologyAssemblyLayerExceptionType.DatabaseException.name(), "Database exception");
		descriptions.put
				(OntologyAssemblyLayerExceptionType.SaveException.name(), "Exception during ontlogy saving");
		descriptions.put
				(OntologyAssemblyLayerExceptionType.WiredName.name(), "Name doesn't exists in ontology set yet");
		descriptions.put
				(OntologyAssemblyLayerExceptionType.LoadException.name(), "Exception durin ontology load process");
	}

	public OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType type) {
		super(type.name());
	}

	public OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType type, String desc) {
		super(type.name(), desc);
	}

	public OntologyAssemblyLayerException(OntologyAssemblyLayerExceptionType type, List<String> errList) {
		super(type.name(), errList);
	}
}
