package com.zushou365.houseimageupload.cfg;


import java.util.Properties;

import com.zushou365.houseimageupload.dbmanager.DBRunner;

import com.zushou365.jspsmart.upload.TastySmartUpload;

/**
 * 
 * @author Seyo
 *
 */
public class ImageConfiguration {
	
	public static String applicationPath ="";
	
	public static Properties configProperties =null;
	
	public static String configFilePath="/house.properties";
	
	public static String imageHost="";
	
	public static String siteLogosHost="";
	
	public static String logFilePath="";
	
	public static int dbMonitorIntervalSeconds=0;
	
	public static int freshTimeNum = 0 ;
	
	public static int schedulerPreMinutes= 30;
	
	static{
		
		try {
			configProperties = ConfigHelper.getConfigProperties(configFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		applicationPath = ConfigHelper.locateApplicationPath();
		TastySmartUpload.ImageAppPath =configProperties.getProperty("imageFolder", "");
		imageHost = configProperties.getProperty("imageHost", "");	
		siteLogosHost = configProperties.getProperty("siteLogosHost", "");
		DBRunner.imagePath =configProperties.getProperty("imageModelFolder", "");
		DBRunner.imageUrl =configProperties.getProperty("imageModelHost", "");
		logFilePath= applicationPath+"log.txt";
		dbMonitorIntervalSeconds = Integer.valueOf(configProperties.getProperty("dbMonitorIntervalSeconds", "300"));
		freshTimeNum = Integer.valueOf(configProperties.getProperty("freshTimeNum", "10"));
		System.out.println("ImagePath:"+TastySmartUpload.ImageAppPath);
	}
	
	public  static void init(){};
	
	public ImageConfiguration() {
		
	}

}
