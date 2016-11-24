/* 
 * AbstractSyncTrade.java  
 * 
 * version TODO
 *
 * 2016年11月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.trade.sync;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月24日 下午2:53:45
 * @since
 */
public abstract class AbstractSyncTrade<T> implements SyncTrade<T> {

	/**
	 * 保存交易日志（核心和渠道）
	 * @param tradeBean
	 */
	public abstract void saveTradeLog(T tradeBean);

	/**
	 * 发送交易报文
	 * @param tradeBean
	 */
	public abstract void sendTradeMessage(T tradeBean);

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
	public abstract ResultBean dealWithTradeResult(T tradeBean);

	/**
	 *
	 * @param tradeBean
	 * @return
	 */
	@Override
	public ResultBean pay(T tradeBean) {
		// TODO Auto-generated method stub
		ResultBean resultBean = null;
		
		saveTradeLog(tradeBean);

		sendTradeMessage(tradeBean);

		updateTradeLog(tradeBean);

		resultBean = dealWithTradeResult(tradeBean);

		return resultBean;
	}

}
