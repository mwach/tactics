package itti.com.pl.eda.tactics.exception;

import java.util.Collection;

public class PoldServerException extends AbstractPoldException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PoldServerException(String exceptionDescription,
			Collection<String> errList) {
		super(exceptionDescription, errList);
	}

}
