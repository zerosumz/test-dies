package com.aperturesoft;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.parboiled.common.FileUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class TestdiesMain {
	public static PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.ALL);
	private static final Logger LOGGER = LoggerFactory.getLogger(TestdiesMain.class);

	public static Map<String, List<SqlRunner>> createFixture(DataSource dataSource, Resource[] resources, DateFormat dateFormat)
			throws IOException, ClassNotFoundException {
		Map<String, List<SqlRunner>> sqlRunnerMap = new HashMap<String, List<SqlRunner>>();
		
		for (Resource resource : resources) {
			char[] mdownSource = FileUtils.readAllChars(resource.getInputStream());
			RootNode rootNode = pegDownProcessor.parseMarkdown(mdownSource);
			String title = getTextFromNode(findNodeByType(rootNode, HeaderNode.class));
			List<SqlRunner> sqlRunners = new ArrayList<SqlRunner>(); 
			
			List<ReferenceNode> footnotes = findAllNodeByType(rootNode, ReferenceNode.class);
			LinkedHashMap<String, Class<?>> columnTypeMap = new LinkedHashMap<String, Class<?>>();
			
			// set footnotes to header types
			for (ReferenceNode footnote : footnotes) {
				Class<?> clazz = Class.forName(footnote.getUrl());
				columnTypeMap.put(getTextFromNode(findNodeByType(footnote, SuperNode.class)), clazz);
						
			}
			
			List<TableNode> tables = findAllNodeByType(rootNode, TableNode.class);
			
			for (TableNode table : tables) {
				String tableName = getTextFromNode(findNodeByType(table, TableCaptionNode.class));
				
				List<Cell> headerRow = new ArrayList<Cell>();
				List<List<Cell>> rows = new ArrayList<List<Cell>>();
				
				TableRowNode headerRowNode = findNodeByType(findNodeByType(table, TableHeaderNode.class) , TableRowNode.class);
				for (TableCellNode cell : findAllNodeByType(headerRowNode, TableCellNode.class)) {
					String columnName = getTextFromNode(cell);
					RefLinkNode linkNode = findNodeByType(cell, RefLinkNode.class);
					String columnTypeKey = getTextFromNode(findNodeByType(linkNode, SuperNode.class));
					headerRow.add(new Cell(columnName, columnTypeMap.get(columnTypeKey)));
				}

				for (TableRowNode row : findAllNodeByType(findNodeByType(table, TableBodyNode.class), TableRowNode.class)) {
					List<Cell> columns = new ArrayList<Cell>();
					
					List<TableCellNode> cells = findAllNodeByType(row, TableCellNode.class); 
					for (int i = 0; i < cells.size(); i++) {
						TableCellNode cell = cells.get(i);
						String text = getTextFromNode(cell);
						Cell cellType = headerRow.get(i);
						columns.add(new Cell(cellType.name, cellType.clazz, text));
					}
					rows.add(columns);
					
				}
				sqlRunners.add(new SqlRunner(dataSource, tableName, headerRow, rows, dateFormat));
			}
			
			sqlRunnerMap.put(title, sqlRunners);
			LOGGER.info("with \'" + title + "\' test, " + tables.size() + " tables will fix.");
		}
		return sqlRunnerMap;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Node> T findNodeByType(Node parentNode, final Class<T> clazz) {
		return (T) Iterables.find(parentNode.getChildren(), new Predicate<Node>() {
			@Override
			public boolean apply(Node node) {
				return clazz.isAssignableFrom(node.getClass());
			}

		}, null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Node> List<T> findAllNodeByType(Node parentNode, final Class<T> clazz) {
		return (List<T>) ImmutableList.<Node> copyOf(Iterables.filter(parentNode.getChildren(), new Predicate<Node>() {
			@Override
			public boolean apply(Node node) {
				return clazz.isAssignableFrom(node.getClass());
			}
		}));
	}
	
	public static String getTextFromNode(Node node){
		return Joiner.on("").join(Iterables.transform(findAllNodeByType(node, TextNode.class), new Function<TextNode, String>() {
			@Override
			public String apply(TextNode input) {
				return StringUtils.trim(input.getText());
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Node> T findNodeByTypes(Node parentNode, Class<? extends Node> ... classes ){
		if(parentNode == null || classes.length == 0){
			return (T) parentNode;
		} else {
			return findNodeByTypes(findNodeByType(parentNode, classes[0]), Arrays.copyOfRange(classes, 1, classes.length));
		}
	}
}
