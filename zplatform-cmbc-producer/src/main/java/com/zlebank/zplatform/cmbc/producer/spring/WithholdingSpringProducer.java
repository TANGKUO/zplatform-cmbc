/* 
 * WithholdingProducer.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.producer.spring;

import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.google.common.base.Charsets;
import com.zlebank.zplatform.cmbc.producer.bean.ResultBean;
import com.zlebank.zplatform.cmbc.producer.enums.WithholdingTagsEnum;
import com.zlebank.zplatform.cmbc.producer.interfaces.Producer;
import com.zlebank.zplatform.cmbc.producer.redis.RedisFactory;

/**
 * 民生渠道代扣生产者
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午9:57:27
 * @since 
 */
public class WithholdingSpringProducer implements Producer{
	private final static Logger logger = LoggerFactory.getLogger(WithholdingSpringProducer.class);
	private static final String KEY = "CMBCWITHHOLDING:";
	private static final  ResourceBundle RESOURCE = ResourceBundle.getBundle("producer_cmbc");
	
	//RocketMQ消费者客户端
	private DefaultMQProducer producer;
	//主题
	private String topic;
	private String namesrvAddr;
	public void init() throws MQClientException{
		logger.info("【初始化WithholdingSpringProducer】");
		if(StringUtils.isEmpty(namesrvAddr)){
			namesrvAddr = RESOURCE.getString("single.namesrv.addr");
		}
		logger.info("【namesrvAddr】"+namesrvAddr);          
		producer = new DefaultMQProducer(RESOURCE.getString("cmbc.withholding.producer.group"));
		producer.setNamesrvAddr(namesrvAddr);
		Random random = new Random();
        producer.setInstanceName(RESOURCE.getString("cmbc.withholding.instancename")+random.nextInt(9999));
        topic = RESOURCE.getString("cmbc.withholding.subscribe");
        logger.info("【初始化WithholdingSpringProducer结束】");
        producer.start();
	}
	
	
	
	
	
	/**
	 *
	 * @param message
	 * @return
	 * @throws MQClientException
	 * @throws RemotingException
	 * @throws InterruptedException
	 * @throws MQBrokerException
	 */
	@Override
	public SendResult sendJsonMessage(String message,Object tags) throws MQClientException,
			RemotingException, InterruptedException, MQBrokerException {
		if(producer==null){
			throw new MQClientException(-1,"SimpleOrderProducer为空");
		}
		WithholdingTagsEnum withholdingTagsEnum = (WithholdingTagsEnum) tags;
		Message msg = new Message(topic, withholdingTagsEnum.getCode(), message.getBytes(Charsets.UTF_8));
		SendResult sendResult = producer.send(msg);
		return sendResult;
	}

	/**
	 *
	 */
	@Override
	public void closeProducer() {
		producer.shutdown();
		producer = null;
	}

	/**
	 *
	 * @param sendResult
	 * @return
	 */
	@Override
	public ResultBean queryReturnResult(SendResult sendResult) {
		logger.info("【SimpleOrderCallback receive Result message】{}",JSON.toJSONString(sendResult));
		logger.info("msgID:{}",sendResult.getMsgId());
		
		for (int i = 0;i<1;i++) {
			String json = getJsonByCycle(sendResult.getMsgId());
			logger.info("从redis中取得key【{}】值为{}",KEY+sendResult.getMsgId(),json);
			if(StringUtils.isNotEmpty(json)){
				ResultBean resultBean = JSON.parseObject(json, ResultBean.class);
				
				logger.info("msgID:{},结果数据:{}",sendResult.getMsgId(),JSON.toJSONString(resultBean));
				return resultBean;
			}else{
				try {
					TimeUnit.MILLISECONDS.sleep(900);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		logger.info("end time {}",System.currentTimeMillis());
		return null;
	}
	private String getJsonByCycle(String msgId){
		Jedis jedis = RedisFactory.getInstance().getRedis();
		//String tn = jedis.get(KEY+msgId);
		List<String> brpop = jedis.brpop(40, KEY+msgId);
		if(brpop.size()>0){
			String tn = brpop.get(1);
			
			if(StringUtils.isNotEmpty(tn)){
				return tn;
			}
		}
		jedis.close();
		return null;
	}

}
