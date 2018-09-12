package com.zushou365.houseimageupload.dbmanager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;

import com.mysql.jdbc.CommunicationsException;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.zushou365.houseimageupload.cfg.ImageConfiguration;
import com.zushou365.houseimageupload.util.DateHelper;
import com.zushou365.houseimageupload.util.StringUtil;
import com.zushou365.jspsmart.upload.TastySmartUpload;


public class DBRunner{
	
	public final static String COMPRESS_EXT = "_s.jpg";

	public final static String ORIGINAL_EXT = "_b.jpg";
	
	public final static String db="house_project_db";
	
	public static HashMap<String,Connection> connections =new HashMap<String,Connection>();
	
	public static String imagePath="D:\\java\\tomcats\\apache-tomcat-6.0.13\\webapps\\ROOT\\house_imgs_model\\";
	
	public static String imageUrl="http://localhost:8085/house_imgs_model/";
	
	public static void main(String[] args){
		DBRunner DBRunner = new DBRunner();
		//DBRunner.process(db,"900001","3110061652");
		//3110143942
		//DBRunner.buildAreaPicXmlR(1,"900001", "3110061652", "0",null, new ArrayList(),false);
	}
	
	public void process(String db,String city,String projectId){
		Connection connection =getConnection(db);
		if(connection==null)
			return ;
		//
		String sql="select ImageType,Image,refindKey from Picture where ProjectId="+projectId;
		try {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ps = connection.prepareStatement(sql);
		rs = ps.executeQuery();
		while (rs.next()) {
			String imageType = rs.getString("ImageType");
			String refindKey = rs.getString("refindKey");
			byte pic[] = rs.getBytes("Image");
			if(pic==null||pic.length<800)
				continue;
			writeOutOriginImage(city,projectId,imageType,refindKey,pic);
			writeOutCompressedImageInner(city,projectId,imageType,refindKey,pic);
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String transAreaPicXmlR(String url,String imageid,String panel,String commodelfile,HashSet pics,boolean edit){
		String checked="";
		if(edit&&pics.contains(imageid)){
			checked="checked";
		}
		String bUrl = StringUtil.replace(url,"_s.","_b.");
		String str="<div class=\"housepicone\"><div class=\"housepic_s\">";
		str+="<a href=\""+bUrl+"\" target=\"_blank\"><img src=\""+url+"\"></a>";
		str+="<div class=\"deletepic2\"><input type=\"checkbox\" onclick=\"javascript:imageSelectHtml(this,'"+panel+"','"+imageid+"','"+url+"','"+commodelfile+"');\" value=\"\" "+checked+">打勾选中</div>";
		str+="</div></div>";
		
		return str;
   
	}

	
	public static Connection getConnection(String db){
		Connection connection = connections.get(db);
		if(connection!=null){
			try {
				if(connection.isClosed()){
					resetConnection(db);
				}
			} catch (Exception e) {
				JdbcUtils.closeConnection(connection);
				resetConnection(db);
			}
		}else{
			resetConnection(db);
		}
		connection = connections.get(db);
		return connection;
	}
	
	public static void resetConnection(String db){
		Connection connection = null;
		try {
			connection = DBConnectionManager.getInstance().getConnection(db);
			connections.put(db, connection);
		} catch (SQLException e) {
			JdbcUtils.closeConnection(connection);
			connection = null;
		}
	}
	
	private String writeOutOriginImage(String city,String projectId,
			String imageType,String refindKey,byte pic[]) {

		try {

			String orgRevPath = refindKey + ORIGINAL_EXT;

			checkPathExists(city,projectId,imageType);

			File orgFile = new File(getFormatedPath(city,projectId,imageType,
					orgRevPath));

			if (!orgFile.exists()) {
				this.writeOutFile(pic, orgFile.getAbsolutePath());
			}

			return getFormatedPath(city,projectId,imageType, orgRevPath);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return "error";
		}
	}

	private void writeOutFile(byte[] pic, String filename) {
		try {
			FileOutputStream os = new FileOutputStream(filename);
			os.write(pic);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String writeOutCompressedImageInner(String city,String projectId,
			String imageType,String refindKey,byte pic[]) {
		try {
			//makeSureDatePathExists(datePath);
			String compressedRevPath = refindKey + COMPRESS_EXT;
			String orgRevPath = refindKey + ORIGINAL_EXT;

			File outFile = new File(getFormatedPath(city,projectId,imageType,
					compressedRevPath));
			File orgFile = new File(getFormatedPath(city,projectId,imageType,
					orgRevPath));

			// 若数据库标记为有图片，则尝试写成文件，写成功则设置图片相对路径
			if (!outFile.exists()) {
				/*byte pic[] = null;

				if (orgFile.exists()) {
					try {
						pic = readBytesFromFile(orgFile);
					} catch (FileNotFoundException e) {
						pic = null;
					} catch (IOException e) {
						pic = null;
					}
				}*/

				if (pic == null) {
					return null;
				}

				if (this.compressBytesToFile(pic, outFile)) {
					return getFormatedPath(city,projectId,imageType,
							compressedRevPath);
				} else {
					// 如果压缩出错，说明图片内容就有问题，这时如果有大图需要删除掉，并生成压缩失败标记文件
					if (orgFile.exists())
						orgFile.delete();
					if (outFile.exists())
						outFile.delete();
				}

			} else {
				return getFormatedPath(city,projectId,imageType,
						compressedRevPath);
			}

		} catch (RuntimeException e) {
			// e.printStackTrace();
		}
		return null;
	}
	
	public String getFormatedPath(String city,String projectId,
			String imageType,String fileName) {
		String projectIdPre = projectId.substring(0,4);
		return (new StringBuffer())
		.append(this.imagePath).append("/")
		.append(city).append("/")
		.append(projectIdPre).append("/")
		.append(projectId).append("/")
		.append(imageType).append("/")
		.append(fileName).toString();
	}
	
	public String checkPathExists(String city,String projectId,String imageType) {
		String projectIdPre = projectId.substring(0,4);
		String dirPath = this.imagePath + "/" +city+ "/" + projectIdPre+"/"+ projectId+"/"+imageType;
		File dir = new File(dirPath);
		dir.mkdirs();
		return city+ "/" + projectIdPre+"/"+ projectId+"/"+imageType;
	}
	
	public boolean checkImageExists(String city,String projectId) {
		String projectIdPre = projectId.substring(0,4);
		String dirPath = this.imagePath + "/" +city+ "/" + projectIdPre+"/"+ projectId+"/";
		File dir = new File(dirPath);
		if(!dir.exists()){
			return false;
		}
		return true;
	}

	private byte[] readBytesFromFile(File file) throws IOException {

		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		Streams.copy(in, bytesOut);
		Streams.closeInputStream(in);
		return bytesOut.toByteArray();
	}
	
	private boolean compressBytesToFile(byte[] pic, File outputFile) {

		try {
			// 读入流
			InputStream inputeStream = new ByteArrayInputStream(pic);

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
				out = new FileOutputStream(outputFile);
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
	
	
	public static int getAreaId(String db,Connection connection,int parentId,String name){

		String sql="select id from house_area where parentid="+parentId+" and areaname='"+name+"'";
		
		ArrayList<String> arr = dataSql(db,connection,sql,",");
		
		int tsize = arr.size();
		if(tsize>0){
			return Integer.valueOf(arr.get(0));
		}else{
			return 0;
		}
		
	}
	
	public static ArrayList<String> dataSql(String sql,String splitStr){
		Connection connection = DBRunner.getConnection("house_db");
		return dataSql("house_db",connection,sql,splitStr);
	}
	
	public static ArrayList<String> dataSql(String db,Connection connection,String sql,String splitStr){
		ArrayList<String> arr = new ArrayList<String>();
		if(splitStr==null||splitStr.length()==0){
			splitStr=",";
		}
		String text1 = sql.toLowerCase().trim();
        int index = text1.indexOf("select ");
        if (index == -1)
        {
            return arr;
        }
        text1 = text1.substring(index + 7);
        index = text1.indexOf(" from");
        if (index == -1)
        {
            return arr;
        }
        text1 = text1.substring(0, index).trim();

        int len = text1.split(",").length;
        
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			try{
				ps = connection.prepareStatement(sql);
				rs = ps.executeQuery();
			}catch(CommunicationsException e){
				connection =DBRunner.getConnection(db);
				ps = connection.prepareStatement(sql);
				rs = ps.executeQuery();
			}
			while (rs.next()) {
				String line = "";
                for (int i = 0; i < len; i++)
                {
                    try
                    {
                        Object obj = rs.getObject(i+1);
                        if (obj == null)
                            line += "";
                        else
                        {
                            if (obj instanceof Date)
                            {
                                line += DateHelper.dateToString((Date)obj);
                            }
                            else
                            {
                                line += obj.toString();
                            }
                        }
                        
                    }
                    catch (Exception ex)
                    {
                        line += "";
                    }
                    if (i < len - 1)
                        line += splitStr;
                }
                arr.add(line);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
		}

		return arr;
	}
	
	public static int excuteSql(String db,ArrayList<String> sqls){
		//db
		int result = 1 ;
		Connection connection =DBRunner.getConnection(db);
		Statement stmt = null;
		String sql = null;
		int i=0;
		try {
			stmt = connection.createStatement();
			for(i=0;i<sqls.size();i++){
				sql = sqls.get(i);
				//System.out.println(sql);
				stmt.execute(sql);
			}
		} catch (Exception e) {
			if(e instanceof CommunicationsException){
				connection =DBRunner.getConnection(db);
				try {
					stmt = connection.createStatement();
					for(int j = i;j<sqls.size();j++){
						sql = sqls.get(j);
						stmt.execute(sql);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}/*finally {
					JdbcUtils.closeStatement(stmt);
				}*/
				
			}else
				e.printStackTrace();
			result =  -1;
		}finally {
			JdbcUtils.closeStatement(stmt);
		}
		return result;
	}
	
	public static int excuteSql(ArrayList<String> sqls){
		return excuteSql("house_db",sqls);
	}
	
	public static int excuteSql(String sql){
		ArrayList<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		return excuteSql("house_db",sqls);
	}
}
