/* 
 * CMBCRealNameAuthServiceImpl.java  
 * 
 * version TODO
 *
 * 2016年10月12日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service.impl;

import io.netty.util.internal.StringUtil;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.dao.RealnameAuthDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.sequence.service.SerialNumberService;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
import com.zlebank.zplatform.cmbc.withholding.bean.CardMessageBean;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCRealNameAuthService;
import com.zlebank.zplatform.cmbc.withholding.service.CMBCWhiteListService;
import com.zlebank.zplatform.cmbc.withholding.service.WithholdingService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月12日 下午3:48:59
 * @since 
 */
@Service
public class CMBCRealNameAuthServiceImpl implements CMBCRealNameAuthService{

	private static final Logger log = LoggerFactory.getLogger(CMBCRealNameAuthServiceImpl.class);
	@Autowired
	private RealnameAuthDAO realnameAuthDAO;
	@Autowired 
	private TxnsWithholdingService txnsWithholdingService;
	@Autowired
	private WithholdingService withholdingService;
	@Autowired
	private CMBCWhiteListService whiteListService;
	@Autowired
	private SerialNumberService serialNumberService;
	/**
	 *
	 * @param realnameAuth
	 * @return
	 * @throws CMBCTradeException
	 */
	@Override
	public ResultBean realNameAuth(PojoRealnameAuth realnameAuth)
			throws CMBCTradeException {
		ResultBean resultBean = null;
        PojoTxnsWithholding withholding = null;
        try {
            CardMessageBean card = new CardMessageBean(realnameAuth);
            PojoRealnameAuth realNameAuth = realnameAuthDAO.getByCardInfo(realnameAuth);
            if(realNameAuth!=null){
                if("00".equals(realNameAuth.getStatus())){//实名认证已经完成，加入绑卡信息中
                    return new ResultBean("RN00");
                }
            }
            withholding = new PojoTxnsWithholding(realnameAuth);
            withholding.setSerialno(serialNumberService.generateCMBCSerialNo());
            //保存实名认证流水
            txnsWithholdingService.saveWithholdingLog(withholding);
            card.setWithholding(withholding);
            
            //民生渠道跨行实名认证
            withholdingService.realNameAuthentication(card);
            //查询实名认证结果
            resultBean = queryResult(withholding.getSerialno());
            if(resultBean.isResultBool()){
            	whiteListService.whiteListCollection(realnameAuth.getCardNo(), realnameAuth.getCustomerNm(), realnameAuth.getCertifId(), realnameAuth.getPhoneNo()+"", realnameAuth.getCertifTp());
            }
            log.info("realNameAuth result:"+JSON.toJSONString(resultBean));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            resultBean = new ResultBean("", "实名认证失败");
            System.out.println(e.getMessage());
            withholding.setExecmsg(e.getMessage());
            txnsWithholdingService.updateWithholdingLogError(withholding);
        }
        return resultBean;
	}

	/**
     * 查询民生银行跨行代扣交易结果
     * @param serialno
     * @return
     */
    private ResultBean queryResult(String serialno) {
        PojoTxnsWithholding withholding = null;
        ResultBean resultBean = null;
        int[] timeArray = new int[]{1000, 2000, 8000, 16000, 32000};
        try {
            for (int i = 0; i < 5; i++) {
                withholding = txnsWithholdingService.getWithholdingBySerialNo(serialno);
                if(!StringUtils.isEmpty(withholding.getExectype())){
                	
                    if("S".equalsIgnoreCase(withholding.getExectype())){
                    	if("00".equals(withholding.getValidatestatus())){
                    		 resultBean = new ResultBean("success");
                    	}else{
                    		resultBean = new ResultBean("0099","实名认证失败");
                    	}
                        break;
                    }else if("E".equalsIgnoreCase(withholding.getExectype())){
                        resultBean = new ResultBean(withholding.getExeccode(),withholding.getExecmsg());
                        break;
                    }
                }
                Thread.sleep(timeArray[i]);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return resultBean;
    }

	/**
	 *
	 * @param tradeBean
	 * @return
	 * @throws CMBCTradeException
	 */
	@Override
	public ResultBean realNameAuth(TradeBean tradeBean)
			throws CMBCTradeException {
		PojoRealnameAuth realnameAuth = new PojoRealnameAuth(tradeBean);
		return realNameAuth(realnameAuth);
	}
    
    

}
