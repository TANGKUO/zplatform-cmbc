package com.zlebank.zplatform.cmbc.insteadpay.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.security.CryptoUtil;

/**
 * 
 * TCP异步长连接输出适配器
 *
 * @author guojia
 * @version
 * @date 2016年10月19日 上午9:41:57
 * @since
 */
public class SocketAsyncLongOutputAdapter {

	/**
	 * 日志对象
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 发送队列
	 */
	private final LinkedBlockingQueue<byte[]> sendQueue;

	/**
	 * 接收队列
	 */
	private final LinkedBlockingQueue<byte[]> receiveQueue;

	/**
	 * 报文配置服务
	 */
	private final MessageConfigService messageConfigService;
	
	private final MessageHandler messageHandler;

	/**
	 * 允许运行标识
	 */
	private volatile boolean canRun = true;
	
	private volatile boolean canRun_heart = false;

	/**
	 * socket客户端对象
	 */
	private final SocketHelper socketHelper = new SocketHelper();
	
	private static SocketAsyncLongOutputAdapter adapter = null; 
	
	
	
	/**
	 * 启动
	 */
	public boolean start() {
		/**
		 * 运行链接检测线程
		 */
		
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (canRun) {
						try {
							if (messageConfigService.getBoolean("CAN_RUN")) {
								int interval = messageConfigService.getInt("HEARTBEAT_INTERVAL", 30) * 1000;// 心跳间隔，单位：秒
								checkConnect(interval);
								Thread.sleep(interval);
							} else {
								int interval = messageConfigService.getInt("RUN_CHECK_INTERVAL", 3) * 60 * 1000;// 运行检测间隔，单位：分
								Thread.sleep(interval);
							}
						} catch (Exception e) {
							logger.error(e.getLocalizedMessage(), e);
							canRun_heart = false;
						}
					}
				}
			}).start();
		
		/**
		 * 运行报文发送线程
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (canRun) {
					try {
						if (messageConfigService.getBoolean("CAN_RUN")) {
							sendMessage();
						} else {
							int interval = messageConfigService.getInt("RUN_CHECK_INTERVAL", 3) * 60 * 1000;// 运行检测间隔，单位：分
							Thread.sleep(interval);
						}
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}).start();
		/**
		 * 运行报文接收线程
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (canRun) {
					try {
						if (messageConfigService.getBoolean("CAN_RUN")) {
							receiveMessage();
						} else {
							int interval = messageConfigService.getInt("RUN_CHECK_INTERVAL", 3) * 60 * 1000;// 运行检测间隔，单位：分
							Thread.sleep(interval);
						}
					} catch (Exception e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			}
		}).start();
		return true;
	}

	/**
	 * 停止
	 */
	public boolean stop() {
		canRun = false;
		return true;
	}

	/**
	 * 连接
	 * 
	 * @return
	 */
	private synchronized void connect() {
		if (!messageConfigService.getBoolean("CAN_RUN")) {
			return;
		}

		long retryInterval = messageConfigService.getLong("CONNET_RETRY_INTERVAL", 10);// 连接重试间隔，单位：秒
		int retryCount = messageConfigService.getInt("CONNET_RETRY_COUNT", 3);// 连接重试次数
		String hostName = messageConfigService.getString("HOST_NAME");// 主机名称
		String hostAddress = messageConfigService.getString("HOST_ADDRESS");// 主机名称
		int hostPort = messageConfigService.getInt("HOST_PORT", 9108);// 主机端口
		int sendBufferSize = messageConfigService.getInt("SEND_BUFFER_SIZE", 200 * 1024);// 发送缓冲区容量，单位：字节
		int receiveBufferSize = messageConfigService.getInt("RECEIVE_BUFFER_SIZE", 200 * 1024);// 接收缓冲区容量，单位：字节
		int connetTimeout = messageConfigService.getInt("CONNET_TIMEOUT", 30 * 1000);// 连接超时，单位：毫秒
		for (int i = 0; i < retryCount; i++) {
			try {
				Socket socket = socketHelper.getSocket();
				if (socket != null) {
					if (socket.isConnected()) {
						return;
					} else {
						this.close();
					}
				}

				socket = new Socket();
				socket.setSendBufferSize(sendBufferSize);
				socket.setReceiveBufferSize(receiveBufferSize);
				socket.setKeepAlive(true);
				socket.setTcpNoDelay(true);
				socket.setOOBInline(true);
				//socket.setSoTimeout(timeout);// 不能设置读超时
				socket.connect(new InetSocketAddress(hostAddress, hostPort), connetTimeout);
				logger.info("对端[{}-{}:{}]连接成功，本地端口[{}]", new Object[] { hostName, hostAddress, hostPort, socket.getLocalPort() });
				socketHelper.setSocket(socket);
				socketHelper.setSocketKey("127.0.0.1:" + socket.getLocalPort());
				socketHelper.setLastActiveTime(new Date());
				socketHelper.setReceivedBytes(null);
				return;
			} catch (Exception e) {
				logger.error("对端[{}-{}:{}]连接失败", new Object[] { hostName, hostAddress, hostPort });
				logger.error(e.getMessage(), e);
				this.close();
			}

			try {
				logger.info("对端[{}-{}:{}]连接失败，[{}]秒后重连", new Object[] { hostName, hostAddress, hostPort, retryInterval });
				Thread.sleep(retryInterval * 1000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.error("对端[{}-{}:{}]连接连续失败达到阀值[{}]，进入休眠", new Object[] { hostName, hostAddress, hostPort, retryCount });
		messageConfigService.set("CAN_RUN", "false");
		// TODO
		// 是否允许运行参数入库的话，则会一直睡眠，直到人工修改才可唤醒
		// 若不入库，重新刷新之后，就会唤醒
		return;
	}

	/**
	 * 关闭
	 */
	private void close() {
		socketHelper.setReceivedBytes(null);// 清除已经保存的粘包块
		Socket socket = socketHelper.getSocket();
		logger.info("socket[{}]:[{{}}]关闭",socket.getInetAddress().getHostAddress(),socket.getLocalPort());
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		socketHelper.setSocket(null);
		socketHelper.setLastActiveTime(null);
		this.stop();
	}

	/**
	 * 检测链接
	 * 
	 * @param heartbeatInterval
	 *            心跳间隔
	 */
	private void checkConnect(int heartbeatInterval) {
		while (socketHelper.getSocket() == null) {
			if (!messageConfigService.getBoolean("CAN_RUN")) {
				return;
			}
			this.connect();
		}

		try {
			Socket socket = socketHelper.getSocket();
			if (((new Date()).getTime() - socketHelper.getLastActiveTime().getTime()) > heartbeatInterval) {
				String heartbeatMessage = messageConfigService.getString("HEARTBEAT_MESSAGE");// 心跳报文
				String charset = messageConfigService.getString("CHARSET");// 字符集
				socketHelper.setLastActiveTime(new Date());
				OutputStream output = socket.getOutputStream();
				output.write(heartbeatMessage.getBytes(charset));
				output.flush();
				logger.info("cmbc insteadpay heart message;socket[{}]:[{}]",socket.getInetAddress().getHostAddress(),socket.getLocalPort());
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			this.close();
		}
	}

	/**
	 * 发送报文
	 * 
	 * @param socketHelper
	 */
	private void sendMessage() {
		while (socketHelper.getSocket() == null) {
			if (!messageConfigService.getBoolean("CAN_RUN")) {
				return;
			}
			this.connect();
		}

		String hostName = messageConfigService.getString("HOST_NAME");// 主机名称
		String hostAddress = messageConfigService.getString("HOST_ADDRESS");// 主机名称
		int hostPort = messageConfigService.getInt("HOST_PORT", 9108);// 主机端口
		byte[] bytes = null;
		try {
			bytes = sendQueue.take();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return;
		}
		if (bytes == null || bytes.length < 1) {
			return;
		}
		try {
			Socket socket = socketHelper.getSocket();
			if (socket == null) {
				sendQueue.put(bytes);
				return;
			}
			logger.info("本地 ===>> 对端[{}-{}:{}] ## {}", new Object[] { hostName, hostAddress, hostPort, CryptoUtil.bytes2string(bytes, 16) });

			OutputStream os = socket.getOutputStream();
			os.write(bytes);
			os.flush();
			socketHelper.setLastActiveTime(new Date());
		} catch (Exception e) {
			logger.error("向对端[{}-{}:{}]发送报文出现异常", new Object[] { hostName, hostAddress, hostPort });
			logger.error(e.getLocalizedMessage(), e);
			this.close();

			try {
				sendQueue.put(bytes);
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
			}
		}
	}

	/**
	 * 接收报文
	 * 
	 * @param socketHelper
	 */
	private void receiveMessage() {
		if(socketHelper.getSocket() == null) {
			if (!messageConfigService.getBoolean("CAN_RUN")) {
				return;
			}
			this.connect();
		}
		Socket socket = socketHelper.getSocket();

		String hostName = messageConfigService.getString("HOST_NAME");// 主机名称
		String hostAddress = messageConfigService.getString("HOST_ADDRESS");// 主机名称
		int hostPort = messageConfigService.getInt("HOST_PORT", 9108);// 主机端口
		String charset = messageConfigService.getString("CHARSET");// 字符集
		int headLength = messageConfigService.getInt("HEAD_LENGTH", 6);// 报文头长度位数
		int maxSingleLength = messageConfigService.getInt("MAX_SINGLE_LENGTH", 200 * 1024);// 单个报文最大长度，单位：字节
		String socketKey = socketHelper.getSocketKey();
		try {
			byte[] bytes = socketHelper.getReceivedBytes();
			if (bytes == null) {
				bytes = new byte[0];
			}
			InputStream input = socket.getInputStream();
			/**
			 * 1、读取报文头
			 */
			if (bytes.length < headLength) {
				byte[] headBytes = new byte[headLength - bytes.length];
				int couter = input.read(headBytes);
				if (couter < 0) {
					logger.error("连接[{} --> {}-{}:{}]已关闭", new Object[] { socketKey, hostName, hostAddress, hostPort });
					this.close();
					return;
				}
				bytes = ArrayUtils.addAll(bytes, ArrayUtils.subarray(headBytes, 0, couter));
				if (couter < headBytes.length) {// 未满足长度位数，可能是粘包造成，保存读取到的
					socketHelper.setReceivedBytes(bytes);
					return;
				}
			}
			String headMsg = new String(ArrayUtils.subarray(bytes, 0, headLength), charset);
			int bodyLength = NumberUtils.toInt(headMsg);
			if (bodyLength <= 0 || bodyLength > maxSingleLength * 1024) {
				logger.error("连接[{} --> {}-{}:{}]出现脏数据，自动断链：{}", new Object[] { socketKey, hostName, hostAddress, hostPort, headMsg });
				this.close();
				return;
			}
			/**
			 * 2、读取报文体
			 */
			if (bytes.length < headLength + bodyLength) {
				byte[] bodyBytes = new byte[headLength + bodyLength - bytes.length];
				int couter = input.read(bodyBytes);
				if (couter < 0) {
					logger.error("连接[{} --> {}-{}:{}]已关闭", new Object[] { socketKey, hostName, hostAddress, hostPort });
					this.close();
					return;
				}
				bytes = ArrayUtils.addAll(bytes, ArrayUtils.subarray(bodyBytes, 0, couter));
				if (couter < bodyBytes.length) {// 未满足长度位数，可能是粘包造成，保存读取到的
					socketHelper.setReceivedBytes(bytes);
					return;
				}
			}
			byte[] bodyBytes = ArrayUtils.subarray(bytes, headLength, headLength + bodyLength);
			logger.info("本地 <<=== 对端[{}-{}:{}] ## {}", new Object[] { hostName, hostAddress, hostPort, CryptoUtil.bytes2string(bodyBytes, 16) });
			receiveQueue.put(bodyBytes);

			bytes = ArrayUtils.subarray(bytes, headLength + bodyLength, bytes.length);
			socketHelper.setReceivedBytes(bytes);
		} catch (Exception e) {
			logger.error("从对端[{}-{}:{}]接收报文出现异常", new Object[] { hostName, hostAddress, hostPort });
			logger.error(e.getLocalizedMessage(), e);
			this.close();
		}
	}

	/**
	 * 
	 */
	public SocketAsyncLongOutputAdapter() {
	    messageConfigService = new MessageConfigService();
		messageConfigService.init();

		messageHandler = new MessageHandler();
		messageHandler.setMessageConfigService(messageConfigService);
		messageHandler.init();

		//final LinkedBlockingQueue<byte[]> 
		sendQueue = new LinkedBlockingQueue<byte[]>();
		//final LinkedBlockingQueue<byte[]> 
		receiveQueue = new LinkedBlockingQueue<byte[]>();
		//this.setMessageConfigService(messageConfigService);
		//this.setSendQueue(sendQueue);
		//this.setReceiveQueue(receiveQueue);
	}

	/**
	 * @return the sendQueue
	 */
	public LinkedBlockingQueue<byte[]> getSendQueue() {
		return sendQueue;
	}

	/**
	 * @return the receiveQueue
	 */
	public LinkedBlockingQueue<byte[]> getReceiveQueue() {
		return receiveQueue;
	}

	/**
	 * @return the messageConfigService
	 */
	public MessageConfigService getMessageConfigService() {
		return messageConfigService;
	}

	/**
	 * @return the socketHelper
	 */
	public SocketHelper getSocketHelper() {
		return socketHelper;
	}

	/**
	 * @return the messageHandler
	 */
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}
	
	
}
