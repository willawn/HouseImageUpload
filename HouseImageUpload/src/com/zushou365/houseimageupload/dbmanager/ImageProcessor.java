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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

//FIXED: 完善测试用例，加入测试日期目录，
//FIXED: 日期的子目录生成需要加入出错处理
//FIXED: 完善测试用例，加入生成新标记测试
//FIXED: 加入生成压缩后删除新标记的测试
//FIXED: 加入压缩失败后生成失败标记的测试

//由于JAI里内存泄漏的BUG，现在的处理方式为：　仅导出原始图片文件。然后启动进程来处理图片压缩
//压缩进程读出原始图片文件进行压缩，接收命令行参数，见ImageCompressRunner
//同时ImageProcssor仍保留
/**
 * @author Adam Bear
 * 
 */
public class ImageProcessor {

	public static Logger log = Logger.getLogger(ImageProcessor.class.getName());

	public final static String COMPRESS_EXT = "_c.jpg";

	public final static String ORIGINAL_EXT = "_o.jpg";

	public final static String BAD_EXT = "_b.jpg";

	public final static String NEW_EXT = "_n.jpg";

	private static Date PASSED_OLD_DATE = null;

	private String realPath = "";

	private String imagePath = "";

	private boolean immediateCompress = true;

	// FIXME:考虑在写出原图前同时生成待处理标记文件，生成成功则删除此标记文件。已利于后台图片压缩进程的查找处理
	// FIXED:加入按日期分区图片的方案。

	static {
		Calendar cal = Calendar.getInstance();
		cal.set(2007, 0, 11);
		PASSED_OLD_DATE = cal.getTime();
	}

	public ImageProcessor() {
		ImageIO.setUseCache(false);
	}

	public void clear() {
		// this.item = null;
		// this.id = "";
	}

	private Connection connection;

	// /----------------------------------------------------------------------
	// 统一的路径处理函数

	/**
	 * 根据参数组合出最后的图片文件的路径
	 * 
	 * @param picsPath
	 *            图片根目录的绝对路径
	 * @param datePath
	 *            图片是时间分隔目录存放，此参数表示时间目录
	 * @param fileName
	 *            图片的全文件名
	 * @return
	 */
	public String getFormatedPath(String picsPath, String datePath,
			String fileName) {
		return (new StringBuffer()).append(picsPath).append("/").append(
				datePath).append("/").append(fileName).toString();
	}

	public String getFormatedPath(String picsPath, String datePath, String id,
			String extension) {
		return getFormatedPath(picsPath, datePath, id + extension);
	}

	/**
	 * 按item的建立时间将图片分隔到不同目录，此函数用来计算图片存放的目录位置
	 * 
	 * @param item
	 * @return 返回指定item时间目录
	 */
	private String getDatePath(Date cd) {
		// Date cd = item.getCreatedDate();
		if (cd == null || cd.before(PASSED_OLD_DATE)) {
			return "2006";
		} else {
			return new SimpleDateFormat("yyyyMMdd").format(cd);
		}
	}

	// /------------------------------------------------------------------------

	public boolean isCompressed(String datePath, String id) {
		String orgRevPath = id + COMPRESS_EXT;
		File orgFile = new File(getFormatedPath(this.realPath, datePath,
				orgRevPath));
		return orgFile.exists();
	}

	public boolean isCompressedItem(String id, Date firstDate) {
		String datePath = getDatePath(firstDate);
		return isCompressed(datePath, id);
	}

	public boolean isExists(String datePath, String id) {
		String orgRevPath = id + ORIGINAL_EXT;
		File orgFile = new File(getFormatedPath(this.realPath, datePath,
				orgRevPath));
		return orgFile.exists();
	}

	public boolean isExistsItem(String id, Date firstDate) {
		String datePath = getDatePath(firstDate);
		return isExists(datePath, id);
	}

	public boolean isBadItem(String id, Date firstDate) {
		String datePath = getDatePath(firstDate);
		return isBadPicture(datePath, id);
	}

	public void deleteImageFile(String datePath, String id) {
		String orgRevPath = id + ORIGINAL_EXT;
		String compressedRevPath = id + COMPRESS_EXT;
		File orgFile = new File(getFormatedPath(this.realPath, datePath,
				orgRevPath));
		File outFile2 = new File(getFormatedPath(this.realPath, datePath,
				compressedRevPath));
		if (orgFile.exists())
			orgFile.delete();
		if (outFile2.exists())
			outFile2.delete();
	}

	public int processImage(String id, Date firstDate) {

		String datePath = getDatePath(firstDate);
		

		//
		if (isCompressed(datePath, id)) {
			return 1;
		}
		boolean hasOrg = isExists(datePath, id);
		if (hasOrg) {
			//return 1;
		}else{
		// 原图找不到考虑从数据库里读取并写出成原图文件
		String orgImagePath = this.writeOutOriginImage(datePath, id);
		if ("nopic".equals(orgImagePath))
			return 2;
		else if ("error".equals(orgImagePath))
			return 3;
		// TODO: 原图可能会取出失败，失败需要暂时作为无图片处理
		if (orgImagePath == null) {
			return 3;
		}
		// 成功生生原图，则生成写成功标记文件，已方便压缩进程查找处理
		if (orgImagePath.length() > 0) {
			createNewMarkedFile(datePath, id);
		}
		}
		// 是否直接压缩图片处理
		if (immediateCompress) {
			try{
			if (processCompressedImageInner(id, datePath))
				return 4;
			else
				return 3;
			}
			catch(Exception e){
				return 3;
			}
		}

		return 4;

	}

	public boolean processCompressedImageInner(String id, String datePath) {
		if (isCompressed(datePath, id)) {
			return true;
		}
		// 如果压缩图片找不到，则生成压缩图片
		String compressedImagePath = this.writeOutCompressedImageInner(
				datePath, id);
		if (compressedImagePath != null && !"".equals(compressedImagePath))
			return true;
		return false;
	}

	// 判断是否是压缩失败的图片
	public boolean isBadPicture(String datePath, String id) {
		if (isCompressed(datePath, id))
			return false;

		// 有压缩失败标记
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				BAD_EXT));
		if (outputFile.exists())
			return true;

		return false;
	}

	// 生成错误标记文件，用来标记出错
	private void createBadMarkedFile(String datePath, String id) {
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				BAD_EXT));
		if (outputFile.exists())
			return;
		FileOutputStream out;
		try {
			out = new FileOutputStream(outputFile);
			out.write(0);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	// 删除错误标记文件
	public void deleteBadMarkedFile(String datePath, String id) {
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				BAD_EXT));
		if (outputFile.exists())
			outputFile.delete();
	}

	// 判断是否是新生成的原图
	public boolean isNewPicture(String datePath, String id) {
		if (isCompressed(datePath, id))
			return false;

		// 有新文件失败标记
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				NEW_EXT));
		if (outputFile.exists())
			return true;

		return false;
	}

	// 生成新图标记文件，用来标记新生成但还未压缩的图片
	private void createNewMarkedFile(String datePath, String id) {
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				NEW_EXT));
		if (outputFile.exists())
			return;
		FileOutputStream out;
		try {
			out = new FileOutputStream(outputFile);
			out.write(0);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	// 删除新图标记文件
	public void deleteNewMarkedFile(String datePath, String id) {
		File outputFile = new File(getFormatedPath(this.realPath, datePath, id,
				NEW_EXT));
		if (outputFile.exists())
			outputFile.delete();
	}

	// 读出原图片文件，并生成缩略图
	// FIXED: 完成测试用例
	public boolean CompressFile(String orgFileName, String outFileName,
			String datePath, String id) {
		File outFile = new File(outFileName);
		File orgFile = new File(orgFileName);
		if (!orgFile.exists())
			return false;

		byte[] pic = null;
		try {
			pic = readBytesFromFile(orgFile);
		} catch (Exception e) {
			return false;
		}

		if (pic == null) {
			createBadMarkedFile(datePath, id);
			return false;
		}

		boolean sucess = this.compressBytesToFile(pic, outFile);

		if (sucess) {
			// 成功生成压缩图片，删除新图片标记，删除错误标记
			deleteNewMarkedFile(datePath, id);
			deleteBadMarkedFile(datePath, id);
		} else {
			// 如果压缩出错，说明图片内容就有问题，这时如果有大图需要删除掉，并生成压缩失败标记文件
			deleteNewMarkedFile(datePath, id);
			if (orgFile.exists())
				orgFile.delete();
			if (outFile.exists())
				outFile.delete();
			createBadMarkedFile(datePath, id);
		}
		return sucess;
	}

	private byte[] getPictureData(String id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection
					.prepareStatement("select picture from details where refindkey ='"
							+ id + "'");
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getBytes(1);
			} else
				return null;

		} catch (SQLException e) {
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeResultSet(rs);
			return null;
		} finally {
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeResultSet(rs);
		}
	}

	private String writeOutCompressedImageInner(String datePath, String id) {

		try {
			if (isBadPicture(datePath, id))
				return null;

			makeSureDatePathExists(datePath);

			String compressedRevPath = id + COMPRESS_EXT;
			String orgRevPath = id + ORIGINAL_EXT;

			File outFile = new File(getFormatedPath(this.realPath, datePath,
					compressedRevPath));
			File orgFile = new File(getFormatedPath(this.realPath, datePath,
					orgRevPath));

			// 若数据库标记为有图片，则尝试写成文件，写成功则设置图片相对路径
			if (!outFile.exists()) {
				byte pic[] = null;

				// 如果已经存在大图，则图片内容从大图文件读取
				if (orgFile.exists()) {
					try {
						pic = readBytesFromFile(orgFile);
					} catch (FileNotFoundException e) {
						pic = null;
					} catch (IOException e) {
						pic = null;
					}
				}

				/*
				 * if(pic == null){
				 * 
				 * pic = getPictureData(id);
				 * 
				 * if(pic != null){ //输出原图 this.writeOutFile(pic,
				 * orgFile.getAbsolutePath()); } }
				 */

				if (pic == null) {
					createBadMarkedFile(datePath, id);
					return null;
				}

				if (this.compressBytesToFile(pic, outFile)) {
					// 成功生成压缩图片，删除新图片标记
					deleteNewMarkedFile(datePath, id);
					deleteBadMarkedFile(datePath, id);
					return getFormatedPath(this.imagePath, datePath,
							compressedRevPath);
				} else {
					// 如果压缩出错，说明图片内容就有问题，这时如果有大图需要删除掉，并生成压缩失败标记文件
					deleteNewMarkedFile(datePath, id);
					if (orgFile.exists())
						orgFile.delete();
					if (outFile.exists())
						outFile.delete();
					createBadMarkedFile(datePath, id);
				}

			} else {
				return getFormatedPath(this.imagePath, datePath,
						compressedRevPath);
			}

		} catch (RuntimeException e) {
			// e.printStackTrace();
		}

		createBadMarkedFile(datePath, id);
		return null;
	}

	private byte[] readBytesFromFile(File file) throws IOException {

		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		Streams.copy(in, bytesOut);
		Streams.closeInputStream(in);
		return bytesOut.toByteArray();
	}

	// FIXME: check fails
	// http://61.129.49.64/view.do?id=61a277dbf7c8ea4477b5b83f34682a4861f08196&query=ipod&detail=true&searchType=1
	private String writeOutOriginImage(String datePath, String id) {

		try {
			// process imagePath
			String orgRevPath = id + ORIGINAL_EXT;

			makeSureDatePathExists(datePath);

			File orgFile = new File(getFormatedPath(this.realPath, datePath,
					orgRevPath));

			if (!orgFile.exists()) {
				byte pic[] = null;
				pic = getPictureData(id);
				if (pic == null || pic.length < 800)
					return "nopic";
				this.writeOutFile(pic, orgFile.getAbsolutePath());
			}

			return getFormatedPath(this.imagePath, datePath, orgRevPath);
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

	private void makeSureDatePathExists(String datePath) {
		String dirPath = this.realPath + "/" + datePath;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getContextPath() {
		return "";
	}

	public void setContextPath(String contextPath) {
		// this.contextPath = contextPath;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isImmediateCompress() {
		return immediateCompress;
	}

	public void setImmediateCompress(boolean immediateCompress) {
		this.immediateCompress = immediateCompress;
	}

}
