package com.zushou365.houseimageupload.controller;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.zushou365.houseimageupload.util.DateHelper;

import com.zushou365.jspsmart.upload.TastyImage;


public class ImageFileUploadController extends AbstractController {


	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//System.out.println("系统提示： 图片上传开始... ");
		PrintWriter out = response.getWriter(); 
		try {
			
			int userId =0; 
			String appFolder = "tmp";
			int imageServer = 1;
			try{
				imageServer = Integer.valueOf(request.getParameter("imageServer"));
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
			if(request.getParameter("userId")!=null && !"".equals(request.getParameter("userId"))){
				userId=Integer.valueOf(request.getParameter("userId"));
			}else {
				return null; 
			}
			if(request.getParameter("fathType") != null && !"".equals(request.getParameter("fathType"))){
				appFolder = request.getParameter("fathType"); 
			}
			String field =request.getParameter("field");
		    appFolder = "uploadcommodelfile".equals(field) ? "fx_pics" :appFolder;  
		    appFolder = "uploadcommpicfile".equals(field) ? "sn_pics" :appFolder;  
		    appFolder = "uploadpropertypicfile".equals(field) ? "xq_pics" :appFolder; 
		   
		    if(appFolder!=null && "tmp".equals(appFolder)){
		    	appFolder="credit_pics"; 
		    }
			String filePath = TastyImage.saveImage(request,response,appFolder,field,String.valueOf(userId),imageServer);
			System.out.println(DateHelper.dateToString(new Date())+" :"+filePath);
			if("".equals(filePath))return null; 
			String[] filePaths = filePath.split(",");
			String imageUrl_ = filePaths[1];
			String[] imgSplit = imageUrl_.split("#");
			String imageUrl = imageUrl_.split("#")[0];
			String imageMd5="";
			if(imgSplit.length>1)
				imageMd5 = imageUrl_.split("#")[1];
			String fieldPanel = request.getParameter("fieldPanel");

			if(fieldPanel!=null&&fieldPanel.length()>0){
				StringBuilder sb = new StringBuilder();
				sb.append(filePaths[0]+"|");
				sb.append(imageUrl+"|");
				sb.append(field+"|");
				sb.append(fieldPanel+"|");
				sb.append(imageMd5);
				out.write(sb.toString()); 

			}else{

				String rst = (new StringBuilder("{imgid:\"")).append(filePaths[0]).append("\",img:\"").append(imageUrl).append("\",field:\"").append(field).append("\",imgmd:\"").append(imageMd5).append("\"};").toString();
	            String backurl = request.getParameter("backurl");
	            if(backurl == null || backurl.length() == 0)
	                backurl = "http://s1.zushou365.com/";
	            if(!backurl.endsWith("/"))
	                backurl = (new StringBuilder(String.valueOf(backurl))).append("/").toString();
	            response.sendRedirect((new StringBuilder(String.valueOf(backurl))).append("AjaxImageBack.jsp?q=").append(rst).toString());
	           
	            response.getWriter().close();
	            return null;
			}
		} catch (Exception e) {
			System.out.println(DateHelper.dateToString(new Date())+e.getMessage());
			e.printStackTrace(); 
		}finally{
			out.close(); 	
		}
		return null;
	}

}
