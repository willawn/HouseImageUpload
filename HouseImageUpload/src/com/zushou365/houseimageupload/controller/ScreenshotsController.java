package com.zushou365.houseimageupload.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.zushou365.houseimageupload.util.Utils;

public class ScreenshotsController extends AbstractController  {

	/**
	 * 图片截图后上传到服务器
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int left = Integer.valueOf(request.getParameter("left"));
		int top = Integer.valueOf(request.getParameter("top"));
		int width = Integer.valueOf(request.getParameter("width"));
		int height = Integer.valueOf(request.getParameter("height"));
		
		//获得文件的存放地址,源文件
		String shotsfile = request.getParameter("shotsfile");
		System.out.println("图片截图后上传");
		//获得源文件的后缀名
		String extname = Utils.getExtension(shotsfile).toLowerCase();
		
		String a = shotsfile.substring(shotsfile.indexOf("credit_pics"), shotsfile.lastIndexOf("/")+1);
		String imgid = shotsfile.substring(shotsfile.lastIndexOf("/"), shotsfile.lastIndexOf("_m"));
		//截图后存放图片的地址,目标图路径
		String dest = "/data/web/huzhiyi/www/house_imgs/"+a+imgid+"_p."+extname;
		
		File fileDest = new File(dest);
		if (!fileDest.getParentFile().exists())
			fileDest.getParentFile().mkdirs(); 
		//获得文件的后缀名
		String ext = Utils.getExtension(dest).toLowerCase();
		
		BufferedImage bi = (BufferedImage) ImageIO.read(new File("D:/apache-tomcat-6.0.30/webapps/ROOT/house_imgs/"+a+imgid+"_s."+extname));
		height = Math.min(height, bi.getHeight());
		width = Math.min(width, bi.getWidth());
		if (height <= 0)
			height = bi.getHeight(); 
		if (width <= 0)
			width = bi.getWidth();
		top = Math.min(Math.max(0, top), bi.getHeight() - height);
		left = Math.min(Math.max(0, left), bi.getWidth() - width);

		BufferedImage bi_cropper = bi.getSubimage(left, top, width, height);
	
		ImageIO.write(bi_cropper, ext.equals("png") ? "png" : "jpeg",fileDest);
		
		return null;
	}


}
