package DocPrecessing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLreader {
	
	private static DocumentBuilderFactory builderFactory;
	Document document=null;
	NodeList questions;
	int qlength;
	int count;
	Map<String, Long> URIList=new HashMap<String, Long>();

	public XMLreader(String filePath){
		builderFactory=DocumentBuilderFactory.newInstance();;
	      try { 
	         //DOM parser instance 
	         DocumentBuilder builder = builderFactory.newDocumentBuilder(); 
	         //parse an XML file into a DOM tree 
	         document = builder.parse(new File(filePath)); 
	         document.normalize();
	      } catch (ParserConfigurationException e) { 
	         e.printStackTrace();  
	      } catch (SAXException e) { 
	         e.printStackTrace(); 
	      } catch (IOException e) { 
	         e.printStackTrace(); 
	      } 
			
		//get children elements
	    questions=document.getElementsByTagName("document");
	    qlength=questions.getLength();
	}
	public Map<String,String> nextQuestion(){
		/********
		 * the data structure return is
		 * hashmap:key      	value
		 * 		   uri			432470
		 * 		   question		subject+content+categories
		 * 		   best			best answer
		 */
		if(count<qlength){
			Map<String,String> q=new HashMap<String,String>();
			Node question=questions.item(count);
			NodeList nodes=question.getChildNodes();
			StringBuilder title = new StringBuilder();;
			for(int j=0;j<nodes.getLength();j++){
				Node node=nodes.item(j);
				if(node.getNodeType()==Node.ELEMENT_NODE){
					 String name=node.getNodeName();
		              String best="";
		              if(name=="uri"){
		            	   String s=node.getTextContent();
		            	   q.put("uri", s);
		            	   //write into files;
		              }
		              else if(name=="subject"||name=="content"){
		            	  title.append(node.getTextContent());
		              }
		              else if(name=="cat"||name=="maincat"||name=="subcat"){
		            	  title.append(node.getTextContent()+" ");
		              }
		              else if(name=="bestanswer"){
		            	  best=node.getTextContent();
		            	  q.put("bestanswer", best);
		              }
				}
			}
			q.put("question", title.toString());
			//output the files contain uri and best answer, one uri per file.
			try{
				String URIfile=q.get("uri")+"\n"+q.get("bestanswer");
				URIfile=removeHTML(URIfile);
				File outputdir=new File("output");
				if(!outputdir.exists()){
					outputdir.mkdirs();
				}
				File file=new File(outputdir.getAbsolutePath()+"/"+String.valueOf(count)+".smq");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fOut = null;
				fOut = new FileOutputStream(file);
				fOut.write(URIfile.getBytes());
				fOut.flush();
				fOut.close();
				URIList.put(q.get("uri"), Long.valueOf(count));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			count++;
			return q;
		}
		return null;
	}
	
	public void URIListOut(){
		try{
			File outputdir=new File("output");
			if(!outputdir.exists()){
				outputdir.mkdirs();
			}
			File file=new File(outputdir.getAbsolutePath()+"/"+"URIList.smq");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(file);
			Iterator<Map.Entry<String, Long>> entries = URIList.entrySet().iterator();
			while(entries.hasNext()){
				Map.Entry<String, Long> entry = entries.next();
				String LContent=entry.getKey()+","+entry.getValue()+".smq"+"\n";
				fOut.write(LContent.getBytes());
				fOut.flush();
			}
			
			fOut.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static String removeHTML(String inputString)
    {
    	if (inputString == null)
    		return null;
    	String htmlStr = inputString; // 含html标签的字符串
    	String textStr = "";
    	java.util.regex.Pattern p_script;
    	java.util.regex.Matcher m_script;
    	java.util.regex.Pattern p_style;
    	java.util.regex.Matcher m_style;
    	java.util.regex.Pattern p_html;
    	java.util.regex.Matcher m_html;
    	java.util.regex.Pattern p_special;
    	java.util.regex.Matcher m_special;
    	try {
    		//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
    		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
    		//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
    		String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
    		// 定义HTML标签的正则表达式
    		String regEx_html = "<[^>]+>";
    		// 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		String regEx_special = "\\&[a-zA-Z]{1,10};";

    		p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
    		m_script = p_script.matcher(htmlStr);
    		htmlStr = m_script.replaceAll(""); // 过滤script标签
    		p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
    		m_style = p_style.matcher(htmlStr);
    		htmlStr = m_style.replaceAll(""); // 过滤style标签
    		p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
    		m_html = p_html.matcher(htmlStr);
    		htmlStr = m_html.replaceAll(""); // 过滤html标签
    		p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
    		m_special = p_special.matcher(htmlStr);
    		htmlStr = m_special.replaceAll(""); // 过滤特殊标签
    		textStr = htmlStr;
    		} catch (Exception e) {
    		e.printStackTrace();
    		}
    	return textStr;// 返回文本字符串
    }
	
}
