
/* 
 * ProvinceDAO.java  
 * 
 * version TODO
 *
 * 2015年9月13日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao;

import java.util.List;

import com.zlebank.zplatform.cmbc.pojo.PojoProvince;

/**
 * Class Description
 *
 * @author yangpeng
 * @version
 * @date 2015年9月13日 下午5:53:21
 * @since 
 */
public interface ProvinceDAO extends BaseDAO<PojoProvince> {
   /**
    * 通过省代码得到省
    * @param code
    * @return PojoProvince
    */
	@Deprecated
    public PojoProvince getProvinceByCode(String code);
    /**
     * 通过省代码得到省
     * @param code
     * @return PojoProvince
     */
    public PojoProvince getProvinceByXZCode(String code);
    
    /**
     * 取得全部省份信息
     * @return
     */
    public List<PojoProvince> getAllProvice();
    
    /**
     * 通过主键获取省份信息
     * @param pid
     * @return
     */
    public PojoProvince get(long pid);
}
