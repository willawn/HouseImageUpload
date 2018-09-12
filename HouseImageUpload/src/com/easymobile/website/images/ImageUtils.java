package com.easymobile.website.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;   

import javax.imageio.ImageIO;


 
import magick.CompositeOperator;
import magick.CompressionType;
import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import magick.PixelPacket;
import magick.PreviewType;  
public class ImageUtils {
	static{
        //不能漏掉这个，不然jmagick.jar的路径找不到
        System.setProperty("jmagick.systemclassloader","no");
    }   
 
	public static Dimension getDimension(File f){
		if(!f.exists())
			return null;
		Dimension d=null;
		try{
			BufferedImage src = ImageIO.read(f); // 读入文件 
		    int width = src.getWidth(); // 得到源图宽 
		    int height = src.getHeight(); // 得到源图长  
		    d =new Dimension(width,height);
		}catch(Exception e){
			System.out.println(f.getAbsolutePath());
		}
		return d;
	}
    /**
     * 压缩图片
     * @param filePath 源文件路径
     * @param toPath   缩略图路径
     * 
     */
	/**
	 * 创建缩略图
     * @param filePath 源文件路径
     * @param toPath   缩略图路径
     * @param scaleToWidth 缩略图宽度
     * @param scaleToHeigth 缩略图高度
     * 如果同时指定了缩略图的高度和宽度，则不按比例压缩
     * 指定scaleToWidth或者scaleToHeigth，另外一个设置为0，则自动按照比例缩放
	 */
    public static void createThumbnail(String filePath, String toPath,int scaleToWidth,int scaleToHeigth) {
        ImageInfo info = null;
        MagickImage image = null;
        Dimension imageDim = null;
        MagickImage scaled = null;
        try{
        	File check=new File(filePath);
        	File f=new File(toPath);
        	try{
        	if(!check.exists() || !check.canRead() || !check.isFile())
        		return;
        	if(!f.getParentFile().exists())
        		f.getParentFile().mkdirs();
//        	if(f.exists())
//        		f.delete();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	//filePath = java.net.URLEncoder.encode(filePath,"UTF-8");
            info = new ImageInfo(filePath);
            info.setQuality(85);
            image = new MagickImage(info);
            image.profileImage("*", null);
            try{
            	imageDim =  image.getDimension();
            }catch(Exception ee){
            	
            	imageDim = getDimension(new File(filePath));
            }
            
            int width = imageDim.width;
            int height = imageDim.height;
            if(scaleToWidth>0 && scaleToHeigth>0)
            {
            	
            }else if(scaleToWidth>0){
            	if(scaleToWidth<width)
            	{
            		scaleToHeigth = scaleToWidth* height / width;
            	}
            	else
            	{
            		
            		image.setFileName(toPath);
            		image.writeImage(info);
            		//copyFile(check,f);
            		return;
            		//scaleToHeigth =height;
            		//scaleToWidth =width;
            	}
            }else if(scaleToHeigth>0){
            	scaleToWidth = scaleToHeigth*width/ height ;
            }
            else
            {
            	scaleToWidth = 120;
            	scaleToHeigth = scaleToWidth* height / width;
            }
           
            scaled = image.scaleImage(scaleToWidth, scaleToHeigth);//小图片文件的大小.
            
            //scaled.setImageFormat("JPG");
            scaled.setFileName(toPath);
            scaled.setCompression(magick.CompressionType.JPEGCompression);
           // scaled.setCompression(magick.CompressionType.ZipCompression);
            scaled.writeImage(info);
        }catch(Exception e){
        	System.out.println( filePath +"\n" +toPath);
        	e.printStackTrace();
        }finally{
        	try{
        	info=null;	
       		if(image!=null)
        			image.destroyImages();
        	}catch(Exception e){
        		//e.printStackTrace();
        	}
        	try{
            if(scaled != null){
                scaled.destroyImages();
            }
        	}catch(Exception e){
        		//e.printStackTrace();
        	}
        }
    }   
    public static void initLogoImg(String filePath, String toImg, String logoPath,boolean onleft)  {
    	initLogoImg( filePath,  toImg,  logoPath,660, onleft);
    }
    public static void initLogoImg(String filePath, String toImg, String logoPath,int maxWidth,boolean onleft) {
    	
    	 ImageInfo info = null;
         MagickImage fImage = null;
         MagickImage sImage = null;
         MagickImage fLogo = null;
         
         MagickImage sLogo = null;
         Dimension imageDim = null;
         // Dimension logoDim = null;
         try {
        	 
	        	try{
	        	File f=new File(toImg);
	         	if(!f.getParentFile().exists())
	         		f.getParentFile().mkdirs();
//	         	if(f.exists())
//	        		f.delete();
	         	}catch(Exception e){
	         		e.printStackTrace();
	         	}
	         info = new ImageInfo();	
             fImage = new MagickImage(new ImageInfo(filePath));
             
           
             fImage.profileImage("*", null);
             //imageDim = fImage.getDimension();
             try{
            	 imageDim =  fImage.getDimension();	
            	 
                 }catch(Exception ee){
                 imageDim = getDimension(new File(filePath));	
                 }
                 
             int width = imageDim.width;
             int height = imageDim.height;
             boolean scaled=false;
             if (maxWidth>0 && width > maxWidth) {
                 height = maxWidth * height / width;
                 width = maxWidth;
                 scaled =true;
             }
             if(width<200)
             	{
            	 
             	fImage.setFileName(toImg);
             	fImage.writeImage(info);
             	return;
             	}
             if(scaled)
            	 sImage = fImage.scaleImage(width, height);   
             else
            	 sImage=fImage;
             fLogo = new MagickImage(new ImageInfo(logoPath));
             Dimension logoDim = fLogo.getDimension();
             int lw = logoDim.width;
             int lh = logoDim.height;
             int slw=lw;
             int slh=lh;
             double ra =lw*1.0/width;
             if( (ra>0.35 || ra<0.25)){ //需要对logo进行缩放//logoPath.endsWith("watermark.png") &&
            	 slw =(int)(width*0.30303);
            	 slh =lh*slw/lw;
            	 sLogo =fLogo.scaleImage(slw, slh);
             }
             
             //System.out.println("lw="+lw+";slw="+slw);
             
             if(onleft)
            	 {
            	 int x =(width/2)-100;
            	 int y =(height/2)-30;
            	 if(sLogo==null)
            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, fLogo,  x, y);
            	 else
            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, sLogo,  x, y);
            	 
//            	 if(sLogo==null)
//            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, fLogo,  10, height-lh-10);
//            	 else
//            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, sLogo,  10, height-lh-10);
            	 }
             else
            	 {
            	 if(sLogo==null)	
            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, fLogo,  width-lw-20, height-lh-10);
            	 else
            		 sImage.compositeImage(CompositeOperator.AtopCompositeOp, sLogo,  width-slw-20, height-slh-10);
            	 
            	 }
             sImage.setFileName(toImg);
             sImage.writeImage(info);
         } 
         catch(Exception e){
        	 System.out.println(filePath+"\n" + logoPath);
        	 e.printStackTrace();
         }
         finally {
	        	 try{
	             	info=null;	
	            		if(fImage!=null)
	            			fImage.destroyImages();
	            	
	             	}catch(Exception e){
	             		
	             		e.printStackTrace();
	             	}
             	
             	 try{
                  
                 		if(sImage!=null)
                 			sImage.destroyImages();
                 		
                  	}catch(Exception e){
                  		
                  		e.printStackTrace();
                  	}
                  	
              	 try{
                 		if(fLogo!=null)
                 			fLogo.destroyImages();
                  	}catch(Exception e){
                  		
                  		e.printStackTrace();
                  	}
         }	
    }
    /**
     * 水印(图片logo)
     * @param filePath  源文件路径
     * @param toImg     修改图路径
     * @param logoPath  logo图路径
     * @throws MagickException
     */

    /**
     * 水印(图片logo)
     * @param filePath  源文件路径
     * @param toImg     修改图路径
     * @param logoPath  logo图路径
     * @throws MagickException
     */
    public static void initLogoImg(String filePath, String toImg, String logoPath) throws MagickException {
    	initLogoImg( filePath,  toImg,  logoPath,false);
    	/*
    	ImageInfo info = new ImageInfo();
        MagickImage fImage = null;
        MagickImage sImage = null;
        MagickImage fLogo = null;
     //   MagickImage sLogo = null;
        Dimension imageDim = null;
      //  Dimension logoDim = null;
        try {
        	try{
	        	File f=new File(toImg);
	         	if(!f.getParentFile().exists())
	         		f.getParentFile().mkdirs();
	         	}catch(Exception e){
	         		e.printStackTrace();
	         	}
	         	
	         	
            fImage = new MagickImage(new ImageInfo(filePath));
            imageDim = fImage.getDimension();
            int width = imageDim.width;
            int height = imageDim.height;
            if (width > 660) {
                height = 660 * height / width;
                width = 660;
            }
            if(width<=250)
            	{
            	fImage.setFileName(toImg);
            	fImage.writeImage(info);
            	return;
            	}
            
            sImage = fImage.scaleImage(width, height);   
 
            fLogo = new MagickImage(new ImageInfo(logoPath));
//            logoDim = fLogo.getDimension();
//            int lw = width / 8;
//            int lh = logoDim.height * lw / logoDim.width;
//            sLogo = fLogo.scaleImage(lw, lh);   
//           
            sImage.compositeImage(CompositeOperator.AtopCompositeOp, fLogo,  width-210, height-110);
            sImage.setFileName(toImg);
            sImage.writeImage(info);
        } finally {
        	try{
             	info=null;	
            		if(fImage!=null)
            			fImage.destroyImages();
            	
             	}catch(Exception e){
             		
             		e.printStackTrace();
             	}
         	
         	 try{
              
             		if(sImage!=null)
             			sImage.destroyImages();
             		
              	}catch(Exception e){
              		
              		e.printStackTrace();
              	}
              	
          	 try{
             		if(fLogo!=null)
             			fLogo.destroyImages();
              	}catch(Exception e){
              		
              		e.printStackTrace();
              	}
        }*/
    }   
    /**
     * 水印(文字)
        * @param filePath 源文件路径
     * @param toImg    修改图路径
     * @param text     名字(文字内容自己随意)
     * @throws MagickException
     */
    public static void initTextToImg(String filePath, String toImg,  String text) throws MagickException{
            ImageInfo info = null;//
            MagickImage scaled =null;
            MagickImage aImage  =null;
            try{
            	info = new ImageInfo(filePath);
            if (filePath.toUpperCase().endsWith("JPG") || filePath.toUpperCase().endsWith("JPEG")) {
                info.setCompression(CompressionType.JPEGCompression); //压缩类别为JPEG格式
                info.setPreviewType(PreviewType.JPEGPreview); //预览格式为JPEG格式
                info.setQuality(95);
            }
            aImage = new MagickImage(info);
            Dimension imageDim = aImage.getDimension();
            int wideth = imageDim.width;
            int height = imageDim.height;
            if (wideth > 660) {
                height = 660 * height / wideth;
                wideth = 660;
            }
            int a = 0;
            int b = 0;
            String[] as = text.split("");
            for (String string : as) {
                if(string.matches("[\u4E00-\u9FA5]")){
                    a++;
                }
                if(string.matches("[a-zA-Z0-9]")){
                    b++;
                }
            }
            int tl = a*12 + b*6 + 300;
            scaled = aImage.scaleImage(wideth, height);
            if(wideth > tl && height > 5){
                DrawInfo aInfo = new DrawInfo(info);
                aInfo.setFill(PixelPacket.queryColorDatabase("white"));
                aInfo.setUnderColor(new PixelPacket(0,0,0,100));
                aInfo.setPointsize(12);
                //解决中文乱码问题,自己可以去随意定义个自己喜欢字体，我在这用的微软雅黑
                String fontPath = "C:/WINDOWS/Fonts/MSYH.TTF";
//              String fontPath = "/usr/maindata/MSYH.TTF";
                aInfo.setFont(fontPath);
                aInfo.setTextAntialias(true);
                aInfo.setOpacity(0);
                aInfo.setText("　" + text + "于　" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "　上传于图房网，版权归作者所有！");
                aInfo.setGeometry("+" + (wideth-tl) + "+" + (height-5));
                scaled.annotateImage(aInfo);
            }
            
            scaled.setFileName(toImg);
            scaled.writeImage(info);
            
            }catch(Exception e){
            	
            }finally{
            	try{
                 scaled.destroyImages();
            	}catch(Exception e){
            		
            	}
            	try{
                 aImage.destroyImages();
            	}catch(Exception e){
            		
            	}
            	
            }
           
    }   
 
    /**
     * 切图
     * @param imgPath 源图路径
     * @param toPath  修改图路径
     * @param w
     * @param h
     * @param x
     * @param y
     * @throws MagickException
     */
    public static void cutImg(String imgPath, String toPath, int w, int h, int x, int y) throws MagickException {
        ImageInfo infoS = null;
        MagickImage image = null;
        MagickImage cropped = null;
        Rectangle rect = null;
        try {
            infoS = new ImageInfo(imgPath);
            image = new MagickImage(infoS);
            rect = new Rectangle(x, y, w, h);
            cropped = image.cropImage(rect);
            cropped.setFileName(toPath);
            cropped.writeImage(infoS);   
 
        } finally {
            if (cropped != null) {
                cropped.destroyImages();
            }
            if (image != null) {
            	image.destroyImages();
            }
        }
    }
	public static void copyFile(File from,File to){
			
			try {
	            
	            File dir = to.getParentFile();
	            if (!dir.exists()) {
	                dir.mkdirs();
	            }
	            if(to.exists() && from.getAbsolutePath().equals(to.getAbsolutePath()))
	            	return;
	            	
	            if(to.exists())
	            	to.delete();
	           
	            to.createNewFile();
	            FileOutputStream out = new FileOutputStream(to);
	           
	            FileInputStream in = new FileInputStream(from);
	            
	            int len;
			    byte[] buffer=new byte[4096];
			    while((len=in.read(buffer))>=0) {
			      out.write(buffer,0,len);
			    }
	            in.close();
	            out.flush();
	            out.close();
	        } catch (Exception e) {
	        	System.out.println( from.getAbsolutePath()+ to.getAbsolutePath());
	            e.printStackTrace();
	        }
		}
	
	public static Boolean compareHW(String sourcepath,int comwidth,int comlength) throws MagickException{
		 	ImageInfo info = null;
	        MagickImage image = null;
	        Dimension imageDim = null;
	        try{
	        	File check=new File(sourcepath);
	        	try{
	        	if(!check.exists() || !check.canRead() || !check.isFile())
	        		return false;
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	            info = new ImageInfo(sourcepath);
	            image = new MagickImage(info);
	            imageDim = image.getDimension();
	            int width = imageDim.width;
	            int height = imageDim.height;
	            if(width>comwidth&&height>comlength)
	            	return true;
	            	return false;
	        }finally{
	             if (image != null) {
	             	image.destroyImages();
	             }
	        }
	}
	
	public static BufferedImage createTransparentGIF(int width,int height,int fontsize,int bold,String text,String color) {
		 
         IndexColorModel cm = createIndexColorModel();;  
         BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, cm);  
         Graphics2D g = im.createGraphics();  
         g.setColor(new Color(0,0,0,0)); //transparent  
         g.fillRect(0, 0, width, height);
         Color c=Color.BLACK;
         if(color!=null)
         {
        	 if(color.startsWith("#"))
        		 color=color.substring(1);
        	 if(color.length()==6){
        		 String red = color.substring(0,2);
        		 String green = color.substring(2,4);
        		 String blue = color.substring(4,6);
        		 int r =Integer.parseInt(red,16);
        		 int gr =Integer.parseInt(green,16);
        		 int b =Integer.parseInt(blue,16);
        		// System.out.println("r="+r+";g="+gr+";b="+b);
        		 c =new Color(r,gr,b);
        	 }
        	 else if(color.length()==3){
        		 String red = color.substring(0,1);
        		 String green = color.substring(1,2);
        		 String blue = color.substring(2,3);
        		 int r =Integer.parseInt(red+red,16);
        		 int gr =Integer.parseInt(green+green,16);
        		 int b =Integer.parseInt(blue+blue,16);
        		// System.out.println("r="+r+";g="+gr+";b="+b);
        		 c =new Color(r,gr,b);
        	 }
         }
        // GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
         Font f;
        	 
        	
         if(bold==1)
        	 f =new Font("Monospaced",Font.BOLD, fontsize);
         else
        	 f= new Font("Monospaced",0, fontsize);
         g.setFont(f);
         //g.setColor(getRandColor(20, 130));
         g.setColor(c);
         g.drawString(text, 1, height-2);
         g.dispose();
         return im;  
     }  
    
     static IndexColorModel createIndexColorModel() {  
         BufferedImage ex = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);  
         IndexColorModel icm = (IndexColorModel) ex.getColorModel();  
         int SIZE = 256;  
         byte[] r = new byte[SIZE];  
         byte[] g = new byte[SIZE];  
         byte[] b = new byte[SIZE];  
         byte[] a = new byte[SIZE];  
         icm.getReds(r);  
         icm.getGreens(g);  
         icm.getBlues(b);  
         java.util.Arrays.fill(a, (byte)255);  
         r[0] = g[0] = b[0] = a[0] = 0; //transparent  
         return  new IndexColorModel(8, SIZE, r, g, b, a);  
     }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String file="/home/work/1.jpg";
		//file = Function.changeCode("GBK", "ISO8859_1", file);
		System.out.println( file );
		System.out.println(System.getProperty("java.library.path"));
		//com.easymobile.website.images.ImageUtils.createThumbnail(file,"d:/111.jpg",150,0);
		com.easymobile.website.images.ImageUtils.createThumbnail(file,"/home/work/11.jpg",120,0);
		//com.easymobile.website.images.ImageUtils.createThumbnail("d:/111.jpg","d:/111.jpg",160,0);
		//com.easymobile.website.images.ImageUtils.createThumbnail("E:/sinadata/1月14日楼盘提交/1月14日楼盘提交/板块/CBD/出租/世贸天阶/效果图/外景.jpg","d:/2.jpg",60,0);
		//com.easymobile.website.images.ImageUtils.createThumbnail("E:/11.jpg","d:/3.jpg",200,0);
		//com.easymobile.website.images.ImageUtils.initLogoImg("d:/22.jpg","d:/222.jpg","d:/yz.png",300,false);
		//boolean ok = ImageIO.write(createTransparentGIF(120,20,16,1,"text","ff0000"), "gif", new File("C://test.gif"));  
		//System.out.println("success=" + ok);
		/*
		File file=new File("d:/20090715154954_8506.jpg");
		ImageUtils.createThumbnail(file.getAbsolutePath(), "d:/2.jpg", 420, 0);
		
		com.easymobile.website.images.ImageUtils.initLogoImg("d:/2.jpg","d:/2.jpg","d:/mark2.png",true);
		
		if(true)return;
		File dir=new File("D:/tmp/17/big");
		File[] files=dir.listFiles();
		for(File f:files){
			try{	
			String path=f.getAbsolutePath();
			ImageUtils.createThumbnail(path, path.replaceAll("big", "current"), 240, 0);
			ImageUtils.createThumbnail(path, path.replaceAll("big", "mini"), 120, 0);
			ImageUtils.initLogoImg(f.getAbsolutePath(), f.getAbsolutePath(), "D:\\workspace\\ToFun0623\\ToFun\\static\\watermark.png");
			}catch(Exception e){}
		}
		//ImageUtils.initLogoImg("d:\\tmp\\20090703113050_8000.jpg", "d:\\tmp\\test.jpg", "D:\\workspace\\ToFun0623\\ToFun\\static\\logo.gif");
		 */
		 
	}

}
