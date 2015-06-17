test-dies
===========

이건뭐지?
-------

마크다운 형식으로 작성된 픽스쳐 데이터를 가지고 테스트를 할 수 있게 하는 작은 테스트 프레임워크.

usage
-----

* 마크다운으로 픽스쳐를 작성하여 준비한다.
    * 다음 예를 참고.
    * 작성하여 프로젝트의 리소스 폴더에 위치시킨다.

<pre>
SELECT POST TEST
================

* 포스트 조회기능의 간단한 테스트
* 한 개의 포스트는 여러 개의 코멘트를 가진다.
* 그러므로 특정 포스트를 조회하여 코멘트가 존재함을 보임.

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


* 확인할 것.
    * `ID` 가 1인 포스트는 세 개의 코멘트를 가지고 있는가?

[^int]:java.lang.Integer

[^String]:java.lang.String

</pre>

> i used footnote to describe column type. not visible github favor..
> table caption too. 


스프링 설정 파일은 (예를들어 xml은) 다음과 같다 
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
	
<bean id="testDies" class="com.aperturesoft.TestdiesConfigurer">
    <property name="mdownFiles">
        <list>
            <value>classpath:/select_post_test.md</value>
        </list>
    </property>
    <property name="dateFormatString" value="yyyy-MM-dd"/>
    <property name="dataSource" ref="dataSource"/>
</bean>
````

`@RunWith(TestdiesSpringTestRunner.class)` 와 `@WithFixture("SELECT POST TEST")` 어노테이션을 사용.
* 프레임워크가 하는 일은 단순함.
    * 테스트 메서드가 실행전 마크다운 테이블을 보고 테스트 데이터베이스에 해당 테이블을 지우고 인서트.

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

NOTE
----

* 처음 빈 H2 database 파일을 생성하면 스키마가 없어서 접속 URL이 안먹음..
    * 처음엔 스키마를 빼고 돌린후
    * 다시 스키마를 넣고 테스트를...

