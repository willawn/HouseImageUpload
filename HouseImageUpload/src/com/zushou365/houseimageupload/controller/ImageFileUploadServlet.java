package com.zushou365.houseimageupload.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.zushou365.houseimageupload.actions.TastyConfigHelper;
import com.zushou365.houseimageupload.cfg.ImageConfiguration;
import com.zushou365.houseimageupload.util.DateHelper;
import com.zushou365.houseimageupload.util.ImgMD5;
import com.zushou365.jspsmart.upload.SmartUploadException;
import com.zushou365.jspsmart.upload.TastySmartUpload;

/**
 * 上传房源图片接口Api（室内图）
 * @author sana
 * @data 2012-06-27
 * 
 */
public class ImageFileUploadServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public ImageFileUploadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
          doPost(request, response);
	}

	/**
	 * 上传图片处理
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 // request.setCharacterEncoding("UTF-8");
		  response.setContentType("text/html;charset=UTF-8");
		//System.out.println("开始接收文件");
		int imageServer = 1;   // 上传图片服务器
		StringBuffer result = new StringBuffer();
		Integer userId = 0;
		PrintWriter outWriter = response.getWriter();
		if(request.getParameter("userId")!=null && !"".equals(request.getParameter("userId"))){
			userId=Integer.valueOf(request.getParameter("userId"));
		}else {
			result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"用户编码为空！！\\\",\\\"uploadcommpicfile\\\":\\\"无\\\"}\"}");
			outWriter.write(result.toString());
			return;
		}
		
		try{
			imageServer = Integer.valueOf(request.getParameter("imageServer"));
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
			
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		//upload.setFileSizeMax(1000000);
		if (!upload.isMultipartContent(request)) {
			result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"非附件上传调用，接口调用格式错误！\\\",\\\"uploadcommpicfile\\\":\\\"无\\\"}\"}");//,\"uploadcommpicfiletitle\":""
			System.out.println("非附件上传调用，接口调用格式错误！");
			outWriter.write(result.toString());
			return;
		}
		List fileItems;
		try {
			fileItems = upload.parseRequest(request);
			Iterator itr = fileItems.iterator();
			while (itr.hasNext()) {
				FileItem fi = (FileItem) itr.next();
            	
            	TastySmartUpload ta = new TastySmartUpload();
            	// afolder = ta.radomPath(directory,a,b);
            	
				if (!fi.isFormField()) {
					InputStream inputeStream = fi.getInputStream();
					String fiName = fi.getName();
					int startData = 1;
					int fiSize =(int) fi.getSize();
					byte[] fiByte = fi.get();
					//System.out.println("\nNAME: " + fi.getName());
					//System.out.println("SIZE: " + fi.getSize());
					try {
						/**************************************/
						//获取上传文件名
						String fileName =  fi.getName();
						try  {
							InputStream in = null;
							OutputStream out = null;
				            try{  
				            	//System.err.println(fileName+"====");
				            	int isRight = fileName.indexOf(".");
				            	if(isRight==-1){
				            		// 上传图片是否正确
				            		result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"上传图片错误原因\\\",\\\"uploadcommpicfile\\\":\\\"\\\"}\"}");//,\"uploadcommpicfiletitle\":""
					    			outWriter.write(result.toString());
				            		return;
				            	}
				            	String format = fileName.substring(isRight+1);
				            	if(!format.equals("jpeg")&&!format.equals("jpg")&&!format.equals("png")&&!format.equals("gif")&&!format.equals("bmp")&&!format.equals("JPEG")&&!format.equals("JPG")
				            			&&!format.equals("GIF")&&!format.equals("BMP")&&!format.equals("PNG")){
				            		// 上传图片格式错误
				            		result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"上传图片格式错误原因\\\",\\\"uploadcommpicfile\\\":\\\"\\\"}\"}");//,\"uploadcommpicfiletitle\":""
					    			outWriter.write(result.toString());
				            		return;
				            	}
				            	/**上传图片**/
				            	String picUrl = saveImg(inputeStream,userId.toString(),fiSize,imageServer);
				            	result.append("{\"zushou365\":\"{\\\"isOk\\\":1,\\\"errorMsg\\\":\\\"上传成功\\\",\\\"uploadcommpicfile\\\":\\\""+picUrl+"\\\"}\"}");//,\"uploadcommpicfiletitle\":""
				    			outWriter.write(result.toString());
				        	} catch (Exception e) {
				    			e.printStackTrace(); 
				    		}finally{
				    			outWriter.close(); 	
				    		}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				   // TODO Auto-generated catch block
					e.printStackTrace();
					result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"系统错误原因\\\",\\\"uploadcommpicfile\\\":\\\"\\\"}\"}");//,\"uploadcommpicfiletitle\":""
					outWriter.write(result.toString());
		    }
						
		 } else {
			//  System.out.println("Field =" + fi.getFieldName());
		 }
		}

		} catch (FileUploadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			result.append("{\"zushou365\":\"{\\\"isOk\\\":0,\\\"errorMsg\\\":\\\"系统错误原因\\\",\\\"uploadcommpicfile\\\":\\\"\\\"}\"}");//,\"uploadcommpicfiletitle\":""
			outWriter.write(result.toString());
		}
		//System.err.println("完成");
	}
    
	
  /**************图片处理操****************/
  public String saveImg(InputStream inputeStream,String userId,int m_size,int imageServer){
	    String type = "1";  // 室内图
	    String picUrl = "";
//		if(request.getParameter("userId")!=null && !"".equals(request.getParameter("userId"))){
//			userId=Integer.valueOf(request.getParameter("userId"));
//		}else {
//			return null; 
//		}
			try  {
				InputStream in = inputeStream;
				OutputStream out = null;
	            try{  
	            	String appFolder="sn_pics/";
	            	 if(appFolder!=null && "tmp".equals(appFolder)){
	     		    	appFolder="credit_pics/"; 
	     		    }
	            //  String imgsAddress = "D:/installDevTools/tomcat6.0.14/webapps/ROOT/house_imgs/api/";
	              String imgsAddress = TastySmartUpload.ImageAppPath;
	              String directory = imgsAddress+appFolder;    
	            	java.util.Random r=new java.util.Random(); 
	            	int ti = r.nextInt();
	            	ti = Math.abs(ti);
	            	String c =String.valueOf(ti);

	            	Date t = new Date();
	            	String a = DateHelper.dateToString(t,"yyyyMMdd");
	            	String b = DateHelper.dateToString(t,"HHmm");
	            	String d = DateHelper.dateToString(t,"HHmmss");
	            	
	            	TastySmartUpload ta = new TastySmartUpload();
	            	String afolder = ta.radomPath(directory,a,b);
	            	String imageId = d+c;
	            	
	            	
	    		    
	    		    /**************************flash压缩后的图片**************************************/
	            	//in = new BufferedInputStream(request.getInputStream(), 2);
	            	
	            	File dst=new File(afolder+imageId+"_b.jpeg");
	            	File folder=new File(afolder);
	            	if(!folder.exists()){
	            		folder.mkdirs();
	            	}
	            	 out = new BufferedOutputStream(new FileOutputStream(dst), 2);
	                byte [] buffer = new byte [2]; 
	                int bytesRead = 0;
	                while ((bytesRead = in.read(buffer, 0, 2)) != -1)  {
	                   out.write(buffer, 0, bytesRead);
	                }
	              
	                /**************************生成缩略图**************************************/
	                File fo = new File(afolder+imageId+"_m.jpeg"); //将要转换出的小图文件
	                AffineTransform transform = new AffineTransform();
	                BufferedImage bis = ImageIO.read(dst);

	                int w = bis.getWidth();
	                int h = bis.getHeight();
	                double scale = (double)w/h;
	                int nw = 350;
	                int nh = (nw * h) / w;
	                if(nh>350) {
	                    nh = 350;
	                    nw = (nh * w) / h;
	                }

	                double sx = (double)nw / w;
	                double sy = (double)nh / h;

	                transform.setToScale(sx,sy);

	                AffineTransformOp ato = new AffineTransformOp(transform, null);
	                BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
	                ato.filter(bis,bid);
	                ImageIO.write(bid, "JPEG", fo);
	                
	                
	               /****************生成固定大小的图片************************/
	                Image srcImg = ImageIO.read(dst);  
	                BufferedImage buffImg = null;  
	                buffImg = new BufferedImage(99, 99, BufferedImage.TYPE_INT_RGB);  
	                buffImg.getGraphics().drawImage(  
	                        srcImg.getScaledInstance(99, 99, Image.SCALE_SMOOTH), 0,  
	                        0, null);  
	                ImageIO.write(buffImg, "JPEG", new File(afolder+imageId+"_s.jpeg")); 
	              
	                /**********************************************/
	                byte[] bytes = new byte[m_size];
	     	       // inputeStream.read(bytes);
	                String imgMd5 = imgMd5 = ImgMD5.MD5(bytes);
	     	        //System.out.println(imgMd5);
	     	        String imgExistStr = TastyConfigHelper.checkImgMd5Exists(imgMd5,imageServer);
	     	        if(imgExistStr.length()>0){
	     	        	String[] strSplit = imgExistStr.split(",");
	     	        	String picId = strSplit[0];
	     	        	String pic = strSplit[1];
	     	        	String share = strSplit[2];
	     	        	String orginUserId = strSplit[3];
	     	        	
	     	        	pic +="#"+imgMd5;
	     	        	
	     	        	if(share.equals("1")){
	     	        		//return pic;
	     	        	}else{
	     	        		if(userId.equals(orginUserId)){
	     	        		//	return pic;
	     	        		}else{
	     	        			TastyConfigHelper.recordHousePicsUser(userId,picId);
	     	        			//return pic;
	     	        		}
	     	        	}
	     	        	
	     	        }
	     	         picUrl= appFolder+a+"/"+b+"/"+imageId+"_s.jpeg";
	     	       // System.err.println(type+appFolder+a+"/"+b+"/"+imageId);
	              //  picUrl +="#"+ imgMd5;
	            }finally {
	            	if(null != in)  {
	            		in.close();
	            	}if(null != out)  {
	            		out.close();
	            	} 
	            } 
			} catch (Exception e)  {
				e.printStackTrace();
	        } finally{
	        }
			return picUrl;
		}
	
	
	/***************************************************/

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
