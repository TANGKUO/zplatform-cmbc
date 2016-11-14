package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealNameResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCWithholdingQueryResultBean;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;

public interface TxnsWithholdingDAO extends BaseDAO<PojoTxnsWithholding>{

	/**
	 * 根据流水号获取代扣交易流水数据
	 * @param serialno 流水号（民生报文中）
	 * @return 民生代扣交易流水数据pojo
	 */
	public PojoTxnsWithholding getWithholdingBySerialNo(String serialno);
	
	/**
	 * 更新民生实名认证交易结果
	 * @param withholding 实名认证POJO
	 */
	public void updateRealNameResult(PojoTxnsWithholding withholding);
	
	/**
	 * 更新民生代付交易流水日志错误应答信息
	 * @param withholding
	 */
	public void updateWithholdingLogError(PojoTxnsWithholding withholding);
	
	/**
	 * 更新民生代付交易结果
	 * @param realTimeWithholdingResultBean 实时代扣结果 
	 */
	public void updateWithholdingResult(CMBCRealTimeWithholdingResultBean realTimeWithholdingResultBean);
	
	/**
	 * 更新民生实名认证交易结果
	 * @param realNameResultBean
	 */
	public void updateRealNameResult(CMBCRealNameResultBean realNameResultBean);
	
	/**
	 * 更新民生代扣交易查询结果
	 * @param withholdingQueryResultBean
	 */
	public void updateWithholdingQueryResult(CMBCWithholdingQueryResultBean withholdingQueryResultBean);
}
