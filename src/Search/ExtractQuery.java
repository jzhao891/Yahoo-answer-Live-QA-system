package Search;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;
import Classes.StopWordRemover;
import Classes.QueryHis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Read and parse TREC queries
 * -- INFSCI 2140: Information Storage and Retrieval Spring 2016
 */
public class ExtractQuery {
	

	public Query GetQueries(String q) throws Exception {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: third topic title is ----Star Trek "The Next Generation"-----, if your code can recognize the phrase marked by "", 
		//    and further process the phrase in search, you will get extra points.
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		//BufferedReader reader=new BufferedReader(new FileReader(Path.TopicDir)); // Entrance of the query input from the GUI.
		Stemmer stem=new Stemmer();
		StopWordRemover SWR=new StopWordRemover();
		Query query = new Query();
		//String result=null;
		//String query=new String();
		//String qTime=new String();
		//Boolean isEnd=false;
		 
		//String line=null;
		//get query history, get ready to input new query
		
		//Boolean isTime=false;
		/*
		while(true){
			String line=reader.readLine();
			
			if(line==null)
				break;
			line=line.trim();
			//Handle the tags in the line;
			
			if(line.contains("<query>")){
				query=line.replace("<query>", "");
				query=query.trim();
				isEnd=true;
				
			}
			if(line.contains("<time>")){
				qTime=line.replace("<time>", "");
				qTime=qTime.trim();
				isTime=true;
			}*/
			//Process the tokenize, lowercase, stemmer and removal of stopwords;
			int count=0;
			ArrayList<String> qcontent=new ArrayList<String>();
				String QContent=q;
				if(QContent!=null){
					String[] str=QContent.split("[^\\pL\\pN]");
					//QContent="";
					for(int i=0;i<str.length;i++){
						str[i]=str[i].replaceAll("[^a-z^A-Z^0-9]", "");
						str[i]=str[i].toLowerCase();
						if(SWR.isStopword(str[i].toCharArray())){
							str[i]="";
						}
						if(str[i]!=""){
							stem.add(str[i].toCharArray(), str[i].length());
							stem.stem();
							str[i]=stem.toString();
							count++;
							qcontent.add(str[i]);
						}
					}
					
					//input query into query history.
					query.SettermSum(count);
					query.SetQueryContent(qcontent);
					
					/*Qresult.SetQueryTime(qTime);
					Qresult.SetQueryContent(QContent);
					result.add(Qresult);
					query="";
					qTime="";
					isEnd=false;
					isTime=false;*/
					
				}
				return query;
		}
	

	
	/*public void doCore() throws Exception{
		ExtractQuery test=new ExtractQuery();
		List<Query> result=new ArrayList<Query>();
		result=test.GetQueries();
		for (int i=0;i<result.size();i++){
			if(result.get(i).GetQueryContent().contains("  ")){
				result.get(i).GetQueryContent().replaceAll("  ", " ");
			}
			System.out.println(result.get(i).GetQueryTime());
			System.out.println(result.get(i).GetQueryContent());
		}		
	}*/
//test main
//	public static void main(String[] args) throws Exception {
//		ExtractQuery test=new ExtractQuery();
//		List<Query> result=new ArrayList<Query>();
//		result=test.GetQueries();
//		for (int i=0;i<result.size();i++){
//			if(result.get(i).GetQueryContent().contains("  ")){
//				result.get(i).GetQueryContent().replaceAll("  ", " ");
//			}
//			System.out.println(result.get(i).GetTopicId());
//			System.out.println(result.get(i).GetQueryContent());
//		}
//		
//		
//	}
}
