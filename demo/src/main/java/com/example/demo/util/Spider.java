package com.example.demo.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class Spider {
	
	public static void main(String[] args) throws Exception {
		singlePageAnalysisAndInsert("http://mp.weixin.qq.com/s?src=11&timestamp=1522307538&ver=783&signature=YX*PemHKIRUrcq*ElEdxH120baE20GCHBBkZl1v1sjLgPPlICXrkRHfz7OS2PjOmx26EMhZ1ftMgKSlaGjiFh8BCHr4yQE8niKE7Ea3u6DeImx-eUctlxCWuQSSeZw9a&new=1");
	}
	
	public static void singlePageAnalysisAndInsert(String strUrl) throws Exception {
		// 初始化数据库
		Class.forName("com.mysql.jdbc.Driver");
		String jdbcUrl = "jdbc:mysql://localhost:3306/springboot";
		String user = "root";
		String password = "123456";
		Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
		String sql = "";
		PreparedStatement pst = conn.prepareStatement(sql);
		List<String> list = new ArrayList<>();
		
		// 从url获取输入流
		URL url = new URL(strUrl);
		BufferedReader bufr = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		
		// 获取目标行html代码 targetLine
		String targetLine = "";
		String line = "";
		String title = null;
		String postDate = null;
		boolean flag = false;
		while ((line = bufr.readLine()) != null) {
			if (line.indexOf("id=\"activity-name\"") != -1) {
				line = bufr.readLine().trim();
				if (line.indexOf("</h2>") != -1) {
					title = line.substring(0, line.length() - 5).trim();
					list.add(title);
				}
			}
			
			if (line.indexOf("id=\"post-date\"") != -1) {
				line = line.substring(line.indexOf(">") + 1).trim();
				postDate = line.substring(0, line.length() - 5).trim();
				list.add(postDate);
			}
			
			if (line.indexOf("id=\"js_content\"") != -1) {
				flag = true;
				line = bufr.readLine();
			}
			
			if (flag && !line.trim().equals("")) {
				targetLine = line;
				break;
			}
		}
		
		// 解析targetLine
		for (String str : targetLine.trim().split("</p>")) {
			str = str.substring(str.indexOf(">") + 1).trim();
			if ("".equals(str)) {
				continue;
			}
			
			if (str.indexOf("</span>") != -1) {
				StringBuilder sb = new StringBuilder();
				for (String spanStr : str.split("</span>")) {
					spanStr = spanStr.substring(spanStr.indexOf(">") + 1).trim();
					if ("".equals(spanStr)) {
						continue;
					}
					if (containChinese(spanStr)) {
						sb.append(killTag(spanStr));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
					}
				}
				if (!"".equals(sb.toString())) {
					list.add(sb.toString());
				}
			}
			
			if (str.indexOf("<img") != -1) {
				str = str.substring(str.indexOf("<img"));
				String picUrl = str.split("data-src=\"")[1].split("\"")[0];
				list.add(picUrl);
			}
		}
		
		for (String a : list) {
			System.out.println(a);
		}
	}
	
	private static String killTag(String str) {
		if (str.indexOf(">") == -1) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (String s : str.split(">")) {
			int temp = s.indexOf("<");
			if (temp != -1) {
				sb.append(s.substring(0, temp));
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}
	
	private static boolean containChinese(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		for (char c : str.toCharArray()) {
			if (c >= 0x4E00 &&  c <= 0x9FA5) {
				return true;
			}
		}
		return false;
	}
}
