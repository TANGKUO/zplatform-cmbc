/* 
 * SingleReexchangeBean.java  
 * 
 * version TODO
 *
 * 2016年10月25日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.common.bean;

import java.io.Serializable;

/**
 * 单笔退汇bean
 *
 * @author guojia
 * @version
 * @date 2016年10月25日 下午4:30:48
 * @since 
 */
public class SingleReexchangeBean implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -488474326690482684L;
	//交易服务码|合作方流水号|银行处理流水号|收款人账户号|收款人账户名|交易金额(单位为分)|应答码类型|应答码|应答描述|银行对账日期|退汇日期
	private String busiCode;
	private String tranId;
	private String bankTranId;
	private String accNo;
	private String accName;
	private String transAmt;
	private String respType;
	private String respCode;
	private String respMsg;
	private String bankBillDate;
	private String reexchangeDate;
	
	
	
	/**
	 * @param busiCode
	 * @param tranId
	 * @param bankTranId
	 * @param accNo
	 * @param accName
	 * @param transAmt
	 * @param respType
	 * @param respCode
	 * @param respMsg
	 * @param bankBillDate
	 * @param reexchangeDate
	 */
	public SingleReexchangeBean(String[] body) {
		super();
		this.busiCode = body[0];
		this.tranId = body[1];
		this.bankTranId = body[2];
		this.accNo = body[3];
		this.accName = body[4];
		this.transAmt = body[5];
		this.respType = body[6];
		this.respCode = body[7];
		this.respMsg = body[8];
		this.bankBillDate = body[9];
		this.reexchangeDate = body[10];
	}
	
	
	/**
	 * 
	 */
	public SingleReexchangeBean() {
		super();
	}


	/**
	 * @return the busiCode
	 */
	public String getBusiCode() {
		return busiCode;
	}
	/**
	 * @param busiCode the busiCode to set
	 */
	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}
	/**
	 * @return the tranId
	 */
	public String getTranId() {
		return tranId;
	}
	/**
	 * @param tranId the tranId to set
	 */
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}
	/**
	 * @return the bankTranId
	 */
	public String getBankTranId() {
		return bankTranId;
	}
	/**
	 * @param bankTranId the bankTranId to set
	 */
	public void setBankTranId(String bankTranId) {
		this.bankTranId = bankTranId;
	}
	/**
	 * @return the accNo
	 */
	public String getAccNo() {
		return accNo;
	}
	/**
	 * @param accNo the accNo to set
	 */
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	/**
	 * @return the accName
	 */
	public String getAccName() {
		return accName;
	}
	/**
	 * @param accName the accName to set
	 */
	public void setAccName(String accName) {
		this.accName = accName;
	}
	/**
	 * @return the transAmt
	 */
	public String getTransAmt() {
		return transAmt;
	}
	/**
	 * @param transAmt the transAmt to set
	 */
	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}
	/**
	 * @return the respType
	 */
	public String getRespType() {
		return respType;
	}
	/**
	 * @param respType the respType to set
	 */
	public void setRespType(String respType) {
		this.respType = respType;
	}
	/**
	 * @return the respCode
	 */
	public String getRespCode() {
		return respCode;
	}
	/**
	 * @param respCode the respCode to set
	 */
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	/**
	 * @return the respMsg
	 */
	public String getRespMsg() {
		return respMsg;
	}
	/**
	 * @param respMsg the respMsg to set
	 */
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	/**
	 * @return the bankBillDate
	 */
	public String getBankBillDate() {
		return bankBillDate;
	}
	/**
	 * @param bankBillDate the bankBillDate to set
	 */
	public void setBankBillDate(String bankBillDate) {
		this.bankBillDate = bankBillDate;
	}
	/**
	 * @return the reexchangeDate
	 */
	public String getReexchangeDate() {
		return reexchangeDate;
	}
	/**
	 * @param reexchangeDate the reexchangeDate to set
	 */
	public void setReexchangeDate(String reexchangeDate) {
		this.reexchangeDate = reexchangeDate;
	}
	
	
	
}
