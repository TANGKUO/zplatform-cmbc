/* 
 * CMBCRealTimeInsteadPayResultBean.java  
 * 
 * version TODO
 *
 * 2016年10月21日 
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
 * @date 2016年10月21日 下午2:45:43
 * @since 
 */
public class CMBCRealTimeInsteadPayQueryResultBean {
	 private String resp_type;// 应答码类型
	    private String resp_code;// 应答码
	    private String resp_msg;// 应答描述
	    private String mchnt_cd;// 商户编号
	    private String tran_date;// 交易日期
	    private String tran_time;// 交易时间
	    private String tran_id;// 渠道交易流水号
	   
	    
	    private String ori_tran_date;//	原交易日期
	    private String ori_tran_id;//	原合作方流水号
	    private String ori_bank_tran_id;//	银行处理流水号
	    private String ori_bank_tran_date;//	银行交易日期
	    private String ori_resp_type;//	原应答码类型
	    private String ori_resp_msg	;//原交易应答描述
	    private String ori_resp_code;//	原交易应答码
	    private String resv;// 备用
		/**
		 * @return the resp_type
		 */
		public String getResp_type() {
			return resp_type;
		}
		/**
		 * @param resp_type the resp_type to set
		 */
		public void setResp_type(String resp_type) {
			this.resp_type = resp_type;
		}
		/**
		 * @return the resp_code
		 */
		public String getResp_code() {
			return resp_code;
		}
		/**
		 * @param resp_code the resp_code to set
		 */
		public void setResp_code(String resp_code) {
			this.resp_code = resp_code;
		}
		/**
		 * @return the resp_msg
		 */
		public String getResp_msg() {
			return resp_msg;
		}
		/**
		 * @param resp_msg the resp_msg to set
		 */
		public void setResp_msg(String resp_msg) {
			this.resp_msg = resp_msg;
		}
		/**
		 * @return the mchnt_cd
		 */
		public String getMchnt_cd() {
			return mchnt_cd;
		}
		/**
		 * @param mchnt_cd the mchnt_cd to set
		 */
		public void setMchnt_cd(String mchnt_cd) {
			this.mchnt_cd = mchnt_cd;
		}
		/**
		 * @return the tran_date
		 */
		public String getTran_date() {
			return tran_date;
		}
		/**
		 * @param tran_date the tran_date to set
		 */
		public void setTran_date(String tran_date) {
			this.tran_date = tran_date;
		}
		/**
		 * @return the tran_time
		 */
		public String getTran_time() {
			return tran_time;
		}
		/**
		 * @param tran_time the tran_time to set
		 */
		public void setTran_time(String tran_time) {
			this.tran_time = tran_time;
		}
		/**
		 * @return the tran_id
		 */
		public String getTran_id() {
			return tran_id;
		}
		/**
		 * @param tran_id the tran_id to set
		 */
		public void setTran_id(String tran_id) {
			this.tran_id = tran_id;
		}
		/**
		 * @return the ori_tran_date
		 */
		public String getOri_tran_date() {
			return ori_tran_date;
		}
		/**
		 * @param ori_tran_date the ori_tran_date to set
		 */
		public void setOri_tran_date(String ori_tran_date) {
			this.ori_tran_date = ori_tran_date;
		}
		/**
		 * @return the ori_tran_id
		 */
		public String getOri_tran_id() {
			return ori_tran_id;
		}
		/**
		 * @param ori_tran_id the ori_tran_id to set
		 */
		public void setOri_tran_id(String ori_tran_id) {
			this.ori_tran_id = ori_tran_id;
		}
		/**
		 * @return the ori_bank_tran_id
		 */
		public String getOri_bank_tran_id() {
			return ori_bank_tran_id;
		}
		/**
		 * @param ori_bank_tran_id the ori_bank_tran_id to set
		 */
		public void setOri_bank_tran_id(String ori_bank_tran_id) {
			this.ori_bank_tran_id = ori_bank_tran_id;
		}
		/**
		 * @return the ori_bank_tran_date
		 */
		public String getOri_bank_tran_date() {
			return ori_bank_tran_date;
		}
		/**
		 * @param ori_bank_tran_date the ori_bank_tran_date to set
		 */
		public void setOri_bank_tran_date(String ori_bank_tran_date) {
			this.ori_bank_tran_date = ori_bank_tran_date;
		}
		/**
		 * @return the ori_resp_type
		 */
		public String getOri_resp_type() {
			return ori_resp_type;
		}
		/**
		 * @param ori_resp_type the ori_resp_type to set
		 */
		public void setOri_resp_type(String ori_resp_type) {
			this.ori_resp_type = ori_resp_type;
		}
		/**
		 * @return the ori_resp_msg
		 */
		public String getOri_resp_msg() {
			return ori_resp_msg;
		}
		/**
		 * @param ori_resp_msg the ori_resp_msg to set
		 */
		public void setOri_resp_msg(String ori_resp_msg) {
			this.ori_resp_msg = ori_resp_msg;
		}
		/**
		 * @return the ori_resp_code
		 */
		public String getOri_resp_code() {
			return ori_resp_code;
		}
		/**
		 * @param ori_resp_code the ori_resp_code to set
		 */
		public void setOri_resp_code(String ori_resp_code) {
			this.ori_resp_code = ori_resp_code;
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
