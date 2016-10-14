/* 
 * WithholdingCacheResultService.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service;

/**
 * 缓存方法执行结果
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午11:07:48
 * @since 
 */
public interface WithholdingCacheResultService {

	/**
	 * 将消费者执行后的结果缓存到redis中
	 * @param key redis主键
	 * @param json JSON结果
	 */
	public void saveWithholdingResult(String key,String json);
}
