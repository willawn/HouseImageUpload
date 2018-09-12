package com.zushou365.houseimageupload.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class SpellEnName {
	public static String[] getAllSeq(String cnStr){
		ArrayList<String> arr = getAllCharSeq(cnStr);
		//
		
		HashSet<String> hs = new HashSet<String>();
		HashSet<String> hs2 = new HashSet<String>();
		ArrayList<ArrayList<String>> okarr = allSeq(arr);
		for(int i=0;i<okarr.size();i++){
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			ArrayList<String> tmpArr = okarr.get(i);
			for(int k=0;k<tmpArr.size();k++){
				String tmpStr = tmpArr.get(k);
				sb.append(tmpStr);
				tmpStr = String.valueOf(tmpStr.charAt(0));
				sb2.append(tmpStr);
			}
			hs.add(sb.toString().toLowerCase());
			hs2.add(sb2.toString().toLowerCase());
		}
		String fullEnName = transSet(hs);
		String firstEnName = transSet(hs2);
		
		String[] result = new String[]{fullEnName,firstEnName};
		
		return result;
		
	}
	public static String transSet(HashSet hs){
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = hs.iterator();
		int i = 0 ;
		int tSize = hs.size();
		while(it.hasNext()){
			i++;
			sb.append(it.next());
			if(i<tSize)
				sb.append(",");
		}
		return sb.toString();
	}
	public static ArrayList<String> getAllCharSeq(String cnStr){
		ArrayList<String> arr = new ArrayList<String>();
		char[] chars = cnStr.toCharArray();
		for (int i = 0, Len = chars.length; i < Len; i++) {
			String ascii = CnToSpell.getCnAscii(chars[i]);
			if (ascii.length() == 0) { 
				arr.add(String.valueOf(chars[i]));
			} else {
				String spell = CnToSpell.getSpellByAscii(ascii);
				if (spell == null) {
					arr.add(String.valueOf(chars[i]));
				} else {
					arr.add(spell);
				} // end of if spell == null
			} // end of if ascii <= -20400
		} // end of for
		return arr;
	}
	
	public static ArrayList<ArrayList<String>> allSeq(ArrayList<String> arr){
		int x = 0;
		HashMap<Integer,String[]> sets = new HashMap<Integer,String[]>();
		for(int i=0;i<arr.size();i++){
			if(arr.get(i).equals(",")){
				
			}else{
				if(arr.get(i).indexOf(",")>-1){
					x++;
					String[] tmpStrSplit = arr.get(i).split(",");
					sets.put(i, tmpStrSplit);
				}
			}
		}
		
		ArrayList<ArrayList<String>> okArr = new ArrayList<ArrayList<String>>();
		okArr.add(arr);
		if(x==0){
			return okArr;
		}
		
		
		Iterator<Integer> it = sets.keySet().iterator();
		while(it.hasNext()){
			int index = it.next();
			okArr = getArr(sets,index,okArr);
		}
		
		return okArr;
	}
	
	public static ArrayList<ArrayList<String>> getArr(HashMap<Integer,String[]> sets,int index,ArrayList<ArrayList<String>> arr){
		
		ArrayList<ArrayList<String>> okarr = new ArrayList<ArrayList<String>>();
		
		String tmpStr;
		String[] tmpStrSpit = sets.get(index);
		for(int j=0;j<tmpStrSpit.length;j++){
			
			tmpStr = tmpStrSpit[j];
			
			for(int i=0;i<arr.size();i++){
				ArrayList<String> tLineArr = arr.get(i);
				ArrayList<String> tmpArr = new ArrayList<String>();
				for(int k=0;k<tLineArr.size();k++){
					String tLineStr = tLineArr.get(k);
					if(k==index){
						tmpArr.add(tmpStr);
					}else{
						tmpArr.add(tLineStr);
					}
				}
				
				okarr.add(tmpArr);
			}
			
		}
		
		return okarr;
	}
	
}
