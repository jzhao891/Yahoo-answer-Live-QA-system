import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import Classes.Path;
import DocPrecessing.*;
import IndexingLucene.MyIndexReader;
import IndexingLucene.MyIndexWriter;
import IndexingLucene.PreProcessedCorpusReader;
public class DataServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try{
			DataServer instance=new DataServer();
			instance.doCore();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void doCore() throws Exception{
		Scanner sc=new Scanner(System.in);
		String s=null;
			DataServer ds=new DataServer();
//			IRFinalGui fg=new IRFinalGui();
			long startTime=System.currentTimeMillis();
			ds.dataReader(Path.dataSource);
			ds.WriteIndex();
			long entTime=System.currentTimeMillis();
			System.out.println(entTime-startTime);
		sc.close();
	}
	
	
	
	public static void dataReader(String filePath) throws IOException{
		XMLreader xml=new XMLreader("data/output.xml");
		Map<String,String> question;
		//loading stopword list and initiate the wtopwordRemover and word normalizer
		StopWordRemover stopwordRemover = new StopWordRemover();
		WordNormalizer normalizer = new WordNormalizer();
		//initialize the collection writer
		FileWriter wr=new FileWriter(Path.Collection);
		//derive question information from xml source and transform it to trectext file and store it on disk
		while((question=xml.nextQuestion())!=null){
			/*System.out.println("success!");
			System.out.println(question.get("uri"));
			System.out.printf("q:%s\n",question.get("question"));
			System.out.printf("best:%s\n",question.get("bestanswer"));*/
			wr.append(question.get("uri")+"\n");
			//deal with question content
			WordTokenizer tokenizer=new WordTokenizer(question.get("question"));
			String term=null;
			while((term=tokenizer.nextToken())!=null){
				term=normalizer.lowercase(term);
				if(!stopwordRemover.isStopword(term)){
					wr.append(normalizer.stem(term.toCharArray())+" ");
				}
			}
//			wr.append("\n");
			//deal with answer part
			tokenizer=new WordTokenizer(question.get("bestanswer"));
			term=null;
			while((term=tokenizer.nextToken())!=null){
				term=normalizer.lowercase(term);
				if(!stopwordRemover.isStopword(term)){
					wr.append(normalizer.stem(term.toCharArray())+" ");
				}
			}
			wr.append("\n");
		}
		xml.URIListOut();
		wr.close();
	}
	public void WriteIndex() throws Exception {
		// Initiate pre-processed collection file reader
		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader();
		
		// initiate the output object
		MyIndexWriter output=new MyIndexWriter();
		
		// initiate a doc object, which can hold document number and document content of a document
		Map<String, String> doc = null;

		int count=0;
		// build index of corpus document by document
		while ((doc = corpus.nextDocument()) != null) {
			// load document number and content of the document
			String docno = doc.keySet().iterator().next();
			String content = doc.get(docno);
			// index this document
			output.index(docno, content); 
			
			/*count++;
			if(count%30000==0)
				System.out.println("finish "+count+" docs");*/
		}
		System.out.println("totaly document count:  "+count);
		output.close();
	}
	
	public void ReadIndex(String dataType, String token) throws Exception {
		// Initiate the index file reader
		MyIndexReader ixreader=new MyIndexReader("content");
		
		// do retrieval
		int df = ixreader.DocFreq(token);
		long ctf = ixreader.CollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		if(df>0){
			int[][] posting = ixreader.getPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String docno = ixreader.getDocno(docid);
				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
			}
		}
		ixreader.close();
	} 
}
