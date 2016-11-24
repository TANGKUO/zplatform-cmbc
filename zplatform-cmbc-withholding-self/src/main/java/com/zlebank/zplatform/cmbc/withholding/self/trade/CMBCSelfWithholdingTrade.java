/* 
 * CMBCSelfWithholdTrade.java  
 * 
 * version TODO
 *
 * 2016年11月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.self.trade;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.enums.BankEnmu;
import com.zlebank.zplatform.cmbc.common.enums.CMBCCardTypeEnum;
import com.zlebank.zplatform.cmbc.common.enums.CertifTypeEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChannelEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsOrderinfoDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.queue.service.TradeQueueService;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.self.bean.TradeBean;
import com.zlebank.zplatform.cmbc.withholding.self.bean.WithholdingMessageBean;
import com.zlebank.zplatform.cmbc.withholding.self.net.netty.NettyClientBootstrap;
import com.zlebank.zplatform.cmbc.withholding.self.net.netty.SocketChannelHelper;
import com.zlebank.zplatform.cmbc.withholding.self.request.bean.RealTimeSelfWithholdingBean;
import com.zlebank.zplatform.task.service.TradeNotifyService;
import com.zlebank.zplatform.trade.acc.service.TradeAccountingService;
import com.zlebank.zplatform.trade.bean.ResultBean;
import com.zlebank.zplatform.trade.semisync.AbstractSemiSyncTrade;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月24日 下午4:19:21
 * @since 
 */
@Service
public class CMBCSelfWithholdingTrade extends AbstractSemiSyncTrade<TradeBean>{

	private static final Logger logger = LoggerFactory.getLogger(CMBCSelfWithholdingTrade.class);
	@Autowired
	private SerialNumberService serialNumberService;
	@Autowired
	private TxnsWithholdingService txnsWithholdingService;
	@Autowired
	private RspmsgDAO rspmsgDAO;
	@Autowired
	private TradeQueueService tradeQueueService;
	@Autowired
	private TxnsLogDAO txnsLogDAO;
	@Autowired
	private TradeAccountingService tradeAccountingService;
	@Autowired
	private TradeNotifyService tradeNotifyService;
	@Autowired
	private TxnsOrderinfoDAO txnsOrderinfoDAO;
	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	@Override
	public ResultBean queryTrade(TradeBean tradeBean) {
		String serialno = tradeBean.getWithholdingMessageBean().getWithholding().getSerialno();
		PojoTxnsWithholding withholding = null;
        ResultBean resultBean = null;
        int[] timeArray = new int[]{1, 2, 8, 16, 32};
        try {
            for (int i = 0; i < 5; i++) {
                withholding = txnsWithholdingService.getWithholdingBySerialNo(serialno);
                tradeBean.setWithholding(withholding);
                if(!StringUtils.isEmpty(withholding.getExectype())){
                    if("S".equalsIgnoreCase(withholding.getExectype())){
                        resultBean = new ResultBean(withholding);
                        return resultBean;
                    }else if("E".equalsIgnoreCase(withholding.getExectype())){
                    	PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, withholding.getExeccode());
                        resultBean = new ResultBean("E",msg.getRspinfo());
                        return resultBean;
                    }else if("R".equalsIgnoreCase(withholding.getExectype())){
                        resultBean = new ResultBean("R","正在支付中");
                        continue;
                    }
                }
                TimeUnit.SECONDS.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            resultBean = new ResultBean("R", e.getMessage());
        }
        resultBean = new ResultBean("R", "交易超时，无法再规定时间内取得交易结果");
        return resultBean;
	}

	/**
	 *
	 * @param tradeBean
	 */
	@Override
	public void asyncQueueQueryTrade(TradeBean tradeBean) {
		// TODO Auto-generated method stub
		tradeQueueService.addTradeQueue(tradeBean.getTxnseqno());
	}

	/**
	 *
	 * @param tradeBean
	 */
	@Override
	public void saveTradeLog(TradeBean tradeBean) {
		// TODO Auto-generated method stub
		PojoTxnsWithholding withholding = createCMBCSelfPojo(tradeBean);
        withholding.setSerialno(serialNumberService.generateCMBCSerialNo());
        txnsWithholdingService.saveWithholdingLog(withholding);
        WithholdingMessageBean withholdingMsg = new WithholdingMessageBean(withholding);
        withholdingMsg.setWithholding(withholding);
        tradeBean.setWithholdingMessageBean(withholdingMsg);
	}

	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	@Override
	public void sendTradeMessage(TradeBean tradeBean) {
		final RealTimeSelfWithholdingBean realTimeSelfWithholdingBean = new RealTimeSelfWithholdingBean(tradeBean.getWithholdingMessageBean());
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(1);
		executors.execute(new Runnable() {
			@Override
			public void run() {
				try {
					SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
					byte[] bytes = socketChannelHelper.getMessageHandler().pack(realTimeSelfWithholdingBean);
					String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS", Constant.getInstance().getCmbc_self_withholding_ip());// 主机名称
					int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_self_withholding_port());// 主机端口
					NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
					bootstrap.sendMessage(bytes);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
			}
		});
		executors.shutdown();
	}

	/**
	 *
	 * @param tradeBean
	 */
	@Override
	public void updateTradeLog(TradeBean tradeBean) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *
	 * @param tradeBean
	 */
	@Override
	public void dealWithTradeResult(TradeBean tradeBean) {
		PojoTxnsWithholding withholding = tradeBean.getWithholding();
		PayPartyBean payPartyBean = null;
        if(StringUtils.isNotEmpty(withholding.getOrireqserialno())){
            PojoTxnsWithholding old_withholding = txnsWithholdingService.getWithholdingBySerialNo(withholding.getOrireqserialno());
            //更新支付方信息
            payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),"01", withholding.getOrireqserialno(), old_withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",old_withholding.getAccno(),withholding.getPayserialno());
        }else{
            payPartyBean = new PayPartyBean(tradeBean.getTxnseqno(),"01", withholding.getSerialno(), withholding.getChnlcode(), Constant.getInstance().getCmbc_merid(), "", DateUtil.getCurrentDateTime(), "",withholding.getAccno(),withholding.getPayserialno());
        }
        payPartyBean.setPanName(withholding.getAccname());
        payPartyBean.setPayretcode(withholding.getExeccode());
        payPartyBean.setPayretinfo(withholding.getExecmsg());
        txnsLogDAO.updateCMBCTradeData(payPartyBean);
        if(withholding.getExectype().equals("S")){
        	txnsOrderinfoDAO.updateOrderToSuccess(tradeBean.getTxnseqno());
        	tradeAccountingService.accountingFor(tradeBean.getTxnseqno());
        	tradeNotifyService.notify(tradeBean.getTxnseqno());
        }else if(withholding.getExectype().equals("E")){
        	txnsOrderinfoDAO.updateOrderToFail(tradeBean.getTxnseqno());
        }else if(withholding.getExectype().equals("R")){
        	asyncQueueQueryTrade(tradeBean);
        }
	}
	
	
	public PojoTxnsWithholding createCMBCSelfPojo(TradeBean trade){
		PojoTxnsWithholding txnsWithholding = new PojoTxnsWithholding();
    	txnsWithholding.setMerid( Constant.getInstance().getCmbc_self_merid());
        txnsWithholding.setMername(Constant.getInstance().getCmbc_mername());
        txnsWithholding.setTransdate(DateUtil.getCurrentDate());
        txnsWithholding.setTranstime(DateUtil.getCurrentTime());;
        txnsWithholding.setServicecode(Constant.WITHHOLDINGSELF);
        txnsWithholding.setCardtype(CMBCCardTypeEnum.fromCardType(trade.getCardType()).getCode());
        txnsWithholding.setAccno(trade.getCardNo());
        txnsWithholding.setAccname(trade.getAcctName());
        txnsWithholding.setCerttype(CertifTypeEnmu.fromValue("01").getCmbcCode());
        txnsWithholding.setCertno(trade.getCertId());
        txnsWithholding.setPhone(trade.getMobile());
        txnsWithholding.setPayerbankinscode(trade.getBankCode().trim().length()==8?trade.getBankCode().trim():trade.getBankCode()+"0000");
        txnsWithholding.setProvno("");
        txnsWithholding.setMemberid(trade.getMerchId());
        txnsWithholding.setOrderno(trade.getOrderId());
        txnsWithholding.setPayerbankname(BankEnmu.fromValue(txnsWithholding.getPayerbankinscode()).getBankName());
        txnsWithholding.setCvn2(StringUtils.isEmpty(trade.getCvv2())?"":trade.getCvv2());
        txnsWithholding.setExpired(trade.getValidthru());
        txnsWithholding.setBiztype("");
        txnsWithholding.setBizno("");
        txnsWithholding.setTranamt(new BigDecimal(Long.valueOf(trade.getAmount())));
        txnsWithholding.setCurrency("RMB");
        txnsWithholding.setPurpose("代收业务");
        txnsWithholding.setCityno("");
        txnsWithholding.setAgtno("");
        txnsWithholding.setTxnseqno(trade.getTxnseqno());
        txnsWithholding.setChnlcode(ChannelEnmu.CMBCSELFWITHHOLDING.getChnlcode());
		return txnsWithholding;
    }

}
