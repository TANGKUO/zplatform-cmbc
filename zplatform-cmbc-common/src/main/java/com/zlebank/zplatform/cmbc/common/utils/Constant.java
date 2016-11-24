/* 
 * Constant.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 上午9:32:56
 * @since 
 */
public class Constant {

	private static final Logger log = LoggerFactory.getLogger(Constant.class);
	public static final String REALNAMEAUTH = "1004";
    public static final String REALNAMEAUTHQUERY = "3004";
    public static final String WITHHOLDING = "1003";
    public static final String WITHHOLDINGQUERY = "3003";
    public static final String WHITELIST = "1007";
    public static final String WHITELISTQUERY = "3007";
    public static final String WITHHOLDINGSELF = "1009";
    public static final String WITHHOLDINGSELFQUERY = "3009";
    
    public static final String WITHHOLDINGSELF_REALNAME = "1104";
    public static final String WITHHOLDINGSELF_REALNAMEQUERY = "3104";
    
    public static final String REALTIME_INSTEADPAY = "1002";
    public static final String REALTIME_INSTEADPAY_QUERY = "3002";
	
    
    private String cmbc_version;
    private String cmbc_merid;
    private String cmbc_mername;
    private String cmbc_withholding_chnl_code;
    private String cmbc_withholding_public_key;
    private String cmbc_withholding_private_key;
    private String cmbc_withholding_ip;
    private int cmbc_withholding_port;
    private String cmbc_insteadpay_ip;
    private int cmbc_insteadpay_port;
    private String cmbc_insteadpay_merid;
    private String cmbc_self_withholding_ip;
    private int cmbc_self_withholding_port;
    private String cmbc_insteadpay_batch_md5;
    private String cmbc_secretfilepath;
    private String cmbc_download_file_path;
    private String cmbc_plainfilepath;
    private String cmbc_self_merid;
    private String cmbc_self_merchant;
    private String cmbc_withholding_self_public_key;
    private String cmbc_withholding_self_private_key;
    private String cmbc_withholding_self_chnl_code;
    
    private String cmbc_insteadpay_privatekey;
    private String cmbc_insteadpay_publickey;
    private boolean canRun;
    private String refresh_interval;
    private static Constant constant;
    public static synchronized Constant getInstance(){
		if(constant==null){
			constant = new Constant();
		}
		return constant;
	}
    
	private Constant(){
		refresh();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (canRun) {
					try {
						refresh();
						int interval = NumberUtils.toInt(refresh_interval, 60) * 1000;// 刷新间隔，单位：秒
						log.info("refresh Constant datetime:"+DateUtil.getCurrentDateTime());
						Thread.sleep(interval);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	public void refresh(){
		try {
			String path = "/home/web/trade/";
			File file = new File(path+ "zlrt.properties");
			if(!file.exists()){
			    path = getClass().getResource("/").getPath();
			    file = null;
			}
			Properties prop = new Properties();
			InputStream stream = null;
			stream = new BufferedInputStream(new FileInputStream(new File(path+ "zlrt.properties")));
			prop.load(stream);
			
			cmbc_version=prop.getProperty("cmbc_version");
			cmbc_merid=prop.getProperty("cmbc_merid");
			cmbc_mername=prop.getProperty("cmbc_mername");
			cmbc_withholding_chnl_code=prop.getProperty("cmbc_withholding_chnl_code");
			cmbc_withholding_public_key=prop.getProperty("cmbc_withholding_public_key");
			cmbc_withholding_private_key=prop.getProperty("cmbc_withholding_private_key");
			cmbc_withholding_ip=prop.getProperty("cmbc_withholding_ip");
			cmbc_withholding_port=Integer.valueOf(prop.getProperty("cmbc_withholding_port"));
			cmbc_insteadpay_port=Integer.valueOf(prop.getProperty("cmbc_insteadpay_port"));
			cmbc_insteadpay_merid=prop.getProperty("cmbc_insteadpay_merid");
			cmbc_self_withholding_ip=prop.getProperty("cmbc_self_withholding_ip");
			cmbc_self_withholding_port=Integer.valueOf(prop.getProperty("cmbc_self_withholding_port"));
			cmbc_insteadpay_batch_md5=prop.getProperty("cmbc_insteadpay_batch_md5");
			cmbc_secretfilepath=prop.getProperty("cmbc_secretfilepath");
			cmbc_download_file_path=prop.getProperty("cmbc_download_file_path");
			cmbc_plainfilepath=prop.getProperty("cmbc_plainFilePath");
			cmbc_self_merid=prop.getProperty("cmbc_self_merid");
			cmbc_self_merchant=prop.getProperty("cmbc_self_merchant");
			cmbc_withholding_self_public_key=prop.getProperty("cmbc_withholding_self_public_key");
			cmbc_withholding_self_private_key=prop.getProperty("cmbc_withholding_self_private_key");
			cmbc_withholding_self_chnl_code=prop.getProperty("cmbc_withholding_self_chnl_code");
			cmbc_insteadpay_ip = prop.getProperty("cmbc_insteadpay_ip");
			
			cmbc_insteadpay_privatekey = prop.getProperty("cmbc_insteadpay_privatekey");
			cmbc_insteadpay_publickey = prop.getProperty("cmbc_insteadpay_publickey");
			canRun = true;
			refresh_interval = prop.getProperty("refresh_interval");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @return the cmbc_version
	 */
	public String getCmbc_version() {
		return cmbc_version;
	}
	/**
	 * @param cmbc_version the cmbc_version to set
	 */
	public void setCmbc_version(String cmbc_version) {
		this.cmbc_version = cmbc_version;
	}
	/**
	 * @return the cmbc_merid
	 */
	public String getCmbc_merid() {
		return cmbc_merid;
	}
	/**
	 * @param cmbc_merid the cmbc_merid to set
	 */
	public void setCmbc_merid(String cmbc_merid) {
		this.cmbc_merid = cmbc_merid;
	}
	/**
	 * @return the cmbc_mername
	 */
	public String getCmbc_mername() {
		return cmbc_mername;
	}
	/**
	 * @param cmbc_mername the cmbc_mername to set
	 */
	public void setCmbc_mername(String cmbc_mername) {
		this.cmbc_mername = cmbc_mername;
	}
	/**
	 * @return the cmbc_withholding_chnl_code
	 */
	public String getCmbc_withholding_chnl_code() {
		return cmbc_withholding_chnl_code;
	}
	/**
	 * @param cmbc_withholding_chnl_code the cmbc_withholding_chnl_code to set
	 */
	public void setCmbc_withholding_chnl_code(String cmbc_withholding_chnl_code) {
		this.cmbc_withholding_chnl_code = cmbc_withholding_chnl_code;
	}
	/**
	 * @return the cmbc_withholding_public_key
	 */
	public String getCmbc_withholding_public_key() {
		return cmbc_withholding_public_key;
	}
	/**
	 * @param cmbc_withholding_public_key the cmbc_withholding_public_key to set
	 */
	public void setCmbc_withholding_public_key(String cmbc_withholding_public_key) {
		this.cmbc_withholding_public_key = cmbc_withholding_public_key;
	}
	/**
	 * @return the cmbc_withholding_private_key
	 */
	public String getCmbc_withholding_private_key() {
		return cmbc_withholding_private_key;
	}
	/**
	 * @param cmbc_withholding_private_key the cmbc_withholding_private_key to set
	 */
	public void setCmbc_withholding_private_key(String cmbc_withholding_private_key) {
		this.cmbc_withholding_private_key = cmbc_withholding_private_key;
	}
	/**
	 * @return the cmbc_withholding_ip
	 */
	public String getCmbc_withholding_ip() {
		return cmbc_withholding_ip;
	}
	/**
	 * @param cmbc_withholding_ip the cmbc_withholding_ip to set
	 */
	public void setCmbc_withholding_ip(String cmbc_withholding_ip) {
		this.cmbc_withholding_ip = cmbc_withholding_ip;
	}
	/**
	 * @return the cmbc_withholding_port
	 */
	public int getCmbc_withholding_port() {
		return cmbc_withholding_port;
	}
	/**
	 * @param cmbc_withholding_port the cmbc_withholding_port to set
	 */
	public void setCmbc_withholding_port(int cmbc_withholding_port) {
		this.cmbc_withholding_port = cmbc_withholding_port;
	}
	/**
	 * @return the cmbc_insteadpay_port
	 */
	public int getCmbc_insteadpay_port() {
		return cmbc_insteadpay_port;
	}
	/**
	 * @param cmbc_insteadpay_port the cmbc_insteadpay_port to set
	 */
	public void setCmbc_insteadpay_port(int cmbc_insteadpay_port) {
		this.cmbc_insteadpay_port = cmbc_insteadpay_port;
	}
	/**
	 * @return the cmbc_insteadpay_merid
	 */
	public String getCmbc_insteadpay_merid() {
		return cmbc_insteadpay_merid;
	}
	/**
	 * @param cmbc_insteadpay_merid the cmbc_insteadpay_merid to set
	 */
	public void setCmbc_insteadpay_merid(String cmbc_insteadpay_merid) {
		this.cmbc_insteadpay_merid = cmbc_insteadpay_merid;
	}
	/**
	 * @return the cmbc_self_withholding_ip
	 */
	public String getCmbc_self_withholding_ip() {
		return cmbc_self_withholding_ip;
	}
	/**
	 * @param cmbc_self_withholding_ip the cmbc_self_withholding_ip to set
	 */
	public void setCmbc_self_withholding_ip(String cmbc_self_withholding_ip) {
		this.cmbc_self_withholding_ip = cmbc_self_withholding_ip;
	}
	/**
	 * @return the cmbc_self_withholding_port
	 */
	public int getCmbc_self_withholding_port() {
		return cmbc_self_withholding_port;
	}
	/**
	 * @param cmbc_self_withholding_port the cmbc_self_withholding_port to set
	 */
	public void setCmbc_self_withholding_port(int cmbc_self_withholding_port) {
		this.cmbc_self_withholding_port = cmbc_self_withholding_port;
	}
	/**
	 * @return the cmbc_insteadpay_batch_md5
	 */
	public String getCmbc_insteadpay_batch_md5() {
		return cmbc_insteadpay_batch_md5;
	}
	/**
	 * @param cmbc_insteadpay_batch_md5 the cmbc_insteadpay_batch_md5 to set
	 */
	public void setCmbc_insteadpay_batch_md5(String cmbc_insteadpay_batch_md5) {
		this.cmbc_insteadpay_batch_md5 = cmbc_insteadpay_batch_md5;
	}
	/**
	 * @return the cmbc_secretfilepath
	 */
	public String getCmbc_secretfilepath() {
		return cmbc_secretfilepath;
	}
	/**
	 * @param cmbc_secretfilepath the cmbc_secretfilepath to set
	 */
	public void setCmbc_secretfilepath(String cmbc_secretfilepath) {
		this.cmbc_secretfilepath = cmbc_secretfilepath;
	}
	/**
	 * @return the cmbc_download_file_path
	 */
	public String getCmbc_download_file_path() {
		return cmbc_download_file_path;
	}
	/**
	 * @param cmbc_download_file_path the cmbc_download_file_path to set
	 */
	public void setCmbc_download_file_path(String cmbc_download_file_path) {
		this.cmbc_download_file_path = cmbc_download_file_path;
	}
	/**
	 * @return the cmbc_plainfilepath
	 */
	public String getCmbc_plainfilepath() {
		return cmbc_plainfilepath;
	}
	/**
	 * @param cmbc_plainfilepath the cmbc_plainfilepath to set
	 */
	public void setCmbc_plainfilepath(String cmbc_plainfilepath) {
		this.cmbc_plainfilepath = cmbc_plainfilepath;
	}
	/**
	 * @return the cmbc_self_merid
	 */
	public String getCmbc_self_merid() {
		return cmbc_self_merid;
	}
	/**
	 * @param cmbc_self_merid the cmbc_self_merid to set
	 */
	public void setCmbc_self_merid(String cmbc_self_merid) {
		this.cmbc_self_merid = cmbc_self_merid;
	}
	/**
	 * @return the cmbc_self_merchant
	 */
	public String getCmbc_self_merchant() {
		return cmbc_self_merchant;
	}
	/**
	 * @param cmbc_self_merchant the cmbc_self_merchant to set
	 */
	public void setCmbc_self_merchant(String cmbc_self_merchant) {
		this.cmbc_self_merchant = cmbc_self_merchant;
	}
	/**
	 * @return the cmbc_withholding_self_public_key
	 */
	public String getCmbc_withholding_self_public_key() {
		return cmbc_withholding_self_public_key;
	}
	/**
	 * @param cmbc_withholding_self_public_key the cmbc_withholding_self_public_key to set
	 */
	public void setCmbc_withholding_self_public_key(
			String cmbc_withholding_self_public_key) {
		this.cmbc_withholding_self_public_key = cmbc_withholding_self_public_key;
	}
	/**
	 * @return the cmbc_withholding_self_private_key
	 */
	public String getCmbc_withholding_self_private_key() {
		return cmbc_withholding_self_private_key;
	}
	/**
	 * @param cmbc_withholding_self_private_key the cmbc_withholding_self_private_key to set
	 */
	public void setCmbc_withholding_self_private_key(
			String cmbc_withholding_self_private_key) {
		this.cmbc_withholding_self_private_key = cmbc_withholding_self_private_key;
	}
	/**
	 * @return the cmbc_withholding_self_chnl_code
	 */
	public String getCmbc_withholding_self_chnl_code() {
		return cmbc_withholding_self_chnl_code;
	}
	/**
	 * @param cmbc_withholding_self_chnl_code the cmbc_withholding_self_chnl_code to set
	 */
	public void setCmbc_withholding_self_chnl_code(
			String cmbc_withholding_self_chnl_code) {
		this.cmbc_withholding_self_chnl_code = cmbc_withholding_self_chnl_code;
	}

	/**
	 * @return the cmbc_insteadpay_ip
	 */
	public String getCmbc_insteadpay_ip() {
		return cmbc_insteadpay_ip;
	}

	/**
	 * @param cmbc_insteadpay_ip the cmbc_insteadpay_ip to set
	 */
	public void setCmbc_insteadpay_ip(String cmbc_insteadpay_ip) {
		this.cmbc_insteadpay_ip = cmbc_insteadpay_ip;
	}

	/**
	 * @return the cmbc_insteadpay_privatekey
	 */
	public String getCmbc_insteadpay_privatekey() {
		return cmbc_insteadpay_privatekey;
	}

	/**
	 * @param cmbc_insteadpay_privatekey the cmbc_insteadpay_privatekey to set
	 */
	public void setCmbc_insteadpay_privatekey(String cmbc_insteadpay_privatekey) {
		this.cmbc_insteadpay_privatekey = cmbc_insteadpay_privatekey;
	}

	/**
	 * @return the cmbc_insteadpay_publickey
	 */
	public String getCmbc_insteadpay_publickey() {
		return cmbc_insteadpay_publickey;
	}

	/**
	 * @param cmbc_insteadpay_publickey the cmbc_insteadpay_publickey to set
	 */
	public void setCmbc_insteadpay_publickey(String cmbc_insteadpay_publickey) {
		this.cmbc_insteadpay_publickey = cmbc_insteadpay_publickey;
	}
	
	
}
