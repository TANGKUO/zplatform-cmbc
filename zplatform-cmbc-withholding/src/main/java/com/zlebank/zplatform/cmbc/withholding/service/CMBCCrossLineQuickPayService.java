/* 
 * CMBCCrossLineQuickPayService.java  
 * 
 * version TODO
 *
 * 2016年7月21日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;


/**
 * 民生代扣渠道封装成快捷支付业务
 *
 * @author guojia
 * @version
 * @date 2016年7月21日 下午2:11:15
 * @since 
 */
public interface CMBCCrossLineQuickPayService {

	/**
	 * 民生跨行代扣--银行卡签约
	 * @param tradeBean
	 * @return
	 */
	public ResultBean bankSign(TradeBean tradeBean);
	
	/**
	 * 民生跨行代扣--确认支付
	 * @param tradeBean
	 * @return
	 */
	public ResultBean submitPay(TradeBean tradeBean);
	
	/**
	 * 民生跨行代扣--交易后账务处理
	 * @param txnseqno
	 * @param resultBean
	 * @return
	 */
	public ResultBean dealWithAccounting(String txnseqno,ResultBean resultBean);
	
	/**
	 * 民生跨行代扣--交易查询
	 * @param txnseqno
	 * @return
	 */
	public ResultBean queryTrade(String txnseqno);
}
