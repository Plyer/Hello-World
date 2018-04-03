package com.example.demo.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
	public static List<String> getMatcher(String regex, String source) {  
         List<String> result = new ArrayList<>(); 
         Pattern pattern = Pattern.compile(regex);  
         Matcher matcher = pattern.matcher(source);  
         while (matcher.find()) {  
             result.add(matcher.group(1));
         }  
         return result;  
     }  
     
	public static List<String> get(String tagName) {
		if (tagName == null || "".equals(tagName)) {
			return null;
		}
		
		List<String> list = new ArrayList<>();
		String regex = "<" + tagName + ".*?" + ">";
		return list;
	}
	
	public static void main(String[] args) {
		String url = "http://172.12.1.123/test.txt/1.1.1.1";
		String regex = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
	 //        String regex = "(\\d{1,3}\\.){1,3}(\\d{1,3})";
	    for (String i : getMatcher(regex, url)) {
	    	System.out.println(i);
	    }
	}
}
