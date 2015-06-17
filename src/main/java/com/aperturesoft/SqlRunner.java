package com.aperturesoft;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.springframework.format.datetime.joda.DateTimeParser;

public class SqlRunner {
	private DataSource dataSource;
	private String tableName;
	private String sql;
	private List<List<Cell>> bodyRows = new ArrayList<List<Cell>>();
	private DateTimeFormatter dateFormatter;

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlRunner.class);

	public SqlRunner(DataSource dataSource, String tableName, List<Cell> headerRow, List<List<Cell>> bodyRows,
			DateTimeFormatter dateFormatter) {
		super();
		this.dataSource = dataSource;
		this.tableName = tableName;
		this.bodyRows = bodyRows;
		this.dateFormatter = dateFormatter;

		String[] questions = new String[headerRow.size()];
		Arrays.fill(questions, "?");

		sql = "INSERT INTO " + tableName + " ( "
				+ Joiner.on(" , ").join(Iterables.transform(headerRow, new Function<Cell, String>() {
					@Override
					public String apply(Cell cell) {
						return cell.name;
					}
				})) + " ) VALUES ( \n" + Joiner.on(" , ").join(questions) + " ) ";
	}

	public void insertRows() throws ParseException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			for (List<Cell> row : bodyRows) {

				for (int i = 0; i < row.size(); i++) {
					Cell cell = row.get(i);
					int paramIndex = i + 1;

					Class<?> clazz = cell.clazz;
					String value = cell.value;
					if(StringUtils.isEmpty(value))
						preparedStatement.setObject(paramIndex, null);
					else if (clazz.equals(Integer.class))
						preparedStatement.setInt(paramIndex, Integer.parseInt(value));
					else if (clazz.equals(Long.class))
						preparedStatement.setLong(paramIndex, Long.parseLong(value));
					else if (clazz.equals(Double.class))
						preparedStatement.setDouble(paramIndex, Double.parseDouble(value));
					else if (clazz.equals(Float.class))
						preparedStatement.setFloat(paramIndex, Float.parseFloat(value));
					else if (clazz.equals(Date.class))
						preparedStatement.setDate(paramIndex, new java.sql.Date(dateFormatter.parseDateTime(value).toDate().getTime()));
					else if (clazz.equals(Boolean.class))
						preparedStatement.setBoolean(paramIndex, Boolean.parseBoolean(value));
					else if (Number.class.isAssignableFrom(clazz))
						preparedStatement.setBigDecimal(paramIndex, new BigDecimal(value));
					else
						preparedStatement.setString(paramIndex, value);
				}
				preparedStatement.addBatch();
			}
			LOGGER.info("now filling \'" + tableName + "\' table ...");
			LOGGER.debug(sql);
			
			preparedStatement.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			try {
				e.printStackTrace();
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new BeanCreationException("fail to read markdown");

		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void deleteRows() {
		String sql = "DELETE FROM " + tableName;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
				if (connection != null) {
					connection.close();
					connection = null;
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

}
