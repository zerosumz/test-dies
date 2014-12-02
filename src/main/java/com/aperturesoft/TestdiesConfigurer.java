package com.aperturesoft;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import static org.springframework.util.Assert.*;

public class TestdiesConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean,
		ApplicationContextAware, BeanNameAware {
	private ApplicationContext applicationContext;
	private String beanName;
	private Resource[] mdownFiles;
	private DataSource dataSource;
	private DateFormat dateFormat;
	private Map<String, List<SqlRunner>> sqlRunnersMap;
	private Boolean donotDelete = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(TestdiesConfigurer.class);

	/**
	 * after bean factory init
	 * 
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	/**
	 * after properties set
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		notNull(mdownFiles, "markdown Files empty!");
		notNull(dataSource, "datasource ref please!");
		try {
			if (this.dateFormat == null)
				this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			LOGGER.info("initializing fixture sql runner . . .");
			this.sqlRunnersMap = TestdiesMain.createFixture(dataSource, mdownFiles, dateFormat);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BeanCreationException(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BeanCreationException(e.getMessage());
		}
	}

	/**
	 * after <b>this</b> dependency init
	 * 
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Resource[] getMdownFiles() {
		return mdownFiles;
	}

	public void setMdownFiles(Resource[] mdownFiles) {
		this.mdownFiles = mdownFiles;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Map<String, List<SqlRunner>> getSqlRunnersMap() {
		return sqlRunnersMap;
	}

	public void setSqlRunnersMap(Map<String, List<SqlRunner>> sqlRunnersMap) {
		this.sqlRunnersMap = sqlRunnersMap;
	}

	public Boolean getDonotDelete() {
		return donotDelete;
	}

	public void setDonotDelete(Boolean donotDelete) {
		this.donotDelete = donotDelete;
	}

};
