/* 
 * TxnsOrderinfoDAOImpl.java  
 * 
 * version TODO
 *
 * 2015年8月29日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.TxnsOrderinfoDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsOrderinfo;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2015年8月29日 下午3:40:27
 * @since
 */
@Repository("txnsOrderinfoDAO")
public class TxnsOrderinfoDAOImpl extends
		HibernateBaseDAOImpl<PojoTxnsOrderinfo> implements TxnsOrderinfoDAO {

	private static final Logger log = LoggerFactory.getLogger(TxnsOrderinfoDAOImpl.class);
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateOrderToFail(String txnseqno) {
		String hql = "update PojoTxnsOrderinfo set status = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "03");
		query.setString(1, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToFail() effect rows:"+rows);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
	public void updateOrderToSuccess(String txnseqno) {
		String hql = "update PojoTxnsOrderinfo set status = ? where relatetradetxn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccess() effect rows:"+rows);
	}

	/**
	 *
	 * @param tn
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateOrderToSuccessByTN(String tn) {
		// TODO Auto-generated method stub
		String hql = "update PojoTxnsOrderinfo set status = ? where tn = ? ";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setString(0, "00");
		query.setString(1, tn);
		int rows = query.executeUpdate();
		log.info("updateOrderToSuccessByTN() effect rows:"+rows);
	}

}
