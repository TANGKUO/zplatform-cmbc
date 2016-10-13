/* 
 * CMBCWhiteListService.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.withholding.service;

import com.zlebank.zplatform.cmbc.common.bean.ResultBean;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 下午3:17:12
 * @since 
 */
public interface CMBCWhiteListService {

	/**
     * 白名单采集
     * @param bankaccno
     * @param bankaccname
     * @param certno
     * @param mobile
     * @param certtype
     * @return
     */
    public ResultBean whiteListCollection(String bankaccno, String bankaccname,String certno, String mobile,String certtype);
}
