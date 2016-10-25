package com.zlebank.zplatform.cmbc.insteadpay.net;
import java.net.Socket;
import java.util.Date;

/**
 * <strong>Title : SocketHelper</strong><br>
 * <strong>Description : socket辅助器</strong><br>
 * <strong>Create on : 2015-9-30</strong><br>
 * 
 * @author linda1@cmbc.com.cn<br>
 */
public class SocketHelper {
	private static SocketHelper socketHelper = null;
	/**
	 * socket关键字
	 */
	private String socketKey;

	/**
	 * socket客户端对象
	 */
	private Socket socket;

	/**
	 * 最后活动时间
	 */
	private Date lastActiveTime = new Date();

	/**
	 * 已经接收的粘包块
	 */
	private byte[] receivedBytes;

	/**
	 * @return the socketKey
	 */
	public String getSocketKey() {
		return socketKey;
	}

	/**
	 * @param socketKey
	 *            the socketKey to set
	 */
	public void setSocketKey(String socketKey) {
		this.socketKey = socketKey;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @return the lastActiveTime
	 */
	public Date getLastActiveTime() {
		return lastActiveTime;
	}

	/**
	 * @param lastActiveTime
	 *            the lastActiveTime to set
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
	 * @param receivedBytes
	 *            the receivedBytes to set
	 */
	public void setReceivedBytes(byte[] receivedBytes) {
		this.receivedBytes = receivedBytes;
	}
	
	public static synchronized SocketHelper getInstance(){
		if(socketHelper==null){
			socketHelper = new SocketHelper();
		}
		return socketHelper;
	}
}
