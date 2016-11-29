/* 
 * WithholdingSelfTradeBean.java  
 * 
 * version TODO
 *
 * 2016年11月28日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.self.bean;

import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.framework.trade.bean.BankCardBean;
import com.zlebank.zplatform.framework.trade.bean.SimpleTradeBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月28日 上午10:11:26
 * @since 
 */
public class WithholdingSelfTradeBean extends SimpleTradeBean {
	
	/**
	 * 扣款的银行卡
	 */
	private BankCardBean outBankCard;
	/**
	 * 代扣消息bean
	 */
	private WithholdingMessageBean withholdingMessageBean;
	/**
	 * 本行代扣查询结果pojo
	 */
	private PojoTxnsWithholding txnsWithholding;
	
	/**
	 * @return the outBankCard
	 */
	public BankCardBean getOutBankCard() {
		return outBankCard;
	}
	/**
	 * @param outBankCard the outBankCard to set
	 */
	public void setOutBankCard(BankCardBean outBankCard) {
		this.outBankCard = outBankCard;
	}
	/**
	 * @return the withholdingMessageBean
	 */
	public WithholdingMessageBean getWithholdingMessageBean() {
		return withholdingMessageBean;
	}
	/**
	 * @param withholdingMessageBean the withholdingMessageBean to set
	 */
	public void setWithholdingMessageBean(
			WithholdingMessageBean withholdingMessageBean) {
		this.withholdingMessageBean = withholdingMessageBean;
	}
	/**
	 * @return the txnsWithholding
	 */
	public PojoTxnsWithholding getTxnsWithholding() {
		return txnsWithholding;
	}
	/**
	 * @param txnsWithholding the txnsWithholding to set
	 */
	public void setTxnsWithholding(PojoTxnsWithholding txnsWithholding) {
		this.txnsWithholding = txnsWithholding;
	}
	
}
