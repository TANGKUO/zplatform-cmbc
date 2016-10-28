/* 
 * InsteadPayListener.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.consumer.listener;

import java.util.List;
import java.util.ResourceBundle;

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
import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.SingleReexchangeBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.consumer.enums.InsteadPayTagsEnum;
import com.zlebank.zplatform.cmbc.consumer.enums.WithholdingTagsEnum;
import com.zlebank.zplatform.cmbc.insteadpay.service.InsteadPayCacheResultService;
import com.zlebank.zplatform.cmbc.insteadpay.service.InsteadPayService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 下午2:31:41
 * @since 
 */
@Service("insteadPayListener")
public class InsteadPayListener implements MessageListenerConcurrently{
	private static final Logger log = LoggerFactory.getLogger(WithholdingListener.class);
	private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("consumer_cmbc");
	private static final String KEY = "CMBCINSTEADPAY:";
	
	@Autowired
	private InsteadPayService insteadPayService;
	@Autowired
	private InsteadPayCacheResultService insteadPayCacheResultService;
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
			if (msg.getTopic().equals(RESOURCE.getString("cmbc.insteadpay.subscribe"))) {
				InsteadPayTagsEnum insteadPayTagsEnum = InsteadPayTagsEnum.fromValue(msg.getTags());
				if (insteadPayTagsEnum==InsteadPayTagsEnum.INSTEADPAY_REALTIME) {
					
						String json = new String(msg.getBody(), Charsets.UTF_8);
						log.info("接收到的MSG:" + json);
						log.info("接收到的MSGID:" + msg.getMsgId());
						InsteadPayTradeBean tradeBean = JSON.parseObject(json,InsteadPayTradeBean.class);
						if (tradeBean == null) {
							log.warn("MSGID:{}JSON转换后为NULL,无法生成订单数据,原始消息数据为{}",msg.getMsgId(), json);
							break;
						}
						ResultBean resultBean = null;
						try {
							resultBean = insteadPayService.realTimeSingleInsteadPay(tradeBean);
						} catch (CMBCTradeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							resultBean = new ResultBean(e.getCode(), e.getMessage());
						}catch (Throwable e) {
							// TODO: handle exception
							resultBean = new ResultBean("T000", e.getMessage());
						}
						insteadPayCacheResultService.saveInsteadPayResult(KEY + msg.getMsgId(), JSON.toJSONString(resultBean));
				}else if(insteadPayTagsEnum==InsteadPayTagsEnum.INSTEADPAY_REALTIME_REEXCHANGE){
					String json = new String(msg.getBody(), Charsets.UTF_8);
					log.info("接收到的MSG:" + json);
					log.info("接收到的MSGID:" + msg.getMsgId());
					SingleReexchangeBean tradeBean = JSON.parseObject(json,SingleReexchangeBean.class);
					if (tradeBean == null) {
						log.warn("MSGID:{}JSON转换后为NULL,无法生成订单数据,原始消息数据为{}",msg.getMsgId(), json);
						break;
					}
					
					try {
						insteadPayService.reexchange(tradeBean);
					} catch (CMBCTradeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (Throwable e) {
						// TODO: handle exception
					}
				}else if(insteadPayTagsEnum==InsteadPayTagsEnum.QUERY_INSTEADPAY_REALTIME_ACCOUNTING){
					String json = new String(msg.getBody(), Charsets.UTF_8);
					log.info("接收到的MSG:" + json);
					log.info("接收到的MSGID:" + msg.getMsgId());
					TradeBean tradeBean = JSON.parseObject(json,TradeBean.class);
					if (tradeBean == null) {
						log.warn("MSGID:{}JSON转换后为NULL,无法生成订单数据,原始消息数据为{}",msg.getMsgId(), json);
						break;
					}
					
					try {
						insteadPayService.queryAndAccounting(tradeBean.getTxnseqno());
					} catch (Throwable e) {
						// TODO: handle exception
					}
				}

			}
			log.info(Thread.currentThread().getName()+ " Receive New Messages: " + msgs);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/*public static void main(String[] args) {
		
		String json ="{\"acc_name\":\"郭佳\",\"acc_no\":\"6228480018543668976\",\"bank_name\":\"\",\"bank_type\":\"\",\"remark\":\"测试\",\"trans_amt\":\"10\"}";
		InsteadPayTradeBean tradeBean = JSON.parseObject(json,InsteadPayTradeBean.class);
	}*/
}
