package Search;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A language retrieval model for ranking documents
 * -- INFSCI 2140: Information Storage and Retrieval Spring 2016
 */

class DocDetail{
    //save <token,c(token,doc)>
    //token is string or integer?
    HashMap<String,Integer> tdFreq;
    //save |doc|
    int docSize;
    
}

//class Result{
//    int docId;
//    double score;
//}

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

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
        private int colLength = 142065539;
	private int u = 2000;
        
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		
                
                ArrayList<String> tokens = aQuery.GetQueryContent();

                //Key is integer DocId
                HashMap<Integer,DocDetail> docList = new HashMap<>();               
                //<token,P(token|REF)>
                HashMap<String, Double> tokenRefProb = new HashMap<>();
                
                for(int i=0;i<tokens.size();i++){
                    int postings[][] = indexReader.getPostingList(tokens.get(i));
                    //remove token that doesn't appear in the collection
                    if (postings == null || postings.length == 0) { 
			tokens.remove(tokens.get(i));
			continue;
                    }
                    
                    //put detail information to doclist
                    for(int j = 0;j<postings.length;j++){
                        int docId = postings[j][0];
                        //出现的次数
                        int docFreq = postings[j][1];
                        DocDetail dde = new DocDetail();
                        dde.tdFreq = new HashMap<>();
                        dde.tdFreq.put(tokens.get(i), docFreq);
                        docList.put(docId, dde);                       
                    }
                    
                    //calculate P(token|REF) and save into tokenRefProb
                    double tokenRef = indexReader.CollectionFreq(tokens.get(i));
                    tokenRefProb.put(tokens.get(i),tokenRef);
                }
                
                ArrayList<Document> rList = new ArrayList<Document>();
                
                Iterator iter = docList.entrySet().iterator();
                while(iter.hasNext()){
                    double score = 1;
                    Map.Entry entry = (Map.Entry)iter.next();
                    int docId = (int) entry.getKey();
                    DocDetail docdetail = (DocDetail) entry.getValue();
                    int docSize = indexReader.docLength(docId);
                    
                    for(String token: tokens){
                        long cf = indexReader.CollectionFreq(token);
                        HashMap dmap = docdetail.tdFreq;
                        //System.out.println(dmap.get(token)+"hello");
                        //因为这个doc里不一定包含每个token,可能只有其中的一个或几个
                        int df = dmap.containsKey(token) ? (int)dmap.get(token):0;
                        double proWD = (double)(df+(double)u*cf/colLength)/(docSize+u);
                        score*=proWD;
                      
                    }
                    if(score != 0){
                      String docno = indexReader.getDocno(docId);
		      Document doc = new Document(String.valueOf(docId), docno, score);
                      rList.add(doc);
                    }
                }
                System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(rList, new ResultComprator());
		
        System.out.print(rList.size());        
		return rList.subList(0, TopN);
	}
	
}