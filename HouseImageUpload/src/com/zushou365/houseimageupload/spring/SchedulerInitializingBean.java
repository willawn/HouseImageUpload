package com.zushou365.houseimageupload.spring;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.zushou365.houseimageupload.cfg.ImageConfiguration;
import com.zushou365.houseimageupload.dbmanager.DBConnectionManager;
public class SchedulerInitializingBean implements FactoryBean,
InitializingBean, DisposableBean, BeanNameAware {
	
	public Object getObject() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Class getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

	public void afterPropertiesSet() throws Exception {
	  System.out.println("启动加载....");
      ImageConfiguration.init(); 
      
	 
	}

	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setBeanName(String arg0) {
		// TODO Auto-generated method stub
		
	}
	


	
	
}
