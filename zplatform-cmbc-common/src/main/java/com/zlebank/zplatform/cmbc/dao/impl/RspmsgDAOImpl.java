/* 
 * RspmsgDAOImpl.java  
 * 
 * version TODO
 *
 * 2015年10月22日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年10月22日 下午1:45:26
 * @since 
 */
@Repository("rspmsgDAO")
public class RspmsgDAOImpl  extends HibernateBaseDAOImpl<PojoRspmsg> implements RspmsgDAO{
    private static final Logger log=LoggerFactory.getLogger(RspmsgDAOImpl.class);
    
    /**
     *
     * @param rspid
     * @return
     */
    @Override
    @Transactional(readOnly=true)
    public PojoRspmsg get(String rspid) {
        PojoRspmsg rspmsg = (PojoRspmsg) getSession().get(PojoRspmsg.class, rspid);
        if(rspmsg!=null){
            getSession().evict(rspmsg);
        }
        return rspmsg;
    }

    @SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public PojoRspmsg getRspmsgByChnlCode(ChnlTypeEnum chnlType,String retCode) {
        List<PojoRspmsg> result = null;
        String  queryString = null;
        if(chnlType==null){
        	queryString = "from PojoRspmsg where chnlrspcode=?";
        }else{
        	queryString = "from PojoRspmsg where chnltype=? and chnlrspcode=?";
        }
        try {
            log.info("getRspmsgByChnlCode() queryString:"+queryString);
            Query query = getSession().createQuery(queryString);
            if(chnlType==null){
            	query.setParameter(0, retCode);
            }else{
            	query.setParameter(0,chnlType.getTradeType());
                query.setParameter(1, retCode);
            }
            result = query.list();
            if(result.size()>0){
                return result.get(0);
            }
        } catch (HibernateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
