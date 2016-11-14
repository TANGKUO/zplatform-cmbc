/* 
 * RealnameAuthDAO.java  
 * 
 * version TODO
 *
 * 2015年11月24日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;

/**
 * 实名认证DAO
 *
 * @author Luxiaoshuai
 * @version
 * @date 2015年11月24日 下午12:30:18
 * @since 
 */
public interface RealnameAuthDAO extends BaseDAO<PojoRealnameAuth>{

    /**
     * 保存实名认证数据
     * @param realnameAuth
     */
    public void saveRealNameAuth(PojoRealnameAuth realnameAuth);
    
    /**
     * 根据卡号和持卡人姓名得到
     * @param cardNo
     * @param accName
     * @param phoneNo 
     * @param string 
     * @return 
     */
    public PojoRealnameAuth getByCardNoAndName(String cardNo, String accName, String certifId, String phoneNo);
    
    /**
     * 通过卡信息获取实名认证数据
     * @param realnameAuth
     * @return
     */
    public PojoRealnameAuth getByCardInfo(PojoRealnameAuth realnameAuth);
    
   
    

}
