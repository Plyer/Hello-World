package com.example.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.dao.UserRepository;
import com.example.demo.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class UserJPATest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void test() throws Exception {
		userRepository.save(new User(null, "Jack", 20));
		userRepository.save(new User(null, "Marry", 21));
		
		Assert.assertEquals(20, userRepository.findByName("Jack").getAge().longValue());
		Assert.assertEquals(21, userRepository.findByName("Marry").getAge().longValue());
	}
}
