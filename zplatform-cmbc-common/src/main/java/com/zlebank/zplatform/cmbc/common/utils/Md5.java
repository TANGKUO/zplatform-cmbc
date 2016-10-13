package com.zlebank.zplatform.cmbc.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	public String str;
	private static Md5 instance;
	private static long currentTime;
	
    public static Md5 getInstance() {
	    if (instance == null) {
	        instance = new Md5();
	    }
	    return instance;
    }
    
    public static void setNull(){
    	if (instance != null) {
    		instance = null;
    	}
    }
    
    public static void setTimeNull(){
    	currentTime = 0;
    }
	
	public String md5s(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			
			int i;
			
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
//			str = buf.toString().substring(8, 24);
			
//			str = MyNative.happy(str);
//			System.out.println("result: " + buf.toString());// 32λ�ļ���
//			System.out.println("result: " + buf.toString().substring(8, 24));// 16λ�ļ���
			
			return str;
		}catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

	 public long getTime(){
		 if (currentTime == 0) {
			 currentTime = System.currentTimeMillis();
		    }
		 return currentTime;
	 }
}
