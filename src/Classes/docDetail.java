package Classes;

import java.util.HashMap;

public class docDetail {
	private HashMap<String, Integer> tdFreq;
	// save <token, c(token, doc)>
	private int docSize;
	// save |doc|
	
	public void setDocSize(Integer size){
		docSize=size;
	}
	
	public Integer getDocSize(){
		return docSize;
	}
	
	public void setTdFreq(HashMap<String, Integer> tFreq){
		tdFreq=tFreq;
	}
	
	public HashMap<String, Integer> getTdFreq(){
		return tdFreq;
	}

}
