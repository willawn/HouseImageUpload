package com.zushou365.houseimageupload.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;


public class ImgMD5 {
	public static String MD5(byte[] s)
    {
        //16进制字符
        char hexDigits[] =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try
        {
            byte[] strTemp = s;
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            //移位 输出字符串
            for (int i = 0; i < j; i++)
            {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e)
        {
            return null;
        }
    }
	
	public static byte[] getImgBytes(String fileName){
		byte[] b = null;
        try{
            InputStream in = new FileInputStream(fileName);
            b = new byte[in.available()];
            in.read(b, 0 ,b.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
	}
	
	public static String MD5(String fileName){
		return MD5(getImgBytes(fileName));
	}
}
