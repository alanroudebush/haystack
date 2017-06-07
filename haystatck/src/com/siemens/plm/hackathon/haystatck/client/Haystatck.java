package com.siemens.plm.hackathon.haystatck.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.siemens.plm.hackathon.haystatck.shared.VersionDTO;

public class Haystatck implements EntryPoint {


	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	private Layout layout = new Layout(greetingService);

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(layout);
		
		greetingService.getVersions(layout.getCurrentProduct(), new AsyncCallback<List<VersionDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<VersionDTO> result) {
				// TODO Auto-generated method stub
				layout.setVersions(result);				
			}
		});
	}
}
