package itti.com.pl.eda.tactics.exception;


public class PoldHibernateException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public enum PoldHibernateExceptionType{
		DeletePolicy, 
		InitializationException,
		GetOntologyByName,
		GetDefOntology, 
		GetQoSRefinementMap,
		PolicyReplace, 
		PolicyActivate;
	};

	static{

		descriptions.put(PoldHibernateExceptionType.DeletePolicy.name(), "Invalid argument at deletePolicy function");
		descriptions.put(PoldHibernateExceptionType.InitializationException.name(), "Exception during hibernate initialization");
		descriptions.put(PoldHibernateExceptionType.GetDefOntology.name(), "Exception during loading ontologies from DB");
		descriptions.put(PoldHibernateExceptionType.GetDefOntology.name(), "Exception during loading refinement parameters from DB");
	}
	
	public PoldHibernateException(PoldHibernateExceptionType excType, String desc) {
		super(excType.name(), desc);
	}

	public PoldHibernateException(PoldHibernateExceptionType excType) {
		super(excType.name());
	}

}
