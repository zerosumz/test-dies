test-dies
===========

what is this
------------

it is very tiny and simple project does insert fixture data to database for test.
it use real database.
just prepare markdown file for test. and go. 


usage
-----

prepare some markdown files.


SIMPLE SELECT TEST
------------------

|    ID[^Integer]   |   AGE[^Integer]   | BIRTHDAY[^Date] |
|-------------------|-------------------|-----------------|
| 1                 | 12                | 2001-10-01      |
| 2                 | 33                | 1981-10-22      |
[FOO]

[^Integer]:java.lang.Integer

[^Date]:java.util.Date

> i used footnote to describe column type. not visible current readme file.
> table caption too. 
> <s>MARKDOWN EXAMPLE IN README.md</s>


write some configurations to spring application context;
```
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource" >
	<property name="driverClassName" value="org.h2.Driver" />
	<property name="url" value="jdbc:h2:file:test;MODE=Oracle" />
	<property name="username" value="sa" />
	<property name="password" value="" />
</bean>

<bean id="dateFormat" class="java.text.SimpleDateFormat">
	<constructor-arg value="yyyy-MM-dd" />
</bean>

<bean id="testDies" class="com.aperturesoft.TestdiesConfigurer">
    <property name="mdownFiles">
        <list>
            <value>classpath:/a.md</value>
        </list>
    </property>
    <property name="dateFormat" ref="dateFormat"/>
    <property name="dataSource" ref="dataSource"/>
</bean>
```

use `@RunWith(TestdiesSpringTestRunner.class)` and `@WithFixture("SIMPLE SELECT TEST")` annotation on junit test class.
before test method invoke, specificated markdown tables' row will inserted each database table. 
and delete data after method invoked.

```
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

```
 
note
----
1. it does not create tables now 


