package com.siemens.plm.hackathon.haystatck.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.perforce.p4java.server.IServer;
import com.siemens.plm.hackathon.haystatck.client.GreetingService;
import com.siemens.plm.hackathon.haystatck.shared.BaselineDTO;
import com.siemens.plm.hackathon.haystatck.shared.Constants;
import com.siemens.plm.hackathon.haystatck.shared.ModuleDTO;
import com.siemens.plm.hackathon.haystatck.shared.RepositoryDTO;
import com.siemens.plm.hackathon.haystatck.shared.TestDTO;
import com.siemens.plm.hackathon.haystatck.shared.VersionDTO;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	

	// CC parsing
    private static final String CC_SRC_PATH_BEGIN = "<function name=\"src/";
    private static final String CC_SRC_PATH_END = "/";
    private static final String CC_REP_MODULE_SEPARATOR = ".rep:";
    private static final String CC_MODULE_TEST_SEPARATOR = ".seq:";
    private static final String URL_FWD_SLASH = "-0f";

	public static final String TC_DMS_PW = "";
    // DMS DB

    private static final String NX_DMS_PW = "";
    private static Connection dms;
    private static Statement getBaselines;
    private static String baselinePrefix = null;
    // perforce
    private static IServer perforce;
    
	Map<String, String> productMap = new HashMap<String,String>(){{put("TC","PLM");put("NX","z__NX");}};
	
	//ResourceBundle;
	ResourceBundle rb = ResourceBundle.getBundle("cfg");


	@Override
	public List<VersionDTO> getVersions(String product) {
		java.util.List<VersionDTO> versions = new java.util.ArrayList<VersionDTO>();
		
        try {

            // get list of versions for this product
        	URL ccVersions = new URL("TC".equals(product) ? rb.getString(Constants.TC_CODE_COV) : rb.getString(Constants.NX_CODE_COV));
            BufferedReader in = new BufferedReader(new InputStreamReader(ccVersions.openStream()));
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                inputLine = inputLine.trim();
                int endIndex = inputLine.indexOf("/</a>");
                if(endIndex!=-1)
                {
                	String version = inputLine.substring(inputLine.substring(0, endIndex).lastIndexOf(">")+1, endIndex).trim();
                	// make sure it's a number
                	try {
                	    Integer.parseInt(version);
                	    VersionDTO vdto = new VersionDTO();
                	    vdto.setName(version);
                	    
                	    // populate baselines
                	    for (String baseline: getBaselines(product, version))
                	    {
                	    	BaselineDTO bdto = new BaselineDTO();
                	    	bdto.setName(baseline);
                	    	vdto.getBaselines().add(bdto);
                	    }
                	    
           
//                	    getRepositories(product, version);
//                	    for (String rep: repModTest.keySet())
//                	    {
//                	    	RepositoryDTO rdto= new RepositoryDTO();
//                	    	rdto.setName(rep);
//                	    	vdto.getRepositories().add(rdto);
//                	    	for (String mod: repModTest.get(rep).keySet())
//                	    	{
//                	    		ModuleDTO mdto = new ModuleDTO();
//                	    		mdto.setName(mod);
//                	    		rdto.getModules().add(mdto);
//                	    		
//                	    		for (String test: repModTest.get(rep).get(mod))
//                	    		{
//                	    			TestDTO tdto = new TestDTO();
//                	    			tdto.setName(test);
//                	    			mdto.getTests().add(tdto);
//                	    		}
//                	    	}
//                	    	
//                	    }
                	    
                	    
                	    versions.add(vdto);
                	}
                	catch (NumberFormatException nfe) {
                		// not a version
                	}
                }
            }
            in.close();
            
//            Collections.sort(versions, new Comparator<String>() {
//				@Override
//				public int compare(String s1, String s2) {
//					return Integer.parseInt(s2) - Integer.parseInt(s1);
//				}});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        

        return versions;
	}
	
	private List<String> getBaselines(String product, String version) {
		// convert version from CC to DMS: TC 1120 -> tc11.2.0
		java.util.List<String> sortedBaselines = new java.util.ArrayList<String>();
		
		if(product.equals("TC")) {
			baselinePrefix = new String(product.toLowerCase() + version.substring(0, version.length()-2) + "." + version.charAt(version.length()-2) + "." + version.charAt(version.length()-1));
		} else {
			baselinePrefix = new String(product.toLowerCase() + version);
		}

		try {
			String query;
			if(product.equals("TC")) {
				dms = DriverManager.getConnection(rb.getString(Constants.TC_DMS_URL), rb.getString(Constants.TC_DMS_USER), TC_DMS_PW);
		    	query = "SELECT name FROM baseline WHERE hidden='0' AND name LIKE '" + baselinePrefix + ".20%' AND IDEnvironment IN (SELECT IDenvironment FROM environment WHERE envtype='release' AND idproduct IN (SELECT IDProduct FROM product WHERE name='" + productMap.get(product) + "'))"; 
			} else {
				dms = DriverManager.getConnection(rb.getString(Constants.NX_DMS_URL), rb.getString(Constants.NX_DMS_USER), NX_DMS_PW);
		    	query = "SELECT name FROM baseline WHERE hidden='0' AND name LIKE '" + baselinePrefix + "%' AND IDEnvironment IN (SELECT IDenvironment FROM environment WHERE envtype='release' AND idproduct IN (SELECT IDProduct FROM product WHERE name='" + productMap.get(product) + "'))"; 
			}
	    	getBaselines = dms.createStatement();
	    	ResultSet baselines = getBaselines.executeQuery(query);
	    	
	        
	    	while (baselines.next()) {
	    		sortedBaselines.add(baselines.getString(1));
	        }
	    	
			if(product.equals("NX")) {
	    	Collections.sort(sortedBaselines, new Comparator<String>()	{
				@Override
				public int compare(String s1, String s2) {
					String[] split1 = s1.split("\\.");
					String[] split2 = s2.split("\\.");
					return Integer.parseInt(split1[1]) - Integer.parseInt(split2[1]);
				}} );
			}


		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return sortedBaselines;
	}
	
	
	@Override
	public List<RepositoryDTO> getRepositories(String product, String version) {
		//repModTest.clear();
		Map<String, Map<String, Set<String>>> repModTest = new HashMap<String, Map<String, Set<String>>>();
        try {
			// get list of versions for this product
        	java.util.List<URL> testURLs = new ArrayList<URL>();
        	if(product.equals("TC")) {
        		testURLs.add(new URL(rb.getString(Constants.TC_CODE_COV) + version + rb.getString(Constants.TC_CODE_COV_AOS_TESTS)));
        		testURLs.add(new URL(rb.getString(Constants.TC_CODE_COV) + version + rb.getString(Constants.TC_CODE_COV_UTESTS)));
        	} else {
        		testURLs.add(new URL(rb.getString(Constants.NX_CODE_COV) + version + rb.getString(Constants.NX_CODE_COV_TESTS)));
        	}
        	for(URL testURL : testURLs) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(testURL.openStream()));
	            String inputLine;
	            while ((inputLine = in.readLine()) != null) {
	            	// fix begining of string
	                inputLine = inputLine.replaceFirst("0\\s+", "");
	                // build a tree of repositories, modules, tests
	                for(String testFQ : inputLine.split(":\\s+")) {
	                	// modules can have submodules, which is /, or -0f in html
	                	testFQ = testFQ.replaceAll(URL_FWD_SLASH, "/");
	                	// split string into repository and module-test
	                	String[] repository = testFQ.split(CC_REP_MODULE_SEPARATOR);
	                	// split remainder into module and test
	                	if(repository.length == 2) {
	                    	String[] module = repository[1].split(CC_MODULE_TEST_SEPARATOR);
	                    	// add to tree
	                    	Map<String, Set<String>> modules = repModTest.get(repository[0]);
	                    	if(modules == null) {
	                    		modules = new HashMap<String,Set<String>>();
	                    	}
	                		Set<String> tests = modules.get(module[0]);
	                    	if(tests == null) {
	                    		tests = new HashSet<String>();
	                    	}
	                    	tests.add(module[1]);
	                    	modules.put(module[0], tests);
	                    	repModTest.put(repository[0], modules);
	                	}
	                }
	                // add repositories to list
	            }
	            in.close();
        	}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        // sort and add to UI
        java.util.List<String> reps = new java.util.ArrayList<String>(repModTest.keySet());
        Collections.sort(reps, new Comparator<String>()	{
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}} );

	
        List<RepositoryDTO> out = new ArrayList<RepositoryDTO>();
	    for (String rep: repModTest.keySet())
	    {
	    	RepositoryDTO rdto= new RepositoryDTO();
	    	rdto.setName(rep);
	    	out.add(rdto);
	    	for (String mod: repModTest.get(rep).keySet())
	    	{
	    		ModuleDTO mdto = new ModuleDTO();
	    		mdto.setName(mod);
	    		rdto.getModules().add(mdto);
	    		
	    		for (String test: repModTest.get(rep).get(mod))
	    		{
	    			TestDTO tdto = new TestDTO();
	    			tdto.setName(test);
	    			mdto.getTests().add(tdto);
	    		}
	    	}
	    	
	    }
	    
	    return out;
	}
}
