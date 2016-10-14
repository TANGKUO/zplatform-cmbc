/* 
 * WithholdingTagsEnum.java  
 * 
 * version TODO
 *
 * 2016年10月14日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.consumer.enums;


/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月14日 上午10:19:01
 * @since 
 */
public enum WithholdingTagsEnum {
	/**
	 * 跨行代扣-直联代扣
	 */
	WITHHOLDING("TAG_001"),
	/**
	 * 快捷-银行卡签约
	 */
	QUICKEPAY_BANKSIGN("TAG_002"),
	/**
	 * 快捷-支付
	 */
	QUICKEPAY_PAY("TAG_003"),
	/**
	 * 民生跨行代扣-查询
	 */
	QUERY_TRADE("TAG_004");
	private String code;

	/**
	 * @param code
	 */
	private WithholdingTagsEnum(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	
	public static WithholdingTagsEnum fromValue(String code){
		for(WithholdingTagsEnum tagsEnum : values()){
			if(tagsEnum.getCode().equals(code)){
				return tagsEnum;
			}
		}
		return null;
	}
	
}
