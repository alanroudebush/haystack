package com.siemens.plm.hackathon.haystatck.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.siemens.plm.hackathon.haystatck.shared.RepositoryDTO;
import com.siemens.plm.hackathon.haystatck.shared.VersionDTO;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	List<VersionDTO> getVersions(String product);
	
	List<RepositoryDTO> getRepositories(String product, String version);
}
