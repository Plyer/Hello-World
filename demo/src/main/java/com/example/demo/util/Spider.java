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

public class Spider implements Runnable {
	
	private static String[] filterWord = {"点击关注", "公众号", "公号", "每日推送", "<a ", "原创文章", "邮箱", "微信号"};
	private static String[] endWord = {"阅读原文", "阅读全文", "end", "END", "更多行业新闻", "推荐阅读", "点击下面图片", "请戳下面链接", "二维码", "版权", "侵权"};
	
    public static void main(String[] args) throws Exception {
    	new Spider().run();
    }
    
	@Override
	public void run() {
		try {
			List<String> list = getArticleUrlList("http://weixin.sogou.com/pcindex/pc/pc_7/pc_7.html");
			List<Article> arList = new ArrayList<>();
			for (String i : list) {
				arList.add(singlePageAnalysis(i));
			}
			insert(arList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insert(List<Article> list) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		// 初始化数据库
		Class.forName("com.mysql.jdbc.Driver");
		String jdbcUrl = "jdbc:mysql://10.90.60.201:3306/car_only";
		String user = "hxwebuser";
		String password = "hx2car88212994";
		Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
		conn.setAutoCommit(false);
		String sql = "insert into car_article (create_time,modify_time,title,post_date,content,pic_url,flag,origin) values (?,?,?,?,?,?,?,?)";
		PreparedStatement pst = conn.prepareStatement(sql);
		
		
		for (Article arc : list) {
			if (arc == null) {
				continue;
			}
			String now = sdf.format(new Date());
			pst.setString(1, now);
			pst.setString(2, now);
			pst.setString(3, arc.getTitle());
			pst.setString(4, sdf2.format(arc.getPostDate()));
			pst.setString(5, arc.getContent());
			pst.setString(6, arc.getPicUrl());
			pst.setInt(7, arc.getFlag());
			pst.setString(8, arc.getOrigin());
			pst.addBatch();
		}
		pst.executeBatch();
		conn.commit();
		pst.close();
		conn.close();
	}
	
	private List<String> getArticleUrlList(String rootUrl) throws Exception {
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
	
	private Article singlePageAnalysis(String strUrl) throws Exception {
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
			if (line.indexOf("id=\"post-date\"") != -1) {
				line = line.substring(line.indexOf(">") + 1).trim();
				postDate = line.substring(0, line.length() - 5).trim();
				if (!postDate.equals(sdf.format(new Date()))) {
					return null;
				}
				article.setPostDate(sdf.parse(postDate));
				continue;
			}
			
			if (line.indexOf("id=\"activity-name\"") != -1) {
				line = bufr.readLine().trim();
				if (line.indexOf("</h2>") != -1) {
					title = line.substring(0, line.length() - 5).trim();
					article.setTitle(title);
				}
				continue;
			}
			
			if (line.indexOf("id=\"post-user\"") != -1) {
				if (line.indexOf(">") != -1) {
					String origin = line.trim().split(">")[1].split("<")[0].trim();
					article.setOrigin(origin);
				}
				continue;
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
			
			int fi = filter(str);
			if (fi == 2) {
				break;
			} else if (fi == 1) {
				continue;
			}
			
			if (str.indexOf("<img") != -1) {
			    String temp = str;
			    temp = temp.substring(temp.indexOf("<img"));
                String picUrl = temp.split("data-src=\"")[1].split("\"")[0];
                list.add(picUrl);
            }
			
			String temp = str.replaceAll("<([^>]*)>", "").trim();
			if (containChinese(temp)) {
			    list.add(temp);
			}
		}
		
		StringBuilder content = new StringBuilder();
		StringBuilder picUrl = new StringBuilder();
		String conStr = null;
		String picStr = null;
		for (int i = 0, j = 0; i < list.size(); i++) {
			if (list.get(i).startsWith("http")) {
				// 如果第一条或最后一条内容是图片则不记录
				if (i == 0 || i == list.size() - 1) {
					continue;
				}
				String key = "url" + j;
				picUrl.append(key + "|" + list.get(i) + "@@");
				list.set(i, key);
				j++;
			}
			content.append(list.get(i) + "|");
		}
		if (content.toString().endsWith("|")) {
			conStr = content.substring(0, content.length() - 1);
		}
		if (picUrl.toString().endsWith("@@")) {
			picStr = picUrl.substring(0, picUrl.length() - 2);
		}
		article.setContent(conStr);
		article.setFlag(0);
		article.setPicUrl(picStr);
		return article;
	}
	
	/**
	 * 判断字符串是否包含指定关键字
	 * @param str
	 * @return 包含过滤字符串则返回1,包含结尾字符串返回2,都不包含返回0
	 */
	private int filter(String str) {
		for (String i : filterWord) {
			if (str.indexOf(i) != -1) {
				return 1;
			}
		}
		
		str = str.replaceAll("<([^>]*)>", "").trim();
		for (String i : endWord) {
			if (str.indexOf(i) != -1) {
				return 2;
			}
		}
		return 0;
	}
	
	private boolean containChinese(String str) {
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