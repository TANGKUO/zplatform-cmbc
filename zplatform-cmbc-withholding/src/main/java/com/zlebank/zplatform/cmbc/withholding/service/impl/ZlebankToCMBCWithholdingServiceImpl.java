/* 
 * CMBCZlebankWithholdingServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCCrossLineQuickPayService;
import com.zlebank.zplatform.cmbc.withholding.service.ZlebankToCMBCWithholdingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 下午5:19:16
 * @since 
 */
@Service
public class ZlebankToCMBCWithholdingServiceImpl implements
		ZlebankToCMBCWithholdingService {

	@Autowired
	private CMBCCrossLineQuickPayService cmbcCrossLineQuickPayService;
	/**
	 *
	 * @param tradeBean
	 */
	@Override
	public ResultBean withholding(TradeBean tradeBean) {
		/**
		 * 代扣业务流程
		 * 1。银行卡签约：实名认证，白名单采集
		 * 2.代扣
		 * 3.账务处理
		 */
		ResultBean resultBean = cmbcCrossLineQuickPayService.bankSign(tradeBean);
		
		resultBean = cmbcCrossLineQuickPayService.submitPay(tradeBean);
		
		cmbcCrossLineQuickPayService.dealWithAccounting(tradeBean.getTxnseqno(), resultBean);
		
		return resultBean;
	}

}
