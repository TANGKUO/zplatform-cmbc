/* 
 * CMBCSelfWithholdingService.java  
 * 
 * version TODO
 *
 * 2016年11月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.self.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月24日 上午10:52:03
 * @since 
 */
public interface CMBCSelfWithholdingService{

	/**
	 * 民生本行代扣
	 * @param tradeBean 交易bean
	 * @return
	 */
	public ResultBean withholding(TradeBean tradeBean);
	
	
	public ResultBean dealWithAccounting();
	
}
