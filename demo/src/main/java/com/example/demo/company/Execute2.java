package com.example.demo.company;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.car.dal.object.personaltag.PersonalBrowseTag;

public class Execute2 implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void method() {
		List<PersonalBrowseTag> list = personalBrowseTagDao.getTagListRecentTwoMonth();
		// tag: 148+12-24万+5-10年:6|148+12-24万+3-5年:1|
		Set<String> set = new HashSet<>();
		
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
						set.add(j.split(":")[0]);
					} else {
						continue;
					}
				}
			}
		}
		
		Map<String, String> finalMap = new HashMap<>();
		for (String i : set) {
			StringBuilder sb = new StringBuilder("");
			for (PersonalBrowseTag j : list) {
				if (j.getTag().indexOf(i) != -1) {
					sb.append(j.getMobile() + ",");
				}
			}
			finalMap.put(i, sb.toString());
		}
		
		
	}
	
}
