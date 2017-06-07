package com.siemens.plm.hackathon.haystatck.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class VersionDTO implements Serializable{
	
	private String name;
	
	//private List<RepositoryDTO> repositories = new ArrayList<RepositoryDTO>();
	
	private List<BaselineDTO> baselines = new ArrayList<BaselineDTO>();
	
	public VersionDTO()
	{
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public List<RepositoryDTO> getRepositories() {
//		return repositories;
//	}
//
//	public void setRepositories(List<RepositoryDTO> repositories) {
//		this.repositories = repositories;
//	}

	public List<BaselineDTO> getBaselines() {
		return baselines;
	}

	public void setBaselines(List<BaselineDTO> baselines) {
		this.baselines = baselines;
	}

}
