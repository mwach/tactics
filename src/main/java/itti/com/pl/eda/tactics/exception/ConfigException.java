package itti.com.pl.eda.tactics.exception;

public class ConfigException extends AbstractPoldException{

	private static final long serialVersionUID = 1L;

	public static enum ConfigExceptionType{
		FileNotFoundException, 
		ParseException, 
		MissingAttributeException, 
		EmptyAttributeException, 
		ParseConfigAttributeException
	};

	static{
		descriptions.put
				(ConfigExceptionType.FileNotFoundException.name(), "Configuration file not found");
		descriptions.put
				(ConfigExceptionType.ParseException.name(), "XML parser exception");
		descriptions.put
				(ConfigExceptionType.MissingAttributeException.name(), "Missing attribute");
		descriptions.put
				(ConfigExceptionType.EmptyAttributeException.name(), "Empty value for attribute");
		descriptions.put
				(ConfigExceptionType.ParseConfigAttributeException.name(), "Exception during parsing server settings' attribute");
	}

	public ConfigException(ConfigExceptionType excType, String desc) {
		super(excType.name(), desc);
	}

	public ConfigException(ConfigExceptionType excType) {
		super(excType.name());
	}
}
