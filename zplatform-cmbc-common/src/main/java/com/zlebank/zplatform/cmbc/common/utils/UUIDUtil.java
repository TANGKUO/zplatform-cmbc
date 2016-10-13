package com.zlebank.zplatform.cmbc.common.utils;

import java.util.UUID;

public class UUIDUtil {

	public static String uuid(){
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
	public static void main(String[] args) {
        System.out.println( uuid());
    }
	
}
