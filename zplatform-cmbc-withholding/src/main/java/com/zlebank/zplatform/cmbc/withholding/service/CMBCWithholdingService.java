package com.zlebank.zplatform.cmbc.withholding.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;

/**
 * 民生银行跨行代扣
 * 
 *
 * @author guojia
 * @version
 * @date 2015年12月16日 下午1:57:21
 * @since
 */
public interface CMBCWithholdingService {

    /**
     * 跨行代扣
     * @param trade
     * @return
     */
    public ResultBean crossLineWithhold(TradeBean trade);
    
    /**
     * 查询交易结果通过渠道流水
     * @param serialno 渠道流水
     * @return
     */
    public ResultBean queryResult(String serialno);
    
    /**
     * 查询民生跨行代扣交易结果
     * @param txnseqno
     * @return
     */
    public ResultBean queryCrossLineTrade(String txnseqno);
    
    /**
     * 本行代扣
     * @param trade
     * @return
     */
    public ResultBean innerLineWithhold(TradeBean trade);
    
}
