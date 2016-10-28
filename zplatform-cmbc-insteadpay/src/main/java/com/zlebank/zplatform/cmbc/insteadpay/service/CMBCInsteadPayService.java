/* 
 * CMBCInsteadPayService.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimeQueryBean;

/**
 * 民生代付接口：实时单笔代付，批量代付（实时和非实时）
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 上午11:19:09
 * @since 
 */
public interface CMBCInsteadPayService {

	/**
	 * 单笔实时代付
	 * @param realTimePayBean 实时代付bean
	 * @return 结果bean 
	 */
	public ResultBean realTimeInsteadPay(final RealTimePayBean realTimePayBean);
	
	/**
	 * 实时批量代付
	 * @param batchNo 代付批次号
	 * @return 结果bean
	 */
	public ResultBean realtimeBatchInsteadPay(String batchNo);
	
	/**
	 * 批量代付-非实时
	 * @param batchNo 代付批次号
	 * @return 结果bean
	 */
	public ResultBean batchInsteadPay(String batchNo);
	
	/**
	 * 查询实时代付账务交易结果
	 * @param queryBean 查询bean
	 * 
	 */
	public void queryInsteadPay(RealTimeQueryBean queryBean);
}
