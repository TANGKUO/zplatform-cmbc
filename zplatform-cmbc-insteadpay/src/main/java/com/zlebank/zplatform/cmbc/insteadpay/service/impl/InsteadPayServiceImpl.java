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

import com.zlebank.zplatform.cmbc.common.bean.CMBCTradeQueueBean;
import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.SingleReexchangeBean;
import com.zlebank.zplatform.cmbc.common.enums.ChannelEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.enums.TradeStatFlagEnum;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.common.utils.BeanCopyUtil;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.dao.InsteadPayRealtimeDAO;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsCmbcInstPayLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimeQueryBean;
import com.zlebank.zplatform.cmbc.insteadpay.service.CMBCInsteadPayService;
import com.zlebank.zplatform.cmbc.insteadpay.service.InsteadPayService;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsCmbcInstPayLog;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsLog;
import com.zlebank.zplatform.cmbc.queue.service.TradeQueueService;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.task.service.TradeNotifyService;
import com.zlebank.zplatform.trade.acc.service.InsteadPayAccountingService;
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
	@Autowired
	private TradeNotifyService tradeNotifyService;
	@Autowired
	private InsteadPayAccountingService insteadPayAccountingService;
	@Autowired
	private TradeQueueService tradeQueueService;
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
			//throw new CMBCTradeException("");
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
			if(!resultBean.getErrCode().equals("E")){
				insteadPayRealtimeDAO.updateInsteadFail(insteadPayTradeBean.getTxnseqno(), resultBean.getErrCode(), resultBean.getErrMsg());
			}else{
				//加入交易查询队列
				tradeQueueService.addTradeQueue(txnsLog.getTxnseqno());
				return resultBean;
			}
			
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
                        resultBean = new ResultBean("R","正在付款中");
                        continue;
                    }
                }
                TimeUnit.SECONDS.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("T000", e.getMessage());
        }
        resultBean = new ResultBean("T000", "交易超时，请稍后查询交易结果");
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
		RealTimeQueryBean queryBean = new RealTimeQueryBean(ori_tran_date, ori_tran_id);
		
		PojoTxnsCmbcInstPayLog cmbcInstPayQueryLog = new PojoTxnsCmbcInstPayLog();
		cmbcInstPayQueryLog.setOriTranDate(ori_tran_date);
		cmbcInstPayQueryLog.setOriTranId(ori_tran_id);
		cmbcInstPayQueryLog.setTranId(serialNumberService.generateCMBCInsteadPaySerialNo());
		cmbcInstPayQueryLog.setTranDate(DateUtil.getCurrentDate());
		cmbcInstPayQueryLog.setTranTime(DateUtil.getCurrentDateTime());
		txnsCmbcInstPayLogDAO.savePayLog(cmbcInstPayQueryLog);
		cmbcInsteadPayService.queryInsteadPay(queryBean);
		return queryResult(ori_tran_id);
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
        txnsLogDAO.updateAppInfo(cmbcInstPayLog.getTxnseqno());
        tradeAccountingService.accountingFor(cmbcInstPayLog.getTxnseqno());
        if("S".equals(cmbcInstPayLog.getRespType())){
        	tradeNotifyService.notify(cmbcInstPayLog.getTxnseqno());
        }
		return null;
	}

	/**
	 *
	 * @param reexchangeBean
	 * @throws CMBCTradeException 
	 */
	@Override
	public void reexchange(SingleReexchangeBean reexchangeBean) throws CMBCTradeException {
		// TODO Auto-generated method stub
		/**
		 * 退汇流程：
		 * 1.根据流水号查询民生实时代付流水数据，有无此交易
		 * 2.由民生代付流水取得交易序列号，以此取得交易日志，代付订单
		 * 3.更新代付订单表 状态 05 退汇， 新增交易流水4000， 4000002退汇类交易
		 * 4.退汇账务处理
		 */
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(reexchangeBean.getTranId());
		if(cmbcInstPayLog==null){
			throw new CMBCTradeException("");
		}
		insteadPayRealtimeDAO.updateInsteadReexchange(cmbcInstPayLog.getTxnseqno(), reexchangeBean.getRespCode(), reexchangeBean.getRespMsg());
		PojoTxnsLog txnsLog = generateReexchangTxnsLog(cmbcInstPayLog.getTxnseqno(), reexchangeBean);
		txnsLogDAO.saveTxnsLog(txnsLog);
		//账务处理
		insteadPayAccountingService.reexchangeAccounting(txnsLog.getTxnseqno());
	}
	
	private PojoTxnsLog generateReexchangTxnsLog(String txnseqno_og,SingleReexchangeBean reexchangeBean){
		String txnseqno = serialNumberService.generateTxnseqno();
		PojoTxnsLog txnsLog = BeanCopyUtil.copyBean(PojoTxnsLog.class, txnsLogDAO.getTxnsLogByTxnseqno(txnseqno_og));
		txnsLog.setTxnseqno(txnseqno);
		txnsLog.setTxndate(DateUtil.getCurrentDate());
		txnsLog.setTxntime(DateUtil.getCurrentTime());
		txnsLog.setBusitype("4000");
		txnsLog.setBusicode("40000002");
		//支付方信息
		txnsLog.setPayordno(reexchangeBean.getTranId());
		txnsLog.setPayordcomtime(DateUtil.getCurrentDateTime());
		txnsLog.setPayordfintime(DateUtil.getCurrentDateTime());
		txnsLog.setPayrettsnseqno(reexchangeBean.getBankTranId());
		txnsLog.setPayretcode(reexchangeBean.getRespCode());
		txnsLog.setPayretinfo(reexchangeBean.getRespMsg());
		//中心应答信息
		PojoRspmsg rspmsg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, reexchangeBean.getRespCode());
		if(rspmsg!=null){
			txnsLog.setRetcode(rspmsg.getWebrspcode());
			txnsLog.setRetinfo(rspmsg.getRspinfo());
		}else{
			txnsLog.setRetcode("01HH");
			txnsLog.setRetinfo("交易失败，详情请咨询证联金融客服010-84298418");
		}
		txnsLog.setTradestatflag(TradeStatFlagEnum.FINISH_SUCCESS.getStatus());
		txnsLog.setRetdatetime(DateUtil.getCurrentDateTime());
		txnsLog.setTxnseqnoOg(txnseqno_og);
		//应用方信息
		txnsLog.setAppordcommitime(DateUtil.getCurrentDateTime());
		txnsLog.setApporderstatus("");
		txnsLog.setApporderinfo("");
		txnsLog.setAccbusicode("40000002");
		return txnsLog;
	}

	/**
	 *
	 * @param txnseqno
	 */
	@Override
	public void queryAndAccounting(String txnseqno) {
		// TODO Auto-generated method stub
		PojoTxnsCmbcInstPayLog cmbcInstPayLog = txnsCmbcInstPayLogDAO.queryByTranId(txnseqno);
		PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(txnseqno);
		ResultBean resultBean = queryRealTimeInsteadPay(cmbcInstPayLog.getTranDate(), cmbcInstPayLog.getTranId());
		if(resultBean.isResultBool()){
			PojoTxnsCmbcInstPayLog cmbcInstPayLog_query = (PojoTxnsCmbcInstPayLog) resultBean.getResultObj();
			if("S".equals(cmbcInstPayLog_query.getOriRespType())){
				insteadPayRealtimeDAO.updateInsteadSuccess(txnseqno);
			}else if("E".equals(cmbcInstPayLog_query.getOriRespType())){
				insteadPayRealtimeDAO.updateInsteadFail(txnseqno, resultBean.getErrCode(), resultBean.getErrMsg());
			}else if("R".equals(cmbcInstPayLog_query.getOriRespType())){
				CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
				queueBean.setTxnseqno(txnseqno);
				queueBean.setPayInsti(ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode());
				queueBean.setBusiType(txnsLog.getBusitype());
				queueBean.setTxnDateTime(txnsLog.getTxntime());
				//加入交易查询队列
				tradeQueueService.addTradeQueue(queueBean);
				return;
			}
			
		}else{
			if(resultBean.getErrCode().equals("R")){
				CMBCTradeQueueBean queueBean = new CMBCTradeQueueBean();
				queueBean.setTxnseqno(txnseqno);
				queueBean.setPayInsti(ChannelEnmu.CMBCINSTEADPAY_REALTIME.getChnlcode());
				queueBean.setBusiType(txnsLog.getBusitype());
				queueBean.setTxnDateTime(txnsLog.getTxntime());
				//加入交易查询队列
				tradeQueueService.addTradeQueue(queueBean);
				return;
			}
		}
		dealWithInsteadPay(txnsLog.getPayordno());
	}

}
