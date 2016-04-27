package Classes;

import java.util.HashMap;

public class QueryHis {
	public static int termSum=0;
	public static int queryCont=0;
	public static int averageLen=0;
	public static HashMap<Integer,Query> collection=new HashMap<Integer,Query>();
	
	public void iniQueryHis(){
		termSum=0;
		queryCont=0;
		averageLen=0;
		collection=new HashMap<Integer,Query>();
	}
	public int getTermSum(){
		return termSum;
	}
	public int getQueryCont(){
		return queryCont;
	}
	public int getAverageLen(){
		return averageLen;
	}
	public String getCollection(){
		return collection.toString();
	}
	
	public void addQuery(int queryNo, Query query){
		collection.put(queryNo, query);
	}
	public void clean(){
		iniQueryHis();
	}
	

}
