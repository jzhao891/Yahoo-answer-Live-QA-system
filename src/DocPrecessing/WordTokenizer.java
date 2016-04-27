package DocPrecessing;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is for the assignment of INFSCI 2140 in 2016 Spring
 * <p>
 * TextTokenizer can split a sequence of text into individual word tokens, the delimiters can be any common punctuation marks(space, period etc.).
 */
public class WordTokenizer {
    //you can add any essential private method or variable
    private List<String> words;
    private int cusor = 0;

    // YOU MUST IMPLEMENT THIS METHOD
    public WordTokenizer(String texts) {
        // this constructor will tokenize the input texts (usually it is the char array for a whole document)
        String[] wordsArr = texts.split("[^\\pL\\pN]");
        words = new ArrayList<String>();
        for (String word : wordsArr) {
            if (!word.trim().equals("")) {
                words.add(word);
            }
        }
    }

    // YOU MUST IMPLEMENT THIS METHOD
    public String nextToken() {
        // read and return the next word of the document
        // or return null if it reaches the end of the document
        if (words == null || cusor >= words.size()) {
            return null;
        }
        // read and return the next word of the document
        // or return null if it reaches the end of the document
        if (words == null || cusor >= words.size()) {
            return null;
        }
        return words.get(cusor++).toString();
    }

}
