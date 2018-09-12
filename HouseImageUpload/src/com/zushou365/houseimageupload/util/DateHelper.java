package com.zushou365.houseimageupload.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;




/**
 * 
 * @author Seyo
 * 1.02
 * 20100530
 */
public class DateHelper {
	
	public static String dateToString(Object o) {
		if (o == null)
			return null;
		String pvalue = "";
		SimpleDateFormat df = null;
		try{
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			pvalue = df.format(o);
			
		}catch(Exception ex){
			df = new SimpleDateFormat("yyyy-MM-dd");
			try{
				pvalue = df.format(o);
			}catch(Exception ex2){
				
			}
		}
		if(pvalue!=null&&pvalue.endsWith(" 00:00:00")){
			int index = pvalue.indexOf(" 00:00:00");
			pvalue = pvalue.substring(0,index);
		}
		return pvalue;
	}

	public static String dateToString(Object o, String format) {
		if (o == null)
			return null;
		try{
			SimpleDateFormat df = new SimpleDateFormat(format);
			return df.format(o);
		}catch(Exception ex){
			return "";
		}
	}

	public static Date stringToDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			return df.parse(dateStr);
		}catch(Exception ex){
			df = new SimpleDateFormat("yyyy-MM-dd");
			try{
				return df.parse(dateStr);
			}catch(Exception ex2){
				
			}
		}
		return null;
	}

	public static Date stringToDate(String dateStr, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			Date date = df.parse(dateStr);
			return date;
		} catch (Exception ex) {

		}
		return null;
	}
	
	public static Date getDate(Date date, int day) {
		long myTime=(date.getTime()/1000)+(60*60*24)*(day);
		Date nDate = new Date(myTime*1000);
		return nDate;
	}
	
	public static Date getMonth(Date date, int month) {
		 Calendar cal = Calendar.getInstance();   
		 cal.setTime(date);
		 cal.add(Calendar.MONTH, month);   
		 return cal.getTime();
	}
	
	public static Date getYear(Date date, int year) {
		 Calendar cal = Calendar.getInstance();   
		 cal.setTime(date);
		 cal.add(Calendar.YEAR, year);   
		 return cal.getTime();
	}
	
	public static int getDateInternal(Date d1,Date d2){
		int internal = 0 ;
		long internalTime = Math.abs((d1.getTime() - d2.getTime())/1000);
		int oneDay = 86400;
		internal = (int)internalTime/oneDay;
		if(internalTime%oneDay>0)
			internal++;
		return internal;
	}
	
	public static Date getHour(Date date, int hour) {
		long myTime=(date.getTime()/1000)+(60*60)*(hour);
		Date nDate = new Date(myTime*1000);
		return nDate;
	}
	
	public static Date getMinute(Date date, int minute) {
		long myTime=(date.getTime()/1000)+(60)*(minute);
		Date nDate = new Date(myTime*1000);
		return nDate;
	}
	
	public static boolean isSameDate(Date d1,Date d2){
		Calendar c1 = Calendar.getInstance();   
        Calendar c2 = Calendar.getInstance();   
        c1.setTime(d1);   
        c2.setTime(d2);   
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))   
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))   
                && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));  
	}
	
	

	public static boolean compareTime(String time1, String time2) {
		Date dt1 = stringToDate(time1, "HH:mm");
		Date dt2 = stringToDate(time2, "HH:mm");
		if (dt1.after(dt2))
			return true;
		else
			return false;
	}
	
	public static int getDateSecond(Date beginDate, Date endDate){
		// 86400000 = 一天
		return (int) ((int)endDate.getTime() -beginDate.getTime()); 
	}
	
	public static Date getCurDate(){
		return stringToDate(dateToString(new Date()),"yyyy-MM-dd");
	}
}
