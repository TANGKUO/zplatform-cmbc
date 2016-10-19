/* 
 * InsteadPayServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月17日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.service.CMBCInsteadPayService;
import com.zlebank.zplatform.cmbc.insteadpay.service.InsteadPayService;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月17日 下午12:14:49
 * @since
 */
@Service("insteadPayService")
public class InsteadPayServiceImpl implements InsteadPayService {

	@Autowired
	private CMBCInsteadPayService cmbcInsteadPayService;
	@Autowired
	private SerialNumberService serialNumberService;
	/**
	 *
	 * @param insteadPayTradeBean
	 * @return
	 */
	@Override
	public ResultBean realTimeSingleInsteadPay(InsteadPayTradeBean insteadPayTradeBean) {
		/**
		 * 实时代付业务流程：
		 * 1.更新交易流水订单状态
		 * 2.
		 * 3.
		 * 4.
		 */
		RealTimePayBean realTimePayBean = new RealTimePayBean(insteadPayTradeBean);
		realTimePayBean.setTranId(serialNumberService.generateCMBCInsteadPaySerialNo());
		ResultBean resultBean = cmbcInsteadPayService.realTimeInsteadPay(realTimePayBean);
		return resultBean;
	}

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean batchInsteadPay(String batchNo) {

		return null;
	}

	/**
	 *
	 * @param ori_tran_date
	 * @param ori_tran_id
	 * @return
	 */
	@Override
	public ResultBean queryRealTimeInsteadPay(String ori_tran_date,
			String ori_tran_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
