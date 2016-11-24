/* 
 * CMBCSelfWithholdingServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年11月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.self.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.enums.ChannelEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.self.bean.WithholdingMessageBean;
import com.zlebank.zplatform.cmbc.withholding.self.net.netty.NettyClientBootstrap;
import com.zlebank.zplatform.cmbc.withholding.self.net.netty.SocketChannelHelper;
import com.zlebank.zplatform.cmbc.withholding.self.request.bean.RealTimeSelfWithholdingBean;
import com.zlebank.zplatform.cmbc.withholding.self.service.CMBCSelfWithholdingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月24日 上午11:00:53
 * @since 
 */
@Service("cmbcSelfWithholdingService")
public class CMBCSelfWithholdingServiceImpl implements CMBCSelfWithholdingService {

	private static final Logger log = LoggerFactory.getLogger(CMBCSelfWithholdingServiceImpl.class);
	@Autowired
	private SerialNumberService serialNumberService;
	@Autowired
	private TxnsWithholdingService txnsWithholdingService;
	@Autowired
	private RspmsgDAO rspmsgDAO;
	/**
	 *
	 * @param t
	 * @return
	 */
	
	public ResultBean execute(WithholdingMessageBean messageBean) {
		final RealTimeSelfWithholdingBean realTimeSelfWithholdingBean = new RealTimeSelfWithholdingBean(messageBean);
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
					log.error(e.getMessage(), e);
				}
			}
		});
		executors.shutdown();
		return null;
	}

	/**
	 *
	 * @param txnseqno
	 * @return
	 */
	public ResultBean query(String txnseqno) {
		
		return null;
	}

	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	public ResultBean withholding(TradeBean tradeBean) {
    	PojoTxnsWithholding withholding = new PojoTxnsWithholding(tradeBean,ChannelEnmu.CMBCSELFWITHHOLDING);
        withholding.setSerialno(serialNumberService.generateCMBCSerialNo());
        txnsWithholdingService.saveWithholdingLog(withholding);
        WithholdingMessageBean withholdingMsg = new WithholdingMessageBean(withholding);
        withholdingMsg.setWithholding(withholding);
        /*withholdingService.realTimeWitholdingSelf(withholdingMsg);
        resultBean = queryResult(withholding.getSerialno());*/
        execute(withholdingMsg);
        queryResult(withholding.getSerialno());
		return null;
	}

	 public ResultBean queryResult(String serialno) {
	        PojoTxnsWithholding withholding = null;
	        ResultBean resultBean = null;
	        int[] timeArray = new int[]{1, 2, 8, 16, 32};
	        try {
	            for (int i = 0; i < 5; i++) {
	                withholding = txnsWithholdingService.getWithholdingBySerialNo(serialno);
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
	 * @return
	 */
	@Override
	public ResultBean dealWithAccounting() {
		// TODO Auto-generated method stub
		return null;
	} 
	

}
