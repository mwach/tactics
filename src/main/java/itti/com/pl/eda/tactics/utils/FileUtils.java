package itti.com.pl.eda.tactics.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * tool class for working with files
 * @author marcin
 *
 */
public class FileUtils {

	/**
	 * checks if file exists on disk
	 * @param fileName absolute file path
	 * @return true if file exists, false otherwise
	 * @throws Exception
	 */
	public static boolean isDocumentExists(String fileName) throws Exception{
		if(fileName != null){
			File file = new File(fileName);
			return file.exists();
		}else{
			throw new Exception("File name is NULL");
		}
	}

	/**
	 * loads file content into list of strings (each file content line is stored in one string)
	 * @param fileName absolute file path
	 * @return file content in list of strings form
	 */
	public static List<String> loadFileContent(String fileName){

		List<String> fileContent = null;
		BufferedReader br = null;

		try{
			br = new BufferedReader(new FileReader(fileName));

			fileContent = new ArrayList<String>();

			String line = null;

			while((line = br.readLine()) != null){
				fileContent.add(line);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}finally{

			if(br != null){
				try {
					br.close();
				} catch (IOException e) {}
			}
		}

		return fileContent;
	}
}
