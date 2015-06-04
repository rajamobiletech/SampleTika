package com.smapletika.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Main {
	public static void main(String args[]){
		BodyContentHandler handler1 = new BodyContentHandler();
		BodyContentHandler handler2 = new BodyContentHandler();
		int startOffset = 130;
		int endOffset = 137;
		String String1 = "";
		String String2 = "";
		
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
	        String1 = handler1.toString().replaceAll("\t", "");
	        String1 = String1.replaceAll("\n", "");
	        System.out.println("File1:>>>>>>>"+String1);
	        //String1 = handler1.toString();
	        parser1.parse(f2, handler2, metadata1);
	        System.out.println();
	        //System.out.println("File2:>>>>>>>"+handler2.toString());
	        String2 = handler2.toString();
	    } catch (IOException | SAXException | TikaException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} finally {
		}
	    String newStr1 = String1.substring(startOffset, endOffset);
	    System.out.println("Highlighted String="+newStr1);
	}
}

