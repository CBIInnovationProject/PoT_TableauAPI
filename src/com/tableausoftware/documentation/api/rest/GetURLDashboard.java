package com.tableausoftware.documentation.api.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.tableausoftware.documentation.api.rest.bindings.TableauCredentialsType;
import com.tableausoftware.documentation.api.rest.bindings.ViewType;
import com.tableausoftware.documentation.api.rest.bindings.WorkbookType;
import com.tableausoftware.documentation.api.rest.util.RestApiUtils;

public class GetURLDashboard {
	private static final RestApiUtils s_restApiUtils = RestApiUtils.getInstance();
    private static Logger s_logger = Logger.getLogger(TestSignIn.class);
    private static Properties s_properties = new Properties();
    private static List<ViewType> views ;
    private static StringBuilder sbxml = new StringBuilder();
    
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
        
        sbxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n<!DOCTYPE struts PUBLIC\n")
		.append("\t\"-//Apache Software Foundation//DTD Struts Configuration 2.0//EN\"\n")
		.append("\t\"http://struts.apache.org/dtds/struts-2.0.dtd\">\n\n<struts>\n");
        
        TableauCredentialsType credential = s_restApiUtils.invokeSignIn(username, password, contentUrl);
		
        List<WorkbookType> currentUserWorkbooks = s_restApiUtils.invokeQueryWorkbooks(credential).getWorkbook();
        
        
        int i = 1;
        System.out.println("List Workbooks : ");
        for (WorkbookType workbook: currentUserWorkbooks) {
			System.out.println(i+". "+workbook.getContentUrl());
			sbxml.append("\t<!-- "+workbook.getContentUrl()+" -->\n");
			sbxml.append("\t<package name=\""+workbook.getContentUrl()+"\" extends=\"default\" namespace=\"/module/"+workbook.getContentUrl()+"\">\n");
			views = s_restApiUtils.invokeQueryViews(credential, credential.getSite().getId(), workbook.getId()).getView();
			int j = 1;
			for(ViewType view: views){
				System.out.println(j+". "+view.getContentUrl());
				sbxml.append("\t\t<action name=\""+view.getContentUrl().replace(workbook.getContentUrl()+"/sheets/", "")+"\" class=\"com.cbi.eis.controller.EISController\">\n");
				sbxml.append("\t\t\t<result name=\"success\" type=\"velocity\">\n");
				sbxml.append("\t\t\t\t/eis/view.vm\n");
				sbxml.append("\t\t\t</result>\n\t\t</action>\n");
				j++;
			}
			sbxml.append("\t</package>\n\n");
			i++;
        }
        sbxml.append("</struts>");
        (new File("C:/eis")).mkdirs();

    	File filexml = new File("C:/eis/struts-eis.xml");
    	if(filexml.exists()){
			System.out.println("File is exist");
		} else {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(filexml));
				writer.write(sbxml.toString());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
