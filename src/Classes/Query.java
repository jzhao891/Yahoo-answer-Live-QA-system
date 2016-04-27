package Classes;

import java.util.ArrayList;

/**
 *  INFSCI 2140: Information Storage and Retrieval Spring 2016
 */
public class Query {
	//you can modify this class

	private ArrayList<String> queryContent;	
	//private String queryTime;
	//private String queryno;
	private int termSum;
	
	public Query(){
		queryContent=null;
		termSum=0;
	}
	public ArrayList<String> GetQueryContent() {
		return queryContent;
	}
	public int GettermSum() {
		return termSum;
	}
	
	public void SetQueryContent(String content){
		ArrayList<String> c=new ArrayList<String>();
		String[] s=content.split(" ");
		for(String term : s){
			c.add(term);
		}
		queryContent=c;
	}	
	public void SetQueryContent(ArrayList<String> content){
		queryContent=content;
	}	
	public void SettermSum(int count){
		termSum=count;
	}
	
}
