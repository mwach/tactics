package itti.com.pl.eda.tactics.utils;

/**
 * tool class for working with strings
 * @author marcin
 *
 */
public class StringUtils {

	/**
	 * compare two strings
	 * @param str1 first string
	 * @param str2 second string
	 * @return true if booth are equal (also if both are null), false otherwise
	 */
	public static boolean compareStrings(String str1, String str2) {

		if(str1 == null && str2 == null){
			return true;
		}else if(str1 == null || str2 == null){
			return false;
		}else{
			return str1.equals(str2);
		}
	}

	/**
	 * checks if string is null or empty
	 * @param string examined string
	 * @return true, if string is null or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(String string){
		return string == null || string.trim().length() == 0;
	}


	/**
	 * formats string by upp'ing its first letter 
	 * @param string string
	 * @return formated string
	 */
	public static String getValidPojoForm(String string){
		if(string != null && string.length() > 1){
			return string.substring(0, 1).toLowerCase() + string.substring(1);
		}else{
			return null;
		}
	}


	/**
	 * check if string value can be parsed to int
	 * @param string examined string
	 * @return true, if value can be parsed to int, false otherwise
	 */
	public static boolean isInt(String string){

		if(string == null || string.trim().equals("")){
			return false;
		}{
			boolean result = true;
			try{
				Integer.parseInt(string.trim());
			}catch(Exception e){
				result = false;
			}
			return result;
		}
	}


	/**
	 * parses given string into int
	 * @param string string to parse
	 * @return parsed string, or -1 if parse process fails
	 */
	public static int getIntValue(String string){

		if(string == null || string.trim().equals("") || string.trim().equalsIgnoreCase("null")){
			return -1;
		}{
			int result = -1;
			try{
				result = Integer.parseInt(string.trim());
			}catch(Exception e){
				if(string.indexOf('.') != -1 || string.indexOf(',') != -1){
					string = string.replace(',', '.');
					string = string.substring(0, string.indexOf('.'));
					return getIntValue(string);
				}else{
					result = -1;
				}
			}
			return result;
		}
	}


	/**
	 * parses given string into boolean
	 * @param string string to parse
	 * @return parsed string, or false if parse process fails
	 */
	public static boolean getBooleanValue(String value) {

		boolean retValue = false;

		if(isNullOrEmpty(value)){
			return false;
		}

		try{
			retValue = Boolean.parseBoolean(value.trim());
		}catch (Exception e) {
		}
		return retValue;
	}


	/**
	 * formats int value by adding some zeros into it 
	 * @param value value to parse
	 * @param dec number of zeros to add
	 * @return formatted value in string format
	 */
	public static String getFormattedStringAddDecimals(int value, int dec){

		if (dec == 2){
			return (value < 10) ? "0" + String.valueOf(value) : String.valueOf(value);

		} else if(dec == 4){
			return (value < 10) ? "000" + String.valueOf(value) : 
				((value < 100) ? "00" + String.valueOf(value) : 
					((value < 1000) ? "0" + String.valueOf(value) :
						String.valueOf(value)));
		}
		return String.valueOf(value);
	}

	public static String removePrefix(String string) {
		if(!isNullOrEmpty(string)){
			int prefixPos = string.lastIndexOf(':');
			if(prefixPos != -1){
				return string.substring(prefixPos + 1);
			}
		}
		return string;
	}

	public static String getPrefix(String string) {
		if(!isNullOrEmpty(string)){
			int prefixPos = string.indexOf(':');
			if(prefixPos != -1){
				return string.substring(0, prefixPos);
			}
		}
		return string;
	}
}
