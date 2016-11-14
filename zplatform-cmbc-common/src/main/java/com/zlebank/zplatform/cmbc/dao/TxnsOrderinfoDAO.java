/* 
 * ITxnsOrderinfoDAO.java  
 * 
 * version TODO
 *
 * 2015年8月29日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.pojo.PojoTxnsOrderinfo;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年8月29日 下午3:39:25
 * @since 
 */
public interface TxnsOrderinfoDAO extends BaseDAO<PojoTxnsOrderinfo>{


	/**
	 * 更新订单状态为失败
	 * @param txnseqno 交易序列号
	 */
    public void updateOrderToFail(String txnseqno);
    /**
     * 更新订单状态为成功
     * @param txnseqno 交易序列号
     */
    public void updateOrderToSuccess(String txnseqno) ;
    /**
     * 更新订单状态为成功
     * @param tn 受理订单号
     */
    public void updateOrderToSuccessByTN(String tn) ;
}
