/* 
 * AbstractSemiSyncTrade.java  
 * 
 * version TODO
 *
 * 2016年11月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.semisync;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月24日 下午3:28:47
 * @since 
 */
public abstract class AbstractSemiSyncTrade<T> implements SemiSyncTrade<T> {

	/**
	 * 保存交易日志（核心和渠道）
	 * @param tradeBean
	 */
	public abstract void saveTradeLog(T tradeBean);

	/**
	 * 发送交易报文
	 * @param tradeBean
	 */
	public abstract ResultBean sendTradeMessage(T tradeBean);

	/**
	 * 更新交易日志（渠道）
	 * @param tradeBean
	 */
	public abstract void updateTradeLog(T tradeBean);

	/**
	 * 处理交易结果
	 * @param tradeBean
	 * @return
	 */
	public abstract void dealWithTradeResult(T tradeBean);

	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	@Override
	public ResultBean pay(T tradeBean) {
		saveTradeLog(tradeBean);
		sendTradeMessage(tradeBean);
		updateTradeLog(tradeBean);
		ResultBean resultBean = queryTrade(tradeBean);
		dealWithTradeResult(tradeBean);
		return resultBean;
	}
	
	
}
