package com.zlebank.zplatform.cmbc.withholding.net;

import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;


public interface Client {
    /**
     * 发送
     * @param data 报文
     * @throws CMBCTradeException
     */
	public void sendMessage(byte[] data) throws CMBCTradeException;
	
	public void shutdown();
}
