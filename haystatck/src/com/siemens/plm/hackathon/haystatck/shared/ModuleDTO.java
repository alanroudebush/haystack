package com.siemens.plm.hackathon.haystatck.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ModuleDTO implements Serializable {
	private String name;
	private List<TestDTO> tests = new ArrayList<TestDTO>();
	public ModuleDTO()
	{
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TestDTO> getTests() {
		return tests;
	}
	public void setTests(List<TestDTO> tests) {
		this.tests = tests;
	}

}
