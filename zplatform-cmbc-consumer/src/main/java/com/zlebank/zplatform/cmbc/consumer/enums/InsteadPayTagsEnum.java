/* 
 * InsteadPayEnum.java  
 * 
 * version TODO
 *
 * 2016年10月19日 
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
 * @date 2016年10月19日 下午1:53:58
 * @since 
 */
public enum InsteadPayTagsEnum {

	/**
	 * 代付-直联代扣
	 */
	INSTEADPAY_REALTIME("TAG_IP_001"),
	
	/**
	 * 民生批量代付
	 */
	INSTEADPAY_BATCH("TAG_IP_002"),
	
	/**
	 * 批量实时代付
	 */
	INSTEADPAY_BATCH_REALTIME("TAG_IP_003"),
	/**
	 * 民生实时代付-查询
	 */
	QUERY_INSTEADPAY_REALTIME("TAG_IP_004"),
	/**
	 * 民生实时代付退汇
	 */
	INSTEADPAY_REALTIME_REEXCHANGE("TAG_IP_005"),
	/**
	 * 民生实时代付查询并处理账务
	 */
	QUERY_INSTEADPAY_REALTIME_ACCOUNTING("TAG_IP_006");
	
	private String code;

	/**
	 * @param code
	 */
	private InsteadPayTagsEnum(String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	
	public static InsteadPayTagsEnum fromValue(String code){
		for(InsteadPayTagsEnum tagsEnum : values()){
			if(tagsEnum.getCode().equals(code)){
				return tagsEnum;
			}
		}
		return null;
	}
	
}
