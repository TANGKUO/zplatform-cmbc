/* 
 * ITradeReceiveProcessor.java  
 * 
 * version TODO
 *
 * 2015年11月17日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.common.processor;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年11月17日 下午2:27:10
 * @since 
 */
public interface ITradeReceiveProcessor {
	/**
	 * 处理交易结果
	 * @param resultBean
	 * @param tradeBean
	 * @param tradeType
	 */
    public void onReceive(ResultBean resultBean,TradeBean tradeBean,TradeTypeEnum tradeType);
    /**
     * 生成异步通知报文
     * @param txnseqno
     * @return
     */
    public ResultBean generateAsyncRespMessage(String txnseqno);
}
