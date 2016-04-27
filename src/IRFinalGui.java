import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.lucene.util.packed.PackedLongValues.Iterator;

import Search.ExtractQuery;
import Search.QueryRetrievalModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

import Classes.Path;
import Classes.Query;
import Classes.QueryHis;
import IndexingLucene.MyIndexWriter;
import PseudoRFSearch.PseudoRFRetrievalModel;
import IndexingLucene.MyIndexReader;
import Classes.Document;
import java.util.List;
import java.util.Set;
import java.util.Iterator.*;

public class IRFinalGui extends JFrame {

    private static IRFinalGui j;
    private JTextField entry;
    private JLabel jLabel1;
    private JTextPane pane;
    private JButton button;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

    final static Color  HILIT_COLOR = Color.LIGHT_GRAY;

    final Color entryBg;
    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;


    public IRFinalGui() throws FileNotFoundException {
        try {
			initComponents();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);

        entryBg = entry.getBackground();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * @throws IOException 
     * @throws NumberFormatException 
     */

    private void initComponents() throws NumberFormatException, IOException {
        entry = new JTextField();
        jLabel1 = new JLabel("Yahoo Label");
        button = new JButton("GO");
        pane = new JTextPane();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yahoo Client");


        GridBagLayout layout = new GridBagLayout();
        getContentPane().setSize(600, 100);
        getContentPane().setLayout(layout);

        
        
        GridBagConstraints c = new GridBagConstraints();

        jLabel1.setText("Enter your question:");
        jLabel1.setForeground(Color.decode("#7B0099"));
        Font myFont = new Font("Serif", Font.BOLD, 18);
        jLabel1.setFont(myFont);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(jLabel1, c);

        entry.setPreferredSize(new Dimension(500, 50));
        entry.setFont(myFont);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        getContentPane().add(entry, c);
/////////////////////////////////////////////////        
        //QueryHis qh=new QueryHis();
        BufferedReader reader=new BufferedReader(new FileReader(Path.TopicDir)); // Entrance of the query input from the GUI.
        String line=reader.readLine();
        if(line!=null){
        	for(int i=0;i<4;i++){
   			 if(line!=null){
   				 if(i==0){
   						QueryHis.termSum=Integer.valueOf(line);
   						line=reader.readLine();
   					}
   					else if(i==1){
   						QueryHis.queryCont=Integer.valueOf(line);
   						line=reader.readLine();
   					}
   					else if(i==2){
   						QueryHis.averageLen=Integer.valueOf(line);
   						line=reader.readLine();
   					}
   					else{
   						
   						//boolean start=false;
   						Query q=null;
   						int qid=0;
   						while(line!=null){
   							if(line.contains("<queryno>")){
   								//start=true;
   								q=new Query();
   								qid=Integer.valueOf(line.substring(9));
   							}
   							else if(line.contains("<len>")){
   								q.SettermSum(Integer.valueOf(line.substring(5)));
   							}
   							else if(line.contains("<content>")){
   								q.SetQueryContent(line.substring(9));
   								QueryHis.collection.put(qid,q);
   							}
   							line=reader.readLine();
   						}
   					}
   			 	}
   				
        	}
        }
        reader.close();
		 
/////////////////////////////////
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //code for yahoo stuff
                String textInput = entry.getText();
                System.out.println(entry.getText());
                try {/*
                    URL url = new URL("http://ptest.ysm.yahoo.com/SampleYPASearch/?");
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                    writer.write("p=" + textInput);
                    writer.flush();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));*/
                    
                	//StringBuilder result = new StringBuilder();
                    //result.append(entry.getText());
                    
                    /*while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        result.append(line);
                    }
                    writer.close();
                    reader.close();*/
                    //pane.setText(entry.getText());
                    //QuerySave(entry.getText());
                	MyIndexReader ixreader=new MyIndexReader("content");
                	PseudoRFRetrievalModel model=new PseudoRFRetrievalModel(ixreader);
                	if(textInput==null){
                		System.out.println("Please input a question!");
                	}
                	else{
                		ExtractQuery eq=new ExtractQuery();
                        Query query=eq.GetQueries(textInput);
                        //query.GetQueryContent().split(" ")
                        if(query.GetQueryContent().size()!=0){
                        	List<Document> results=model.RetrieveQuery(query, 3);
                            if (results != null) {
            					int rank = 1;
            					for (Document result : results) {
            						System.out.println(" Q0 " + result.docno() + " " + rank + " " + result.score() + " MYRUN");
            						rank++;
            					}
            				}
                            else{
                            	System.out.println("No related question asked in system! Please waiting for manual answer!");
                            }
                        }
                        else{
                        	System.out.println("No related question asked in system! Please waiting for manual answer!");
                        }
                        ixreader.close();
                        //after retrieval, add new query into the postinglist of queryhistory and queryhis list
                        QueryHis.termSum+=query.GettermSum();
    					QueryHis.queryCont++;
    					QueryHis.averageLen=(QueryHis.averageLen+query.GettermSum())/QueryHis.queryCont;
    					QueryHis.collection.put(QueryHis.queryCont, query);
    					MyIndexWriter output=new MyIndexWriter();
    					ArrayList<String> q=query.GetQueryContent();
    					java.util.Iterator<String> it=q.iterator();
    					StringBuilder queryContent=new StringBuilder();
    					while(it.hasNext()){
    						queryContent.append(it.next()+" ");
    					}
    					output.indexHq(String.valueOf(QueryHis.queryCont), queryContent.toString().trim());
    					output.close();
                        //
                        //////..............
                	}
                    
                    
//                    pane.setText(result.toString());
                    /*j.setSize(600,600);*/
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        button.setForeground(Color.decode("#7B0099"));
        button.setFont(myFont);
        getContentPane().add(button, c);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 600;
        c.gridwidth = 600;

        pane.setSize(600,600);
        pane.setContentType("text/html");
        getContentPane().add(pane, c);
    }
    
    public void click(String uri){
    	
    }


    public static void main(String args[]) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                try {
					j = new IRFinalGui();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                j.setSize(600,200);
                j.setResizable(true);
                j.setVisible(true);
            }
        });
        ///////////
        try {
			qhClean();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //////////
    }
    //////////////////////////////
    public static void qhClean() throws IOException{
    	FileWriter fw=new FileWriter(Path.TopicDir);
    	fw.append(String.valueOf(QueryHis.termSum));
    	fw.append("\n");
    	fw.append(String.valueOf(QueryHis.queryCont));
    	fw.append("\n");
    	fw.append(String.valueOf(QueryHis.averageLen));
    	fw.append("\n");
    	Set<Integer> keys=QueryHis.collection.keySet();
    	for(int k : keys){
    		fw.append("<queryno>"+String.valueOf(k));
    		fw.append("\n");
    		fw.append("<len>"+String.valueOf(QueryHis.collection.get(k).GettermSum()));
    		fw.append("\n");
    		fw.append("<content>"+QueryHis.collection.get(k).GetQueryContent());
    		fw.append("\n");
    	}
    	QueryHis.averageLen=0;
    	QueryHis.collection=new HashMap<Integer,Query>();
    	QueryHis.queryCont=0;
    	QueryHis.termSum=0;
    	fw.close();
    }
    //////////////////////////////
}
