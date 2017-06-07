package com.siemens.plm.hackathon.haystatck.client;

import java.util.List;

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.siemens.plm.hackathon.haystatck.shared.BaselineDTO;
import com.siemens.plm.hackathon.haystatck.shared.ModuleDTO;
import com.siemens.plm.hackathon.haystatck.shared.RepositoryDTO;
import com.siemens.plm.hackathon.haystatck.shared.TestDTO;
import com.siemens.plm.hackathon.haystatck.shared.VersionDTO;

public class Layout extends Composite  {

	private static LayoutUiBinder uiBinder = GWT.create(LayoutUiBinder.class);

	interface LayoutUiBinder extends UiBinder<Widget, Layout> {
	}
	
	@UiField
    ListBox versionSelect,repositorySelect,moduleSelect,testSelect,baseline1Select,baseline2Select;
   
	@UiField
    Button analyzeButton;
	
	private List<VersionDTO> versions;
	private List<RepositoryDTO> repositories;
	private GreetingServiceAsync greetingService;

	public Layout(GreetingServiceAsync greetingService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.greetingService = greetingService;
	}
	
	public void setVersions(List<VersionDTO> versions)
	{
		this.versions = versions;
		versionSelect.clear();
		
		for (VersionDTO version: versions)
		{
			versionSelect.addItem(version.getName());
		}
		
		onVersionChange(null);			
	}
	
    @UiHandler("versionSelect")
    protected void onVersionChange(ChangeEvent e) {
    	System.out.println("versoin change");
    	String version = versionSelect.getSelectedValue();
    	for (VersionDTO v: this.versions)
    	{
    		if (v.getName().equals(version))
    		{
    			populateBaselines(v.getBaselines());
    			break;    			
    		}
    	}  
    	

    	getRepositories(this.getCurrentProduct(), version);
    }
    
    private void getRepositories(String product, String version)
    {
    	repositorySelect.clear();
    	moduleSelect.clear();
    	testSelect.clear();
    	repositorySelect.setEnabled(false);
    	moduleSelect.setEnabled(false);
    	testSelect.setEnabled(false);
    	greetingService.getRepositories(product, version, new AsyncCallback<List<RepositoryDTO>>() {
			
			@Override
			public void onSuccess(List<RepositoryDTO> result) {
				repositories = result;
				populateRepository(result);
		    	repositorySelect.setEnabled(true);
		    	moduleSelect.setEnabled(true);
		    	testSelect.setEnabled(true);				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
    	
    }
    
    private void populateRepository(List<RepositoryDTO> repositories)
    {
    	repositorySelect.clear();
    	for (RepositoryDTO rdto:  repositories)
    	{
    		repositorySelect.addItem(rdto.getName());    		
    	} 
    	
    	onRepositoryChange(null);
    }
    
    @UiHandler("repositorySelect")
    protected void onRepositoryChange(ChangeEvent e) {
    	String rep = this.repositorySelect.getSelectedValue();
		for (RepositoryDTO r: this.repositories)
		{
			
			if (r.getName().equals(rep))
			{
                populateModules(r.getModules());
    			break;				
			}
			
		}
    		
    }
    
    @UiHandler("moduleSelect")
    protected void onModuleChange(ChangeEvent e) {
    	String rep = this.repositorySelect.getSelectedValue();
    	String mod = this.moduleSelect.getSelectedValue();
    			
		for (RepositoryDTO r: this.repositories)
		{
			
			if (r.getName().equals(rep))
			{
				
				for (ModuleDTO m: r.getModules())
				{
					if (m.getName().equals(mod))
					{
						this.populateTests(m.getTests());
						break;
					}
				}
				
				break;				
			}			
		}	    	     	
    }   

    
    private void populateBaselines(List<BaselineDTO> baselines)
    {
    	baseline1Select.clear();
    	baseline2Select.clear();
    	
    	for (BaselineDTO baseline:baselines)
    	{
    		baseline1Select.addItem(baseline.getName());
    		baseline2Select.addItem(baseline.getName());
    		
    	}
    	
    }
    
    private void populateModules(List<ModuleDTO> modules)
    {
    	
    	this.moduleSelect.clear();
    	for (ModuleDTO module: modules)
    	{
    		moduleSelect.addItem(module.getName());    		
    	}
    	
    	this.onModuleChange(null);
    	
    }
    
    private void populateTests(List<TestDTO> tests)
    {
    	this.testSelect.clear();
    	
    	for (TestDTO test: tests)
    	{
    		testSelect.addItem(test.getName());
    	}
    }
    
    public String getCurrentProduct()
    {
    	return "NX";
    }



}
