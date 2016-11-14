/* 
 * TxnsCmbcInstPayLogDAO.java  
 * 
 * version TODO
 *
 * 2016年10月21日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeInsteadPayQueryResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeInsteadPayResultBean;
import com.zlebank.zplatform.cmbc.common.bean.SingleReexchangeBean;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsCmbcInstPayLog;


/**
 * 民生实时代付DAO接口
 *
 * @author guojia
 * @version
 * @date 2016年10月21日 下午1:03:53
 * @since 
 */
public interface TxnsCmbcInstPayLogDAO {
	/***
	 * 保存实时代付交易日志
	 * @param cmbcInstPayLog
	 */
	public void savePayLog(PojoTxnsCmbcInstPayLog cmbcInstPayLog);
	
	/***
	 * 根据渠道流水ID查询实时代付
	 * @param tranId
	 * @return
	 */
	public PojoTxnsCmbcInstPayLog queryByTranId(String tranId);
	/****
	 * 修改实时代付交易日志
	 * @param cmbcInstPayLog
	 */
	public void updatePayLog(PojoTxnsCmbcInstPayLog cmbcInstPayLog);
	
	/**
	 * 
	 * @param realTimePayResultBean
	 */
	public void updateInsteadPayResult(CMBCRealTimeInsteadPayResultBean realTimePayResultBean);
	
	/**
	 * 更新代付退汇结果
	 * @param reexchangeBean 退汇bean
	 */
	public void updateReexchangeResult(SingleReexchangeBean reexchangeBean);
	
	/**
	 * 更新实时代付查询结果
	 * @param realTimePayResultBean
	 */
	public void updateInsteadPayQueryResult(CMBCRealTimeInsteadPayQueryResultBean cmbcRealTimeInsteadPayQueryResultBean);
}
