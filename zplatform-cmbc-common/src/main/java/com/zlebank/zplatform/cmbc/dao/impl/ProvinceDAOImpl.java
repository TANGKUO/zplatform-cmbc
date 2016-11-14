/* 
 * ProvinceDAOImpl.java  
 * 
 * version TODO
 *
 * 2015年9月13日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.ProvinceDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoProvince;

/**
 * Class Description
 *
 * @author yangpeng
 * @version
 * @date 2015年9月13日 下午6:04:01
 * @since 
 */
@Repository("provinceDAO")
public class ProvinceDAOImpl extends HibernateBaseDAOImpl<PojoProvince>implements ProvinceDAO {

    /**
     *
     * @param code
     * @return
     */
    @Override
    public PojoProvince getProvinceByCode(String code) {
        return (PojoProvince)this.getSession().createCriteria(PojoProvince.class)
                .add(Restrictions.eq("", code)).uniqueResult();
    }

    @Override
    @Transactional(readOnly=true)
    public PojoProvince getProvinceByXZCode(String code) {
        String hql = "from PojoProvince where xzCode = ?";
        Query query = getSession().createQuery(hql);
        query.setString(0, code);
        return (PojoProvince) query.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
    public List<PojoProvince> getAllProvice(){
    	String hql = "from PojoProvince order by provinceId asc";
        Query query = getSession().createQuery(hql);
    	return query.list();
    }

	/**
	 *
	 * @param pid
	 * @return
	 */
	@Override
	@Transactional(readOnly=true)
	public PojoProvince get(long pid) {
		String hql = "from PojoProvince where provinceId = ?";
        Query query = getSession().createQuery(hql);
        query.setLong(0, pid);
        return (PojoProvince) query.uniqueResult();
	}

}
