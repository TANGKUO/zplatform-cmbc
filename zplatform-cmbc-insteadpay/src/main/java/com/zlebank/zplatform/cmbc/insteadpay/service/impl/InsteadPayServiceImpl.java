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

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.enums.ChannelEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.enums.TradeStatFlagEnum;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.common.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.common.pojo.PojoTxnsCmbcInstPayLog;
import com.zlebank.zplatform.cmbc.common.pojo.PojoTxnsLog;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.dao.InsteadPayRealtimeDAO;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsCmbcInstPayLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.service.CMBCInsteadPayService;
import com.zlebank.zplatform.cmbc.insteadpay.service.InsteadPayService;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.trade.acc.service.TradeAccountingService;

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
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private TxnsCmbcInstPayLogDAO txnsCmbcInstPayLogDAO;
	@Autowired
	private RspmsgDAO rspmsgDAO;
	@Autowired
	private TradeAccountingService tradeAccountingService;
	@Autowired
	private InsteadPayRealtimeDAO insteadPayRealtimeDAO;
	
	/**
	 *
	 * @param insteadPayTradeBean
	 * @return
	 * @throws CMBCTradeException 
	 */
	@Override
	public ResultBean realTimeSingleInsteadPay(InsteadPayTradeBean insteadPayTradeBean) throws CMBCTradeException {
		/**
		 * 实时代付业务流程：
		 * 1.获取交易日志数据
		 * 2.校验交易日志数据，如果是成功的交易拒绝，失败的交易或者未交易的通过
		 * 3.更新支付方数据
		 * 4.记录渠道交易流水
		 */
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(insteadPayTradeBean.getTxnseqno());
		if(txnsLog==null){
			throw new CMBCTradeException("");
		}
		if("0000".equals(txnsLog.getRetcode())){
			throw new CMBCTradeException("");
		}
		PayPartyBean payPartyBean = new PayPartyBean(insteadPayTradeBean.getTxnseqno(),
				"04", serialNumberService.generateCMBCInsteadPaySerialNo(), ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(),
				Constant.getInstance().getCmbc_insteadpay_merid(), "",
				DateUtil.getCurrentDateTime(), "", "");
		txnsLogDAO.updatePayInfo(payPartyBean);
		
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = new PojoTxnsCmbcInstPayLog(insteadPayTradeBean);
		cmbcInstPayLog.setTranId(payPartyBean.getPayordno());
		txnsCmbcInstPayLogDAO.savePayLog(cmbcInstPayLog);
		RealTimePayBean realTimePayBean = new RealTimePayBean(insteadPayTradeBean);
		realTimePayBean.setTranId(payPartyBean.getPayordno());
		ResultBean resultBean = cmbcInsteadPayService.realTimeInsteadPay(realTimePayBean);
		txnsLogDAO.updateTradeStatFlag(txnsLog.getTxnseqno(), TradeStatFlagEnum.PAYING);
		resultBean = queryResult(payPartyBean.getPayordno());
		if(resultBean.isResultBool()){
			insteadPayRealtimeDAO.updateInsteadSuccess(insteadPayTradeBean.getTxnseqno());
		}else{
			//insteadPayRealtimeDAO.updateInsteadFail(insteadPayTradeBean.getTxnseqno(), resultBean.getErrCode(), resultBean.getErrMsg());
		}
		dealWithInsteadPay(payPartyBean.getPayordno());
		return resultBean;
	}
	
	private ResultBean queryResult(String tranId){
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
		ResultBean resultBean = null;
        int[] timeArray = new int[]{1, 2, 8, 16, 32};
        try {
            for (int i = 0; i < 5; i++) {
            	cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
                if(StringUtils.isNotEmpty(cmbcInstPayLog.getRespType())){
                    if("S".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                        resultBean = new ResultBean(cmbcInstPayLog);
                        return resultBean;
                    }else if("E".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                    	PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, cmbcInstPayLog.getRespCode());
                        resultBean = new ResultBean(msg.getWebrspcode(),msg.getRspinfo());
                        return resultBean;
                    }else if("R".equalsIgnoreCase(cmbcInstPayLog.getRespType())){
                        resultBean = new ResultBean("R","正在支付中");
                        continue;
                    }
                }
                TimeUnit.SECONDS.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("09", e.getMessage());
        }
        resultBean = new ResultBean("T000", "交易超时，无法再规定时间内取得交易结果");
		return null;
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

	/**
	 *
	 * @param tranId
	 * @return
	 */
	@Override
	public ResultBean dealWithInsteadPay(String tranId) {
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(tranId);
		PayPartyBean payPartyBean = new PayPartyBean(cmbcInstPayLog.getTxnseqno(),"04", cmbcInstPayLog.getTranId(),ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode(), Constant.getInstance().getCmbc_insteadpay_merid(), "", DateUtil.getCurrentDateTime(), "",cmbcInstPayLog.getAccNo(),cmbcInstPayLog.getBankTranId());
		payPartyBean.setPayretcode(cmbcInstPayLog.getRespCode());
        payPartyBean.setPayretinfo(cmbcInstPayLog.getRespMsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
        //AppPartyBean appParty = new AppPartyBean("","000000000000", commiteTime,DateUtil.getCurrentDateTime(), txnseqno, "");
        txnsLogDAO.updateAppInfo(cmbcInstPayLog.getTxnseqno());
        tradeAccountingService.accountingFor(cmbcInstPayLog.getTxnseqno());
		return null;
	}

}
