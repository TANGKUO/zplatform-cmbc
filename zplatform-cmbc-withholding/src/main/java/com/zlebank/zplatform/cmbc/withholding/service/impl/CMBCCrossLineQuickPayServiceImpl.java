/* 
 * CMBCCrossLineQuickPayServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年7月21日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.CMBCTradeQueueBean;
import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.enums.ChannelEnmu;
import com.zlebank.zplatform.cmbc.common.enums.TradeStatFlagEnum;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.dao.ProvinceDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsOrderinfoDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsLog;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.queue.service.TradeQueueService;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCCrossLineQuickPayService;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCRealNameAuthService;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCWithholdingService;
import com.zlebank.zplatform.task.service.TradeNotifyService;
import com.zlebank.zplatform.trade.acc.service.TradeAccountingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年7月21日 下午2:10:19
 * @since 
 */
@Service("cmbcCrossLineQuickPayService")
public class CMBCCrossLineQuickPayServiceImpl implements CMBCCrossLineQuickPayService{
	
	private static final Logger log = LoggerFactory.getLogger(CMBCCrossLineQuickPayServiceImpl.class);
	
	@Autowired
	private ProvinceDAO provinceDAO;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private TxnsOrderinfoDAO txnsOrderinfoDAO;
	@Autowired
	private TxnsWithholdingService txnsWithholdingService;
	@Autowired
	private TradeNotifyService tradeNotifyService;
	@Autowired
	private SerialNumberService serialNumberService;
	@Autowired
	private CMBCWithholdingService cmbcWithholdingService;
	@Autowired
	private CMBCRealNameAuthService cmbcRealNameAuthService;
	@Autowired
	private TradeAccountingService tradeAccountingService;
	@Autowired
	private TradeQueueService tradeQueueService;
	
	public ResultBean bankSign(TradeBean tradeBean){
		
		ResultBean resultBean = null;
		try {
			// 卡信息进行实名认证
			PojoRealnameAuth realnameAuth = new PojoRealnameAuth(tradeBean);
			resultBean = cmbcRealNameAuthService.realNameAuth(realnameAuth);
		} catch (CMBCTradeException e1) {
			// TODO Auto-generated catch block
			resultBean = new ResultBean(e1.getCode(), e1.getMessage());
			e1.printStackTrace();
			return resultBean;
		} 
		txnsLogDAO.updateTradeStatFlag(tradeBean.getTxnseqno(), TradeStatFlagEnum.READY);
		return resultBean;
	}

	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	@Override
	public ResultBean submitPay(TradeBean tradeBean) {
		ResultBean resultBean = null;
		try {
			log.info("CMBC submit Pay start!");
			resultBean = null;
			// 更新支付方信息
			PayPartyBean payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),"01", serialNumberService.generateCMBCSerialNo(), ChannelEnmu.CMBCWITHHOLDING.getChnlcode(),
					Constant.getInstance().getCmbc_merid(), "",
					DateUtil.getCurrentDateTime(), "", tradeBean.getCardNo());
			payPartyBean.setPanName(tradeBean.getAcctName());
			txnsLogDAO.updatePayInfo(payPartyBean);
			tradeBean.setPayOrderNo(payPartyBean.getPayordno());
			tradeBean.setPayinstiId(ChannelEnmu.CMBCWITHHOLDING.getChnlcode());
			// 获取持卡人所属省份代码 （民生银行报文中的要求）
			tradeBean.setProvno(provinceDAO.getProvinceByXZCode(tradeBean.getCertId().substring(0, 2)).getProvinceId()+ "");
			// 记录快捷交易流水
			txnsLogDAO.updateTradeStatFlag(tradeBean.getTxnseqno(), TradeStatFlagEnum.PAYING);
			resultBean = cmbcWithholdingService.crossLineWithhold(tradeBean);
			if(resultBean.isResultBool()) {
				//更新订单状态
	            txnsOrderinfoDAO.updateOrderToSuccess(tradeBean.getTxnseqno());
			} else {// 交易失败
				if("E".equals(resultBean.getErrCode())){
					txnsOrderinfoDAO.updateOrderToFail(tradeBean.getTxnseqno());
					return resultBean;
				}else{
					//加入交易查询队列
					tradeQueueService.addTradeQueue(tradeBean.getTxnseqno());
					return resultBean;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			txnsOrderinfoDAO.updateOrderToFail(tradeBean.getTxnseqno());
			resultBean = new ResultBean("T000", "交易失败");
		}

		log.info("CMBC submit Pay end!");
		return resultBean;
	}
	
	public ResultBean dealWithAccounting(String txnseqno,ResultBean resultBean){
		
		PojoTxnsWithholding withholding = (PojoTxnsWithholding) resultBean.getResultObj();
		//PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
        PayPartyBean payPartyBean = null;
        if(StringUtils.isNotEmpty(withholding.getOrireqserialno())){
            PojoTxnsWithholding old_withholding = txnsWithholdingService.getWithholdingBySerialNo(withholding.getOrireqserialno());
            //更新支付方信息
            payPartyBean = new PayPartyBean(txnseqno,"01", withholding.getOrireqserialno(), old_withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",old_withholding.getAccno(),withholding.getPayserialno());
        }else{
            payPartyBean = new PayPartyBean(txnseqno,"01", withholding.getSerialno(), withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",withholding.getAccno(),withholding.getPayserialno());
        }
        payPartyBean.setPanName(withholding.getAccname());
        payPartyBean.setPayretcode(withholding.getExeccode());
        payPartyBean.setPayretinfo(withholding.getExecmsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
        if(withholding.getExectype().equals("S")){
        	tradeAccountingService.accountingFor(txnseqno);
        	tradeNotifyService.notify(txnseqno);
        }
		return resultBean;
	}
	
	public ResultBean queryTrade(String txnseqno){
		
		return cmbcWithholdingService.queryCrossLineTrade(txnseqno);
	}
	
}
