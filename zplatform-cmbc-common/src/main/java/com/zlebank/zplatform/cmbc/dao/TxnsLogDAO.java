/* 
 * ITxnsLogDAO.java  
 * 
 * version TODO
 *
 * 2015年8月27日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import java.util.Map;

import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.enums.TradeStatFlagEnum;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsLog;


/**
 * 交流流水表DAO
 *
 * @author guojia
 * @version
 * @date 2015年8月27日 下午2:20:50
 * @since 
 */
public interface TxnsLogDAO extends BaseDAO<PojoTxnsLog>{
	
	/**
	 * 根据交易序列号获取交易日志数据
	 * @param txnseqno 交易序列号
	 * @return
	 */
	public PojoTxnsLog getTxnsLogByTxnseqno(String txnseqno);
	
	/**
	 * 更新交易流水日志数据
	 * @param txnsLog 交易流水日志pojo
	 */
	public void updateTxnsLog(PojoTxnsLog txnsLog);
	
	/**
	 * 更新支付交易交易数据（支付前）
	 * @param payPartyBean 支付方数据bean
	 */
	public void updatePayInfo(PayPartyBean payPartyBean);
	
	/**
	 * 更新交易标记状态
	 * @param txnseqno 交易序列号
	 * @param tradeStatFlagEnum 交易标记状态
	 */
	public void updateTradeStatFlag(String txnseqno,
			TradeStatFlagEnum tradeStatFlagEnum);
	/**
	 * 更新支付交易交易数据（支付后）
	 * @param payPartyBean 支付方数据bean
	 */
	public void updateCMBCTradeData(PayPartyBean payPartyBean);
	
	/**
	 * 获取银行卡信息
	 * @param cardNo 银行卡号
	 * @return
	 */
	public Map<String, Object> getCardInfo(String cardNo);
	
	/**
	 * 更新应用方信息
	 * @param txnseqno
	 */
	public void updateAppInfo(String txnseqno);
	
	/**
	 * 更新民生代付退汇结果
	 * @param txnseqno 交易序列号
	 * @param retCode 应答码
	 * @param retMsg 应答信息
	 */
	public void updateCMBCReexchange(String txnseqno,String retCode,String retMsg);
	
	/**
	 * 保存交易日志
	 * @param txnsLog 交易日志pojo
	 */
	public void saveTxnsLog(PojoTxnsLog txnsLog);
}
