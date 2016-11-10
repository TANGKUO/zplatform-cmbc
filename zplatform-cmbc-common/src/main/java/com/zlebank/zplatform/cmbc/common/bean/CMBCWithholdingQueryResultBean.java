/* 
 * CMBCWithholdingQueryResultBean.java  
 * 
 * version TODO
 *
 * 2016年11月10日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.common.bean;

import java.io.Serializable;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年11月10日 下午3:05:51
 * @since 
 */
public class CMBCWithholdingQueryResultBean implements Serializable{
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String version;// 版本号
    private String settdate;// 清算日期
    private String transtime;// 交易时间
    private String reqserialno;// 请求流水号
    private String exectype;// 响应类型
    private String execcode;// 响应代码
    private String execmsg;// 响应描述
    private String merid;// 商户号
    private String orireqserialno;// 原交易流水号
    private String orisettdate;// 原交易清算日期
    private String oritranstime;// 原交易处理时间
    private String oripayserialno;// 原交易平台流水号
    private String oriexectype;// 原交易响应类型
    private String oriexeccode;// 原交易响应代码
    private String oriexecmsg;// 原交易响应描述
    private String resv;// 备用域
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
	 * @return the orireqserialno
	 */
	public String getOrireqserialno() {
		return orireqserialno;
	}
	/**
	 * @param orireqserialno the orireqserialno to set
	 */
	public void setOrireqserialno(String orireqserialno) {
		this.orireqserialno = orireqserialno;
	}
	/**
	 * @return the orisettdate
	 */
	public String getOrisettdate() {
		return orisettdate;
	}
	/**
	 * @param orisettdate the orisettdate to set
	 */
	public void setOrisettdate(String orisettdate) {
		this.orisettdate = orisettdate;
	}
	/**
	 * @return the oritranstime
	 */
	public String getOritranstime() {
		return oritranstime;
	}
	/**
	 * @param oritranstime the oritranstime to set
	 */
	public void setOritranstime(String oritranstime) {
		this.oritranstime = oritranstime;
	}
	/**
	 * @return the oripayserialno
	 */
	public String getOripayserialno() {
		return oripayserialno;
	}
	/**
	 * @param oripayserialno the oripayserialno to set
	 */
	public void setOripayserialno(String oripayserialno) {
		this.oripayserialno = oripayserialno;
	}
	/**
	 * @return the oriexectype
	 */
	public String getOriexectype() {
		return oriexectype;
	}
	/**
	 * @param oriexectype the oriexectype to set
	 */
	public void setOriexectype(String oriexectype) {
		this.oriexectype = oriexectype;
	}
	/**
	 * @return the oriexeccode
	 */
	public String getOriexeccode() {
		return oriexeccode;
	}
	/**
	 * @param oriexeccode the oriexeccode to set
	 */
	public void setOriexeccode(String oriexeccode) {
		this.oriexeccode = oriexeccode;
	}
	/**
	 * @return the oriexecmsg
	 */
	public String getOriexecmsg() {
		return oriexecmsg;
	}
	/**
	 * @param oriexecmsg the oriexecmsg to set
	 */
	public void setOriexecmsg(String oriexecmsg) {
		this.oriexecmsg = oriexecmsg;
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
