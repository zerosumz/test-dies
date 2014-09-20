package com.aperturesoft;

public class Cell{
	public String name;
	public String value;
	public Class<?> clazz;
	
	public Cell(String name, Class<?> clazz) {
		super();
		this.name = name;
		this.clazz = clazz;
	}
	
	public Cell(String name, Class<?> clazz, String value) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.value = value;
	}

}