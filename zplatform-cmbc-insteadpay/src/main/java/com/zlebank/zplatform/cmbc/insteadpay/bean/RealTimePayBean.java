/* 
 * RealTimePayBean.java  
 * 
 * version TODO
 *
 * 2015年11月5日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.insteadpay.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.zlebank.zplatform.cmbc.common.bean.InsteadPayTradeBean;
import com.zlebank.zplatform.cmbc.common.utils.Constant;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年11月5日 下午1:45:41
 * @since
 */
@XStreamAlias("TRAN_REQ")
public class RealTimePayBean {
	public static final String XMLHEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	/**
	 *  商户编号
	 */
	@XStreamAlias("MCHNT_CD")
	private String mchntCD;
	/**
	 *  交易日期
	 */
	@XStreamAlias("TRAN_DATE")
	private String tranDate;
	/**
	 *  交易时间
	 */
	@XStreamAlias("TRAN_TIME")
	private String tranTime;
	/**
	 *  合作方流水号
	 */
	@XStreamAlias("TRAN_ID")
	private String tranId;
	/**
	 *  交易币种
	 */
	@XStreamAlias("CURRENCY")
	private String currency;
	/**
	 *  付款人账户号
	 */
	@XStreamAlias("PAY_ACCT_NO")
	private String payAcctNo;
	/**
	 *  付款人账户名
	 */
	@XStreamAlias("PAY_ACC_NAME")
	private String payAccName;
	/**
	 *  付款人账户联行号
	 */
	@XStreamAlias("PAY_BANK_TYPE")
	private String payBankType;
	/**
	 *  付款人账户开户行名称
	 */
	@XStreamAlias("PAY_BANK_NAME")
	private String payBankName;
	/**
	 *  收款人账户号
	 */
	@XStreamAlias("ACC_NO")
	private String accNo;
	/**
	 *  收款人账户名
	 */
	@XStreamAlias("ACC_NAME")
	private String accName;
	/**
	 *  收款人账户联行号
	 */
	@XStreamAlias("BANK_TYPE")
	private String bankType;
	/**
	 *  收款人账户开户行名称
	 */
	@XStreamAlias("BANK_NAME")
	private String bankName;
	/**
	 *  交易金额
	 */
	@XStreamAlias("TRANS_AMT")
	private String transAmt;
	/**
	 *  摘要代码
	 */
	@XStreamAlias("REMARK_CD")
	private String remarkCD;
	/**
	 *  客户流水摘要
	 */
	@XStreamAlias("REMARK")
	private String remark;
	/**
	 *  合作方对账日期
	 */
	@XStreamAlias("COMPANY_DATE")
	private String companyDate;
	/**
	 *  备用域
	 */
	@XStreamAlias("RESV")
	private String resv;

	
	
	/**
	 * @return the mchntCD
	 */
	public String getMchntCD() {
		return mchntCD;
	}



	/**
	 * @param mchntCD the mchntCD to set
	 */
	public void setMchntCD(String mchntCD) {
		this.mchntCD = mchntCD;
	}



	/**
	 * @return the tranDate
	 */
	public String getTranDate() {
		return tranDate;
	}



	/**
	 * @param tranDate the tranDate to set
	 */
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}



	/**
	 * @return the tranTime
	 */
	public String getTranTime() {
		return tranTime;
	}



	/**
	 * @param tranTime the tranTime to set
	 */
	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
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
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}



	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}



	/**
	 * @return the payAcctNo
	 */
	public String getPayAcctNo() {
		return payAcctNo;
	}



	/**
	 * @param payAcctNo the payAcctNo to set
	 */
	public void setPayAcctNo(String payAcctNo) {
		this.payAcctNo = payAcctNo;
	}



	/**
	 * @return the payAccName
	 */
	public String getPayAccName() {
		return payAccName;
	}



	/**
	 * @param payAccName the payAccName to set
	 */
	public void setPayAccName(String payAccName) {
		this.payAccName = payAccName;
	}



	/**
	 * @return the payBankType
	 */
	public String getPayBankType() {
		return payBankType;
	}



	/**
	 * @param payBankType the payBankType to set
	 */
	public void setPayBankType(String payBankType) {
		this.payBankType = payBankType;
	}



	/**
	 * @return the payBankName
	 */
	public String getPayBankName() {
		return payBankName;
	}



	/**
	 * @param payBankName the payBankName to set
	 */
	public void setPayBankName(String payBankName) {
		this.payBankName = payBankName;
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
	 * @return the bankType
	 */
	public String getBankType() {
		return bankType;
	}



	/**
	 * @param bankType the bankType to set
	 */
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}



	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}



	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
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
	 * @return the remarkCD
	 */
	public String getRemarkCD() {
		return remarkCD;
	}



	/**
	 * @param remarkCD the remarkCD to set
	 */
	public void setRemarkCD(String remarkCD) {
		this.remarkCD = remarkCD;
	}



	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}



	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}



	/**
	 * @return the companyDate
	 */
	public String getCompanyDate() {
		return companyDate;
	}



	/**
	 * @param companyDate the companyDate to set
	 */
	public void setCompanyDate(String companyDate) {
		this.companyDate = companyDate;
	}



	/**
	 * @return the resv
	 */
	public String getResv() {
		return resv;
	}



	/**
	 * @param resv the resv to set
	 */
	public void setResv(String resv) {
		this.resv = resv;
	}



	/**
	 * 
	 */
	public RealTimePayBean() {
		super();
		// TODO Auto-generated constructor stub
	}



	
	public RealTimePayBean(InsteadPayTradeBean insteadPayTradeBean) {
		super();
		this.mchntCD = Constant.getInstance().getCmbc_insteadpay_merid();
		this.tranDate = DateUtil.getCurrentDate();
		this.tranTime = DateUtil.getCurrentTime();
		//this.tranId = tranId;
		this.currency = "RMB";
		//this.payAcctNo = payAcctNo;
		//this.payAccName = payAccName;
		//this.payBankType = payBankType;
		//this.payBankName = payBankName;
		this.accNo = insteadPayTradeBean.getAcc_no();
		this.accName = insteadPayTradeBean.getAcc_name();
		this.bankType = insteadPayTradeBean.getBank_type();
		this.bankName = insteadPayTradeBean.getBank_name();
		this.transAmt = insteadPayTradeBean.getTrans_amt();
		//this.remarkCD = remarkCD;
		this.remark = StringUtils.isEmpty(insteadPayTradeBean.getRemark())?"证联代付":insteadPayTradeBean.getRemark();
		//this.companyDate = companyDate;
		//this.resv = resv;
	}



	



	public synchronized String toXML() {
		XStream xstream = new XStream(new DomDriver(null,
				new XmlFriendlyNameCoder("_-", "_")));
		// xstream.processAnnotations(RealTimePayBean.class);
		xstream.autodetectAnnotations(true);
		String xml = XMLHEAD + xstream.toXML(this);
		Pattern p = Pattern.compile("\\s{2,}|\t|\r|\n");
		Matcher m = p.matcher(xml);
		xml = m.replaceAll("");
		return xml;
	}
}
