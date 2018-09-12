package com.zushou365.houseimageupload.controller;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.zushou365.houseimageupload.util.DateHelper;
import com.zushou365.jspsmart.upload.TastySmartUpload;





public class ImgDealController extends AbstractController {
	
	/**
	 * 图片压缩后上传到服务器
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//获取上传文件名
	    String fileName = request.getHeader("fileName");
	    request.getParameter("imageServer");
	    request.getParameter("field");
	    String type = request.getParameter("type");
	   
	    //上传到服务器的路径
	    String imgsAddress = "/data/web/huzhiyi/www/house_imgs/";
	    //本地测试
	    //String imgsAddress = "D:/apache-tomcat-6.0.30/webapps/ROOT/house_imgs/";
			
			try  {
				InputStream in = null;
				OutputStream out = null;
	            try{  
	            	String appFolder = "tmp";  
	            	if(type!=null&&type.length()>0){
		            	if(type.endsWith("1")){
		            		appFolder="sn_pics/";
		            	}else if(type.endsWith("2")){
		            		appFolder="fx_pics/";
		            	}else if(type.endsWith("3")){
		            		appFolder="xq_pics/";
		            	}
	            	}
	            	 if(appFolder!=null && "tmp".equals(appFolder)){
	     		    	appFolder="credit_pics/"; 
	     		    }
	            	
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
	            	in = new BufferedInputStream(request.getInputStream(), 2);
	            	
	            	File dst=new File(afolder+imageId+"_b.jpg");
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
	                File fo = new File(afolder+imageId+"_m.jpg"); //将要转换出的小图文件
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
	                ImageIO.write(bid, "jpg", fo);
	                
	                
	               /****************生成固定大小的图片************************/
	                Image srcImg = ImageIO.read(dst);  
	                BufferedImage buffImg = null;  
	                buffImg = new BufferedImage(99, 99, BufferedImage.TYPE_INT_RGB);  
	                buffImg.getGraphics().drawImage(  
	                        srcImg.getScaledInstance(99, 99, Image.SCALE_SMOOTH), 0,  
	                        0, null);  
	                ImageIO.write(buffImg, "jpg", new File(afolder+imageId+"_s.jpg")); 
	              
	                response.getWriter().print(type+appFolder+a+"/"+b+"/"+imageId); 
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
			return null;
		}

	 }
	



