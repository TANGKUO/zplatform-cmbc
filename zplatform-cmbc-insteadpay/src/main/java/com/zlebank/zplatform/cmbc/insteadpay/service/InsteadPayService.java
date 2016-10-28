/* 
 * InsteadPayService.java  
 * 
 * version TODO
 *
 * 2016年10月17日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.service;

import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.SingleReexchangeBean;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;

/**
 * 民生代付接口（实时代付和批量代付）
 *
 * @author guojia
 * @version 
 * @date 2016年10月17日 上午11:51:58
 * @since 
 */
public interface InsteadPayService {

	/**
	 * 
	 * @return
	 */
	public ResultBean realTimeSingleInsteadPay(InsteadPayTradeBean insteadPayTradeBean) throws CMBCTradeException;
	
	/**
	 * 批量代付代付
	 * @param batchNo
	 * @return
	 */
	public ResultBean batchInsteadPay(String batchNo);
	
	/**
	 * 查询实时代付交易结果
	 * @param ori_tran_date 原交易日期
	 * @param ori_tran_id 原交易流水号
	 * @return 
	 */
	public ResultBean queryRealTimeInsteadPay(String ori_tran_date,String ori_tran_id);
	
	/**
	 * 
	 * @param tranId
	 * @return
	 */
	public ResultBean dealWithInsteadPay(String tranId);
	
	/**
	 * 实时代付退汇处理
	 * @param reexchangeBean
	 */
	public void reexchange(SingleReexchangeBean reexchangeBean) throws CMBCTradeException;
	
	/**
	 * 查询并处理账务
	 * @param txnseqno 交易序列号
	 */
	public void queryAndAccounting(String txnseqno);
}
