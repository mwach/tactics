package itti.com.pl.eda.tactics.exception;


public class OntologyManagerException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;


	public static enum OntologyManagerExceptionType{
		OntologyManagerIsNull, 
		UnrecoginzedOption,
	}

	static{
		
		descriptions.put
				(OntologyManagerExceptionType.OntologyManagerIsNull.name(), "Ontology manager is NULL");
		descriptions.put
				(OntologyManagerExceptionType.UnrecoginzedOption.name(), "Unrecognized option: ");
	}

	public OntologyManagerException(OntologyManagerExceptionType exceptionType) {
		super(exceptionType.name());
	}

	public OntologyManagerException(OntologyManagerExceptionType exceptionType, String desc) {
		super(exceptionType.name(), desc);
	}

}
