package tableau.api.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import tableau.api.rest.bindings.TableauCredentialsType;
import tableau.api.rest.bindings.UserType;
import tableau.api.rest.util.RestApiUtils;

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
	    
		TableauCredentialsType credential = s_restApiUtils.invokeSignIn("Cybertrend", "passcbi2015", "");
		String currentSiteId = credential.getSite().getId();
		System.out.println(currentSiteId);
		System.out.println(s_restApiUtils.invokeGetUser(credential, credential.getUser().getId()));
		
//		List<UserType> users = s_restApiUtils.invokeQueryUsersOnSite(credential).getUser();
//		for(UserType user: users){
//			System.out.println(user.getId()+" | "+user.getName()+" | "+user.getSiteRole()+" | "+user.getLastLogin());
//		}
		
//		s_logger.info(String.format("Authentication token: %s", credential.getToken()));
//        s_logger.info(String.format("Site ID: %s", currentSiteId));
	}

}
