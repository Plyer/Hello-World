package com.example.demo.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.Article;

public class Spider {
	
	public static void main(String[] args) throws Exception {
		
		List<String> list = getArticleUrlList("http://weixin.sogou.com/pcindex/pc/pc_7/pc_7.html");
		List<Article> arcList = new ArrayList<>();
		for (String l : list) {
			arcList.add(singlePageAnalysis(l));
		}
		insert(arcList);
	}
	
	public static void insert(List<Article> list) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		// 初始化数据库
		Class.forName("com.mysql.jdbc.Driver");
		String jdbcUrl = "jdbc:mysql://10.90.60.201:3306/car_only";
		String user = "hxwebuser";
		String password = "hx2car88212994";
		Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
		conn.setAutoCommit(false);
		String sql = "insert into car_article (create_time,modify_time,title,post_date,content,pic_url,flag) values (?,?,?,?,?,?,?)";
		PreparedStatement pst = conn.prepareStatement(sql);
		
		
		for (Article arc : list) {
			String now = sdf.format(new Date());
			System.out.println(now);
			pst.setString(1, now);
			pst.setString(2, now);
			pst.setString(3, arc.getTitle());
			pst.setString(4, sdf2.format(arc.getPostDate()));
			pst.setString(5, arc.getContent());
			pst.setString(6, arc.getPicUrl());
			pst.setInt(7, arc.getFlag());
			pst.addBatch();
		}
		pst.executeBatch();
		conn.commit();
		pst.close();
		conn.close();
	}
	
	public static List<String> getArticleUrlList(String rootUrl) throws Exception {
		URL url = new URL(rootUrl);
		BufferedReader bufr = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		
		List<String> urlList = new ArrayList<>();
		String line = "";
		int count = 0;
		while ((line = bufr.readLine()) != null) {
			if (line.indexOf("<h3><a uigs=\"pc_7_" + count + "_title\" href=\"") != -1) {
				String targetUrl = line.trim().split("href")[1].split("\"")[1];
				urlList.add(targetUrl);
				count++;
			}
		}
		return urlList;
	}
	
	public static Article singlePageAnalysis(String strUrl) throws Exception {
		List<String> list = new ArrayList<>();
		Article article = new Article();
		
		// 从url获取输入流
		URL url = new URL(strUrl);
		BufferedReader bufr = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
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
					article.setTitle(title);
				}
			}
			
			if (line.indexOf("id=\"post-date\"") != -1) {
				line = line.substring(line.indexOf(">") + 1).trim();
				postDate = line.substring(0, line.length() - 5).trim();
				article.setPostDate(sdf.parse(postDate));
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
		bufr.close();
		
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
				picUrl = DownloadImg.run(picUrl);
				list.add(picUrl);
			}
		}
		
		StringBuilder content = new StringBuilder();
		StringBuilder picUrl = new StringBuilder();
		String conStr = null;
		String picStr = null;
		for (int i = 0, j = 0; i < list.size(); i++) {
			if (list.get(i).startsWith("https://")) {
				String key = "url" + j;
				picUrl.append(key + "|" + list.get(i) + "@@");
				list.set(i, key);
				j++;
			}
			content.append(list.get(i) + "|");
		}
		if (content.toString().endsWith("|")) {
			conStr = content.substring(0, content.length() - 7);
		}
		if (picUrl.toString().endsWith("@@")) {
			picStr = picUrl.substring(0, picUrl.length() - 1);
		}
		article.setContent(conStr);
		article.setFlag(0);
		article.setPicUrl(picStr);
		return article;
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
