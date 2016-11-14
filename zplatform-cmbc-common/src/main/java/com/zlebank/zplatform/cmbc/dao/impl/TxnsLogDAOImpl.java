/* 
 * TxnsLogDAOImpl.java  
 * 
 * version TODO
 *
 * 2016年10月13日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.common.bean.PayPartyBean;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.enums.TradeStatFlagEnum;
import com.zlebank.zplatform.cmbc.common.utils.DateUtil;
import com.zlebank.zplatform.cmbc.common.utils.UUIDUtil;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsLogDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsLog;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年10月13日 下午2:07:03
 * @since
 */
@Repository("txnsLogDAO")
public class TxnsLogDAOImpl extends HibernateBaseDAOImpl<PojoTxnsLog> implements
		TxnsLogDAO {

	private static final Logger log = LoggerFactory
			.getLogger(TxnsLogDAOImpl.class);

	@Autowired
	private RspmsgDAO rspmsgDAO;
	/**
	 *
	 * @param txnseqno
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public PojoTxnsLog getTxnsLogByTxnseqno(String txnseqno) {
		Criteria criteria = getSession().createCriteria(PojoTxnsLog.class);
		criteria.add(Restrictions.eq("txnseqno", txnseqno));
		PojoTxnsLog txnsLog = (PojoTxnsLog) criteria.uniqueResult();
		if(txnsLog!=null){
			getSession().evict(txnsLog);
		}
		return txnsLog;
	}

	/**
	 *
	 * @param txnsLog
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateTxnsLog(PojoTxnsLog txnsLog) {
		update(txnsLog);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updatePayInfo(PayPartyBean payPartyBean) {
		String hql = "update PojoTxnsLog set paytype=?,payordno=?,payinst=?,payfirmerno=?,payordcomtime=?,payrettsnseqno=? where txnseqno=?";
		Query query = getSession().createQuery(hql);
		Object[] paramaters = new Object[] {
				StringUtils.isNotEmpty(payPartyBean.getPaytype()) ? payPartyBean
						.getPaytype() : "01", payPartyBean.getPayordno(),
				payPartyBean.getPayinst(), payPartyBean.getPayfirmerno(),
				payPartyBean.getPayordcomtime(),
				payPartyBean.getPayrettsnseqno(), payPartyBean.getTxnseqno() };

		for (int i = 0; i < paramaters.length; i++) {
			query.setParameter(i, paramaters[i]);
		}
		int rows = query.executeUpdate();
		log.info("updatePayInfo() effect rows:" + rows);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateTradeStatFlag(String txnseqno,
			TradeStatFlagEnum tradeStatFlagEnum) {
		String hql = "update PojoTxnsLog set tradestatflag = ? where txnseqno = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, tradeStatFlagEnum.getStatus());
		query.setParameter(1, txnseqno);
		int rows = query.executeUpdate();
		log.info("updateTradeStatFlag() effect rows:" + rows);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateCMBCTradeData(PayPartyBean payPartyBean){
		Object[] paramaters = null;
        String hql = "update PojoTxnsLog set paytype=?,payordno=?,payinst=?,payfirmerno=?,payordcomtime=?,payrettsnseqno=?,payretcode=?,payretinfo=?,retcode=?,retinfo=?,tradestatflag=?,payordfintime=?, retdatetime=?,tradetxnflag=?,relate=?,tradeseltxn=? where txnseqno=?";
        Query query = getSession().createQuery(hql);
	    try {
	        PojoRspmsg msg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING,payPartyBean.getPayretcode().trim());
	        paramaters = new Object[]{
	        		StringUtils.isNotEmpty(payPartyBean.getPaytype())?payPartyBean.getPaytype():"01",
					payPartyBean.getPayordno(),
					payPartyBean.getPayinst(),
					payPartyBean.getPayfirmerno(),
					payPartyBean.getPayordcomtime(),
				    payPartyBean.getPayrettsnseqno(),
				    payPartyBean.getPayretcode().trim(),
				    payPartyBean.getPayretinfo().trim(),
				    msg.getWebrspcode(),msg.getRspinfo(),
	        		"0000".equals(msg.getWebrspcode())?TradeStatFlagEnum.FINISH_SUCCESS.getStatus():TradeStatFlagEnum.FINISH_FAILED.getStatus(),
	        		DateUtil.getCurrentDateTime(),
	        		DateUtil.getCurrentDateTime(),
	        		"10000000",
	        		"10000000",
	        		UUIDUtil.uuid(),
	        		payPartyBean.getTxnseqno()};
	    } catch (Exception e) {
	        paramaters = new Object[]{
	        		StringUtils.isNotEmpty(payPartyBean.getPaytype())?payPartyBean.getPaytype():"01",
					payPartyBean.getPayordno(),
					payPartyBean.getPayinst(),
					payPartyBean.getPayfirmerno(),
					payPartyBean.getPayordcomtime(),
				    payPartyBean.getPayrettsnseqno(),
				    payPartyBean.getPayretcode().trim(),
				    payPartyBean.getPayretinfo().trim(),
				    payPartyBean.getPayretcode().trim(),
				    payPartyBean.getPayretinfo().trim(),
	        		TradeStatFlagEnum.FINISH_FAILED.getStatus(),
	        		DateUtil.getCurrentDateTime(),
	        		DateUtil.getCurrentDateTime(),
	        		"10000000",
	        		"10000000",
	        		UUIDUtil.uuid(),
	        		payPartyBean.getTxnseqno()};
	    }
	    for (int i = 0; i < paramaters.length; i++) {
			query.setParameter(i, paramaters[i]);
		}
		int rows = query.executeUpdate();
		log.info("updateCMBCTradeData() effect rows:" + rows);
	}

	/**
	 *
	 * @param cardNo
	 * @return
	 */
	@Override
	@Transactional(readOnly=true)
	public Map<String, Object> getCardInfo(String cardNo){
		StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT type,bankcode,bankname ");
        sqlBuffer.append("FROM (SELECT t.TYPE,t.BANKCODE,b.bankname ");
        sqlBuffer.append("FROM t_card_bin t, t_bank_insti b ");
        sqlBuffer.append("WHERE t.bankcode = b.bankcode ");
        sqlBuffer.append("AND ? LIKE t.cardbin || '%' ");
        sqlBuffer.append("AND t.cardlen = ? ");
        sqlBuffer.append("ORDER BY t.cardbin DESC) ");
        sqlBuffer.append("WHERE ROWNUM = 1 ");
        
        SQLQuery sqlQuery = (SQLQuery) getSession().createSQLQuery(sqlBuffer.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        sqlQuery.setParameter(0, cardNo);
        sqlQuery.setParameter(1, cardNo.trim().length());
        List<Map<String, Object>> routList =  (List<Map<String, Object>>)sqlQuery.list();
       
        if(routList.size()>0){
            return routList.get(0);
        }
		return null;
	}

	/**
	 *
	 * @param txnseqno
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateAppInfo(String txnseqno) {
		// TODO Auto-generated method stub
		String hql = "update PojoTxnsLog set appinst=?,appordcommitime=?,appordno=? where txnseqno = ?";
		 Query query = getSession().createQuery(hql);
		 query.setParameter(0, "000000000000");
		 query.setParameter(1, DateUtil.getCurrentDateTime());
		 query.setParameter(2, "");
		 query.setParameter(3, txnseqno);
		 int rows = query.executeUpdate();
		 log.info("updateAppInfo() effect rows:" + rows);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateCMBCReexchange(String txnseqno,String retCode,String retMsg){
		String hql = "update PojoTxnsLog set payretcode = ?,payretinfo = ?,retcode = ?,retinfo = ? where txnseqno = ?";
		 Query query = getSession().createQuery(hql);
		 query.setParameter(0, retCode);
		 query.setParameter(1, retMsg);
		 query.setParameter(2, "02HH");
		 query.setParameter(3, "交易失败，详情请咨询证联金融客服010-84298418");
		 query.setParameter(4, txnseqno);
		 int rows = query.executeUpdate();
		 log.info("updateAppInfo() effect rows:" + rows);
	}

	/**
	 *
	 * @param txnsLog
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void saveTxnsLog(PojoTxnsLog txnsLog) {
		saveEntity(txnsLog);
	}
}
