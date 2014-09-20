package com.aperturesoft;

import java.util.List;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class TestdiesSpringTestRunner extends SpringJUnit4ClassRunner {

	TestdiesConfigurer configurer;
	List<SqlRunner> sqlRunners;

	public TestdiesSpringTestRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		getTestContextManager().getTestExecutionListeners().add(new TestExecutionListener() {

			public void prepareTestInstance(TestContext testContext) throws Exception {
				configurer = testContext.getApplicationContext().getBean(TestdiesConfigurer.class);
				WithFixture withFixture = testContext.getTestInstance().getClass().getAnnotation(WithFixture.class);
				if (withFixture != null) {
					sqlRunners = configurer.getSqlRunnersMap().get(withFixture.value());
				}
			}

			public void beforeTestMethod(TestContext testContext) throws Exception {
				if (sqlRunners != null) {
					for (SqlRunner sqlRunner : sqlRunners) {
						sqlRunner.insertRows();
					}
				}
			}

			public void beforeTestClass(TestContext testContext) throws Exception {
			}

			public void afterTestMethod(TestContext testContext) throws Exception {

				if (sqlRunners != null) {
					for (SqlRunner sqlRunner : sqlRunners) {
						sqlRunner.deleteRows();
					}
				}
			}

			public void afterTestClass(TestContext testContext) throws Exception {
			}
		});
	}
}
