package com.aperturesoft;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.aperturesoft.TestdiesSpringTestRunner;
import com.aperturesoft.WithFixture;
import com.aperturesoft.model.Post;
import com.aperturesoft.repo.PostMapper;

@RunWith(TestdiesSpringTestRunner.class)
@WithFixture("SELECT POST TEST")
@ContextConfiguration(value = { "classpath:/spring-config-db.xml" })
public class TestPostMapperTest {
	
	@Autowired
	PostMapper postMapper;

	@Test
	public void test() {
		Post post =postMapper.selectPostById(1);
		assertEquals(3, post.getComments().size());
	}

}
