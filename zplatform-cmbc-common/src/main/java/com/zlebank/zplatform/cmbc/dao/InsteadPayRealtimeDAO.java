package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.common.pojo.PojoInsteadPayRealtime;

/**
 * 
 * 实时代付订单DAO接口
 *
 * @author guojia
 * @version
 * @date 2016年10月17日 下午2:37:10
 * @since
 */
public interface InsteadPayRealtimeDAO extends BaseDAO<PojoInsteadPayRealtime>  {

	
	
	/***
	 * 代付成功
	 * @param txnseqno
	 */
	public void updateInsteadSuccess(String txnseqno);
	/****
	 * 代付失败
	 * @param txnseqno
	 * @param retCode
	 * @param retMsg
	 */
	public void updateInsteadFail(String txnseqno, String retCode, String retMsg);
	
	
}
