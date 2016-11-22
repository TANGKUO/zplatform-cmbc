/* 
 * NettyClientHandler.java  
 * 
 * version TODO
 *
 * 2016年11月2日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.net.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealNameResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeInsteadPayResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCWithholdingQueryResultBean;
import com.zlebank.zplatform.cmbc.common.utils.BeanCopyUtil;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.SpringContext;
import com.zlebank.zplatform.cmbc.dao.TxnsCmbcInstPayLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsWithholdingDAO;
import com.zlebank.zplatform.cmbc.security.CryptoUtil;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealNameAuthResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealTimeWithholdingQueryResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.WhiteListResultBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月2日 上午8:54:46
 * @since 
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<byte[]>{

	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
	
	private TxnsWithholdingDAO txnsWithholdingDAO = (TxnsWithholdingDAO) SpringContext.getContext().getBean("txnsWithholdingDAO");
	/**
	 *
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	protected synchronized void channelRead0(ChannelHandlerContext ctx, byte[] msg)
			throws Exception {
		// TODO Auto-generated method stub
		SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
		String hostName = socketChannelHelper.getMessageConfigService().getString("HOST_NAME");// 主机名称
		String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS");// 主机名称
		int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_withholding_port());// 主机端口
		String charset = socketChannelHelper.getMessageConfigService().getString("CHARSET");// 字符集
		int headLength = socketChannelHelper.getMessageConfigService().getInt("HEAD_LENGTH", 6);// 报文头长度位数
		int maxSingleLength = socketChannelHelper.getMessageConfigService().getInt("MAX_SINGLE_LENGTH", 200 * 1024);// 单个报文最大长度，单位：字节
		ByteArrayInputStream input = new ByteArrayInputStream(msg);
		SocketChannelHelper  socketHelper = SocketChannelHelper.getInstance();
		//inputStream.read(b)
		byte[] bytes = socketHelper.getReceivedBytes();
		if (bytes == null) {
			bytes = new byte[0];
		}
		if (bytes.length < headLength) {
			byte[] headBytes = new byte[headLength - bytes.length];
			int couter = input.read(headBytes);
			if (couter < 0) {
				logger.error("连接[{} --> {}-{}:{}]已关闭", new Object[] { socketHelper.getSocketKey(), hostName, hostAddress, hostPort });
				
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
			logger.error("连接[{} --> {}-{}:{}]出现脏数据，自动断链：{}", new Object[] { socketHelper.getSocketKey(), hostName, hostAddress, hostPort, headMsg });
			return;
		}
		/**
		 * 2、读取报文体
		 */
		if (bytes.length < headLength + bodyLength) {
			byte[] bodyBytes = new byte[headLength + bodyLength - bytes.length];
			int couter = input.read(bodyBytes);
			if (couter < 0) {
				logger.error("连接[{} --> {}-{}:{}]已关闭", new Object[] { socketHelper.getSocketKey(), hostName, hostAddress, hostPort });
				return;
			}
			bytes = ArrayUtils.addAll(bytes, ArrayUtils.subarray(bodyBytes, 0, couter));
			if (couter < bodyBytes.length) {// 未满足长度位数，可能是粘包造成，保存读取到的
				socketHelper.setReceivedBytes(bytes);
				return;
			}
		}
		byte[] bodyBytes = ArrayUtils.subarray(bytes, headLength, headLength + bodyLength);
		logger.info("本地[{}] <-- 对端[{}-{}:{}] ## {}", new Object[] {socketHelper.getSocketKey(), hostName, hostAddress, hostPort, CryptoUtil.bytes2string(bodyBytes, 16) });
		//receiveQueue.put(bodyBytes);
		Map<String, Object> dataContainer = socketChannelHelper.getMessageHandler().unpack(bodyBytes);
		if (dataContainer != null) {
			String respType = StringUtils.trimToNull((String) dataContainer.get("YHYDLX"));
			if ("FAIL".equalsIgnoreCase(respType)) {
				logger.error("解包失败:{}", new Object[] { dataContainer });
			}else{
				if(Constant.WITHHOLDING.equals(dataContainer.get("messagecode").toString())){
					RealTimeWithholdingResultBean realTimePayResultBean = (RealTimeWithholdingResultBean) dataContainer.get("result");
					txnsWithholdingDAO.updateWithholdingResult( BeanCopyUtil.copyBean(CMBCRealTimeWithholdingResultBean.class, realTimePayResultBean));
				}else if(Constant.REALNAMEAUTH.equals(dataContainer.get("messagecode").toString())){
					RealNameAuthResultBean nameAuthResultBean = (RealNameAuthResultBean) dataContainer.get("result");
					txnsWithholdingDAO.updateRealNameResult( BeanCopyUtil.copyBean(CMBCRealNameResultBean.class, nameAuthResultBean));
				}else if(Constant.WHITELIST.equals(dataContainer.get("messagecode").toString())){
					WhiteListResultBean whiteListResultBean = (WhiteListResultBean) dataContainer.get("result");
					txnsWithholdingDAO.updateWithholdingResult( BeanCopyUtil.copyBean(CMBCRealTimeWithholdingResultBean.class, whiteListResultBean));
				}else if(Constant.WITHHOLDINGQUERY.equals(dataContainer.get("messagecode").toString())){
					RealTimeWithholdingQueryResultBean realTimeWithholdingQueryResultBean = (RealTimeWithholdingQueryResultBean) dataContainer.get("result");
					txnsWithholdingDAO.updateWithholdingQueryResult(BeanCopyUtil.copyBean(CMBCWithholdingQueryResultBean.class, realTimeWithholdingQueryResultBean));
				}
			}
		}
		
		bytes = ArrayUtils.subarray(bytes, headLength + bodyLength, bytes.length);
		socketHelper.setReceivedBytes(bytes);
		
		
	}

	

}
