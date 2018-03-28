package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

	private Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());
	
	/*{
		User user = new User();
		user.setAge(21);
		user.setId(1l);
		user.setName("Jack");
		users.put(1l, user);
	}*/
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public List<User> getUserList() throws Exception {
		List<User> r = new ArrayList<>(users.values());
		throw new Exception("发生错误");
		// return r;
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addUser(User user) throws Exception {
		User tuser = users.put(user.getId(), user);
		System.out.println(tuser);
		throw new Exception("发生错误");
//		return "success";
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteUser(Long id) {
		User tuser = users.remove(id);
		System.out.println(tuser);
		return "success";
	}
	
	@RequestMapping(value = "/upd", method = RequestMethod.GET)
	public String updUser(User user) {
		System.out.println("修改前     " + users.get(user.getId()));
		users.put(user.getId(), user);
		System.out.println("修改后     " + users.get(user.getId()));
		return "success";
	}
}
