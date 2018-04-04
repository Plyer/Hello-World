package com.example.demo.test;

import java.util.ArrayList;
import java.util.Arrays;
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
     
	public static String[] get(String tagName, String source) {
		if (tagName == null || "".equals(tagName)) {
			return null;
		}
		
		String regex = "<" + tagName + " .*?" + ">|</" + tagName + ">|<" + tagName + ">";
		String[] arr = source.split(regex);
		return Arrays.copyOfRange(arr, 1, arr.length - 1);
	}
	
	public static void main(String[] args) {
		String source = "<p hahahahaha heheh 12313 >你好<p>Java<span clo>span</span></p></p><p hahahahaha heheh 12313 >世界</p>";
		for (String i : get("p", source)) {
		   /* if ("".equals(i)) {
		        continue;
		    }*/
		    System.out.println(i);
		}
	}
}
