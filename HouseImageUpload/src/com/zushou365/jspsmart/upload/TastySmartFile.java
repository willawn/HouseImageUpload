package com.zushou365.jspsmart.upload;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.easymobile.website.images.ImageUtils;
import com.zushou365.houseimageupload.actions.TastyConfigHelper;
import com.zushou365.houseimageupload.util.MD5Util;

public class TastySmartFile
{
	private Logger logger = Logger.getLogger(this.getClass());
    private TastySmartUpload m_parent;
    private int m_startData;
    private int m_endData;
    private int m_size;
    private String m_fieldname;
    private String m_filename;
    private String m_fileExt;
    private String m_filePathName;
    private String m_contentType;
    private String m_contentDisp;
    private String m_typeMime;
    private String m_subTypeMime;
    //private String m_contentString;
    private boolean m_isMissing;
    public static final int SAVEAS_AUTO = 0;
    public static final int SAVEAS_VIRTUAL = 1;
    public static final int SAVEAS_PHYSICAL = 2;
    private FileItem fileItem ;

    public FileItem getFileItem() {
		return fileItem;
	}

	public void setFileItem(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	TastySmartFile()
    {
        m_startData = 0;
        m_endData = 0;
        m_size = 0;
        m_fieldname = "";//new String("");
        m_filename = "";//new String();
        m_fileExt = "";//new String();
        m_filePathName = "";//new String("");
        m_contentType = "";//new String();
        m_contentDisp = "";//new String();
        m_typeMime = "";//new String();
        m_subTypeMime = "";//new String();
        //m_contentString = "";//new String();
        m_isMissing = true;
    }

    public void saveAs(String s) throws IOException,SmartUploadException
    {
        saveAs(s,0);
    }

    public void saveAs(String s,int i) throws IOException,SmartUploadException
    {
        //Method invokes dubious new String() constructor; just use ""
        //Creating a new java.lang.String object using the no-argument constructor wastes memory because the object so created will be functionally indistinguishable from the empty string constant "".\u00A0 Java guarantees that identical string constants will be represented by the same String object.\u00A0 Therefore, you should just use the empty string constant directly.
        //String s1 = new String();
        String s1 = "";
        s1 = m_parent.getPhysicalPath(s,i);
        if(s1 == null)
        {
            throw new IllegalArgumentException("There is no specified destination file (1140).");
        }
        try
        {
            java.io.File file = new java.io.File(s1);
            
            this.fileItem.write(file);
           /* 
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.write(m_parent.m_binArray,m_startData,m_size);
            fileoutputstream.close();*/
        }
        catch(Exception ioexception)
        {
            throw new SmartUploadException("File can't be saved (1120).");
        }
    }

    public void fileToField(ResultSet resultset,String s) throws ServletException,IOException,SmartUploadException,SQLException
    {
    	 throw new SmartUploadException("除非有特别特别的理由，不要把图片存到数据库里面，这个方法被我禁用了");
    	
    /*	long l = 0L;
        int i = 0x10000;
        int j = 0;
        int k = m_startData;
        if(resultset == null)
        {
            throw new IllegalArgumentException("The RecordSet cannot be null (1145).");
        }
        if(s == null)
        {
            throw new IllegalArgumentException("The columnName cannot be null (1150).");
        }
        if(s.length() == 0)
        {
            throw new IllegalArgumentException("The columnName cannot be empty (1155).");
        }
        l = BigInteger.valueOf(m_size).divide(BigInteger.valueOf(i)).longValue();
        j = BigInteger.valueOf(m_size).mod(BigInteger.valueOf(i)).intValue();
        try
        {
            for(int i1 = 1;(long)i1 < l;i1++)
            {
                resultset.updateBinaryStream(s,this.fileItem.getInputStream(),i);
                k = k != 0 ? k : 1;
                k = i1 * i + m_startData;
            }

            if(j > 0)
            {
                resultset.updateBinaryStream(s,new ByteArrayInputStream(m_parent.m_binArray,k,j),j);
            }
        }
        catch(SQLException sqlexception)
        {
            byte abyte0[] = new byte[m_size];
            System.arraycopy(m_parent.m_binArray,m_startData,abyte0,0,m_size);
            resultset.updateBytes(s,abyte0);
        }
        catch(Exception exception)
        {
            throw new SmartUploadException("Unable to save file in the DataBase (1130).");
        }*/
    }

    public boolean isMissing()
    {
        return m_isMissing;
    }

    public String getFieldName()
    {
        return m_fieldname;
    }

    public String getFileName()
    {
        return m_filename;
    }

    public String getFilePathName()
    {
        return m_filePathName;
    }

    public String getFileExt()
    {
        return m_fileExt;
    }

    public String getContentType()
    {
        return m_contentType;
    }

    public String getContentDisp()
    {
        return m_contentDisp;
    }

    public String getContentString()
    {
        String s = new String(m_parent.m_binArray,m_startData,m_size);
        return s;
    }

    public String getTypeMIME() throws IOException
    {
        return m_typeMime;
    }

    public String getSubTypeMIME()
    {
        return m_subTypeMime;
    }

    public int getSize()
    {
        return m_size;
    }

    protected int getStartData()
    {
        return m_startData;
    }

    protected int getEndData()
    {
        return m_endData;
    }

    protected void setParent(TastySmartUpload smartupload)
    {
        m_parent = smartupload;
    }

    protected void setStartData(int i)
    {
        m_startData = i;
    }

    protected void setEndData(int i)
    {
        m_endData = i;
    }

    protected void setSize(int i)
    {
        m_size = i;
    }

    protected void setIsMissing(boolean flag)
    {
        m_isMissing = flag;
    }

    protected void setFieldName(String s)
    {
        m_fieldname = s;
    }

    protected void setFileName(String s)
    {
        m_filename = s;
    }

    protected void setFilePathName(String s)
    {
        m_filePathName = s;
    }

    protected void setFileExt(String s)
    {
        m_fileExt = s;
    }

    protected void setContentType(String s)
    {
        m_contentType = s;
    }

    protected void setContentDisp(String s)
    {
        m_contentDisp = s;
    }

    protected void setTypeMIME(String s)
    {
        m_typeMime = s;
    }

    protected void setSubTypeMIME(String s)
    {
        m_subTypeMime = s;
    }

    public byte getBinaryData(int i)
    {
        if(m_startData + i > m_endData)
        {
            throw new ArrayIndexOutOfBoundsException("Index Out of range (1115).");
        }
        if(m_startData + i <= m_endData)
        {
            return m_parent.m_binArray[m_startData + i];
        }
        else
        {
            return 0;
        }
    }
    
    //
    
    public String saveImg(String path,String random,String field,String userId,String slab,int imageServer) throws IOException,SmartUploadException
    {
        //Method invokes dubious new String() constructor; just use ""
        //Creating a new java.lang.String object using the no-argument constructor wastes memory because the object so created will be functionally indistinguishable from the empty string constant "".\u00A0 Java guarantees that identical string constants will be represented by the same String object.\u00A0 Therefore, you should just use the empty string constant directly.
        //String s1 = new String();
    	String init=TastySmartUpload.ImageAppPath+path+random+"_init.jpeg";//+this.m_fileExt;
        String s = TastySmartUpload.ImageAppPath+path+random+"_s.jpeg";//+this.m_fileExt;
        String b = TastySmartUpload.ImageAppPath+path+random+"_b.jpeg";//+this.m_fileExt;
        String m = TastySmartUpload.ImageAppPath+path+random+"_m.jpeg";//+this.m_fileExt;
        
        java.io.File initFile =null;
        try
        {
        	initFile = new java.io.File(init);
        	this.fileItem.write(initFile);
        }
        catch (Exception exception)
        {
        	throw new SmartUploadException("File can't be saved (1120).");
        }
        if(initFile==null || !initFile.exists()){
        	throw new SmartUploadException("File can't be saved (1120).");
        }
        
      
        
        String imgMd5= MD5Util.getFileMD5String(initFile);
        
        //check image size width height
        if(field.equals("uploadcommpicfile")||field.equals("uploadcommodelfile")||field.equals("uploadpropertypicfile")){
        	if(/*m_size<=50*1024||*/m_size>=1024*1024  && !"1".equals(slab)){
	        	return "length_error";
	        }
	       
	     
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
	        		return pic;
	        	}else{
	        		if(userId.equals(orginUserId)){
	        			return pic;
	        		}else{
	        			TastyConfigHelper.recordHousePicsUser(userId,picId);
	        			return pic;
	        		}
	        	}
	        	
	        }
	        Dimension dim =ImageUtils.getDimension(initFile);
	        
	        if(dim==null || dim.getWidth()<200 || dim.getHeight()<200){
	        	return "size_error";
	        }
	        
	        ImageUtils.createThumbnail(initFile.getAbsolutePath(), b, 800,0);
	        
	        if("1".equals(slab)){
	        	
	        	
	        	//String s1 = TastySmartUpload.ImageAppPath+path+random+"_s1.jpeg"; //w:92 * 92
	    		String s2 = TastySmartUpload.ImageAppPath+path+random+"_s2.jpeg"; //w:90 * 120
	    		String z =TastySmartUpload.ImageAppPath+path+random+"_z.jpeg"; //w:240 * 180
	    		String o = TastySmartUpload.ImageAppPath+path+random+"_o.jpeg"; //原图
	    		//String s1 = TastySmartUpload.ImageAppPath+path+random+"_s1.jpeg";
	    		//compressBytesToFileByWH(s1, m_parent.m_binArray, m_startData, m_size, 92, 92); 
	    		ImageUtils.createThumbnail(initFile.getAbsolutePath(), s2, 90,0);
	    		ImageUtils.createThumbnail(initFile.getAbsolutePath(), z, 240,0);
	    		ImageUtils.copyFile(initFile, new File(o)) ;
	    		
	    		//compressBytesToFileByWH(s2, bytes,0,m_size, 90, 120);
	    		//compressBytesToFileByWH(z, bytes,0,m_size, 240, 180); 
	    		//compressBytesToFileByWH(b1, m_parent.m_binArray, m_startData, m_size, 600, 800);
	    		//compressBytesToFileByWH(o, bytes,0,m_size, src.getWidth(null), src.getHeight(null)); 
	    	}
        }else {
        	if(m_size<20*1024||m_size>=500*1024){
	        	return "length_error";
	        }
        	ImageUtils.copyFile(initFile, new File(b)) ;
        	
        }/*else if(field.equals("personImage")){//101203
        	writeOutFile(b,m_parent.m_binArray,m_startData,m_size);
            compressBytesToFile(s,m_parent.m_binArray,m_startData,m_size);
            //
            
            TastyJpeg ps = new TastyJpeg();
            ps.proce1(b, 120, 160,
                    1, "2");
            return path+random+"_s.jpeg";
        }*/
        
        ImageUtils.createThumbnail(initFile.getAbsolutePath(), m, 400,0);
        ImageUtils.createThumbnail(initFile.getAbsolutePath(), s, 100,100);
        //writeOutFile(m,bytes,0,m_size);
        //compressBytesToFile(s,bytes,0,m_size);
        //
        //logger.info(b + new File(b).exists());
        //TastySmartUpload.jpegImage(this.m_fileExt,b,1);
        return path+random+"_s.jpeg"+"#"+imgMd5;
        
    }
    
   /* private boolean writeOutFile(String filename,byte[] pic,int start,int size) {
    	try {
			FileOutputStream os = new FileOutputStream(filename);
			os.write(pic,start,size);
			os.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
    
    private Image getImageIO(byte[] pic,int start,int size) {
    	InputStream inputeStream = new ByteArrayInputStream(pic,start,size);
    	// 构造Image对象
		Image src = null;
		try {
			src = ImageIO.read(inputeStream);
			inputeStream.close();
			return src;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
    }
    private boolean imageJpeg(String filename,byte[] pic,int start,int size) {

		try {
			File outFile = new File(filename);
			// 读入流
			InputStream inputeStream = new ByteArrayInputStream(pic,start,size);

			// 构造Image对象
			Image src;
			try {
				src = ImageIO.read(inputeStream);
				inputeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			if (src == null)
				return false;

			if (src.getHeight(null) <= 0 || src.getWidth(null) <= 0)
				return false;

			int SQUAL = 99;

			int height = src.getHeight(null);
			int weight = src.getWidth(null);

			if (height > weight) {
				weight = SQUAL * weight / height;
				height = SQUAL;
			} else {
				height = SQUAL * height / weight;
				weight = SQUAL;
			}

			int top = (SQUAL - height) / 2;
			int left = (SQUAL - weight) / 2;

			BufferedImage bi = new BufferedImage(SQUAL, SQUAL,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D grap = bi.createGraphics();
			grap.setBackground(Color.white);
			grap.clearRect(0, 0, SQUAL, SQUAL);
			grap.drawImage(src, left, top, weight, height, Color.white, null); // 绘制缩小后的图
			src.flush();
			grap.dispose();
			bi.flush();

			// 输出到文件流
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

			try {
				encoder.encode(bi); // 近JPEG编码
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
    
	private boolean compressBytesToFile(String filename,byte[] pic,int start,int size) {

		try {
			File outFile = new File(filename);
			// 读入流
			InputStream inputeStream = new ByteArrayInputStream(pic,start,size);

			// 构造Image对象
			Image src;
			try {
				src = ImageIO.read(inputeStream);
				inputeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			if (src == null)
				return false;

			if (src.getHeight(null) <= 0 || src.getWidth(null) <= 0)
				return false;

			int SQUAL = 99;

			int height = src.getHeight(null);
			int weight = src.getWidth(null);

			if (height < weight) {
				weight = SQUAL * weight / height;
				height = SQUAL;
			} else {
				height = SQUAL * height / weight;
				weight = SQUAL;
			}

			int top = (SQUAL - height) / 2;
			int left = (SQUAL - weight) / 2;

			BufferedImage bi = new BufferedImage(SQUAL, SQUAL,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D grap = bi.createGraphics();
			grap.setBackground(Color.white);
			grap.clearRect(0, 0, SQUAL, SQUAL);
			grap.drawImage(src, left, top, weight, height, Color.white, null); // 绘制缩小后的图
			src.flush();
			grap.dispose();
			bi.flush();

			// 输出到文件流
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

			try {
				encoder.encode(bi); // 近JPEG编码
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean compressBytesToFile2(String filename,byte[] pic,int start,int size) {
		
		try {
			File outFile = new File(filename);
			// 读入流
			//InputStream inputeStream =this.fileItem.getInputStream();
			InputStream inputeStream = new ByteArrayInputStream(pic,start,size);
			// 构造Image对象
			Image src;
			try {
				src = ImageIO.read(inputeStream);
				inputeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			if (src == null)
				return false;

			if (src.getHeight(null) <= 0 || src.getWidth(null) <= 0)
				return false;

			int SQUALWIDTH = 800;
			int SQUALHEIGHT = 600;
			

			int height = src.getHeight(null);
			int weight = src.getWidth(null);
			
			if (height/weight<SQUALHEIGHT/SQUALWIDTH) {
				weight = weight * SQUALHEIGHT/height;
				height = SQUALHEIGHT;
			} else {
				height = height * SQUALWIDTH / weight;
				weight = SQUALWIDTH;
			}

			int top = (SQUALHEIGHT - height) / 2;
			int left = (SQUALWIDTH - weight) / 2;

			BufferedImage bi = new BufferedImage(SQUALWIDTH, SQUALHEIGHT,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D grap = bi.createGraphics();
			grap.setBackground(Color.white);
			grap.clearRect(0, 0, SQUALWIDTH, SQUALHEIGHT);
			grap.drawImage(src, left, top, weight, height, Color.white, null); // 绘制缩小后的图
			src.flush();
			grap.dispose();
			bi.flush();

			// 输出到文件流
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

			try {
				encoder.encode(bi); // 近JPEG编码
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}*/
/*
private boolean compressBytesToFileByWH(String filename,byte[] pic,int start,int size,int W, int H ) {
		
		try {
			File outFile = new File(filename);
			// 读入流
			InputStream inputeStream = new ByteArrayInputStream(pic,start,size);
			//InputStream inputeStream =this.fileItem.getInputStream();
			// 构造Image对象
			Image src;
			try {
				src = ImageIO.read(inputeStream);
				inputeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			if (src == null)
				return false;

			if (src.getHeight(null) <= 0 || src.getWidth(null) <= 0)
				return false;

			int SQUALWIDTH = W;
			int SQUALHEIGHT = H;

			int height = src.getHeight(null);
			int weight = src.getWidth(null);
			
			if (height/weight<SQUALHEIGHT/SQUALWIDTH) {
				weight = weight * SQUALHEIGHT/height;
				height = SQUALHEIGHT;
			} else {
				height = height * SQUALWIDTH / weight;
				weight = SQUALWIDTH;
			}

			int top = (SQUALHEIGHT - height) / 2;
			int left = (SQUALWIDTH - weight) / 2;

			BufferedImage bi = new BufferedImage(SQUALWIDTH, SQUALHEIGHT,
					BufferedImage.TYPE_INT_RGB);
			
			Graphics2D grap = bi.createGraphics();
			grap.setBackground(Color.white);
			grap.clearRect(0, 0, SQUALWIDTH, SQUALHEIGHT);
			grap.drawImage(src, left, top, weight, height, Color.white, null); // 绘制缩小后的图
			src.flush();
			grap.dispose();
			bi.flush();

			// 输出到文件流
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			try {
				encoder.encode(bi); // 近JPEG编码
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	*/
	
}
