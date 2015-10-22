package com.vp.adpautomation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class Configuration 
{
	InputStream inputStream;
	
	private String projectName;
	private String hoursPerDay;
	private String customer;
	private String earningsCode;
	private String username;
	private String password;
 
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getHoursPerDay() {
		return hoursPerDay;
	}

	public String getCustomer() {
		return customer;
	}

	public String getEarningsCode() {
		return earningsCode;
	}

	public Configuration()
			throws IOException 
	{
		Properties prop = new Properties();
		String propFileName = "./config.properties";

		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		projectName = prop.getProperty("project.name");
		earningsCode = prop.getProperty("earnings.code");
		customer = prop.getProperty("customer");
		hoursPerDay = prop.getProperty("hours");
		username = prop.getProperty("username");
		password = prop.getProperty("password");
		
	}


}
