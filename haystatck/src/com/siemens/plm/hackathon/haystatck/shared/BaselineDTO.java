package com.siemens.plm.hackathon.haystatck.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaselineDTO implements Serializable{
	
	private String name;
	
	public BaselineDTO()
	{
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
