/* 
 * WithholdingServiceImpl.java  
 * 
 * version TODO
 *
 * 2015年11月23日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service.impl;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.security.RSAHelper;
import com.zlebank.zplatform.cmbc.withholding.bean.CardMessageBean;
import com.zlebank.zplatform.cmbc.withholding.bean.WhiteListMessageBean;
import com.zlebank.zplatform.cmbc.withholding.bean.WithholdingMessageBean;
import com.zlebank.zplatform.cmbc.withholding.net.netty.NettyClientBootstrap;
import com.zlebank.zplatform.cmbc.withholding.net.netty.SocketChannelHelper;
import com.zlebank.zplatform.cmbc.withholding.request.bean.RealNameAuthBean;
import com.zlebank.zplatform.cmbc.withholding.request.bean.RealNameAuthQueryBean;
import com.zlebank.zplatform.cmbc.withholding.request.bean.RealTimeWithholdingBean;
import com.zlebank.zplatform.cmbc.withholding.request.bean.RealTimeWithholdingQueryBean;
import com.zlebank.zplatform.cmbc.withholding.request.bean.WhiteListBean;
import com.zlebank.zplatform.cmbc.withholding.request.bean.WhiteListQueryBean;
import com.zlebank.zplatform.cmbc.withholding.service.WithholdingService;
import com.zlebank.zplatform.cmbc.withholding.socket.crossline.CMBCWithholdingReciveProcessor;
import com.zlebank.zplatform.cmbc.withholding.socket.crossline.CMBCWithholdingResultReciveProcessor;
import com.zlebank.zplatform.cmbc.withholding.socket.crossline.WithholdingLongSocketClient;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年11月23日 下午2:28:54
 * @since
 */
@Service("withholdingService")
public class WithholdingServiceImpl implements WithholdingService {
    private static final String ENCODING = "UTF-8";
    private static final Logger log = LoggerFactory.getLogger(WithholdingServiceImpl.class);
    public static final String REALNAMEAUTH = "1004    ";
    public static final String REALNAMEAUTHQUERY = "3004    ";
    public static final String WITHHOLDING = "1003    ";
    public static final String WITHHOLDINGQUERY = "3003    ";
    public static final String WHITELIST = "1007    ";
    public static final String WHITELISTQUERY = "3007    ";
    public static final String WITHHOLDINGSELF = "1009    ";
    public static final String WITHHOLDINGQUERYSELF = "3009    ";
    
   
    /**
     *
     * @param json
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean realNameAuthentication(String json)
            throws CMBCTradeException {
        // TODO Auto-generated method stub
       /* CardMessageBean card = null;
        try {
            card = JSON.parseObject(json, CardMessageBean.class);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new CMBCTradeException("");
        }*/
        //RealNameAuthBean realNameAuthBean = new RealNameAuthBean(card);

        return null;
    }
    /**
     *
     * @param json
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean realNameAuthentication(CardMessageBean card)
            throws CMBCTradeException {
        final RealNameAuthBean realNameAuthBean = new RealNameAuthBean(card);
        int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
						byte[] bytes = socketChannelHelper.getMessageHandler().pack(realNameAuthBean);
						String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS", Constant.getInstance().getCmbc_withholding_ip());// 主机名称
						int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_withholding_port());// 主机端口
						NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
						bootstrap.sendMessage(bytes);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
        return null;
        
        ///////////////////////////////////////////////////////////////////////////////////////
        /*String message = realNameAuthBean.toXML();
        log.info("send realNameAuth msg xml:"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(Constant.getInstance().getCmbc_withholding_private_key(),Constant.getInstance().getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
            		Constant.getInstance()
                    .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+REALNAMEAUTH.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+REALNAMEAUTH+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;*/
    }
    
    
    
    
    
    /**
     * 
     * @param priKey
     *            私钥
     * @param message
     *            报文
     * @return
     * @throws Exception
     */
    private byte[] signMsg(String priKey, String pubKey, String message)
            throws Exception {
        if(log.isDebugEnabled()){
            log.debug("priKey:"+priKey);
            log.debug("pubKey:"+pubKey);
            log.debug("message:"+message);
        }
        RSAHelper cipher = new RSAHelper();
        cipher.initKey(priKey, pubKey, 2048);
        byte[] signBytes = cipher.signRSA(message.getBytes(ENCODING), false,ENCODING);
        return signBytes;
    }
    private byte[] encryptMsg(String priKey, String pubKey, String message) throws Exception{
        RSAHelper cipher = new RSAHelper();
        cipher.initKey(priKey, pubKey, 2048);
        byte[] cryptedBytes = cipher.encryptRSA(message.getBytes(ENCODING),
                false, ENCODING);
        return cryptedBytes;
    }
    
    private String getCMBCMerId(){
        String merId = Constant.getInstance().getCmbc_merid();
        int length = 8-merId.length();
        for(int i=0;i<length;i++){
            merId+=" ";
        }
        return merId;
    }
    
    private String getCMBCSelfMerId(){
        String merId = Constant.getInstance().getCmbc_self_merid();
        int length = 15-merId.length();
        for(int i=0;i<length;i++){
            merId+=" ";
        }
        return merId;
    }
   
    public  byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
    
    
    /**
     *
     * @param withholdingMsg
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean realTimeWitholding(WithholdingMessageBean withholdingMsg) throws CMBCTradeException {
    	ResultBean resultBean = null;
        final RealTimeWithholdingBean realNameAuthBean = new RealTimeWithholdingBean(withholdingMsg);
        int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
						byte[] bytes = socketChannelHelper.getMessageHandler().pack(realNameAuthBean);
						String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS", Constant.getInstance().getCmbc_withholding_ip());// 主机名称
						int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_withholding_port());// 主机端口
						NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
						bootstrap.sendMessage(bytes);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
        return null;
        ////////////////////////////////////////////以下为原始socket长连接//////////////////////////////////////////////////////////
        /*String message = realNameAuthBean.toXML();
        log.info("send realTimeWitholding msg xml :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
            		Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                            Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
            		Constant.getInstance()
                    .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WITHHOLDING.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDING+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            log.info("send org msg bytes:"+sendBytes);
            WithholdingLongSocketClient client =  WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
            resultBean = new ResultBean("success");
            
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("09", e.getMessage());
        }catch (Exception e) {
        	e.printStackTrace();
        	resultBean = new ResultBean("09", e.getMessage());
		}
        return resultBean;*/
    }
    /**
     *
     * @param whiteListMsg
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean whiteListCollection(WhiteListMessageBean whiteListMsg)
            throws CMBCTradeException {
        final WhiteListBean whiteListBean = new WhiteListBean(whiteListMsg);
        int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
						byte[] bytes = socketChannelHelper.getMessageHandler().pack(whiteListBean);
						String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS", Constant.getInstance().getCmbc_withholding_ip());// 主机名称
						int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_withholding_port());// 主机端口
						NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
						bootstrap.sendMessage(bytes);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
        return null;
        ////////////////////////////////////////////////////////////////////////////////////////////
        /*String message = whiteListBean.toXML();
        log.info("send whiteListCollection msg xml:"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
            		Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                            Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
            		Constant.getInstance()
                    .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WHITELIST.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WHITELIST+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;*/
    }
    /**
     *
     * @param oritransdate
     * @param orireqserialno
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean realNameAuthQuery(String oritransdate,
            String orireqserialno) throws CMBCTradeException {
        RealNameAuthQueryBean realNameAuthQueryBean = new RealNameAuthQueryBean(oritransdate, orireqserialno);
        String message = realNameAuthQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
            		Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                            Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+REALNAMEAUTHQUERY.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+REALNAMEAUTHQUERY+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param oritransdate
     * @param orireqserialno
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean realTimeWitholdinghQuery(String oritransdate,
            String orireqserialno) throws CMBCTradeException {
        RealTimeWithholdingQueryBean realNameAuthQueryBean = new RealTimeWithholdingQueryBean(oritransdate, orireqserialno);
        String message = realNameAuthQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WITHHOLDINGQUERY.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGQUERY+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param bankaccno
     * @return
     * @throws CMBCTradeException
     */
    @Override
    public ResultBean whiteListCollectionQuery(String bankaccno)
            throws CMBCTradeException {
        WhiteListQueryBean realNameAuthQueryBean = new WhiteListQueryBean(bankaccno);
        String message = realNameAuthQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WHITELISTQUERY.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WHITELISTQUERY+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /*public ResultBean selfWithholding(){
        CMBCRealTimeWithholdingBean realTimePayBean = new CMBCRealTimeWithholdingBean("");
        String sendMsg = realTimePayBean.toXML();
        log.info("selfWithholding send msg:"+sendMsg);
        CMBCWithholdingSocketShortClient client =  new CMBCWithholdingSocketShortClient(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 90000);
         try {
            client.setReceiveProcessor(new CMBCSelfWithholdingReciveProcessor());
            client.sendMessage(sendMsg.getBytes(ENCODING));
        } catch (CMBCTradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
    
    /*public ResultBean selfWithholdingQuery(String ori_tran_date, String ori_tran_id){
        CMBCRealTimeWithholdingQueryBean realTimePayBean = new CMBCRealTimeWithholdingQueryBean(ori_tran_date, ori_tran_id);
        String sendMsg = realTimePayBean.toXML();
        log.info("selfWithholdingQuery send msg:"+sendMsg);
        CMBCWithholdingSocketShortClient client =  new CMBCWithholdingSocketShortClient(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 90000);
         try {
            client.setReceiveProcessor(new CMBCSelfWithholdingReciveProcessor());
            client.sendMessage(sendMsg.getBytes());
        } catch (CMBCTradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
    /*
    public ResultBean selfBillFile(String recDate){
        CMBCBillFileRequestBean realTimePayBean = new CMBCBillFileRequestBean(Constant.getInstance().getCmbc_self_merid(),recDate);
        String sendMsg = realTimePayBean.toXML();
        log.info("selfBillFile send msg:"+sendMsg);
        CMBCWithholdingSocketShortClient client =  new CMBCWithholdingSocketShortClient(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 90000);
         try {
            client.setReceiveProcessor(new CMBCSelfWithholdingReciveProcessor());
            client.sendMessage(sendMsg.getBytes());
        } catch (CMBCTradeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
    @Override
    public ResultBean realTimeWitholdinghQuery(PojoTxnsWithholding withholding)
            throws CMBCTradeException {
        final RealTimeWithholdingQueryBean realNameAuthQueryBean = new RealTimeWithholdingQueryBean(withholding);
        int reqPoolSize = 1;
		// 初始化线程池
		ExecutorService executors = Executors.newFixedThreadPool(reqPoolSize);
		for (int i = 0; i < reqPoolSize; i++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						SocketChannelHelper socketChannelHelper = SocketChannelHelper.getInstance();
						byte[] bytes = socketChannelHelper.getMessageHandler().pack(realNameAuthQueryBean);
						String hostAddress = socketChannelHelper.getMessageConfigService().getString("HOST_ADDRESS", Constant.getInstance().getCmbc_withholding_ip());// 主机名称
						int hostPort = socketChannelHelper.getMessageConfigService().getInt("HOST_PORT", Constant.getInstance().getCmbc_withholding_port());// 主机端口
						NettyClientBootstrap bootstrap = NettyClientBootstrap.getInstance(hostAddress, hostPort);
						bootstrap.sendMessage(bytes);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			});
		}
		executors.shutdown();
        return null;
        
        
        
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*String message = realNameAuthQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WITHHOLDINGQUERY.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGQUERY+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;*/
    }
    
    /**
     * 民生本行实时代扣 
     *
     * @param withholdingMsg
     * @return
     * @throws CMBCTradeException
     */
    /*@Override
    public ResultBean realTimeWitholdingSelf(WithholdingMessageBean withholdingMsg) throws CMBCTradeException {
        RealTimeSelfWithholdingBean selfWithholding = new RealTimeSelfWithholdingBean(withholdingMsg);
        String message = selfWithholding.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_self_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_self_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_self_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_self_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCSelfMerId();
        int totalLenght = merId.length()+WITHHOLDINGSELF.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGSELF+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingSelfLongSocketClient client = WithholdingSelfLongSocketClient.getInstance(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 30000);
            client.setReceiveProcessor(new WithholdingSelfReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
    /**
     * 民生本行代扣结果查询
     * @param oritransdate
     * @param orireqserialno
     * @return
     * @throws CMBCTradeException
     */
    /*public ResultBean realTimeSelfWithholdinghQuery(String oritransdate,
            String orireqserialno) throws CMBCTradeException {
        RealTimeSelfWithholdingQueryBean realTimeSelfWithholdingQueryBean = new RealTimeSelfWithholdingQueryBean(oritransdate, orireqserialno);
        String message = realTimeSelfWithholdingQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_self_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_self_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_self_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_self_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCSelfMerId();
        int totalLenght = merId.length()+WITHHOLDINGQUERYSELF.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGQUERYSELF+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingSelfLongSocketClient client = WithholdingSelfLongSocketClient.getInstance(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 30000);
            client.setReceiveProcessor(new WithholdingSelfReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
    @Override
    /*public ResultBean realTimeSelfWithholdinghResult(TxnsWithholdingModel withholding) throws CMBCTradeException {
        RealTimeSelfWithholdingQueryBean realTimeSelfWithholdingQueryBean = new RealTimeSelfWithholdingQueryBean(withholding);
        String message = realTimeSelfWithholdingQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_self_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_self_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_self_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_self_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCSelfMerId();
        int totalLenght = merId.length()+WITHHOLDINGQUERYSELF.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGQUERYSELF+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingSelfLongSocketClient client = WithholdingSelfLongSocketClient.getInstance(Constant.getInstance().getCmbc_self_withholding_ip(), Constant.getInstance().getCmbc_self_withholding_port(), 30000);
            client.setReceiveProcessor(new WithholdingSelfResultProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/
   
    
    public ResultBean realTimeWitholdinghResult(PojoTxnsWithholding withholding) throws CMBCTradeException {
        RealTimeWithholdingQueryBean realTimeWithholdingQueryBean = new RealTimeWithholdingQueryBean(withholding);
        String message = realTimeWithholdingQueryBean.toXML();
        log.info("send msg :"+message);
        byte[] signMsg = null;
        int signMsg_length = 0;
        String df_signMsg_length = "";
        try {
            signMsg = signMsg(
                    Constant.getInstance()
                            .getCmbc_withholding_private_key(),
                    Constant.getInstance()
                            .getCmbc_withholding_public_key(), message);
            signMsg_length = signMsg.length;
            DecimalFormat df = new DecimalFormat("0000");
            df_signMsg_length = df.format(signMsg_length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] cryptedBytes = null;
        int cryptedMsg_length = 0;
        try {
            cryptedBytes = encryptMsg(
                    Constant.getInstance()
                    .getCmbc_withholding_private_key(),
            Constant.getInstance()
                    .getCmbc_withholding_public_key(), message);
            cryptedMsg_length = cryptedBytes.length;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String merId = getCMBCMerId();
        int totalLenght = merId.length()+WITHHOLDINGQUERY.length()+df_signMsg_length.length()+signMsg_length+cryptedMsg_length;
        DecimalFormat df = new DecimalFormat("00000000");
        String df_totalLenght =df.format(totalLenght);

        String headMsg = df_totalLenght+merId+WITHHOLDINGQUERY+df_signMsg_length;
        log.info("head msg :"+headMsg);
        try {
            byte[] sendBytes =  byteMerger(byteMerger(headMsg.getBytes(ENCODING),signMsg),cryptedBytes);
            WithholdingLongSocketClient client = WithholdingLongSocketClient.getInstance(Constant.getInstance().getCmbc_withholding_ip(), Constant.getInstance().getCmbc_withholding_port(), 30000);
            client.setReceiveProcessor(new CMBCWithholdingResultReciveProcessor());
            client.sendMessage(sendBytes);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
