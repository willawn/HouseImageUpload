package com.zushou365.jspsmart.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TastyFile {
	public static String saveFile(HttpServletRequest request,HttpServletResponse response,String AppFolder, String fileName) {
		TastySmartUpload mySmartUpload = new TastySmartUpload();
		try {
		
			mySmartUpload.initialize(request.getSession().getServletContext(),
					request, response);
			
			mySmartUpload.setAllowedFilesList("tfp,TFP");
			// Upload
			mySmartUpload.upload();

			mySmartUpload.saveFile(AppFolder,fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
