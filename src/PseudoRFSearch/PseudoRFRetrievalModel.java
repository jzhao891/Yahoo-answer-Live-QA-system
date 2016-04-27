package PseudoRFSearch;

import Classes.Document;
import Classes.Query;
import Classes.QueryHis;
import IndexingLucene.MyIndexReader;
import Search.QueryRetrievalModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Class of Assignment 4.
 * Implement your pseudo feedback retrieval model here
 * -- INFSCI 2140: Information Storage and Retrieval Spring 2016
 */


class ResultComprator implements Comparator<Object>{
    @Override
    public int compare(Object o1, Object o2) {
        Document d1 = (Document) o1;
        Document d2 = (Document) o2;
        if(d1.score() != d2.score())
            return d1.score()<d2.score()?1:-1;
        else
            return 1;
    }    
}


public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	MyIndexReader hqreader;
        private double u = 2000;   
	private long colLength = 142065539;
	boolean noHq=true;
        
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
		try {
			hqreader=new MyIndexReader("Hq");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			noHq=true;
			System.out.println("no query history");
		}
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback. 
	 * The returned results (retrieved documents) should be ranked by the score1 (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score1 P(token|document) with its score1 in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score1, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		//HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);
		HashMap<String,Double> TokenHqScore=null;
		if(noHq==false&&QueryHis.queryCont!=0){
			TokenHqScore=GetTokenHqScore(aQuery); 
		}
		    
                // sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();
		List<Document> resultsFinal = null;
                
                List<String> tokens = aQuery.GetQueryContent();             
                //<token,P(w|d)>
		HashMap<String,HashMap> tokenfreqMap = new HashMap<String,HashMap>();   

		// find all relevant documents that contains at least one token in the query
		HashSet<Integer> docSet = new HashSet<Integer>();
                for(int i = 0;i<tokens.size();i++){ 
                    // <docid,token's frequency>
                    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(); 
                    int[][] posting = ixreader.getPostingList(tokens.get(i));
                    if (posting == null || posting.length == 0) { 
                    	tokens.remove(tokens.get(i));
                    	i--;
                    	continue;
                    }
                    for (int j = 0; j < posting.length; j++) {
                    	int docid = posting[j][0];
                    	int freq = posting[j][1];
                    	map.put(docid, freq); 
                    	docSet.add(docid); 
                    	tokenfreqMap.put(tokens.get(i), map);
                    }
                }
                if(tokenfreqMap.size()!=0){
                	for(int docid : docSet) { 
                        int docLength = ixreader.docLength(docid);
                        double finalScore = 1;
    			
                        for (String token : tokens){
    			long cf = ixreader.CollectionFreq(token);
    			HashMap tokenMap = tokenfreqMap.get(token);   
    			int freq = tokenMap.containsKey(docid) ? (Integer)tokenMap.get(docid) : 0;
    			double score1 = (freq + u * cf / colLength) / (docLength + u);
    			////////////////////////
    			double score=score1;
    			if(noHq==false){
    				double A=aQuery.GettermSum()/(aQuery.GettermSum()+QueryHis.averageLen);//coefficient of p(w|Q)
    				double B=QueryHis.averageLen/(aQuery.GettermSum()+QueryHis.averageLen);//coefficient of p(w|Hq)
    				if(A==0){
    					A=1;
    				}
    				score=A * score1 + B * TokenHqScore.get(token);
    			}
    			
    			////////////////////////
    			finalScore *= score;		
                        }
                        if(finalScore != 0){
    			String docno = ixreader.getDocno(docid);
    			Document document = new Document(docid+"", docno, finalScore);
    			results.add(document);
                        }			
    		}
    		
    		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    		Collections.sort(results, new ResultComprator());
    		if(results.size()>=3){
        		resultsFinal= results.subList(0, TopN);}
            }
             else{
            	 resultsFinal=results;
             }
               return resultsFinal;
                            
                
                
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score1 in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score1> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
                
                QueryRetrievalModel originalModel = new QueryRetrievalModel(ixreader);
		List<Document> feedbackDocList = originalModel.retrieveQuery(aQuery, TopK);
		     
		List<String> tokens = aQuery.GetQueryContent();
                for(int i=0;i<tokens.size();i++){
                    if (tokens.get(i) == null) {
			continue;
                    }			
                    // get total docLength
                    int docLength = 0;
                    HashSet<Integer> docidSet = new HashSet<Integer>();
                    for (Document doc : feedbackDocList) {
			docLength += ixreader.docLength(Integer.parseInt(doc.docid())); 
			docidSet.add(Integer.parseInt(doc.docid()));
                    }

                    // get total frequency 
                    int freq = 0;
                    int[][] posting = ixreader.getPostingList(tokens.get(i));
                    for (int j = 0; j < posting.length; j++) {
			if (docidSet.contains(posting[j][0])) {
                            freq += posting[j][1];
			}
                    }

                    // Dirichlet smoothing
                    long cf = ixreader.CollectionFreq(tokens.get(i));
                    double score = (freq + u * cf / colLength) / (docLength + u);
                    TokenRFScore.put(tokens.get(i), score);
		}
		return TokenRFScore;
	}
	
	public HashMap<String,Double> GetTokenHqScore(Query aQuery) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenHqScore=new HashMap<String,Double>();
		//Get p(qk+1|Hq(k))=(1/k * ( p(w1|Hq1)+p(w1|Hq2)+...+p(w1|Hqk) )) * (1/k * ( p(w2|Hq1)+p(w2|Hq2)+...+p(w2|Hqk) )) * .......;
		ArrayList<String> tokens=aQuery.GetQueryContent();
		int k=QueryHis.queryCont;//
		for(String token : tokens){
			int[][] pl=hqreader.getPostingList(token);
			double pWHq=0.0;//p(w|Hqi)
			if(pl!=null){
				for(int i=0; i<pl.length;i++){
					int qid=pl[i][0];
					int feq=pl[i][1];
					int queryLen=QueryHis.collection.get(qid).GettermSum();
					pWHq+=feq/queryLen;
				}
				pWHq=1/k * pWHq;
			}
			
			TokenHqScore.put(token, pWHq);
		}
		
		return TokenHqScore;
	}
	
	
}