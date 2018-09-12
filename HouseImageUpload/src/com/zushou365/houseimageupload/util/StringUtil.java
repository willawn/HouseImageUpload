package com.zushou365.houseimageupload.util;

public class StringUtil {
	
	public static String replace(String strSource, String strFrom, String strTo) {
		if(strSource==null||strSource.length()==0)
			return "";
		if (strFrom == null || strFrom.equals(""))
			return strSource;
		String strDest = "";
		int intFromLen = strFrom.length();
		int intPos;
		//System.out.println(strSource);
		while ((intPos = strSource.indexOf(strFrom)) != -1) {
			strDest = strDest + strSource.substring(0, intPos);
			strDest = strDest + strTo;
			strSource = strSource.substring(intPos + intFromLen);
		}
		strDest = strDest + strSource;
		return strDest;
	} 

}
