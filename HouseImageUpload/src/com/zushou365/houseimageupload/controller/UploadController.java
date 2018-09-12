package com.zushou365.houseimageupload.controller;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.zushou365.houseimageupload.cfg.ImageConfiguration;
import com.zushou365.houseimageupload.util.DateHelper;

public class UploadController extends AbstractController
{
  private static final long serialVersionUID = 1L;

  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    System.out.println("单张图片上传测试！！！");
    String type = request.getParameter("type");

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(2097152);
    myProgressListener getBarListener = new myProgressListener(request);
    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setProgressListener(getBarListener);
    try {
      List formList = upload.parseRequest(request);
      Iterator formItem = formList.iterator();

      while (formItem.hasNext()) {
        FileItem item = (FileItem)formItem.next();
        if (item.getSize() > 2048000L) {
          response.getWriter().print(0);
          return null;
        }

        if (item.isFormField()) {
          System.out.println("Field Name:" + item.getFieldName());
        }
        else {
          String fileName = item.getName().substring(item.getName().lastIndexOf("\\") + 1);

          //上传到图片服务器
          String imgsAddress = ImageConfiguration.imageHost;
        
          String appFolder = "tmp";
          if ((type != null) && (type.length() > 0)) {
            if (type.endsWith("1"))
              appFolder = "sn_pics/";
            else if (type.endsWith("2"))
              appFolder = "fx_pics/";
            else if (type.endsWith("3")) {
              appFolder = "xq_pics/";
            }
          }
          if ((appFolder != null) && ("tmp".equals(appFolder))) {
            appFolder = "credit_pics/";
          }

          String directory = imgsAddress + appFolder;
          Random r = new Random();
          int ti = r.nextInt();
          ti = Math.abs(ti);
          String c = String.valueOf(ti);

          Date t = new Date();
      	  String a = DateHelper.dateToString(t,"yyyyMMdd");
    	  String b = DateHelper.dateToString(t,"HHmm");
    	  String d = DateHelper.dateToString(t,"HHmmss");

          String afolder = radomPath(directory, a, b);
          String imageId = d + c;
          File file = new File(afolder + imageId + "_b.jpg");

          OutputStream out = item.getOutputStream();
          InputStream in = item.getInputStream();
          request.getSession().setAttribute("outPutStream", out);
          request.getSession().setAttribute("inPutStream", in);
          item.write(file);

          Image image = ImageIO.read(file);

          int k = image.getWidth(null);
          int g = image.getHeight(null);

          if (((k < 300) || (g < 300) || (k < 300) || (g < 300)) && ((type.endsWith("1")) || (type.endsWith("2"))))
          {
            response.getWriter().print(-2);
            return null;
          }

          if (((k < 300) || (g < 400)) && (type.endsWith("3"))) {
            response.getWriter().print(-1);
            return null;
          }

          double scale = k / g;

          int w = image.getWidth(null) * k / g;
          int h = image.getHeight(null) * k / g;

          BufferedImage tmp = new BufferedImage(w, h, 1);
          Graphics2D g2 = tmp.createGraphics();
          g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
          g2.drawImage(image, 0, 0, w, h, null);
          g2.dispose();
          FileOutputStream newimage = new FileOutputStream(afolder + imageId + "_m.jpg");
          ImageIO.write(tmp, "JPEG", newimage);

          Image srcImg = ImageIO.read(file);
          BufferedImage buffImg = null;
          buffImg = new BufferedImage(99, 99, 1);
          buffImg.getGraphics().drawImage(
            srcImg.getScaledInstance(99, 99, 4), 0, 
            0, null);
          ImageIO.write(buffImg, "JPG", new File(afolder + imageId + "_s.jpg"));

          response.getWriter().print(type + appFolder + a + "/" + b + "/" + imageId);
        }
      }
    }
    catch (FileUploadException e2)
    {
      e2.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String radomPath(String AppFolder, String a, String b)
  {
    String filePath = AppFolder + "/" + a + "/" + b + "/";

    checkPath(AppFolder, a, b);

    return filePath;
  }

  public void checkPath(String AppFolder, String a, String b) {
    String ImageAppPath = "";
    String curPath = ImageAppPath + AppFolder + "/" + a + "/" + b;
    File f = new File(curPath);
    f.mkdirs();
  }
}