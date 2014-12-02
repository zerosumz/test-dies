test-dies
===========

what is this
------------
test with markdown fixture.
it is very tiny and simple project does insert fixture data to database for test.
it use real database.
just prepare markdown file for test. and go. 


usage
-----

prepare some markdown files.

<pre>
SELECT POST TEST
================

* this is a simple test to select posts
* post has some comments
* so we test expected comments exists when mapper method calls. 

| ID[^int] |   CONTENT[^String]   |
|----------|----------------------|
|        1 | hey, i must do test. |
[POST]


| ID[^int] | POST_ID[^int] |      CONTENT[^String]     |
|----------|---------------|---------------------------|
|        1 |             1 | oh, i'going to ready      |
|        2 |             1 | give me just two sec.     |
|        3 |             1 | not my business. get off. |
[COMMENT]


* TESTS
    * there is 3 comments?

[^int]:java.lang.Integer

[^String]:java.lang.String
</pre>

> i used footnote to describe column type. not visible github favor..
> table caption too. 


write some configurations to spring application context;
````xml
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource" >
		<property name="driverClassName" value="org.h2.Driver" />
		<property name="url" value="jdbc:h2:file:test;FILE_LOCK=NO;MODE=MySql;SCHEMA=fooo" />
		<property name="username" value="sa" />
		<property name="password" value="" />
</bean>

<jdbc:initialize-database data-source="dataSource" enabled="true" ignore-failures="ALL">
    <jdbc:script location="classpath:/H2_CREATE.sql" />
</jdbc:initialize-database>
	
<bean id="dateFormat" class="java.text.SimpleDateFormat">
	<constructor-arg value="yyyy-MM-dd" />
</bean>

<bean id="testDies" class="com.aperturesoft.TestdiesConfigurer">
    <property name="mdownFiles">
        <list>
            <value>classpath:/select_post_test.md</value>
        </list>
    </property>
    <property name="dateFormat" ref="dateFormat"/>
    <property name="dataSource" ref="dataSource"/>
</bean>
````

use `@RunWith(TestdiesSpringTestRunner.class)` and `@WithFixture("SELECT POST TEST")` annotation on junit test class.
before test method invoke, specificated markdown tables' row will inserted each database table. 
and delete data after method invoked.

````java
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
````


