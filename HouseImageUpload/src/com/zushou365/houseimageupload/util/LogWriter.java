package com.zushou365.houseimageupload.util;

import java.io.*;
import java.util.*;

public class LogWriter {
	private static final String DefalutLogFilePathName = "";
	private static LogWriter logwriter; 
	private static InputStream fin; 
	private static Properties pro; 
	private static PrintWriter out; 
	private static String logFileName;
	private static PrintWriter iOut;
	

	private LogWriter() {
		outInit();
	}
	
	public LogWriter(String filename) {
		outInit(filename);
	}

	public static synchronized void log(String message) {
		if (logwriter == null || (out == null)) {
			logwriter = new LogWriter();
		}
		if (out != null) {
			out.println(new java.util.Date() + ":" + message);
		}
	}
	
	public static synchronized void write(String text) {
		if (logwriter == null || (out == null)) {
			logwriter = new LogWriter();
		}
		if (out != null) {
			out.println(text);
		}
	}
	
	public static synchronized void write(int num) {
		if (logwriter == null || (out == null)) {
			logwriter = new LogWriter();
		}
		if (out != null) {
			Integer n=(Integer)num;
			out.println("数目:"+n.toString());
		}
	}
	
	public static synchronized void log(int num) {
		if (logwriter == null || (out == null)) {
			logwriter = new LogWriter();
		}
		if (out != null) {
			Integer n=(Integer)num;
			out.println("数目:"+n.toString());
		}
	}


	public static synchronized void log(Exception ex) {
		if (logwriter == null || (out == null))
			logwriter = new LogWriter();
		if (out != null) {
			out.println(new java.util.Date() + ":");
			ex.printStackTrace(out);
		}
	}
	
	

	private void outInit() {
		if (logFileName == null)
			logFileName = getLogFileName();
		try {
			File f=new File(logFileName);
			if(!f.exists())
			{
			f.createNewFile();} 

			if (out == null) {
				out = new PrintWriter(new FileWriter(logFileName, true), true);
				;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			out = null;
		}
	}
	
	private void outInit(String filename) {
		if (filename == null)
			filename = getLogFileName();
		try {
			File f=new File(filename);
			if(!f.exists())
			{
			f.createNewFile();} 

			if (iOut == null) {
				iOut = new PrintWriter(new FileWriter(filename, true), true);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			iOut = null;
		}
	}
	
	public  synchronized void iWrite(String text) {
		if (iOut != null) {
			iOut.println(text);
		}
	}

	public void free() {
		try {
			this.logwriter = null;
			if (out != null)
				this.out.close();
			if (fin != null)
				this.fin.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getLogFileName() {
		return logFileName;
	}

	public static void setLogFileName(String logFileName) {
		LogWriter.logFileName = logFileName;
	}

	
}
