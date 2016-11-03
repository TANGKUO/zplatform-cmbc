/* 
 * CMBCRealTimeWithholdingResultBean.java  
 * 
 * version TODO
 *
 * 2016年11月2日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.common.bean;


/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月2日 下午4:01:36
 * @since 
 */
public class CMBCRealTimeWithholdingResultBean {
    private String version;// 版本号
    private String settdate;// 清算日期
    private String transtime;// 交易时间
    private String reqserialno;// 请求流水号
    private String exectype;// 响应类型
    private String execcode;// 响应代码
    private String execmsg;// 响应描述
    private String merid;// 商户号
    private String payserialno;// 平台处理流水号
    private String resv;// 备用
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the settdate
	 */
	public String getSettdate() {
		return settdate;
	}
	/**
	 * @param settdate the settdate to set
	 */
	public void setSettdate(String settdate) {
		this.settdate = settdate;
	}
	/**
	 * @return the transtime
	 */
	public String getTranstime() {
		return transtime;
	}
	/**
	 * @param transtime the transtime to set
	 */
	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}
	/**
	 * @return the reqserialno
	 */
	public String getReqserialno() {
		return reqserialno;
	}
	/**
	 * @param reqserialno the reqserialno to set
	 */
	public void setReqserialno(String reqserialno) {
		this.reqserialno = reqserialno;
	}
	/**
	 * @return the exectype
	 */
	public String getExectype() {
		return exectype;
	}
	/**
	 * @param exectype the exectype to set
	 */
	public void setExectype(String exectype) {
		this.exectype = exectype;
	}
	/**
	 * @return the execcode
	 */
	public String getExeccode() {
		return execcode;
	}
	/**
	 * @param execcode the execcode to set
	 */
	public void setExeccode(String execcode) {
		this.execcode = execcode;
	}
	/**
	 * @return the execmsg
	 */
	public String getExecmsg() {
		return execmsg;
	}
	/**
	 * @param execmsg the execmsg to set
	 */
	public void setExecmsg(String execmsg) {
		this.execmsg = execmsg;
	}
	/**
	 * @return the merid
	 */
	public String getMerid() {
		return merid;
	}
	/**
	 * @param merid the merid to set
	 */
	public void setMerid(String merid) {
		this.merid = merid;
	}
	/**
	 * @return the payserialno
	 */
	public String getPayserialno() {
		return payserialno;
	}
	/**
	 * @param payserialno the payserialno to set
	 */
	public void setPayserialno(String payserialno) {
		this.payserialno = payserialno;
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
    
    
}
