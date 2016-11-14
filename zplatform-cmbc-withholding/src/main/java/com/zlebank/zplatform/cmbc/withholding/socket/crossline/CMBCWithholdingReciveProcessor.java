/* 
 * RealNameAuthProcessor.java  
 * 
 * version TODO
 *
 * 2015年11月23日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.socket.crossline;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.enums.CMBCCardTypeEnum;
import com.zlebank.zplatform.cmbc.common.enums.CertifTypeEnmu;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.common.utils.SpringContext;
import com.zlebank.zplatform.cmbc.common.utils.UUIDUtil;
import com.zlebank.zplatform.cmbc.dao.RealnameAuthDAO;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsOrderinfoDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsLog;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWhiteList;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.security.RSAHelper;
import com.zlebank.zplatform.cmbc.service.TxnsWhiteListService;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.net.ReceiveProcessor;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealNameAuthQueryResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealNameAuthResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealTimeWithholdingQueryResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.RealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.WhiteListQueryResultBean;
import com.zlebank.zplatform.cmbc.withholding.response.bean.WhiteListResultBean;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCCrossLineQuickPayService;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCWhiteListService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年11月23日 下午5:31:36
 * @since 
 */
public class CMBCWithholdingReciveProcessor implements ReceiveProcessor{
    private static final String ENCODING = "UTF-8";
    private static final Logger log = LoggerFactory.getLogger(CMBCWithholdingReciveProcessor.class);
    private enum MessageType{
        /**
         *  1003    实时代扣请求
            3003    实时跨行代扣结果查询
            1004    实名身份认证
            3004    实名身份认证结果查询
            1007    白名单采集
            3007    白名单查询
         */
        REQUEST_WITHHOLDING ("1003"),
        RESULT_WITHHOLDING ("3003"),
        REQUEST_REALNAME ("1004"),
        RESULT_REALNAME ("3004"),
        REQUEST_WHITELIST ("1007"),
        RESULT_WHITELIST ("3007"),
        UNKNOW("");
        
        private String code;
        private MessageType(String code) {
            this.code = code;
        }
        public static MessageType fromValue(String value){
            for(MessageType messageType:values()){
                if(messageType.code.equals(value)){
                    return messageType;
                }
            }
            return UNKNOW;
        }
    }
    private TxnsWithholdingService txnsWithholdingService = (TxnsWithholdingService) SpringContext.getContext().getBean("txnsWithholdingService");
    private RealnameAuthDAO realnameAuthDAO = (RealnameAuthDAO) SpringContext.getContext().getBean("realnameAuthDAO");
    private TxnsWhiteListService txnsWhiteListService = (TxnsWhiteListService) SpringContext.getContext().getBean("txnsWhiteListService");
    //private ICMBCTransferService cmbcTransferService;
    //private ITradeReceiveProcessor tradeReceiveProcessor = (ITradeReceiveProcessor) SpringContext.getContext().getBean("txnsWithholdingService");
    private TxnsOrderinfoDAO txnsOrderinfoDAO= (TxnsOrderinfoDAO) SpringContext.getContext().getBean("txnsOrderinfoDAO");
    private TxnsLogDAO txnsLogDAO= (TxnsLogDAO) SpringContext.getContext().getBean("txnsLogDAO");
    private RspmsgDAO rspmsgDAO= (RspmsgDAO) SpringContext.getContext().getBean("rspmsgDAO");
    private CMBCWhiteListService cmbcWhiteListService= (CMBCWhiteListService) SpringContext.getContext().getBean("cmbcWhiteListService");
    private CMBCCrossLineQuickPayService cmbcCrossLineQuickPayService = (CMBCCrossLineQuickPayService) SpringContext.getContext().getBean("cmbcCrossLineQuickPayService");;
    /** 
     *
     * @param data
     */
    @Override
    @Transactional
    public void onReceive(Object data) {
        // 8位合作方编号  8位交易码   4位签名域长度 签名域值    XML报文数据主体密文
        byte[] rawData = (byte[]) data;
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(rawData));
        byte[] merId = new byte[8];
        byte[] serviceCode=new byte[8];
        byte[] sign_length = new byte[4];
        try {
            inputStream.read(merId);
            inputStream.read(serviceCode);
            inputStream.read(sign_length);
            int length = Integer.valueOf(new String(sign_length,ENCODING));
            byte[] sign = new byte[length];
            inputStream.read(sign);
            int msg_length = rawData.length-8-8-4-length;
            byte[] cryptedBytes = new byte[msg_length];
            inputStream.read(cryptedBytes);
            
            MessageType msgType = MessageType.fromValue(new String(serviceCode,ENCODING).trim());
            //解密
            byte[] decryptedBytes = decryptMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), cryptedBytes);
            String msg = new String(decryptedBytes, ENCODING);
            XStream xstream = new XStream(new DomDriver(null,new XmlFriendlyNameCoder("_-", "_")));
            log.info("recive xml:"+msg);
            boolean verifyFlag=false;
            switch (msgType) {
                case REQUEST_REALNAME :
                    xstream.processAnnotations(RealNameAuthResultBean.class);
                    xstream.autodetectAnnotations(true);
                    RealNameAuthResultBean realNameAuthResultBean =  (RealNameAuthResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(realNameAuthResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result:"+verifyFlag);
                    if(verifyFlag){
                        //更新交易流水
                        PojoTxnsWithholding withholding = txnsWithholdingService.getWithholdingBySerialNo(realNameAuthResultBean.getReqserialno());
                        if(withholding!=null){
                            withholding.setExectype(realNameAuthResultBean.getExectype());
                            withholding.setExeccode(realNameAuthResultBean.getExeccode());
                            withholding.setExecmsg(realNameAuthResultBean.getExecmsg());
                            withholding.setValidatestatus(realNameAuthResultBean.getValidatestatus());
                            withholding.setSerialno(realNameAuthResultBean.getReqserialno());
                            withholding.setPayserialno(realNameAuthResultBean.getPayserialno());
                            txnsWithholdingService.updateRealNameResult(withholding);
                            if(realNameAuthResultBean.getExectype().equalsIgnoreCase("S")){
                                PojoRealnameAuth realnameAuth = new PojoRealnameAuth();
                                realnameAuth.setCardType(CMBCCardTypeEnum.fromValue(withholding.getCardtype()).getCardType());
                                realnameAuth.setCardNo(withholding.getAccno());
                                realnameAuth.setCertifTp(CertifTypeEnmu.fromCmbcCode(withholding.getCerttype()).getCode());
                                realnameAuth.setCertifId(withholding.getCertno());
                                realnameAuth.setPhoneNo(Long.valueOf(withholding.getPhone()));
                                realnameAuth.setCustomerNm(withholding.getAccname());
                                //判断实名认证数据是否已经记录
                                
                                if(realNameAuthResultBean.getValidatestatus().equals("00")){
                                    realnameAuth.setStatus("00");
                                    realnameAuthDAO.saveRealNameAuth(realnameAuth);
                                }else{
                                    realnameAuth.setStatus("01");
                                }
                                
                                if(realNameAuthResultBean.getValidatestatus().equals("00")){
                                    //白名单采集
                                    cmbcWhiteListService.whiteListCollection(withholding.getAccno(), withholding.getAccname(), withholding.getCertno(), withholding.getPhone(),withholding.getCerttype());
                               
                                }
                            }
                        }
                    }
                    break;
                case REQUEST_WITHHOLDING:
                    xstream.processAnnotations(RealTimeWithholdingResultBean.class);
                    xstream.autodetectAnnotations(true);
                    RealTimeWithholdingResultBean realTimeWithholdingResultBean =  (RealTimeWithholdingResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(realTimeWithholdingResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result"+verifyFlag);
                   
                    if(verifyFlag){
                        //更新交易流水
                        PojoTxnsWithholding withholding = txnsWithholdingService.getWithholdingBySerialNo(realTimeWithholdingResultBean.getReqserialno());
                        if(withholding!=null){
                            withholding.setExectype(realTimeWithholdingResultBean.getExectype());
                            withholding.setExeccode(realTimeWithholdingResultBean.getExeccode());
                            withholding.setExecmsg(realTimeWithholdingResultBean.getExecmsg());
                            withholding.setSerialno(realTimeWithholdingResultBean.getReqserialno());
                            withholding.setPayserialno(realTimeWithholdingResultBean.getPayserialno());
                            withholding.setSettdate(realTimeWithholdingResultBean.getSettdate());
                            withholding.setBanktrandate(realTimeWithholdingResultBean.getSettdate());
                            withholding.setBanktrantime(realTimeWithholdingResultBean.getTranstime());
                            txnsWithholdingService.updateWhithholding(withholding);
                            
                            if(withholding.getExectype().equals("S")){
                                ResultBean resultBean = new ResultBean(withholding);
                                TradeBean tradeBean = new TradeBean();
                                tradeBean.setTxnseqno(withholding.getTxnseqno());
                                //cmbcCrossLineQuickPayService.dealWithAccounting(tradeBean.getTxnseqno(), resultBean);
                            } else if(withholding.getExectype().equals("E")){
                                saveFailedCMBCTrade(withholding);
                            }else{
                                String ori_tran_date = withholding.getTransdate();
                                String ori_tran_id = withholding.getSerialno();
                                String txnseqno = withholding.getTxnseqno();
                                //CMBCQueryTradeThreadPool.getInstance().executeMission(new CMBCQueryTradeThread(ori_tran_date, ori_tran_id, txnseqno));
                            }
                        }
                        
                    }
                    break;
                case REQUEST_WHITELIST:
                    xstream.processAnnotations(WhiteListResultBean.class);
                    xstream.autodetectAnnotations(true);
                    WhiteListResultBean whiteListResultBean =  (WhiteListResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(whiteListResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result"+verifyFlag);
                    if(verifyFlag){
                        //更新交易流水
                        PojoTxnsWithholding withholding = txnsWithholdingService.getWithholdingBySerialNo(whiteListResultBean.getReqserialno());
                        withholding.setExectype(whiteListResultBean.getExectype());
                        withholding.setExeccode(whiteListResultBean.getExeccode());
                        withholding.setExecmsg(whiteListResultBean.getExecmsg());
                        withholding.setSerialno(whiteListResultBean.getReqserialno());
                        txnsWithholdingService.updateWhithholding(withholding);
                        //保存白名单信息
                        PojoTxnsWhiteList whiteList = new PojoTxnsWhiteList(withholding);
                        txnsWhiteListService.saveWhiteList(whiteList);
                    }
                    break;
                case RESULT_REALNAME:
                    xstream.processAnnotations(RealNameAuthQueryResultBean.class);
                    xstream.autodetectAnnotations(true);
                    RealNameAuthQueryResultBean realNameAuthQueryResultBean =  (RealNameAuthQueryResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(realNameAuthQueryResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result"+verifyFlag);
                    if(verifyFlag){
                        
                    }
                    break;
                case RESULT_WITHHOLDING:
                    xstream.processAnnotations(RealTimeWithholdingQueryResultBean.class);
                    xstream.autodetectAnnotations(true);
                    RealTimeWithholdingQueryResultBean timeWithholdingQueryResultBean =  (RealTimeWithholdingQueryResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(timeWithholdingQueryResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result"+verifyFlag);
                    if(verifyFlag){
                        //更新交易流水
                        PojoTxnsWithholding withholding = txnsWithholdingService.getWithholdingBySerialNo(timeWithholdingQueryResultBean.getReqserialno());
                        withholding.setExectype(timeWithholdingQueryResultBean.getExectype());
                        withholding.setExeccode(timeWithholdingQueryResultBean.getExeccode());
                        withholding.setExecmsg(timeWithholdingQueryResultBean.getExecmsg());
                        withholding.setSerialno(timeWithholdingQueryResultBean.getReqserialno());
                        withholding.setOriexectype(timeWithholdingQueryResultBean.getOriexectype());
                        withholding.setOriexeccode(timeWithholdingQueryResultBean.getOriexeccode());
                        withholding.setOriexecmsg(timeWithholdingQueryResultBean.getOriexecmsg());
                        txnsWithholdingService.updateWhithholding(withholding);
                        
                    }
                    break;
                case RESULT_WHITELIST :
                    xstream.processAnnotations(WhiteListQueryResultBean.class);
                    xstream.autodetectAnnotations(true);
                    WhiteListQueryResultBean whiteListQueryResultBean =  (WhiteListQueryResultBean) xstream.fromXML(msg);
                    log.info("receive msg:"+JSON.toJSONString(whiteListQueryResultBean));
                    verifyFlag = verifyMsg(Constant.getInstance().getCmbc_withholding_private_key(), Constant.getInstance().getCmbc_withholding_public_key(), msg, sign);
                    log.info("verify result"+verifyFlag);
                    if(verifyFlag){
                        
                    }
                    break;
			default:
				break;
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
   
    private boolean verifyMsg(String priKey, String pubKey, String message,byte[] signBytes) throws Exception {
        RSAHelper cipher = new RSAHelper();
        cipher.initKey(priKey, pubKey, 2048);
        boolean isValid = cipher.verifyRSA(message.getBytes("UTF-8"),signBytes, false, "UTF-8");
        return isValid;
    }
    private byte[] decryptMsg(String priKey, String pubKey, byte[] cryptedBytes) throws Exception{
        RSAHelper cipher = new RSAHelper();
        cipher.initKey(priKey, pubKey, 2048);
        byte[] decryptedBytes = cipher.decryptRSA(cryptedBytes, false, "UTF-8");
        return decryptedBytes;
    }


    public CMBCWithholdingReciveProcessor() {
        
    }
    
    
    public void saveFailedCMBCTrade(PojoTxnsWithholding withholding){
        PojoTxnsLog txnsLog = txnsLogDAO.getTxnsLogByTxnseqno(withholding.getTxnseqno());
        txnsLog.setPayordfintime(DateUtil.getCurrentDateTime());
        PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, withholding.getExeccode());
        if(msg!=null){
            txnsLog.setRetcode(msg.getWebrspcode());
            txnsLog.setRetinfo(msg.getRspinfo());
        }else{
            txnsLog.setRetcode("0052");
            txnsLog.setRetinfo("交易失败，系统忙，请稍后再试！");
        }
        txnsLog.setRetdatetime(DateUtil.getCurrentDateTime());
        txnsLog.setTradestatflag("00000001");
        txnsLog.setTradetxnflag("10000000");
        txnsLog.setRelate("10000000");
        txnsLog.setTradeseltxn(UUIDUtil.uuid());
        txnsLog.setPayrettsnseqno(withholding.getPayserialno());
        txnsLog.setPayretcode(withholding.getExeccode());
        txnsLog.setPayretinfo(withholding.getExecmsg());
        txnsLogDAO.updateTxnsLog(txnsLog);
        //订单状态更新为失败
        txnsOrderinfoDAO.updateOrderToFail(txnsLog.getTxnseqno());
        
    }
    

}
