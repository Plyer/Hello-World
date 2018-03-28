package com.example.demo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controller.UserController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockServletContext.class)
@WebAppConfiguration
public class UserControllerTest {
	
	private MockMvc mvc;
	
	@Before
	public void setUp() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
	}
	
	@Test
	public void testUserController() throws Exception {
		RequestBuilder req = null;
		
		req = post("/users/");
		mvc.perform(req).andExpect(status().isOk()).andExpect(content().string(equalTo("[]")));
		
		req = get("/users/add").param("id", "1").param("name", "Jack").param("age", "55");
		mvc.perform(req).andExpect(content().string(equalTo("success")));
		
		req = get("/users/upd").param("id", "2").param("name", "Marry").param("age", "20");
		mvc.perform(req).andExpect(content().string(equalTo("success")));
		
		req = get("/users/delete").param("id", "2");
		mvc.perform(req).andExpect(content().string(equalTo("success")));
	}
}
