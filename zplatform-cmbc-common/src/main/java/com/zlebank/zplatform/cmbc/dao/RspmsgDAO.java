/* 
 * RspmsgDAO.java  
 * 
 * version TODO
 *
 * 2015年10月22日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年10月22日 下午1:43:04
 * @since 
 */
public interface RspmsgDAO extends BaseDAO<PojoRspmsg>{

    public PojoRspmsg get(String rspid);
    public PojoRspmsg getRspmsgByChnlCode(ChnlTypeEnum chnlType,String retCode) ;
}
