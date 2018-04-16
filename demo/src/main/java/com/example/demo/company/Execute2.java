package com.example.demo.company;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.demo.model.PersonalBrowseTag;

public class Execute2 implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void method() {
		List<PersonalBrowseTag> list = personalBrowseTagDao.getTagListRecentTwoMonth();
		// tag: 148+12-24万+5-10年:6|148+12-24万+3-5年:1|
		Map<String, String> map = new HashMap<>();
		
		// 获取所有标签
		for (PersonalBrowseTag i : list) {
			String tag = i.getTag();
			if (tag == null || "".equals(tag.trim())) {
				continue;
			}
			
			if (tag.indexOf("|") != -1) {
				String[] arr = tag.split("\\|");
				for (String j : arr) {
					if (j.indexOf(":") != -1) {
						String str = j.split(":")[0];
						String value = map.get(str);
						if (value == null) {
						    map.put(str, i.getMobile());
						} else {
						    map.put(str, value + "," + i.getMobile());
						}
					} else {
						continue;
					}
				}
			}
		}
	}
	
}
