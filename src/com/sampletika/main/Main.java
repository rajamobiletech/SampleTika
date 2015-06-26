package com.sampletika.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;



import com.google.common.base.CharMatcher;
import com.sampletika.model.Highlight;

import org.json.simple.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	public static String firstPageContent;
	public static String secondPageContent;
	public static String moduleCheckContent;
	public static JSONArray highlights;
	public static JSONArray newHighlights;
	public static String bookLang = "en1";
	
	public static void main(String args[]){
		getDbHighlights();
		if(bookLang.equals("en")) {
			getPageContentForEng();
		}else {
			getPageContentForCH();
		}
		getAllHightlight(bookLang);
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

	
	public static void getPageContentForEng() {
		BodyContentHandler handler1 = new BodyContentHandler();
		BodyContentHandler handler2 = new BodyContentHandler();
		BodyContentHandler handler3 = new BodyContentHandler();
		
		File file1 = new File("/Users/tilakk/projects/testPages/154c.html");
		File file2 = new File("/Users/tilakk/projects/testPages/154cnew.html");
		Document pageDoc1 = null;
		Document pageDoc2 = null;
		Document pageDoc3 = null;
		// Removing Glossary pop up elements from content of page v1.0 content
		try {
			pageDoc1 = Jsoup.parse(file1, null);
			Elements els = pageDoc1.getElementsByClass("WysiwygInLineTerm");
			for (Element el : els) {
			    Element j = el.prependElement("div");
			    Elements els1 = el.getElementsByClass("Title_GlossaryItem");
			    for(int i = 0; i < els1.text().length(); i++) {
			    	j.appendText("@");
			    }
			}			
			Elements ele1 = pageDoc1.getElementsByAttributeValue("style", "display: none;");
			ele1.remove();
			els = pageDoc1.getElementsByClass("Col2InnerWrapper");
			for (Element el : els) {
				 Element j = el.appendElement("div");
						 j.appendText("::");
//						 j = el.prependElement("div");
//						 j.appendText("--");
			}
//			System.out.println("page1===>"+pageDoc1);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Removing Glossary pop up elements from content page v2.0 content
		try {
			pageDoc2 = Jsoup.parse(file2, null);
			Elements els = pageDoc2.getElementsByClass("WysiwygInLineTerm");
			for (Element el : els) {
			    Element j = el.prependElement("div");
			    Elements els1 = el.getElementsByClass("Title_GlossaryItem");
			    for(int i = 0; i < els1.text().length(); i++) {
			    	j.appendText("@");
			    }
			}
			Elements ele1 = pageDoc2.getElementsByAttributeValue("style", "display: none;");
			ele1.remove();
			pageDoc3 = pageDoc2.clone();
			
			els = pageDoc2.getElementsByClass("Col2");
			for (Element el : els) {
				 Element j = el.appendElement("div");
						 j.appendText("::");
//						 j = el.prependElement("div");
//						 j.appendText("--");
			}
		} catch (IOException e) {	
			e.printStackTrace();
		}
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		try {
			fis1 = new FileInputStream(file1);
			fis2 = new FileInputStream(file2);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Elements els = pageDoc3.getElementsByClass("Col1");
		els.remove();
		els = pageDoc3.getElementsByClass("CorrResponse");
		els.remove();
		els = pageDoc3.getElementsByClass("ThisIsCorrResponse");
		els.remove();
		els = pageDoc3.getElementsByClass("IncorrResponse");
		els.remove();
		
		org.w3c.dom.Document w3cDoc1= DOMBuilder.jsoup2DOM(pageDoc1);
		org.w3c.dom.Document w3cDoc2= DOMBuilder.jsoup2DOM(pageDoc2);
		org.w3c.dom.Document w3cDoc3= DOMBuilder.jsoup2DOM(pageDoc3);

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        Source xmlSource1 = new DOMSource(w3cDoc1);
        Source xmlSource2 = new DOMSource(w3cDoc2);
        Source xmlSource3 = new DOMSource(w3cDoc3);
        Result outputTarget1 = new StreamResult(outputStream1);
        Result outputTarget2 = new StreamResult(outputStream2);
        Result outputTarget3 = new StreamResult(outputStream3);
        try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource1, outputTarget1);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource2, outputTarget2);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource3, outputTarget3);
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		}
        InputStream is1 = new ByteArrayInputStream(outputStream1.toByteArray());
        InputStream is2 = new ByteArrayInputStream(outputStream2.toByteArray());
        InputStream is3 = new ByteArrayInputStream(outputStream3.toByteArray());
        
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		AutoDetectParser parser1 = new AutoDetectParser();
		Metadata metadata1 = new Metadata();
	    try {
	        parser.parse(is1, handler1, metadata);
	        firstPageContent = handler1.toString().replaceAll("\t", "");
	        firstPageContent = firstPageContent.replaceAll("\n", "");
	        System.out.println("File1:>>>>>>>"+firstPageContent);System.out.println();

	        parser1.parse(is2, handler2, metadata1);
	        secondPageContent = handler2.toString().replaceAll("\t", "").replaceAll("\n", "");
	        System.out.println("File2:>>>>>>>"+secondPageContent);System.out.println();
	        
	        parser1.parse(is3, handler3, metadata1);
	        moduleCheckContent = handler3.toString().replaceAll("\t", "").replaceAll("\n", "");
	        //System.out.println("File3:>>>>>>>"+moduleCheckContent);System.out.println();
	    } catch (IOException | SAXException | TikaException e) {
		e.printStackTrace();
		} finally {
		}
	}
	
	
	public static void getPageContentForCH() {
		File file1 = new File("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp1.html");
		File file2 = new File("/Users/tilakk/Projects/TestCases/samplePhantomjs/temp2.html");
//		BodyContentHandler handler1 = new BodyContentHandler();
//		BodyContentHandler handler2 = new BodyContentHandler();
		int exitStatus1 = 0;
		int exitStatus2 = 0;
		Process process1 = null;
		Process process2 = null;
		try {
			//process1 = Runtime.getRuntime().exec("/usr/local/bin/phantomjs /Users/tilakk/Projects/newgit/SampleTika/src/com/sampletika/main/index.js /Users/tilakk/projects/testPages/17.xhtml /Users/tilakk/projects/testPages/17new.xhtml");
			process1 = Runtime.getRuntime().exec("/usr/local/bin/phantomjs /Users/tilakk/Projects/newgit/SampleTika/src/com/sampletika/main/index.js /Users/tilakk/projects/testPages/154c.html /Users/tilakk/Projects/TestCases/samplePhantomjs/temp1.html");
			exitStatus1 = process1.waitFor();
			process2 = Runtime.getRuntime().exec("/usr/local/bin/phantomjs /Users/tilakk/Projects/newgit/SampleTika/src/com/sampletika/main/index.js /Users/tilakk/projects/testPages/154cnew.html /Users/tilakk/Projects/TestCases/samplePhantomjs/temp2.html");
			exitStatus2 = process2.waitFor();
		} catch (IOException | InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(exitStatus1==0) {
			try {
				firstPageContent = new Scanner(file1).useDelimiter("\\Z").next() ;
				System.out.println("firstPageContent===" + firstPageContent);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(exitStatus2==0) {
			try {
				secondPageContent = new Scanner(file2).useDelimiter("\\Z").next();
				System.out.println("secondPageContent==" + secondPageContent);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void getAllHightlight(String bookLangage) {
		for (Iterator iterator = highlights.iterator(); iterator.hasNext();) {
			Highlight highlight = (Highlight) iterator.next();
			if(!validateSameOffset(highlight, bookLangage)) {
				if(!validateDiffOffSet(highlight, bookLangage)) {
					if(!validateOneOccur(highlight)) {
						System.out.println("validateOneOccur Failure !!!! " +highlight.getSelectedText() );
					}
				}
			}
		}
	}


	public static Boolean validateSameOffset(Highlight highlight, String bookLangage) {
		String oldHighlight = highlight.getSelectedText();
		String highlightText = secondPageContent.substring(highlight.getStartOffset(), highlight.getEndOffset()).replaceAll("\n", "").replaceAll("\t", "");
		

		oldHighlight = oldHighlight.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
		
		if(bookLangage.equals("en")){
			highlightText = highlightText.replaceAll("@{2,}", "").replaceAll("\\s{1}", ".");
	        if(!CharMatcher.ASCII.matchesAllOf(highlightText)) {
	        	highlightText = highlightText.replaceAll("\\P{Print}", ".");
	        }
	        oldHighlight = highlight.getSelectedText().replaceAll("\\s{1}", ".").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
	        if(!CharMatcher.ASCII.matchesAllOf(oldHighlight)) {
	            oldHighlight = oldHighlight.replaceAll("\\P{Print}", ".");
	        }
		}else {
			oldHighlight = oldHighlight.replaceAll(" ", "#").replaceAll("\n", "");
			highlightText = highlightText.replaceAll(" ", "#").replaceAll(" ", "#");
		}

		if(highlightText.equals(oldHighlight)) {
			newHighlights.add(new Highlight(highlight.getId(), highlight.getStartOffset(), highlight.getEndOffset(), highlight.getPageId(),highlight.getSelectedText()));
			System.out.println("====>>>> validateSameOffset Success !!! " + secondPageContent.substring(highlight.getStartOffset(),highlight.getEndOffset()).replaceAll("@{2,}", ""));
		} else {
			System.out.println("m===>>> " + highlightText);
			System.out.println("O===>>> " + oldHighlight);
			return false;
		}
		return true;
	}

	public static Boolean validateDiffOffSet(Highlight highlight, String bookLangage) {
		int count = 0;
		int startRangeOffsetValue = (highlight.getStartOffset()-5 > 0) ? highlight.getStartOffset()-5 : highlight.getStartOffset();
		int endOffRangeSetValue = (highlight.getEndOffset()+5) < firstPageContent.length() ? highlight.getEndOffset()+5 : highlight.getEndOffset();
		String originalHighlight = firstPageContent.substring(startRangeOffsetValue , endOffRangeSetValue);
		int flag=0;
		originalHighlight = Pattern.quote(originalHighlight);
		//originalHighlight = originalHighlight.replaceAll(regex, replacement)
		//originalHighlight = originalHighlight.replaceAll("[({#/})]", ".");
		Pattern p = Pattern.compile(originalHighlight);
		Matcher matcher = p.matcher(secondPageContent);	
		while (matcher.find() && count < 2) {
			count++;
		}
		matcher = p.matcher(secondPageContent);
		if(count ==1 && matcher.find()){
			int startRangeOffsetValue2;
 
			 if (startRangeOffsetValue==0)
				startRangeOffsetValue2 = matcher.start();
			else
				startRangeOffsetValue2 = matcher.start()-5 > 0? matcher.start()+5 : matcher.start();
			int endOffRangeSetValue2 = matcher.end()+5 < secondPageContent.length() ? matcher.end()-5 : matcher.end();
			newHighlights.add(new Highlight(highlight.getId(), startRangeOffsetValue2, endOffRangeSetValue2, highlight.getPageId(),highlight.getSelectedText()));
			System.out.println();
			System.out.println("====>>>> validateDiffOffSet Success !!! " +secondPageContent.substring(startRangeOffsetValue2, endOffRangeSetValue2) +"  OFFSET==>>" + startRangeOffsetValue2+ " // "+ endOffRangeSetValue2  );
		    flag=1;
		}
		if(flag == 0) {
			return false;
		}
		return true;
	}
	
	public static Boolean validateOneOccur(Highlight highlight) {
			int count = 0;
			Pattern p = Pattern.compile(highlight.getSelectedText().replaceAll("[({})]", "."));
			Matcher matcher = p.matcher(secondPageContent);
			while(matcher.find() && count < 2){
				count++;
			}
			matcher = p.matcher(secondPageContent);
			if(count == 1 && matcher.find()) {
				newHighlights.add(new Highlight(highlight.getId(), matcher.start(), matcher.end(), highlight.getPageId(),highlight.getSelectedText()));
				System.out.println("====>>>> validateOneOccur Success !!! " + secondPageContent.substring(matcher.start(), matcher.end() ));
				return true;
			} 
		return false;
	}
}

