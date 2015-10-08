package itti.com.pl.eda.tactics.exception;

public class SwrlEngineException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	static{

		descriptions.put(SwrlEngineExceptionType.OntologyModelIsNull.name(), "OntologyModel is set to NULL");
		descriptions.put(SwrlEngineExceptionType.MissingClass.name(), "OWL class is missing - invalid owl model is used?");
		descriptions.put(SwrlEngineExceptionType.NoPolicyTypes.name(), "Empty or NULL policy type enumerator was given as a parameter?");
	}

	public enum SwrlEngineExceptionType{
		OntologyModelIsNull, 
		MissingClass, 
		NoPolicyTypes, 
		SwrlBridgeException,
	}

	public SwrlEngineException(SwrlEngineExceptionType type){
		super(type.name());
	}

	public SwrlEngineException(SwrlEngineExceptionType type, String desc){
		super(type.name(), desc);
	}
}
