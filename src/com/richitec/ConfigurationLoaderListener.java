package com.richitec;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class ConfigurationLoaderListener extends ContextLoaderListener {
	
	private static final String configurationFile = "/WEB-INF/config/ccs.properties";
	
	public ConfigurationLoaderListener(){
		super();
	}
	
	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);

		ServletContext context = sce.getServletContext();

		// set donkey webcall application context
		ApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);

		// set donkey webcall configuration file
		InputStream webcallConfInputStream = context
				.getResourceAsStream(configurationFile);

		try {
			// init donkey webcall configuration
			Configuration.initialize(webcallConfInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}	

}
