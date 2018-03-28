package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
	
	@RequestMapping("/hello")
	@ResponseBody
	public String hello() {
		return "Hello World !";
	}
	
	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("host", "hello.com");
		return "index";
	}
}
