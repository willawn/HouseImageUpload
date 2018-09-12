package com.zushou365.jspsmart.upload;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.zushou365.houseimageupload.dbmanager.DBRunner;
import com.zushou365.houseimageupload.util.DateHelper;
import com.zushou365.houseimageupload.util.LogWriter;
import com.zushou365.houseimageupload.util.MD5;
import com.zushou365.houseimageupload.util.StringUtil;

public class TastySmartUpload {
	
	protected byte m_binArray[];
	protected HttpServletRequest m_request;
	protected HttpServletResponse m_response;
	protected ServletContext m_application;
	private int m_totalBytes;
	private int m_currentIndex;
	private int m_startData;
	private int m_endData;
	private String m_boundary;
	private long m_totalMaxFileSize;
	private long m_maxFileSize;
	private Vector m_deniedFilesList;
	private Vector m_allowedFilesList;
	private boolean m_denyPhysicalPath;

	// private boolean m_forcePhysicalPath;
	private String m_contentDisposition;
	public static final int SAVE_AUTO = 0;
	public static final int SAVE_VIRTUAL = 1;
	public static final int SAVE_PHYSICAL = 2;
	private TastySmartFiles m_files;
	private SmartRequest m_formRequest;

	public static String ImageAppPath = "";
	private Logger logger = Logger.getLogger(TastySmartUpload.class);

	public TastySmartUpload() {
		m_totalBytes = 0;
		m_currentIndex = 0;
		m_startData = 0;
		m_endData = 0;
		m_boundary = ""; // new String();
		m_totalMaxFileSize = 0L;
		m_maxFileSize = 0L;
		m_deniedFilesList = new Vector();
		m_allowedFilesList = new Vector();
		m_denyPhysicalPath = false;
		// m_forcePhysicalPath = false;
		m_contentDisposition = ""; // new String();
		m_files = new TastySmartFiles();
		m_formRequest = new SmartRequest();
	}

	/**
	 * @deprecated Method init is deprecated
	 */
	public final void init(ServletConfig servletconfig) throws ServletException {
		m_application = servletconfig.getServletContext();
	}

	/**
	 * @deprecated Method service is deprecated
	 */
	public void service(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException, IOException {
		m_request = httpservletrequest;
		m_response = httpservletresponse;
	}

	public final void initialize(ServletConfig servletconfig, HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
			throws ServletException {
		m_application = servletconfig.getServletContext();
		m_request = httpservletrequest;
		m_response = httpservletresponse;
	}

	public final void initialize(ServletContext servletcontext, HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
			throws ServletException {
		m_application = servletcontext;
		m_request = httpservletrequest;
		m_response = httpservletresponse;

		if (ImageAppPath == null || ImageAppPath.length() == 0) {

			ImageAppPath = m_application.getRealPath("/");
			if (ImageAppPath.indexOf("/") != -1) {
				if (ImageAppPath.charAt(ImageAppPath.length() - 1) != '/') {
					ImageAppPath = ImageAppPath + "/";
					// System.out.println("m_application.getRealPath::" + s);
				}
			} else {
				if (ImageAppPath.charAt(ImageAppPath.length() - 1) != '\\') {
					ImageAppPath = ImageAppPath + "\\";
					// System.out.println("m_application.getRealPath" + s);
				}
			}
			// ImageAppPath += "img";
		}
	}

	public final void initialize(PageContext pagecontext) throws ServletException {
		m_application = pagecontext.getServletContext();
		m_request = (HttpServletRequest) pagecontext.getRequest();
		m_response = (HttpServletResponse) pagecontext.getResponse();
	}

	/**
	 * @deprecated Method initialize is deprecated
	 */
	public final void initialize(ServletContext servletcontext, HttpSession httpsession, HttpServletRequest httpservletrequest,
			HttpServletResponse httpservletresponse, JspWriter jspwriter) throws ServletException {
		m_application = servletcontext;
		m_request = httpservletrequest;
		m_response = httpservletresponse;
	}

	@SuppressWarnings("deprecation")
	public void upload() throws ServletException, IOException, SmartUploadException,FileUploadException {
		
		 String encoding = null;
		 if (this.m_request != null)
		      encoding = this.m_request.getCharacterEncoding();
		//int i = 0;
		// boolean flag = false;
		boolean flag1 = false;
		// boolean flag2 = false;
		long l = 0L;
		// String s = "";//new String();
		// String s2 = "";//new String();
		String s4 = ""; // new String();
		String s5 = ""; // new String();
		String s6 = ""; // new String();
		String s7 = ""; // new String();
		String s8 = ""; // new String();
		String s9 = ""; // new String();
		String s10 = ""; // new String();
		m_totalBytes = 0;
		//m_binArray = new byte[m_totalBytes];
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Set factory constraints
		
		factory.setSizeThreshold(10240);
		factory.setRepository(new File("/tmp"));
		
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Set overall request size constraint
		upload.setSizeMax(this.m_totalMaxFileSize);

		// Parse the request
		List /* FileItem */ fileItems = upload.parseRequest(this.m_request);
		
		
		Iterator i = fileItems.iterator();

	      while (i.hasNext()) {
	        FileItem fi = (FileItem)i.next();

	        if (!fi.isFormField()) {
	        	m_totalBytes += fi.getSize();

	          if ((this.m_maxFileSize > 0L) && (fi.getSize() > this.m_maxFileSize))
	            throw new SmartUploadException("File Size cannnot exceed " + this.m_maxFileSize / 1024L + "k");
	          if ((this.m_totalMaxFileSize > 0L) && (m_totalBytes > this.m_totalMaxFileSize)) {
	            throw new SmartUploadException("totalMaxFileSize cannot exceed " + this.m_maxFileSize / 1024L + "k");
	          }

	          String fileName = fi.getName();

	          if ((fileName == null) || (fileName.length() == 0))
	          {
	        	TastySmartFile file = new TastySmartFile();
	            file.setIsMissing(true);
	            file.setFileItem(fi);
	            this.m_files.addFile(file);
	          }
	          else
	          {
				fileName = fileName.replace('\\', '/');
	            fileName = new java.io.File(fileName).getName();
	            TastySmartFile file = new TastySmartFile();
	            file.setParent(this);
	            file.setFieldName(fi.getFieldName());
	            file.setFileName(fileName);
	            file.setFileExt(getFileExt(fileName));
	            file.setFilePathName(fileName);
	            file.setIsMissing(fileName.length() == 0);
	            file.setContentType(fi.getContentType());
	            file.setContentDisp(fi.getContentType());
	            file.setTypeMIME(getTypeMIME(fileName));
	            file.setSubTypeMIME(getSubTypeMIME(fileName));
	            file.setSize((int)fi.getSize());
	            file.setFileItem(fi);
	            this.m_files.addFile(file);
	          }

	        }
	        else if ((encoding != null) && (encoding.length() > 0)) {
	          this.m_formRequest.putParameter(fi.getFieldName(), fi.getString(encoding));
	        } else {
	          this.m_formRequest.putParameter(fi.getFieldName(), fi.getString());
	        }

	      }
	    /*  
		int i = 0;
		// boolean flag = false;
		boolean flag1 = false;
		// boolean flag2 = false;
		long l = 0L;
		// String s = "";//new String();
		// String s2 = "";//new String();
		String s4 = ""; // new String();
		String s5 = ""; // new String();
		String s6 = ""; // new String();
		String s7 = ""; // new String();
		String s8 = ""; // new String();
		String s9 = ""; // new String();
		String s10 = ""; // new String();
		m_totalBytes = m_request.getContentLength();
		m_binArray = new byte[m_totalBytes];
		int j;
		for (; i < m_totalBytes; i += j) {
			try {
				m_request.getInputStream();
				j = m_request.getInputStream().read(m_binArray, i, m_totalBytes - i);
			} catch (Exception exception) {
				throw new SmartUploadException("Unable to upload.");
			}
		}

		for (; !flag1 && m_currentIndex < m_totalBytes; m_currentIndex++) {
			if (m_binArray[m_currentIndex] == 13) {
				flag1 = true;
			} else {
				m_boundary = m_boundary + (char) m_binArray[m_currentIndex];

			}
		}
		if (m_currentIndex == 1) {
			return;
		}
		for (m_currentIndex++; m_currentIndex < m_totalBytes; m_currentIndex = m_currentIndex + 2) {
			String s1 = getDataHeader();
			m_currentIndex = m_currentIndex + 2;
			boolean flag3 = s1.indexOf("filename") > 0;
			String s3 = getDataFieldValue(s1, "name");
			if (flag3) {
				s6 = getDataFieldValue(s1, "filename");
				s4 = getFileName(s6);
				s5 = getFileExt(s4);
				s7 = getContentType(s1);
				s8 = getContentDisp(s1);
				s9 = getTypeMIME(s7);
				s10 = getSubTypeMIME(s7);
			}
			getDataSection();
			if (flag3 && s4.length() > 0) {
				if (m_deniedFilesList.contains(s5)) {
					throw new SecurityException("The extension of the file is denied to be uploaded (1015).");
				}
				if (!m_allowedFilesList.isEmpty() && !m_allowedFilesList.contains(s5)) {
					throw new SecurityException("The extension of the file is not allowed to be uploaded (1010).");
				}
				if (m_maxFileSize > 0L && (long) ((m_endData - m_startData) + 1) > m_maxFileSize) {
					throw new SecurityException("Size exceeded for this file : " + s4 + " (1105).");
				}
				l += (m_endData - m_startData) + 1;
				if (m_totalMaxFileSize > 0L && l > m_totalMaxFileSize) {
					throw new SecurityException("Total File Size exceeded (1110).");
				}
			}
			if (flag3) {
				TastySmartFile file = new TastySmartFile();
				file.setParent(this);
				file.setFieldName(s3);
				file.setFileName(s4);
				file.setFileExt(s5);
				file.setFilePathName(s6);
				file.setIsMissing(s6.length() == 0);
				file.setContentType(s7);
				file.setContentDisp(s8);
				file.setTypeMIME(s9);
				file.setSubTypeMIME(s10);
				if (s7.indexOf("application/x-macbinary") > 0) {
					m_startData = m_startData + 128;
				}
				file.setSize((m_endData - m_startData) + 1);
				file.setStartData(m_startData);
				file.setEndData(m_endData);
				m_files.addFile(file);
			} else {
				String s11 = new String(m_binArray, m_startData, (m_endData - m_startData) + 1, "utf-8");
				m_formRequest.putParameter(s3, s11);
			}
			if ((char) m_binArray[m_currentIndex + 1] == '-') {
				break;
			}
		}*/
	}

	public int save(String s) throws ServletException, IOException, SmartUploadException {
		return save(s, 0);
	}

	public int save(String s, int i) throws ServletException, IOException, SmartUploadException {
		int j = 0;
	    if (s == null)
	      s = this.m_application.getRealPath("/");
	    if (s.indexOf("/") != -1)
	    {
	      if (s.charAt(s.length() - 1) != '/')
	        s = s + "/";
	    }
	    else if (s.charAt(s.length() - 1) != '\\')
	      s = s + "\\";
	    for (int k = 0; k < this.m_files.getCount(); ++k) {
	      if (this.m_files.getFile(k).isMissing()) {
	        continue;
	      }

	      this.m_files.getFile(k).saveAs(s + this.m_files.getFile(k).getFileName(), i);
	      ++j;
	    }

	    return j;
	}

	//add by liudianbing
	
	
	
	// Add
	private String[] FileNames;

	public String[] getFileNames() {
		// Method may expose internal representation by returning array
		// Returning an array value stored in one of the object's fields exposes
		// the internal representation of the object.? For classes shared by
		// other untrusted classes, this could potentially be a security issue.?
		// Returning a new copy of the array is better approach in many
		// situations.
		String[] vFileNames = new String[FileNames.length];
		System.arraycopy(FileNames, 0, vFileNames, 0, FileNames.length);
		return vFileNames;
	}

	public int getSize() {
		return m_totalBytes;
	}

	public byte getBinaryData(int i) {
		byte byte0;
		try {
			byte0 = m_binArray[i];
		} catch (Exception exception) {
			throw new ArrayIndexOutOfBoundsException("Index out of range (1005).");
		}
		return byte0;
	}

	public TastySmartFiles getFiles() {
		return m_files;
	}

	public SmartRequest getRequest() {
		return m_formRequest;
	}

	public void downloadFile(String s) throws ServletException, IOException, SmartUploadException {
		downloadFile(s, null, null);
	}

	public void downloadFile(String s, String s1) throws ServletException, IOException, SmartUploadException, SmartUploadException {
		downloadFile(s, s1, null);
	}

	public void downloadFile(String s, String s1, String s2) throws ServletException, IOException, SmartUploadException {
		downloadFile(s, s1, s2, 65000);
	}

	public void downloadFile(String s, String s1, String s2, int i) throws ServletException, IOException, SmartUploadException {
		if (s == null) {
			throw new IllegalArgumentException("File '" + s + "' not found (1040).");
		}
		if (s.equals("")) {
			throw new IllegalArgumentException("File '" + s + "' not found (1040).");
		}
		if (!isVirtual(s) && m_denyPhysicalPath) {
			throw new SecurityException("Physical path is denied (1035).");
		}
		if (isVirtual(s)) {
			s = m_application.getRealPath(s);
		}
		java.io.File file = new java.io.File(s);
		FileInputStream fileinputstream = new FileInputStream(file);
		long l = file.length();
		// boolean flag = false;
		int k = 0;
		byte abyte0[] = new byte[i];
		if (s1 == null) {
			m_response.setContentType("application/x-msdownload");
		} else {
			if (s1.length() == 0) {
				m_response.setContentType("application/x-msdownload");
			} else {
				m_response.setContentType(s1);
			}
		}
		m_response.setContentLength((int) l);
		m_contentDisposition = m_contentDisposition != null ? m_contentDisposition : "attachment;";
		if (s2 == null) {
			m_response.setHeader("Content-Disposition", m_contentDisposition + " filename=" + getFileName(s));
		} else {
			if (s2.length() == 0) {
				m_response.setHeader("Content-Disposition", m_contentDisposition);
			} else {
				m_response.setHeader("Content-Disposition", m_contentDisposition + " filename=" + s2);
			}
		}
		while ((long) k < l) {
			int j = fileinputstream.read(abyte0, 0, i);
			k += j;
			m_response.getOutputStream().write(abyte0, 0, j);
		}
		fileinputstream.close();
	}

	public void downloadField(ResultSet resultset, String s, String s1, String s2) throws ServletException, IOException, SQLException {
		if (resultset == null) {
			throw new IllegalArgumentException("The RecordSet cannot be null (1045).");
		}
		if (s == null) {
			throw new IllegalArgumentException("The columnName cannot be null (1050).");
		}
		if (s.length() == 0) {
			throw new IllegalArgumentException("The columnName cannot be empty (1055).");
		}
		byte abyte0[] = resultset.getBytes(s);
		if (s1 == null) {
			m_response.setContentType("application/x-msdownload");
		} else {
			if (s1.length() == 0) {
				m_response.setContentType("application/x-msdownload");
			} else {
				m_response.setContentType(s1);
			}
		}
		m_response.setContentLength(abyte0.length);
		if (s2 == null) {
			m_response.setHeader("Content-Disposition", "attachment;");
		} else {
			if (s2.length() == 0) {
				m_response.setHeader("Content-Disposition", "attachment;");
			} else {
				m_response.setHeader("Content-Disposition", "attachment; filename=" + s2);
			}
		}
		m_response.getOutputStream().write(abyte0, 0, abyte0.length);
	}

	public void fieldToFile(ResultSet resultset, String s, String s1) throws ServletException, IOException, SmartUploadException, SQLException {
		try {
			if (m_application.getRealPath(s1) != null) {
				s1 = m_application.getRealPath(s1);
			}
			InputStream inputstream = resultset.getBinaryStream(s);
			FileOutputStream fileoutputstream = new FileOutputStream(s1);
			int i;
			while ((i = inputstream.read()) != -1) {
				fileoutputstream.write(i);
			}
			fileoutputstream.close();
		} catch (Exception exception) {
			throw new SmartUploadException("Unable to save file from the DataBase (1020).");
		}
	}

	private String getDataFieldValue(String s, String s1) {
		String s2 = ""; // = new String();
		String s3 = ""; // = new String();
		int i = 0;
		// boolean flag = false;
		// boolean flag1 = false;
		// boolean flag2 = false;
		s2 = s1 + "=" + '"';
		i = s.indexOf(s2);
		if (i > 0) {
			int j = i + s2.length();
			int k = j;
			s2 = "\"";
			int l = s.indexOf(s2, j);
			if (k > 0 && l > 0) {
				s3 = s.substring(k, l);
			}
		}
		// filter seyo
		if (s3.length() > 0) {
			if (s3.indexOf(".") == -1) {
				for (i = 0; i < m_allowedFilesList.size(); i++) {
					if (s3.endsWith(m_allowedFilesList.get(i).toString())) {
						s3 = "xxx." + m_allowedFilesList.get(i).toString();
						System.out.println(s3);
						break;
					}
				}
			}
		}
		return s3;
	}

	private String getFileExt(String s) {
		String s1; // = new String();
		int i = 0;
		int j = 0;
		if (s == null) {
			return null;
		}
		i = s.lastIndexOf('.') + 1;
		j = s.length();
		s1 = s.substring(i, j);
		if (s.lastIndexOf('.') > 0) {
			return s1;
		} else {
			return "";
		}
	}

	private String getContentType(String s) {
		String s1 = ""; // = new String();
		String s2 = ""; // = new String();
		int i = 0;
		// boolean flag = false;
		s1 = "Content-Type:";
		i = s.indexOf(s1) + s1.length();
		if (i != -1) {
			int j = s.length();
			s2 = s.substring(i, j);
		}
		return s2;
	}

	private String getTypeMIME(String s) {
		// String s1 = new String();
		int i = 0;
		i = s.indexOf("/");
		if (i != -1) {
			return s.substring(1, i);
		} else {
			return s;
		}
	}

	private String getSubTypeMIME(String s) {
		// String s1 = new String();
		// boolean flag = false;
		int i = 0;
		i = s.indexOf("/") + 1;
		if (i != -1) {
			int j = s.length();
			return s.substring(i, j);
		} else {
			return s;
		}
	}

	

	private void getDataSection() {
		// boolean flag = false;
		// String s = "";
		// String s = new String();
		int i = m_currentIndex;
		int j = 0;
		int k = m_boundary.length();
		m_startData = m_currentIndex;
		m_endData = 0;
		while (i < m_totalBytes) {
			if (m_binArray[i] == (byte) m_boundary.charAt(j)) {
				if (j == k - 1) {
					m_endData = ((i - k) + 1) - 3;
					break;
				}
				i++;
				j++;
			} else {
				i++;
				j = 0;
			}
		}
		m_currentIndex = m_endData + k + 3;
	}

	private String getDataHeader() throws UnsupportedEncodingException {
		// boolean flag = false;
		int i = m_currentIndex;
		int j = 0;
		for (boolean flag1 = false; !flag1;) {
			if (m_binArray[m_currentIndex] == 13 && m_binArray[m_currentIndex + 2] == 13) {
				flag1 = true;
				j = m_currentIndex - 1;
				m_currentIndex = m_currentIndex + 2;
			} else {
				m_currentIndex++;
			}
		}
		String s = new String(m_binArray, i, (j - i) + 1, "utf-8");
		return s;
	}

	private String getFileName(String s) {
		// String s1 = ""; // = new String();
		// String s2 = ""; // = new String();
		// boolean flag = false;
		// boolean flag1 = false;
		// boolean flag2 = false;
		int i = 0;
		i = s.lastIndexOf('/');
		if (i != -1) {
			return s.substring(i + 1, s.length());
		}
		i = s.lastIndexOf('\\');
		if (i != -1) {
			return s.substring(i + 1, s.length());
		} else {
			return s;
		}
	}

	public void setDeniedFilesList(String s) throws ServletException, IOException, SQLException {
		// String s1 = "";
		if (s != null) {
			String s2 = "";
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ',') {
					if (!m_deniedFilesList.contains(s2)) {
						m_deniedFilesList.addElement(s2);
					}
					s2 = "";
				} else {
					s2 = s2 + s.charAt(i);
				}
			}

			// if(s2 != "")
			if (!s2.equals("")) {
				m_deniedFilesList.addElement(s2);
			}
		} else {
			m_deniedFilesList = null;
		}
	}

	public void setAllowedFilesList(String s) {
		// String s1 = "";
		if (s != null) {
			String s2 = "";
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ',') {
					if (!m_allowedFilesList.contains(s2)) {
						m_allowedFilesList.addElement(s2);
					}
					s2 = "";
				} else {
					s2 = s2 + s.charAt(i);
				}
			}
			// if(s2 != "")
			if (!s2.equals("")) {
				m_allowedFilesList.addElement(s2);
			}
		} else {
			m_allowedFilesList = null;
		}
	}

	public void setDenyPhysicalPath(boolean flag) {
		m_denyPhysicalPath = flag;
	}

	public void setForcePhysicalPath(boolean flag) {
		// m_forcePhysicalPath = flag;
	}

	public void setContentDisposition(String s) {
		m_contentDisposition = s;
	}

	public void setTotalMaxFileSize(long l) {
		m_totalMaxFileSize = l;
	}

	public void setMaxFileSize(long l) {
		m_maxFileSize = l;
	}

	protected String getPhysicalPath(String s, int i) throws IOException {
		String s1 = ""; // new String();
		String s2 = ""; // new String();
		String s3 = ""; // new String();
		boolean flag = false;
		s3 = System.getProperty("file.separator");
		if (s == null) {
			throw new IllegalArgumentException("There is no specified destination file (1140).");
		}
		if (s.equals("")) {
			throw new IllegalArgumentException("There is no specified destination file (1140).");
		}
		if (s.lastIndexOf("\\") >= 0) {
			s1 = s.substring(0, s.lastIndexOf("\\"));
			s2 = s.substring(s.lastIndexOf("\\") + 1);
		}
		if (s.lastIndexOf("/") >= 0) {
			s1 = s.substring(0, s.lastIndexOf("/"));
			s2 = s.substring(s.lastIndexOf("/") + 1);
		}
		s1 = s1.length() != 0 ? s1 : "/";
		java.io.File file = new java.io.File(s1);
		if (file.exists()) {
			flag = true;
		}
		if (i == 0) {
			if (isVirtual(s1)) {
				s1 = m_application.getRealPath(s1);
				if (s1.endsWith(s3)) {
					s1 = s1 + s2;
				} else {
					s1 = s1 + s3 + s2;
				}
				return s1;
			}
			if (flag) {
				if (m_denyPhysicalPath) {
					throw new IllegalArgumentException("Physical path is denied (1125).");
				} else {
					return s;
				}
			} else {
				throw new IllegalArgumentException("This path does not exist (1135).");
			}
		}
		if (i == 1) {
			if (isVirtual(s1)) {
				s1 = m_application.getRealPath(s1);
				if (s1.endsWith(s3)) {
					s1 = s1 + s2;
				} else {
					s1 = s1 + s3 + s2;
				}
				return s1;
			}
			if (flag) {
				throw new IllegalArgumentException("The path is not a virtual path.");
			} else {
				throw new IllegalArgumentException("This path does not exist (1135).");
			}
		}
		if (i == 2) {
			if (flag) {
				if (m_denyPhysicalPath) {
					throw new IllegalArgumentException("Physical path is denied (1125).");
				} else {
					return s;
				}
			}
			if (isVirtual(s1)) {
				throw new IllegalArgumentException("The path is not a physical path.");
			} else {
				throw new IllegalArgumentException("This path does not exist (1135).");
			}
		} else {
			return null;
		}
	}

	public void uploadInFile(String s) throws IOException, SmartUploadException {
		// boolean flag = false;
		int i = 0;
		int j = 0;
		if (s == null) {
			throw new IllegalArgumentException("There is no specified destination file (1025).");
		}
		if (s.length() == 0) {
			throw new IllegalArgumentException("There is no specified destination file (1025).");
		}
		if (!isVirtual(s) && m_denyPhysicalPath) {
			throw new SecurityException("Physical path is denied (1035).");
		}
		i = m_request.getContentLength();
		m_binArray = new byte[i];
		int k;
		for (; j < i; j += k) {
			try {
				k = m_request.getInputStream().read(m_binArray, j, i - j);
			} catch (Exception exception) {
				throw new SmartUploadException("Unable to upload.");
			}
		}

		if (isVirtual(s)) {
			s = m_application.getRealPath(s);
		}
		try {
			java.io.File file = new java.io.File(s);
			FileOutputStream fileoutputstream = new FileOutputStream(file);
			fileoutputstream.write(m_binArray);
			fileoutputstream.close();
		} catch (Exception exception1) {
			throw new SmartUploadException("The Form cannot be saved in the specified file (1030).");
		}
	}

	private boolean isVirtual(String s) {
		if (m_application.getRealPath(s) != null) {
			java.io.File file = new java.io.File(m_application.getRealPath(s));
			return file.exists();
		} else {
			return false;
		}
	}

	public String saveImg(String AppFolder, String field, String userId, String slab, int imageServer) throws Exception {

		FileNames = new String[m_files.getCount()];
		for (int k = 0; k < m_files.getCount(); k++) {
			if (!m_files.getFile(k).isMissing()) {
				java.util.Random r = new java.util.Random();
				int ti = r.nextInt();
				ti = Math.abs(ti);
				String c = String.valueOf(ti);

				Date t = new Date();

				String a = DateHelper.dateToString(t, "yyyyMMdd");
				String b = DateHelper.dateToString(t, "HHmm");
				String d = DateHelper.dateToString(t, "HHmmss");

				String filePath = radomPath(AppFolder, a, b);
				String imageId = d + c;

				String fileName = m_files.getFile(k).saveImg(filePath, imageId, field, userId, slab, imageServer);
				return imageId + "," + fileName;
			}
		}
		return "";
	}

	public int saveFile(String AppFolder, String fileName) throws ServletException, IOException, SmartUploadException {
		int j = 0;
		Date dt = new Date();
		String tdate = DateHelper.dateToString(dt, "yyyyMMdd");
		String thours = DateHelper.dateToString(dt, "HH");
		String ttimes = DateHelper.dateToString(dt, "yyyy-MM-dd_HH-mm");
		String curPath = ImageAppPath + AppFolder + "/" + tdate + "/" + thours + "/";
		File f = new File(curPath);
		/*
		 * if(!f.exists()){ f.mkdir(); }
		 */
		f.mkdirs();
		// System.out.println("m_application.getRealPath:::" + s);
		FileNames = new String[m_files.getCount()];
		for (int k = 0; k < m_files.getCount(); k++) {
			if (!m_files.getFile(k).isMissing()) {
				// System.out.println("s + m_files.getFile(k).getFileName():" +
				// s + m_files.getFile(k).getFileName());
				String fileName_ = m_files.getFile(k).getFileName();
				int findex_ = fileName_.indexOf(".");
				fileName_ = fileName_.substring(0, findex_) + "_" + ttimes + ".tfp";
				m_files.getFile(k).saveAs(curPath + fileName_, 0);
				FileNames[j] = curPath + m_files.getFile(k).getFileName();
				j++;
			}
		}

		return j;
	}

	public String radomPath(String AppFolder, String a, String b/* ,String c */) {

		//
		String filePath = AppFolder + "/" + a + "/" + b + "/"/* +c+"/" */;
		//
		checkPath(AppFolder, a, b/* ,c */);

		return filePath;
	}

	public void checkPath(String AppFolder, String a, String b/* ,String c */) {
		String curPath = ImageAppPath + AppFolder + "/" + a + "/" + b;
		File f = new File(curPath);
		f.mkdirs();
	}

	public static String copyImage(String folder, String img) {
		if (img.equals("null") || img.length() == 0)
			return "";
		String orginFileName_s = ImageAppPath + img;
		String destFileName_s = StringUtil.replace(orginFileName_s, "tmp", folder);
		String orginFileName_b = StringUtil.replace(orginFileName_s, "_s.", "_b.");
		String destFileName_b = StringUtil.replace(destFileName_s, "_s.", "_b.");

		String orginFileName_m = StringUtil.replace(orginFileName_s, "_s.", "_m.");
		String destFileName_m = StringUtil.replace(destFileName_s, "_s.", "_m.");

		copyFile(orginFileName_s, destFileName_s);
		copyFile(orginFileName_b, destFileName_b);
		copyFile(orginFileName_m, destFileName_m);

		return "";// jpegImage(folder,orginFileName_b);
	}

	public static String jpegImage(String folder, String img, int local) {
		if (img.equals("null") || img.length() == 0)
			return "";
		//
		if(folder!=null){
			folder =folder.toLowerCase();
		}
		try {
			boolean ret = false;
			String img_ =  img.toLowerCase();
			if (img_.startsWith("http://")) {
				String rstr = DBRunner.imageUrl;
				if (local == 1) {
					// 100907
					/*
					 * img_ = StringUtil.replace(img_,rstr,DBRunner.imagePath);
					 * rstr = "http://www.zushou365.com/house_imgs_model/";
					 */
				}
				img_ = StringUtil.replace(img_, rstr, DBRunner.imagePath);
			} else {
				img_ = img;
				if (folder != null && !folder.endsWith("jpeg") && !folder.endsWith("jpg")) {
					ret = true;
				}
			}
			if (!ret)
				return img;
			File file = new File(img_);
			FileInputStream inputeStream = new FileInputStream(file);
			// 构造Image对象
			Image src = null;
			try {
				src = ImageIO.read(inputeStream);
				inputeStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			int SQUAL = 200;
			/*
			 * if(width<SQUAL||height<SQUAL){101203 if(width<=height){ height =
			 * height * SQUAL/width; width = SQUAL; }else{ width = width *
			 * SQUAL/height; height = SQUAL; } ret = true; }
			 */
			// ----
			/*
			 * if(img_.endsWith("jpeg")||img_.endsWith("jpg")){
			 * 
			 * }else{ int index = img_.lastIndexOf("."); img_ =
			 * img_.substring(0,index)+".jpeg"; file = new File(img_); ret =
			 * true; }
			 */

			

			int top = 0;
			int left = 0;

			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D grap = bi.createGraphics();
			grap.setBackground(Color.white);
			grap.clearRect(0, 0, SQUAL, SQUAL);
			grap.drawImage(src, left, top, width, height, Color.white, null); // 绘制图
			src.flush();
			grap.dispose();
			bi.flush();

			// 输出到文件流
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			try {
				encoder.encode(bi); // 近JPEG编码
			} catch (ImageFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogWriter.log(e);
		}
		return "";

	}

	public static void deleteImage(String img) {
		if (img.equals("null") || img.length() == 0)
			return;
		String fileName_s = ImageAppPath + img;
		String fileName_b = StringUtil.replace(fileName_s, "_s.", "_b.");
		File file = new File(fileName_s);
		if (file.exists()) {
			file.delete();
		}
		file = new File(fileName_b);
		if (file.exists()) {
			file.delete();
		}
		file = null;
	}

	private static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	public static void copyFile(String orginFileName, String destFileName) {
		File orginFile = new File(orginFileName);
		int index = destFileName.lastIndexOf('/');
		String destFolderName = destFileName.substring(0, index);
		File destFolder = new File(destFolderName);
		if (!destFolder.exists())
			destFolder.mkdirs();
		try {
			FileInputStream input = new FileInputStream(orginFile);
			FileOutputStream output = new FileOutputStream(destFileName);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {

		} finally {
			orginFile = null;
			destFolder = null;
		}
	}

	public static void copyDirectiory(String topath, String frompath) {

		File copy = new File(topath);
		File bycopy = new File(frompath);

		// 创建拷贝目录
		copy.mkdirs();
		// 开始拷贝
		File[] file = bycopy.listFiles();
		try {

			if (file.length != 0) {

				for (int i = 0; i < file.length; i++) {
					if (file[i].isFile()) {
						FileInputStream input = new FileInputStream(file[i]);
						FileOutputStream output = new FileOutputStream(copy + "/" + file[i].getName());
						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
						output.close();
						input.close();
					}
					if (file[i].isDirectory()) {
						copyDirectiory(copy + "/" + file[i].getName(), bycopy + "/" + file[i].getName());
					}
				}
			}
		} catch (Exception e) {

		} finally {
			file = null;
		}
	}

	public static String copyCreditImage(String folder, String img, int userId, int creditType) {
		if (img.equals("null") || img.length() == 0)
			return "";
		if (!img.startsWith("tmp")) {
			return img;
		}
		String orginFileName_s = ImageAppPath + img;
		String destFileName_s = StringUtil.replace(orginFileName_s, "tmp", folder);
		//
		String imgid = getImageId(img);
		int index = img.lastIndexOf('/');
		String tFolder = img.substring(4, index);

		index = tFolder.indexOf("/");
		String tHm = tFolder.substring(index + 1);
		tHm = tHm + "/" + imgid;

		java.util.Random r = new java.util.Random();
		int ti = r.nextInt();
		ti = Math.abs(ti);
		String c = String.valueOf(ti);
		c = userId + c;
		c = MD5.getMD5(c.getBytes());
		c = userId + "_" + creditType + "_" + c;
		destFileName_s = StringUtil.replace(destFileName_s, tHm, c);

		String orginFileName_b = StringUtil.replace(orginFileName_s, "_s.", "_b.");
		String destFileName_b = StringUtil.replace(destFileName_s, "_s.", "_b.");
		copyFile(orginFileName_s, destFileName_s);
		copyFile(orginFileName_b, destFileName_b);

		String fName = StringUtil.replace(destFileName_s, ImageAppPath, "");
		return fName;
	}

	public static String getImageId(String img) {
		String[] sSplit = img.split("/");
		String tmpImgId_ = sSplit[sSplit.length - 1];
		String[] tmpImgId_Split = tmpImgId_.split("_");
		String imgid = tmpImgId_Split[0];
		return imgid;
	}
}
