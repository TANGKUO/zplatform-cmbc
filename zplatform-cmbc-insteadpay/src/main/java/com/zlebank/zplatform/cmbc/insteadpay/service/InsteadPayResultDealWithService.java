/* 
 * InsteadPayResultDealWithService.java  
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
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayResultBean;

/**
 * 代付结果处理service
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 下午12:41:02
 * @since 
 */
public interface InsteadPayResultDealWithService {

	/**
	 * 处理实时代付交易结果
	 * @param realTimePayResultBean
	 * @return
	 */
	public ResultBean dealWithRealTimeInsteadPay(RealTimePayResultBean realTimePayResultBean);
	
	
}
