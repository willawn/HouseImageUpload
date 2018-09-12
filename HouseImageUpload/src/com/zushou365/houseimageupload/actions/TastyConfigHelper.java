package com.zushou365.houseimageupload.actions;

import java.util.ArrayList;

import com.zushou365.houseimageupload.dbmanager.DBRunner;
public class TastyConfigHelper {
	
	public static String checkImgMd5Exists(String md5,int imageServer){
		String sql="select id,pic,share,userId from house_pics where picMd5='"+md5+"' and imageServer="+imageServer;
		ArrayList<String> datas = DBRunner.dataSql(sql, ",");
		if(datas.size()>0)
			return datas.get(0);
		else
			return "";
	}
	
	public static void recordHousePicsUser(String userId,String picId){
		String sql="insert into house_pics_user(userId,picId) values("+userId+","+picId+")";
		DBRunner.excuteSql(sql);
	}
	
}
