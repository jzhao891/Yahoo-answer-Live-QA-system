package Classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

/**
 * This file is for the assignment of INFSCI 2140 in 2016 Spring
 *
 * StopWordRemover is a class takes charge of judging whether a given word
 * is a stopword by calling the method <i>isStopword(word)</i>.
 */
public class StopWordRemover {
	//you can add essential private methods or variables
	Map<String, Integer> StopWords=new HashMap<String, Integer>(); //map object for saving the stopwords list read out of the file;

	public StopWordRemover( ) throws IOException{
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		BufferedReader SW_reader=new BufferedReader(new FileReader(Path.StopwordDir));
		  
			try{
				String line=SW_reader.readLine();
				int i=0;
				while(line!=null){
					StopWords.put(line, i);
					line=SW_reader.readLine();
					i++;
				}
				//System.out.println(StopWords);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			SW_reader.close();
		}
	
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		String str=String.valueOf(word);
		return StopWords.containsKey(str);
	}
}
