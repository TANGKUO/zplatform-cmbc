/* 
 * SocketChannelHelper.java  
 * 
 * version TODO
 *
 * 2016年11月2日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.net.netty;

import java.util.Date;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月2日 上午11:25:28
 * @since 
 */
public class SocketChannelHelper {
	private static SocketChannelHelper  helper = null;
	/**
	 * socket关键字
	 */
	private String socketKey;
	/**
	 * 最后活动时间
	 */
	private Date lastActiveTime = new Date();

	/**
	 * 已经接收的粘包块
	 */
	private byte[] receivedBytes;
	
	private final MessageConfigService messageConfigService ;
	private final MessageHandler messageHandler ;
	
	
	
	
	
	/**
	 * @return the socketKey
	 */
	public String getSocketKey() {
		return socketKey;
	}

	/**
	 * @param socketKey the socketKey to set
	 */
	public void setSocketKey(String socketKey) {
		this.socketKey = socketKey;
	}

	/**
	 * @return the lastActiveTime
	 */
	public Date getLastActiveTime() {
		return lastActiveTime;
	}

	/**
	 * @param lastActiveTime the lastActiveTime to set
	 */
	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	/**
	 * @return the receivedBytes
	 */
	public byte[] getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @param receivedBytes the receivedBytes to set
	 */
	public void setReceivedBytes(byte[] receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	public static synchronized SocketChannelHelper getInstance(){
		if(helper==null){
			helper = new SocketChannelHelper();
		}
		return helper;
	}
	
	private SocketChannelHelper(){
		messageConfigService = new MessageConfigService();
		messageConfigService.init();
		messageHandler = new MessageHandler();
		messageHandler.setMessageConfigService(messageConfigService);
	}

	/**
	 * @return the messageConfigService
	 */
	public MessageConfigService getMessageConfigService() {
		return messageConfigService;
	}

	/**
	 * @return the messageHandler
	 */
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}
	
	
}
