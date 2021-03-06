package com.semanticweb.receipe.receipeapp.Model;

/**
 * model of language.
 * @author Yi-Ru
 *
 */
public class LanguageItem {
	private String name;
	private String abbreviation;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
