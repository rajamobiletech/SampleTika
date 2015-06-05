package com.sampletika.model;

import org.json.simple.JSONObject;

public class Highlight{
 int id;
 int startOffset;
 int endOffset;
 int PageId;
 String selectedText;

	public Highlight(int id, int startOffset, int endOffset, int PageId, String selectedText){
	        this.id = id;
	        this.startOffset = startOffset;
	        this.endOffset = endOffset;
	        this.PageId = PageId;
	        this.selectedText = selectedText;
	}

	 public String toJSONString(){
		    JSONObject obj = new JSONObject();
		    obj.put("id", id);
		    obj.put("startOffset", startOffset);
		    obj.put("endOffset", endOffset);
		    obj.put("PageId",PageId);
		    obj.put("selectedText", selectedText);
		    return obj.toString();
	 }
	 public String getSelectedText() {
	       return selectedText;
	 }
	 public int getStartOffset() {
	       return startOffset;
	 }
	 public int getEndOffset() {
	       return endOffset;
	 }
	 public int getPageId() {
	       return PageId;
	 }
	 public int getId() {
	       return id;
	 }
}
