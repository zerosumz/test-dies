package com.aperturesoft;

import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.aperturesoft.repo.TestMapper; 

@RunWith(TestdiesSpringTestRunner.class)
@WithFixture("SIMPLE SELECT TEST")
@ContextConfiguration(value = { "classpath:spring-config-db.xml" })
public class TestBasicTest {

	@Autowired
	TestMapper testMapper;
	
	@Test
	public void testBasic() {
		BirthDay birthDay = testMapper.selectMe(2);
		Assert.assertEquals(33, birthDay.getAge());
		Assert.assertEquals("1981", new SimpleDateFormat("YYYY").format(birthDay.getBirthDay()));
	}
	
}
