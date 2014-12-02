package com.aperturesoft;

import java.util.List;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;

public class TestdiesSpringTestRunner extends SpringJUnit4ClassRunner {

	TestdiesConfigurer configurer;
	List<SqlRunner> sqlRunners;

	public TestdiesSpringTestRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		getTestContextManager().getTestExecutionListeners().add(new TestExecutionListener() {

			@Override
			public void prepareTestInstance(TestContext testContext) throws Exception {
				configurer = testContext.getApplicationContext().getBean(TestdiesConfigurer.class);
				WithFixture withFixture = testContext.getTestInstance().getClass().getAnnotation(WithFixture.class);
				if (withFixture != null) {
					sqlRunners = configurer.getSqlRunnersMap().get(withFixture.value());
				}
			}

			@Override
			public void beforeTestMethod(TestContext testContext) throws Exception {
				if (sqlRunners != null) {
					for (SqlRunner sqlRunner : ImmutableList.<SqlRunner>copyOf(sqlRunners).reverse()) {
						sqlRunner.deleteRows();
					}
				}
				
				if (sqlRunners != null) {
					for (SqlRunner sqlRunner : sqlRunners) {
						sqlRunner.insertRows();
					}
				}
			}

			@Override
			public void beforeTestClass(TestContext testContext) throws Exception {
			}

			@Override
			public void afterTestMethod(TestContext testContext) throws Exception {
				
				
			}

			@Override
			public void afterTestClass(TestContext testContext) throws Exception {
			}
		});
	}
}
