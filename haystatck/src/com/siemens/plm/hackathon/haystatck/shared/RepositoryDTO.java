package com.siemens.plm.hackathon.haystatck.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class RepositoryDTO implements Serializable {

	private String name;
	
	private List<ModuleDTO> modules = new ArrayList<ModuleDTO>();
	
	public RepositoryDTO()
	{
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ModuleDTO> getModules() {
		return modules;
	}

	public void setModules(List<ModuleDTO> modules) {
		this.modules = modules;
	}

}
