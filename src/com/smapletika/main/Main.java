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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.sampletika.model.Highlight;

import org.json.simple.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


public class Main {
	public static String firstPageContent;
	public static String secondPageContent;
	public static JSONArray highlights;
	public static JSONArray newHighlights;
	public static org.jsoup.nodes.Document doc1;
	public static org.jsoup.nodes.Document doc2;
	
	public static void main(String args[]){
		getHtmlContent();
		getDbHighlights();
		getPageContent();
		getAllHightlight();
	}
	
	public static void getHtmlContent() {
		File file1 = new File("/Users/tilakk/projects/testPages/3.xhtml");
		File file2 = new File("/Users/tilakk/projects/testPages/4.xhtml");
		//Document doc = JSoup.parse(file, null);
		try {
			doc1 = Jsoup.parse(file1, null);
			Elements ele1 = doc1.getElementsByClass("inlinetermTerm");
			ele1.remove();
			Elements ele2 = doc1.getElementsByClass("inlinedialog");
			ele2.remove();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			doc2 = Jsoup.parse(file2, null);
			Elements ele1 = doc1.getElementsByClass("inlinetermTerm");
			ele1.remove();
			Elements ele2 = doc1.getElementsByClass("inlinedialog");
			ele2.remove();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getDbHighlights() {
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String url = "jdbc:mysql://localhost:3306/reader?user=root";
        
        try {
        	highlights = new JSONArray();
        	newHighlights = new JSONArray();
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
		
		File file1 = new File("/Users/tilakk/projects/testPages/3.xhtml");
		File file2 = new File("/Users/tilakk/projects/testPages/4.xhtml");
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
	        parser1.parse(f2, handler2, metadata1);
	        secondPageContent = handler2.toString().replaceAll("\t", "").replaceAll("\n", "");
	        System.out.println("File2:>>>>>>>"+secondPageContent);System.out.println();
	    } catch (IOException | SAXException | TikaException e) {
		e.printStackTrace();
		} finally {
		}
	}

	public static void getAllHightlight() {
		for (Iterator iterator = highlights.iterator(); iterator.hasNext();) {
			Highlight highlight = (Highlight) iterator.next();
			if(!validateSameOffset(highlight)) {
				if(!validateDiffOffSet(highlight)) {
					if(!validateOneOccur(highlight)) {
						System.out.println("validateOneOccur Failure !!!! " +highlight.getSelectedText() );
					}
				}
			}
		}
	}

	public static Boolean validateSameOffset(Highlight highlight) {
		String highlightText = secondPageContent.substring(highlight.getStartOffset(), highlight.getEndOffset());
		if(highlightText.equals(highlight.getSelectedText())) {
			newHighlights.add(new Highlight(highlight.getId(), highlight.getStartOffset(), highlight.getEndOffset(), highlight.getPageId(),highlight.getSelectedText()));
			System.out.println("====>>>> validateSameOffset Success !!! " + highlight.getSelectedText());
		}else {
			return false;
		}
		return true;
	}

	public static Boolean validateDiffOffSet(Highlight highlight) {
		int count = 0;
		int startRangeOffsetValue = (highlight.getStartOffset()-5 > 0) ? highlight.getStartOffset()-5 : highlight.getStartOffset();
		int endOffRangeSetValue = (highlight.getEndOffset()+5) < firstPageContent.length() ? highlight.getEndOffset()+5 : highlight.getEndOffset();
		System.out.println("startRangeOffsetValue=== " + startRangeOffsetValue + "endOffRangeSetValue=== " + endOffRangeSetValue + " firstPageContent.length=== " + firstPageContent.length() );
		String originalHighlight = firstPageContent.substring(startRangeOffsetValue , endOffRangeSetValue);
		System.out.println("originalHighlight===>>>>" + originalHighlight);
		int flag=0;
		Pattern p = Pattern.compile(originalHighlight);
		Matcher matcher = p.matcher(secondPageContent);
		while (matcher.find() && count < 2) {
			count++;
		}
		matcher = p.matcher(secondPageContent);
		if(count ==1 && matcher.find()){
			newHighlights.add(new Highlight(highlight.getId(), (matcher.start()-5>0? matcher.start()+5 : matcher.start()), (matcher.end()+5 > secondPageContent.length() ? matcher.end() : matcher.end()-5), highlight.getPageId(),highlight.getSelectedText()));
			System.out.println("====>>>> validateDiffOffSet Success !!! " + highlight.getSelectedText());
		    flag=1;
		}
		if(flag == 0) {
			return false;
		}
		return true;
	}
	
	public static Boolean validateOneOccur(Highlight highlight) {
			int count = 0;
			Pattern p = Pattern.compile(highlight.getSelectedText());
			Matcher matcher = p.matcher(secondPageContent);
			while(matcher.find() && count < 2){
				count++;
			}
			matcher = p.matcher(secondPageContent);
			if(count == 1 && matcher.find()) {
				newHighlights.add(new Highlight(highlight.getId(), matcher.start(), matcher.end(), highlight.getPageId(),highlight.getSelectedText()));
				System.out.println("====>>>> validateOneOccur Success !!! " + highlight.getSelectedText());
				return true;
			} 
		return false;
	}
}

