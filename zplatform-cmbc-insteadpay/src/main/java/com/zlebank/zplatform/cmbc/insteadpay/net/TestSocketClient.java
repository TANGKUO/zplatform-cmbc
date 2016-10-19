package com.zlebank.zplatform.cmbc.insteadpay.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Title : TestSocketClient</strong><br>
 * <strong>Description : TCP客户端测试类</strong><br>
 * <strong>Create on : 2015-9-30</strong><br>
 * 
 * @author linda1@cmbc.com.cn<br>
 */
public class TestSocketClient {
	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(TestSocketClient.class);

		final MessageConfigService messageConfigService = new MessageConfigService();
		messageConfigService.init();

		final MessageHandler messageHandler = new MessageHandler();
		messageHandler.setMessageConfigService(messageConfigService);
		messageHandler.init();

		final LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();
		final LinkedBlockingQueue<byte[]> receiveQueue = new LinkedBlockingQueue<byte[]>();

		SocketAsyncLongOutputAdapter adapter = new SocketAsyncLongOutputAdapter();
		//adapter.setMessageConfigService(messageConfigService);
		//adapter.setSendQueue(sendQueue);
		//adapter.setReceiveQueue(receiveQueue);
		adapter.start();

		int reqPoolSize = messageConfigService.getInt("POOL_SIZE_REQ", 1);// 线程池
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					while (messageConfigService.getBoolean("CAN_RUN")) {
						try {
							Map<String, Object> dataContainer = new HashMap<String, Object>();
							/**
							 * 1002单笔代付
							 */
							dataContainer.put("MESSAGE_CODE", messageConfigService.getString("MESSAGE_CODE_TRAN"));
							dataContainer.put("HZFLS", XMLMessageUtil.uuid());
							dataContainer.put("JYZH", "6226222912345678");
							dataContainer.put("JYHM", "张三");
							dataContainer.put("JYHHH", null);
							dataContainer.put("JYHMC", null);
							dataContainer.put("JYJE", 4);
							dataContainer.put("JYZY", "测试摘要");

							/**
							 * 3002结果查询
							 */
							// dataContainer.put("MESSAGE_CODE", messageConfigService.getString("MESSAGE_CODE_QUERY"));
							// dataContainer.put("HZFLS", XMLMessageUtil.uuid());
							// dataContainer.put("YHZFRQ", DateFormatUtils.format(new Date(), "yyyyMMdd"));
							// dataContainer.put("YHZFLS", XMLMessageUtil.uuid());
							byte[] bytes = messageHandler.pack(dataContainer);
							if (bytes != null) {
								sendQueue.put(bytes);
							} else {
								logger.error("打包失败:{}", new Object[] { dataContainer });
							}
							Thread.sleep(60 * 1000);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
		}
		executors.shutdown();

		// 初始化线程池
		int resPoolSize = messageConfigService.getInt("POOL_SIZE_RES", 5);// 线程池
		executors = Executors.newFixedThreadPool(resPoolSize);
		for (int i = 0; i < resPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					while (messageConfigService.getBoolean("CAN_RUN")) {
						try {
							byte[] bytes = receiveQueue.take();
							Map<String, Object> dataContainer = messageHandler.unpack(bytes);
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
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
		}
		executors.shutdown();
	}
}
