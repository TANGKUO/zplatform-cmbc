package com.zlebank.zplatform.cmbc.dao.impl;

import java.util.Date;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.InsteadPayRealtimeDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoInsteadPayRealtime;


@Repository("insteadPayRealtimeDAO")
public class InsteadPayRealtimeDAOImpl extends
		HibernateBaseDAOImpl<PojoInsteadPayRealtime> implements
		InsteadPayRealtimeDAO {
	private static final Logger log = LoggerFactory.getLogger(InsteadPayRealtimeDAOImpl.class);

	
	
	/**
	 *
	 * @param txnseqno
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateInsteadSuccess(String txnseqno) {
		// TODO Auto-generated method stub
		String hql = "update PojoInsteadPayRealtime set status = ?,retCode = ?,retInfo = ?,updateTime = ? where txnseqno = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, "00");
		query.setParameter(1, "0000");
		query.setParameter(2, "交易成功");
		query.setParameter(3, new Date());
		query.setParameter(4, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateInsteadSuccess() effect rows:"+rows);
	}

	/**
	 *
	 * @param txnseqno
	 * @param retCode
	 * @param retMsg
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateInsteadFail(String txnseqno, String retCode, String retMsg) {
		// TODO Auto-generated method stub
		String hql = "update PojoInsteadPayRealtime set status = ?,retCode = ?,retInfo = ?,updateTime = ? where txnseqno = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, "03");
		query.setParameter(1, retCode);
		query.setParameter(2, retMsg);
		query.setParameter(3, new Date());
		query.setParameter(4, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateInsteadSuccess() effect rows:"+rows);
	}

	/**
	 *
	 * @param txnseqno
	 * @param retCode
	 * @param retMsg
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateInsteadReexchange(String txnseqno, String retCode,
			String retMsg) {
		// TODO Auto-generated method stub
		String hql = "update PojoInsteadPayRealtime set status = ?,retCode = ?,retInfo = ?,updateTime = ? where txnseqno = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, "05");
		query.setParameter(1, retCode);
		query.setParameter(2, retMsg);
		query.setParameter(3, new Date());
		query.setParameter(4, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateInsteadSuccess() effect rows:"+rows);
	}

	
}
