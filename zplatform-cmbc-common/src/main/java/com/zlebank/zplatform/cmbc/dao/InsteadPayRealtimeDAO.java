package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.pojo.PojoInsteadPayRealtime;

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
	
	/**
	 * 更新代付订单为退汇状态
	 * @param txnseqno 交易序列号
	 * @param retCode 应答码
	 * @param retMsg 应答信息
	 */
	public void updateInsteadReexchange(String txnseqno, String retCode, String retMsg);
}
