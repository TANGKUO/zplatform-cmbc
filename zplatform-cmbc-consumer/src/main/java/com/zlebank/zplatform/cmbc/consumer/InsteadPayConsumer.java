/* 
 * InsteadPayConsumer.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.consumer;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.zlebank.zplatform.cmbc.consumer.enums.InsteadPayTagsEnum;
import com.zlebank.zplatform.cmbc.insteadpay.net.SocketAsyncLongOutputAdapter;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 下午2:27:27
 * @since 
 */
@Service
public class InsteadPayConsumer implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger log = LoggerFactory.getLogger(WithholdingConsumer.class);
	private static final  ResourceBundle RESOURCE = ResourceBundle.getBundle("consumer_cmbc");
	@Autowired
	@Qualifier("insteadPayListener")
	private MessageListenerConcurrently insteadPayListener;
	
	public void startConsume() throws InterruptedException, MQClientException {
		/**
		 * 当前例子是PushConsumer用法，使用方式给用户感觉是消息从RocketMQ服务器推到了应用客户端。<br>
		 * 但是实际PushConsumer内部是使用长轮询Pull方式从RocketMQ服务器拉消息，然后再回调用户Listener方法<br>
		 */
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RESOURCE.getString("cmbc.insteadpay.producer.group"));
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.setNamesrvAddr(RESOURCE.getString("single.namesrv.addr"));
		consumer.setInstanceName(RESOURCE.getString("cmbc.insteadpay.instancename"));
		String subExpression = "";
		for(InsteadPayTagsEnum tagsEnum:InsteadPayTagsEnum.values()){
			if(StringUtils.isNotEmpty(subExpression)){
				subExpression+=" || ";
			}
			subExpression+=tagsEnum.getCode();
		}
		log.info("subExpression:{}",subExpression);
		consumer.subscribe(RESOURCE.getString("cmbc.insteadpay.subscribe"), subExpression);
		consumer.registerMessageListener(insteadPayListener);//在监听器中实现创建order
		log.info("NamesrvAddr:{},InstanceName:{},subscribe:{},MessageListener:{}",consumer.getNamesrvAddr(),consumer.getInstanceName(),consumer.getSubscription(),consumer.getMessageListener());
		consumer.start();
		log.info("{},InsteadPayConsumer消费者启动",consumer.getInstanceName());
	}
	/**
	 *
	 * @param event
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		if (event.getApplicationContext().getParent() == null) {
			try {
				startConsume();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			} catch (MQClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}

}
