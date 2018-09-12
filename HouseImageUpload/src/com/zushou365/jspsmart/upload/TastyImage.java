package com.zushou365.jspsmart.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



public class TastyImage {
	
	private static Logger logger = Logger.getLogger(TastyImage.class);    
	public static String saveImage(HttpServletRequest request,
										HttpServletResponse response,String AppFolder,String field,String userId,int imageServer){
		TastySmartUpload mySmartUpload = new TastySmartUpload();
		try {
			//int count=0;
			// Initialization
			mySmartUpload.initialize(request.getSession().getServletContext(),request,response);
			mySmartUpload.setAllowedFilesList("jpeg,jpg,png,gif,bmp,JPEG,JPG,GIF,BMP,PNG");
			// Upload
			mySmartUpload.setMaxFileSize(5*1024*1024) ;//单个文件5兆
			mySmartUpload.setTotalMaxFileSize(50*1024*1024);//总50兆
			mySmartUpload.upload();
			String slab = request.getParameter("slab")==null || 
			  "".equals(request.getParameter("slab"))?"2":request.getParameter("slab");
			String filePath = mySmartUpload.saveImg(AppFolder,field,userId,slab,imageServer);
			return filePath;

		} catch (Exception e){
			
			logger.error("saveImage err", e);
			if(e instanceof SecurityException){
				SecurityException e1 = (SecurityException)e;
				String msg = e1.getMessage();
				if(msg.endsWith("(1010).")){
					return ",filetype_error";
				}
			}
			
		}
		return "";
	}
	
}
