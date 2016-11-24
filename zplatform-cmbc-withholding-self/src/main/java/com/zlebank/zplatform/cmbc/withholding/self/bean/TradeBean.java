/* 
 * TradeBean.java  
 * 
 * version TODO
 *
 * 2015年8月27日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.self.bean;

import java.io.Serializable;

import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年8月27日 下午8:25:07
 * @since 
 */
public class TradeBean implements Serializable{
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7990669165684148748L;
    /**银行代码**/
    private String bankCode;
    /**商户订单号**/
    private String orderId;
    /**交易金额**/
    private String amount;
    /**银行卡号**/
    private String cardNo;
    /**账户名称**/    
    private String acctName;
    /**证件号**/
    private String certId;
    /**手机号**/
    private String mobile;
    /**证件类型**/
    private String certType;
    /**交易序列号**/
    private String txnseqno;
    /**商户号**/
    private String merchId;
    /**cvv2**/
    private String cvv2;
    /**信用卡有效期**/
    private String validthru;
    /**交易tn号**/
    private String tn;
    /**银行卡**/
    private String cardType;
    
    private WithholdingMessageBean withholdingMessageBean;
    
    private PojoTxnsWithholding withholding;
	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}
	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	/**
	 * @return the cardNo
	 */
	public String getCardNo() {
		return cardNo;
	}
	/**
	 * @param cardNo the cardNo to set
	 */
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	/**
	 * @return the acctName
	 */
	public String getAcctName() {
		return acctName;
	}
	/**
	 * @param acctName the acctName to set
	 */
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	/**
	 * @return the certId
	 */
	public String getCertId() {
		return certId;
	}
	/**
	 * @param certId the certId to set
	 */
	public void setCertId(String certId) {
		this.certId = certId;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the certType
	 */
	public String getCertType() {
		return certType;
	}
	/**
	 * @param certType the certType to set
	 */
	public void setCertType(String certType) {
		this.certType = certType;
	}
	/**
	 * @return the txnseqno
	 */
	public String getTxnseqno() {
		return txnseqno;
	}
	/**
	 * @param txnseqno the txnseqno to set
	 */
	public void setTxnseqno(String txnseqno) {
		this.txnseqno = txnseqno;
	}
	/**
	 * @return the merchId
	 */
	public String getMerchId() {
		return merchId;
	}
	/**
	 * @param merchId the merchId to set
	 */
	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}
	/**
	 * @return the cvv2
	 */
	public String getCvv2() {
		return cvv2;
	}
	/**
	 * @param cvv2 the cvv2 to set
	 */
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	/**
	 * @return the validthru
	 */
	public String getValidthru() {
		return validthru;
	}
	/**
	 * @param validthru the validthru to set
	 */
	public void setValidthru(String validthru) {
		this.validthru = validthru;
	}
	/**
	 * @return the tn
	 */
	public String getTn() {
		return tn;
	}
	/**
	 * @param tn the tn to set
	 */
	public void setTn(String tn) {
		this.tn = tn;
	}
	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}
	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
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
	 * @return the withholding
	 */
	public PojoTxnsWithholding getWithholding() {
		return withholding;
	}
	/**
	 * @param withholding the withholding to set
	 */
	public void setWithholding(PojoTxnsWithholding withholding) {
		this.withholding = withholding;
	}
    
    
    
}
