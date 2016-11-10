/* 
 * WithholdingServiceTest.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.producer;

import java.io.IOException;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.zlebank.zplatform.cmbc.producer.bean.ResultBean;
import com.zlebank.zplatform.cmbc.producer.enums.WithholdingTagsEnum;
import com.zlebank.zplatform.cmbc.producer.interfaces.Producer;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 下午4:23:04
 * @since 
 */
public class WithholdingServiceTest {

	
	public void test_withholding(){
		String json = "{\"acctName\":\"郭佳\",\"amount\":\"13\",\"amount_y\":\"0.13\",\"bankCode\":\"0103\",\"bindCardId\":\"175\",\"busicode\":\"10000001\",\"busitype\":\"1000\","
				+ "\"cardId\":175,\"cardNo\":\"6228480018543668976\",\"cardType\":\"\",\"cashCode\":\"ZLC00001\",\"certId\":\"110105198610094112\",\"certType\":\"00\",\"currentSetp\":\"\","
				+ "\"identifyingCode\":\"858716\",\"merUserId\":\"100000000000576\",\"merchId\":\"300000000000014\",\"miniCardNo\":\"8976\",\"mobile\":\"18600806796\","
				+ "\"orderId\":\"2016101416431832\",\"payinstiId\":\"93000002\",\"reaPayOrderNo\":\"1610149600006692\",\"subMerchId\":\"0\",\"tn\":\"161014001400058499\",\"tradeType\":\"01\","
				+ "\"txnseqno\":\"1610149900059721\"}";
		TradeBean tradeBean = JSON.parseObject(json, TradeBean.class);
		//tradeBean.setTxnseqno("");
		try {
			Producer producer = new WithholdingProducer("192.168.101.104:9876",WithholdingTagsEnum.WITHHOLDING);
			//producer.sendJsonMessage(JSON.toJSONString(orderBean), OrderTagsEnum.COMMONCONSUME_SIMPLIFIED);
			SendResult sendResult = producer.sendJsonMessage(JSON.toJSONString(tradeBean),WithholdingTagsEnum.WITHHOLDING);
			ResultBean queryReturnResult = producer.queryReturnResult(sendResult);
			System.out.println(JSON.toJSONString(queryReturnResult));
			//TimeUnit.MINUTES.sleep(1);
			
			
			System.in.read();
			producer.closeProducer();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//@Test
	public void test_realname() throws MQClientException, RemotingException, InterruptedException, MQBrokerException{
		String json = "{\"acctName\":\"郭佳\",\"amount\":\"13\",\"amount_y\":\"0.13\",\"bankCode\":\"0103\",\"bindCardId\":\"175\",\"busicode\":\"10000001\",\"busitype\":\"1000\","
				+ "\"cardId\":175,\"cardNo\":\"6228480018543668976\",\"cardType\":\"\",\"cashCode\":\"ZLC00001\",\"certId\":\"110105198610094112\",\"certType\":\"00\",\"currentSetp\":\"\","
				+ "\"identifyingCode\":\"858716\",\"merUserId\":\"100000000000576\",\"merchId\":\"300000000000014\",\"miniCardNo\":\"8976\",\"mobile\":\"18640806796\","
				+ "\"orderId\":\"2016101416431832\",\"payinstiId\":\"93000002\",\"reaPayOrderNo\":\"1610149600006692\",\"subMerchId\":\"0\",\"tn\":\"161014001400058499\",\"tradeType\":\"01\","
				+ "\"txnseqno\":\"1610149900059721\"}";
		TradeBean tradeBean = JSON.parseObject(json, TradeBean.class);
		
		Producer producer = new WithholdingProducer("192.168.101.104:9876",WithholdingTagsEnum.WITHHOLDING);
		//producer.sendJsonMessage(JSON.toJSONString(orderBean), OrderTagsEnum.COMMONCONSUME_SIMPLIFIED);
		SendResult sendResult = producer.sendJsonMessage(JSON.toJSONString(tradeBean),WithholdingTagsEnum.REALNAME);
		ResultBean queryReturnResult = producer.queryReturnResult(sendResult);
		System.out.println(JSON.toJSONString(queryReturnResult));
	}
	@Test
	public void test_query_trade() throws MQClientException, RemotingException, InterruptedException, MQBrokerException{
		TradeBean tradeBean = new TradeBean();
		tradeBean.setTxnseqno("1607149900054859");
		Producer producer = new WithholdingProducer("192.168.101.104:9876",WithholdingTagsEnum.WITHHOLDING);
		SendResult sendResult = producer.sendJsonMessage(JSON.toJSONString(tradeBean),WithholdingTagsEnum.WITHHOLDING_QUERY_ACCOUNTING);
		ResultBean queryReturnResult = producer.queryReturnResult(sendResult);
		System.out.println(JSON.toJSONString(queryReturnResult));
	}
}
