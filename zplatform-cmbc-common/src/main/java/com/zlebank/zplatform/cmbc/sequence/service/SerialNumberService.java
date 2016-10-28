/* 
 * SerialNumberService.java  
 * 
 * version TODO
 *
 * 2016年9月12日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.sequence.service;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年9月12日 下午3:49:23
 * @since 
 */
public interface SerialNumberService {

	/**
	 * 生成民生交易流水号
	 * @return 流水号
	 */
	public String generateCMBCSerialNo();
	
	/**
	 * 生成民生代付交易流水号
	 * @return
	 */
	public String generateCMBCInsteadPaySerialNo();
	
	/**
	 * 生成交易序列号
	 * @return
	 */
	public String generateTxnseqno();
}
