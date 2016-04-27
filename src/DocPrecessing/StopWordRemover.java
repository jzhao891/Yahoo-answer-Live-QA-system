package DocPrecessing;

import Classes.Path;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This file is for the assignment of INFSCI 2140 in 2016 Spring
 * <p>
 * StopWordRemover is a class takes charge of judging whether a given word
 * is a stopword by calling the method <i>isStopword(word)</i>.
 */
public class StopWordRemover {
    //you can add essential private methods or variables

    private Set<String> stopWords = new HashSet<String>();

    public StopWordRemover() {
        // load and store the stop words from the fileinputstream with appropriate data structure
        // that you believe is suitable for matching stop words.
        // address of stopword.txt should be Path.StopwordDir
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(Path.StopwordDir));
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String str = new String(bytes);
            String[] words = str.split("\n");
            //STORE STOPWORDS INTO MEMORY
            for (String word : words) {
                stopWords.add(word);
            }

        } catch (Exception e) {//EXCEPTION
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // YOU MUST IMPLEMENT THIS METHOD
    public boolean isStopword(String word) {
        // return true if the input word is a stopword, or false if not
        return stopWords.contains(word);
    }


}
