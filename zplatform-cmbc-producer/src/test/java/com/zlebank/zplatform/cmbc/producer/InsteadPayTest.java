/* 
 * InsteadPayTest.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.producer;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.zlebank.zplatform.cmbc.producer.bean.ResultBean;
import com.zlebank.zplatform.cmbc.producer.enums.InsteadPayTagsEnum;
import com.zlebank.zplatform.cmbc.producer.interfaces.Producer;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 下午2:47:22
 * @since 
 */
public class InsteadPayTest {

	@Test
	public void test_insteadpay(){
		try {
			Producer producer = new InsteadPayProducer("192.168.101.104:9876", InsteadPayTagsEnum.INSTEADPAY_REALTIME);
			
			InsteadPayTradeBean tradeBean = new InsteadPayTradeBean("");
			tradeBean.setTxnseqno("1610269900000517");
			SendResult sendJsonMessage = producer.sendJsonMessage(JSON.toJSONString(tradeBean),InsteadPayTagsEnum.INSTEADPAY_REALTIME);
			ResultBean resultBean = producer.queryReturnResult(sendJsonMessage);
			System.out.println(JSON.toJSONString(resultBean));
		} catch (MQClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemotingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MQBrokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
