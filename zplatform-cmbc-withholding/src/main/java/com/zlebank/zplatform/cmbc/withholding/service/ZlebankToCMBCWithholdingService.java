/* 
 * CMBCWithholdService.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;

/**
 * 民生-证联代扣service
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 下午5:13:48
 * @since 
 */
public interface ZlebankToCMBCWithholdingService {

	/**
	 * 代扣
	 * @param tradeBean 交易数据bean
	 */
	public ResultBean withholding(TradeBean tradeBean);
}
