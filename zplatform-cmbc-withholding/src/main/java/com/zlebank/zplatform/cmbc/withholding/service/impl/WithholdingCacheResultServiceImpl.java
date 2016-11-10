/* 
 * WithholdingCacheResultServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.withholding.service.WithholdingCacheResultService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午11:16:37
 * @since 
 */
@Service("withholdingCacheResultService")
public class WithholdingCacheResultServiceImpl implements
		WithholdingCacheResultService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	/**
	 *
	 * @param key
	 * @param json
	 */
	@Override
	public void saveWithholdingResult(String key, String json) {
		//ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
		//opsForValue.set(key, json, 10, TimeUnit.MINUTES);
		BoundListOperations<String, Object> boundListOps = redisTemplate.boundListOps(key);
		boundListOps.leftPush(json);
	}

}
