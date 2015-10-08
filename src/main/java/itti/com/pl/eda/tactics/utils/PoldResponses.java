package itti.com.pl.eda.tactics.utils;


/**
 * list of possible pold server responses
 * @author marcin
 *
 */
public enum PoldResponses{

	SystemNotInitialized,
	SystemInitOK,
	SystemInitFailed,

	PolicyHLAddOK,
	PolicyHLAddFailed,

	PolicyHLDeleteOK,
	PolicyHLDeleteFailed,

	PolicyHLReplaceOK,
	PolicyHLReplaceFailed, 

	PolicyILActivationOK, 
	PolicyILActivationFailed, 

	UnrecognizedOption;

	public static PoldResponses getPoldResponse(String val){

		if(StringUtils.isNullOrEmpty(val)){
			return null;
		}else{
			for (PoldResponses response : PoldResponses.values()) {
				if(response.name().equalsIgnoreCase(val)){
					return response;
				}
			}
			return null;
		}
	}
}
