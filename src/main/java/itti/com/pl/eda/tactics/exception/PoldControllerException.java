package itti.com.pl.eda.tactics.exception;


public class PoldControllerException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public enum PoldControllerExceptionType{
		OntologyManagerIsNull, 
		UnrecoginzedPolicyElement, 
		PolicyElementIsNull,
	}

	static{

		descriptions.put(PoldControllerExceptionType.OntologyManagerIsNull.name(), "Ontology manager is NULL");
		descriptions.put(PoldControllerExceptionType.UnrecoginzedPolicyElement.name(), "Unrecoginzed HL Policy element");
	}

	public PoldControllerException(PoldControllerExceptionType excType,	String msg) {
		super(excType.name(), msg);
	}

	public PoldControllerException(PoldControllerExceptionType excType) {
		super(excType.name());
	}
}
