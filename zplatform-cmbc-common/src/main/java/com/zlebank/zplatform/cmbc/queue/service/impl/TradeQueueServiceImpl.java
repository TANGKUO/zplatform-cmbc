/* 
 * TradeQueueServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月26日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.queue.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.cmbc.common.bean.CMBCTradeQueueBean;
import com.zlebank.zplatform.cmbc.common.enums.TradeQueueEnum;
import com.zlebank.zplatform.cmbc.queue.service.TradeQueueService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月26日 上午11:07:52
 * @since
 */
@Service("tradeQueueService")
public class TradeQueueServiceImpl implements TradeQueueService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 *
	 * @param queueBean
	 */
	@Override
	public void addInsteadPayQueue(CMBCTradeQueueBean queueBean) {
		// TODO Auto-generated method stub
		try {
			BoundListOperations<String, String> boundListOps = redisTemplate
					.boundListOps(TradeQueueEnum.TRADEQUEUE.getName());
			boundListOps.rightPush(JSON.toJSONString(queueBean));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
