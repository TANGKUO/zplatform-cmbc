/* 
 * CMBCRealNameAuthService.java  
 * 
 * version TODO
 *
 * 2016年10月12日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.common.exception.CMBCTradeException;
import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月12日 下午3:35:05
 * @since 
 */
public interface CMBCRealNameAuthService {

	/**
     * 实名认证
     * @param  realnameAuth 实名认证信息表实体类
     * @return
     * @throws TradeException 
     * @throws CMBCTradeException 
     */
    public ResultBean realNameAuth(PojoRealnameAuth realnameAuth) throws CMBCTradeException;
    
    /**
     * 实名认证
     * @param tradeBean
     * @return
     */
    public ResultBean realNameAuth(TradeBean tradeBean) throws CMBCTradeException;
}
