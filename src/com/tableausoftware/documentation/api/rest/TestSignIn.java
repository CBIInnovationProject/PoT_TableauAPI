package com.tableausoftware.documentation.api.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.tableausoftware.documentation.api.rest.bindings.TableauCredentialsType;
import com.tableausoftware.documentation.api.rest.bindings.UserType;
import com.tableausoftware.documentation.api.rest.util.RestApiUtils;

public class TestSignIn {
    private static final RestApiUtils s_restApiUtils = RestApiUtils.getInstance();
    private static Logger s_logger = Logger.getLogger(TestSignIn.class);
    private static Properties s_properties = new Properties();
    static {
        // Configures the logger to log to stdout
        BasicConfigurator.configure();
        
        // Loads the values from configuration file into the Properties instance
        try {
            s_properties.load(new FileInputStream("res/config.properties"));
        } catch (IOException e) {
            s_logger.error("Failed to load configuration files.");
        }
    }
    
	public static void main(String[] args) {
		String username = s_properties.getProperty("user.admin.name");
        String password = s_properties.getProperty("user.admin.password");
        String contentUrl = s_properties.getProperty("site.default.contentUrl");
	    
		TableauCredentialsType credential = s_restApiUtils.invokeSignIn(username, password, contentUrl);
		String currentSiteId = credential.getSite().getId();
		
		List<UserType> users = s_restApiUtils.invokeQueryUsersOnSite(credential).getUser();
		for(UserType user: users){
			System.out.println(user.getName()+" "+user.getSiteRole());
		}
		
		s_logger.info(String.format("Authentication token: %s", credential.getToken()));
        s_logger.info(String.format("Site ID: %s", currentSiteId));
	}

}
