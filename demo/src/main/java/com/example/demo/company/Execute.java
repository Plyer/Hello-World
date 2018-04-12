package com.example.demo.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Execute {
	
	public Map<String, TreeMap<String, Integer>> getTags(int day) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, TreeMap<String, Integer>> finalMap = new HashMap<>();
		for (int i = 1; i <= day; i++) {
			c.add(Calendar.DAY_OF_MONTH, -i);
			String date = sdf.format(c.getTime());
			String path = "/hadoopLog/" + date + "/carDetail3-r-00000";
			getTag(path,finalMap);
		}
		return finalMap;
	}
	
	private Map<String, TreeMap<String, Integer>> getTag(String path, Map<String, TreeMap<String, Integer>> finalMap) {
		if (finalMap == null) {
			finalMap = new HashMap<>();
		}
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://mycluster");
		conf.set("dfs.nameservices", "mycluster");
		conf.set("dfs.ha.namenodes.mycluster", "nn1,nn2");
		conf.set("dfs.namenode.rpc-address.mycluster.nn1", "app206:9000");
		conf.set("dfs.namenode.rpc-address.mycluster.nn2", "app205:9000");
		conf.set("dfs.client.failover.proxy.provider.mycluster",
				"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
		BufferedReader bufr = null;
		try {
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			FSDataInputStream hdfsInStream = fs.open(new Path(path));
			bufr = new BufferedReader(new InputStreamReader(hdfsInStream, "utf-8"));
			
			//13001694444|32,5,2017年10月|车系价格年份|	1
			String line = "";
			while ((line = bufr.readLine()) != null) {
				String[] targets = line.split("|");
				if (targets.length != 4) {
					continue;
				}
				
				String phone = targets[0].trim();
				String data = targets[1].trim();
				String type = targets[2].trim();	
				String num = targets[3].replace("	", "").trim();
				int num2 = Integer.parseInt(num);
				String[] detail = data.split(",");
				
				if (phone.equals("") || phone.length() != 11) {
					continue;
				}
				if (!type.equals("车系价格年份")) {
					continue;
				}
				
				TreeMap<String, Integer> treeMap = null;
				HashMap<String, Integer> hashMap = null;
				SortValue cmp = null;
				if (finalMap.containsKey(phone)) {
					treeMap = finalMap.get(phone);
				} else {
					hashMap = new HashMap<>();
					cmp = new SortValue(hashMap);
				}
				detail[1] = price(Integer.parseInt(detail[1]));
				detail[2] = year(Integer.parseInt(detail[2].split("年")[0]));
				String tag = detail[0] + "+" + detail[1] + "+" + detail[2];
				
				if (treeMap != null) {
					if (treeMap.containsKey(tag)) {
						treeMap.put(tag, treeMap.get(tag) + num2);
					} else {
						treeMap.put(tag, num2);
					}
				} else {
					treeMap = new TreeMap<>(cmp);
					hashMap.put(tag, num2);
					treeMap.putAll(hashMap);
					finalMap.put(phone, treeMap);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return finalMap;
	}
	
	private String year(int year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String nowYear = sdf.format(new Date());
		int nYear = Integer.parseInt(nowYear);
		int tmp = nYear - year;
		if (tmp < 1) {
			return "1年以内";
		} else if (tmp < 3 && tmp >= 1) {
			return "1-3年";
		} else if (tmp < 5 && tmp >= 3) {
			return "3-5年";
		} else if (tmp < 10 && tmp >= 5) {
			return "5-10年";
		} else {
			return "10年以上";
		}
	}
	
	private String price(int p) {
		if (p < 3) {
			return "3万以下";
		}
		if (p < 5 && p >= 3) {
			return "3-5万";
		}
		if (p < 8 && p >= 5) {
			return "5-8万";
		}
		if (p < 12 && p >= 8) {
			return "8-12万";
		}
		if (p < 24 && p >= 12) {
			return "12-24万";
		}
		if (p < 30 && p >= 24) {
			return "24-30万";
		}
		if (p < 40 && p >= 30) {
			return "30-40万";
		}
		if (p < 100 && p >= 40) {
			return "40-100万";
		}
		if (p > 100) {
			return "100万以上";
		}
		return "";
	}
	
	private class SortValue implements Comparator<String> {
		
		private Map<String, Integer> map;
		
		public SortValue(Map<String, Integer> map) {
			this.map = map;
		}
		
		@Override
		public int compare(String o1, String o2) {
			if (o1.equals(o2)) {
				return 0;
			}
			if (map.get(o1) < map.get(o2)) {
				return 1;
			} else {
				return -1;
			}
		}

	}
}
