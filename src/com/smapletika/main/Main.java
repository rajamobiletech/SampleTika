package com.smapletika.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.sampletika.model.Highlight;
import org.json.simple.JSONArray;

public class Main {
	public static String firstPageContent;
	public static String secondPageContent;
	public static JSONArray highlights;
	
	public static void main(String args[]){
		getDbHighlights();
		getPageContent();
		getAllHightlight();
	}
	
	public static void getDbHighlights() {
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String url = "jdbc:mysql://localhost:3306/reader?user=root";
        
        try {
        	highlights = new JSONArray(); 
			con = DriverManager.getConnection(url);
			st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Highlights where deleted=0");
			while(rs.next()){
				  highlights.add(new Highlight(rs.getInt("id"), rs.getInt("startOffset"), rs.getInt("endOffset"), rs.getInt("PageId"),rs.getString("SelectedText")));
			}
			  rs.close();
			  st.close();
			  con.close();
		} catch (SQLException e2) {
				e2.printStackTrace();
		}
	}
	
	public static void getPageContent() {
		BodyContentHandler handler1 = new BodyContentHandler();
		BodyContentHandler handler2 = new BodyContentHandler();
		
		File file1 = new File("/Users/rajad/projects/testPages/1.xhtml");
		File file2 = new File("/Users/rajad/projects/testPages/2.xhtml");
		FileInputStream fis = null;
		FileInputStream f2 = null;
		try {
			fis = new FileInputStream(file1);
			f2 = new FileInputStream(file2);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		AutoDetectParser parser1 = new AutoDetectParser();
		Metadata metadata1 = new Metadata();
	    try {
	        parser.parse(fis, handler1, metadata);
	        firstPageContent = handler1.toString().replaceAll("\t", "");
	        firstPageContent = firstPageContent.replaceAll("\n", "");
	        System.out.println("File1:>>>>>>>"+firstPageContent);
	        //String1 = handler1.toString();
	        parser1.parse(f2, handler2, metadata1);
	        secondPageContent = handler2.toString();
	        System.out.println("File2:>>>>>>>"+secondPageContent);
	        
	    } catch (IOException | SAXException | TikaException e) {
		e.printStackTrace();
		} finally {
		}
	}

	public static void getAllHightlight() {
		for (Iterator iterator = highlights.iterator(); iterator.hasNext();) {
			Highlight highlight = (Highlight) iterator.next();
			//System.out.println("Highlights===>>>"+ highlight.getSelectedText());
			String newStr1 = firstPageContent.substring(highlight.getStartOffset(), highlight.getEndOffset());
		    System.out.println("Highlighted String="+newStr1);
		}
	}
}

