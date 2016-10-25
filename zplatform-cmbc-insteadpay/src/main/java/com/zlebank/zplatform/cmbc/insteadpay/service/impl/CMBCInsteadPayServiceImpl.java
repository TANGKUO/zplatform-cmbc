/* 
 * CMBCInsteadPayServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.service.impl;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeInsteadPayResultBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.utils.BeanCopyUtil;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.dao.TxnsCmbcInstPayLogDAO;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayBean;
import com.zlebank.zplatform.cmbc.insteadpay.bean.RealTimePayResultBean;
import com.zlebank.zplatform.cmbc.insteadpay.net.SocketAsyncLongOutputAdapter;
import com.zlebank.zplatform.cmbc.insteadpay.service.CMBCInsteadPayService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 上午11:26:32
 * @since 
 */
@Service("cmbcInsteadPayService")
public class CMBCInsteadPayServiceImpl implements CMBCInsteadPayService {

	private static final Logger logger = LoggerFactory.getLogger(CMBCInsteadPayServiceImpl.class);
	
	@Autowired
	private TxnsCmbcInstPayLogDAO txnsCmbcInstPayLogDAO;
	/**
	 *
	 * @param realTimePayBean
	 * @return
	 */
	@Override
	public ResultBean realTimeInsteadPay(final RealTimePayBean realTimePayBean) {
		final SocketAsyncLongOutputAdapter adapter = new SocketAsyncLongOutputAdapter();
		adapter.start();
		int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						byte[] bytes = adapter.getMessageHandler().pack(realTimePayBean);
						if (bytes != null) {
							adapter.getSendQueue().put(bytes);
						} else {
							logger.error("打包失败:{}", new Object[] { JSON.toJSONString(realTimePayBean) });
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
		
		// 初始化线程池
		int resPoolSize = 1;// 线程池
		executors = Executors.newFixedThreadPool(resPoolSize);
		for (int i = 0; i < resPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					while (adapter.getMessageConfigService().getBoolean("CAN_RUN")) {
						try {
							byte[] bytes = adapter.getReceiveQueue().take();
							Map<String, Object> dataContainer = adapter.getMessageHandler().unpack(bytes);
							if (dataContainer == null) {
								continue;
							}
							String respType = StringUtils.trimToNull((String) dataContainer.get("YHYDLX"));
							if ("FAIL".equalsIgnoreCase(respType)) {
								logger.error("解包失败:{}", new Object[] { dataContainer });
							}else{
								//具体业务处理代码
								/*
								 * 1.代付结果
								 * 2.代付查询结果
								 */
								if(Constant.REALTIME_INSTEADPAY.equals(dataContainer.get("messagecode").toString())){
									RealTimePayResultBean realTimePayResultBean = (RealTimePayResultBean) dataContainer.get("result");
									txnsCmbcInstPayLogDAO.updateInsteadPayResult(BeanCopyUtil.copyBean(CMBCRealTimeInsteadPayResultBean.class, realTimePayResultBean));
									break;
								}
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
		}
		executors.shutdown();
		return null;
	}

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean realtimeBatchInsteadPay(String batchNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * @param batchNo
	 * @return
	 */
	@Override
	public ResultBean batchInsteadPay(String batchNo) {
		// TODO Auto-generated method stub
		return null;
	}

}
