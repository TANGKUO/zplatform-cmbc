/* 
 * RedisSerialNumberServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年9月12日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.sequence.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年9月12日 下午3:50:14
 * @since
 */
@Service("redisSerialNumberService")
public class RedisSerialNumberServiceImpl implements SerialNumberService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	private static final String CMBC_KEY="SEQUENCE:CMBCSERIALNO";
	private static final String CMBC_INSTEADPAY_KEY="SEQUENCE:CMBCINSTEADPAYSERIALNO";
	private static final String TXNSEQNO_KEY="SEQUENCE:TXNSEQNO";
	public String formateSequence(String key){
		ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
		Long increment = opsForValue.increment(key, 1);
		if(increment>=99999999){
			opsForValue.set(key, "0");
		}
		DecimalFormat df = new DecimalFormat("00000000");
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String seqNo = sdf.format(new Date()) + df.format(increment);
		return seqNo;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String generateCMBCSerialNo() {
		String seqNo = formateSequence(CMBC_KEY);
		return seqNo.substring(0, 6) + "93" + seqNo.substring(6);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String generateCMBCInsteadPaySerialNo() {
		String seqNo = formateSequence(CMBC_INSTEADPAY_KEY);
		return seqNo.substring(0, 6) + "93" + seqNo.substring(6);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String generateTxnseqno() {
		String seqNo = formateSequence(TXNSEQNO_KEY);
		return seqNo.substring(0, 6) + "99" + seqNo.substring(6);
	}
}
