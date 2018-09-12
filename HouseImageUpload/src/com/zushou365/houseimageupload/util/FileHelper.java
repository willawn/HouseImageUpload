package com.zushou365.houseimageupload.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;



/**
 * 
 * @author Seyo 100420
 * 1.4
 */
public class FileHelper {
	
	
	public static ArrayList<String> getFile(InputStream stream,String code){
		try {
		 ArrayList<String> arr = new ArrayList<String>(); 
		 String str;	
		 BufferedReader in= null;
		 if(code==null||code.length()==0){
        	 in= new BufferedReader(new InputStreamReader(stream));
         }else
        	 in= new BufferedReader(new InputStreamReader(stream,code));
		 
	      while ((str = in.readLine()) != null ){
	    	 if(!"".equals(str)){
	    		 arr.add(str);
	    	 }
	      }
	      return arr;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<String> getFile(InputStream stream){
		return getFile(stream,null);
	}
	
	public static ArrayList<String> getFileWithSpace(String filename,String code){
		try {
		 ArrayList<String> arr = new ArrayList<String>(); 
		 String word;
		 InputStream setdata = null;
         setdata = new FileInputStream(filename);
		
		 BufferedReader in= null;
		 
		 if(code==null||code.length()==0){
        	 in= new BufferedReader(new InputStreamReader(setdata));
         }else
        	 in= new BufferedReader(new InputStreamReader(setdata,code));
		 
	      while ( (word = in.readLine()) != null ){
	    		 arr.add(word);
	      }
	      in.close();
	      setdata.close();
	      return arr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveFile(String file,ArrayList<String> lines,String encode) {
		StringBuilder textSB = new StringBuilder();
		for(int i=0;i<lines.size();i++){
			textSB.append(lines.get(i));
		}
		saveFile(file,textSB.toString(),encode);
	}
	
	public static void saveFile(String file,ArrayList<String> lines) {
		saveFile(file,lines,null);
	}
	
	public static void saveFile(String file,String text) {
		saveFile(file,text,null);
	}
	
	public static void saveFile(String file,String text,String encode) {
		
		int index = file.lastIndexOf("/");
		if(index==-1){
			index = file.lastIndexOf("\\");
			String sub = file.substring(0,index);
			File f = new File(sub);
			if(!f.exists()){
				f.mkdir();
			}
		}
		PrintWriter out1 =null;
		try {
			if(encode==null||encode.length()==0){
				out1 = new PrintWriter(new FileWriter(file));
			}else
				out1 = new PrintWriter(new File(file),encode);
		} catch (IOException e) {
			System.err.println("err for creake ");
			
		}
		out1.print(text);
		
		out1.close();
	}
	
	public static void save(Properties prppertie,String file) {
		
		int index = file.lastIndexOf("/");
		if(index==-1){
			index = file.lastIndexOf("\\");
			String sub = file.substring(0,index);
			File f = new File(sub);
			if(!f.exists()){
				f.mkdir();
			}
		}
		try {
			prppertie.store(new FileOutputStream(file),DateHelper.dateToString(new Date()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static InputStream getInputStream(String file){
		InputStream stream = null;
        try {
        	stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stream;
	}
	
	public static InputStreamReader getInputStreamReader(String file){
		InputStream setdata;
        try {
			setdata = new FileInputStream(file);
			return new InputStreamReader(setdata);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		return null;
	}
	
	
	
	public static Properties gerProperties(String file) {
		
		try {
			Properties properties = new Properties();
			properties.load( getInputStream(file) );
			return properties;
		}
		catch(IOException e) {
			
		}
		//
		return null;
	}
	
	public static String[] getFolderFilesList(String folder)
	{
		try {
			File f = new File(folder);
			if(f.isDirectory())
			{
				String[] fl = f.list();
				return fl;
			}
		} catch (Exception e) {
			
		}
		return new String[] {};
	}
	
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	} 
	
	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	} 
	
	public static void moveFile(String oldPath, String newPath) { 
		copyFile(oldPath, newPath); 
		deleteFile(oldPath); 
	} 
	
	public static ArrayList<String> getFile(String filename){
		return getFile(filename,null);
	}
	//utf-8 gbk gb2312
	public static ArrayList<String> getFile(String filename,String code){
		try {
		 ArrayList<String> arr = new ArrayList<String>(); 
		 String word;
		 InputStream setdata;
         setdata = new FileInputStream(filename);
         BufferedReader in = null;
         if(code==null||code.length()==0){
        	 in= new BufferedReader(new InputStreamReader(setdata));
         }else
        	 in= new BufferedReader(new InputStreamReader(setdata,code));
	      while ( (word = in.readLine()) != null ){
	    	 if(!"".equals(word)){
	    		 arr.add(word);
	    	 }
	      }
	      return arr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getFileAsText(String filename,String code){
		try {
		 StringBuilder sb = new StringBuilder(); 
		 String str;
		 InputStream setdata;
         setdata = new FileInputStream(filename);
		
         BufferedReader in = null;
         if(code==null||code.length()==0){
        	 in= new BufferedReader(new InputStreamReader(setdata));
         }else
        	 in= new BufferedReader(new InputStreamReader(setdata,code));
         
	      while ( (str = in.readLine()) != null ){
	    	 if(!"".equals(str)){
	    		 //sb.append(str+"\\n");
	    		 sb.append(str);
	    	 }
	      }
	      return sb.toString();
	     /* InputStreamReader in= new InputStreamReader(setdata,code);
			 int ch;
			 while((ch = in.read())!=-1)
			 {
				 System.out.print((char)ch); 
				 sb.append((char)ch);
			 } 
		      return sb.toString();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getFileAsText(String filename){
		return getFileAsText(filename,null);
	}
	
	public static final String findAsResource(final String path) {
		URL url = null;

		// First, try to locate this resource through the current
		// context classloader.
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader!=null) {
			url = contextClassLoader.getResource(path);
		}
		if (url != null)
			return url.getPath();

		// Next, try to locate this resource through this class's classloader
		url = FileHelper.class.getClassLoader().getResource(path);
		if (url != null)
			return url.getPath();

		// Next, try to locate this resource through the system classloader
		url = ClassLoader.getSystemClassLoader().getResource(path);

		// Anywhere else we should look?
		if (url == null) {
			return path;
		}

		return url.getPath();
	}
	public static String replace(String strSource, String strFrom, String strTo) {
		if (strFrom == null || strFrom.equals(""))
			return strSource;
		String strDest = "";
		int intFromLen = strFrom.length();
		int intPos;
		while ((intPos = strSource.indexOf(strFrom)) != -1) {
			strDest = strDest + strSource.substring(0, intPos);
			strDest = strDest + strTo;
			strSource = strSource.substring(intPos + intFromLen);
		}
		strDest = strDest + strSource;
		return strDest;
	} 
	
}
