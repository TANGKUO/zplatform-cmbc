/* 
 * WithholdingListener.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.consumer.listener;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.consumer.enums.WithholdingTagsEnum;
import com.zlebank.zplatform.cmbc.withholding.service.ZlebankToCMBCWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.service.WithholdingCacheResultService;

/**
 * 民生代扣监听器
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午10:52:26
 * @since 
 */
@Service("withholdingListener")
public class WithholdingListener implements MessageListenerConcurrently{

	private static final Logger log = LoggerFactory.getLogger(WithholdingListener.class);
	private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("consumer_cmbc");
	private static final String KEY = "CMBCWITHHOLDING:";
	
	@Autowired
	private ZlebankToCMBCWithholdingService cmbcZlebankWithholdingService;
	@Autowired
	private WithholdingCacheResultService withholdingCacheResultService;
	/**
	 *
	 * @param msgs
	 * @param context
	 * @return
	 */
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {
		for (MessageExt msg : msgs) {
			if (msg.getTopic().equals(RESOURCE.getString("cmbc.withholding.subscribe"))) {
				WithholdingTagsEnum withholdingTagsEnum = WithholdingTagsEnum.fromValue(msg.getTags());
				if(withholdingTagsEnum == WithholdingTagsEnum.WITHHOLDING){
					String json = new String(msg.getBody(), Charsets.UTF_8);
					log.info("接收到的MSG:" + json);
					log.info("接收到的MSGID:" + msg.getMsgId());
					TradeBean tradeBean = JSON.parseObject(json,TradeBean.class);
					if (tradeBean == null) {
						log.warn("MSGID:{}JSON转换后为NULL,无法生成订单数据,原始消息数据为{}",msg.getMsgId(), json);
						break;
					}
					ResultBean resultBean = null;
					try {
						resultBean = cmbcZlebankWithholdingService.withholding(tradeBean);
						withholdingCacheResultService.saveWithholdingResult(KEY + msg.getMsgId(), JSON.toJSONString(resultBean));
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						resultBean = new ResultBean("T000", e.getLocalizedMessage());
					}
				}
				

			}
			log.info(Thread.currentThread().getName()+ " Receive New Messages: " + msgs);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
