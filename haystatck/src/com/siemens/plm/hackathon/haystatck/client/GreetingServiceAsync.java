package com.siemens.plm.hackathon.haystatck.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.siemens.plm.hackathon.haystatck.shared.RepositoryDTO;
import com.siemens.plm.hackathon.haystatck.shared.VersionDTO;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getVersions(String product, AsyncCallback<List<VersionDTO>> callback);
	
	void getRepositories(String product, String version, AsyncCallback<List<RepositoryDTO>> callback);
}
