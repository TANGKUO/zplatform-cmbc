package com.zlebank.zplatform.cmbc.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeInsteadPayResultBean;
import com.zlebank.zplatform.cmbc.common.enums.ChnlTypeEnum;
import com.zlebank.zplatform.cmbc.common.pojo.PojoRspmsg;
import com.zlebank.zplatform.cmbc.common.pojo.PojoTxnsCmbcInstPayLog;
import com.zlebank.zplatform.cmbc.dao.RspmsgDAO;
import com.zlebank.zplatform.cmbc.dao.TxnsCmbcInstPayLogDAO;
@Repository("txnsCmbcInstPayLogDAO")
public class TxnsCmbcInstPayLogDAOImpl extends HibernateBaseDAOImpl<PojoTxnsCmbcInstPayLog> implements TxnsCmbcInstPayLogDAO {

	private static final Logger log = LoggerFactory.getLogger(TxnsCmbcInstPayLogDAOImpl.class);
	//@Autowired
	//private RspmsgDAO rspmsgDAO;
	/**
	 *
	 * @param payLog
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void savePayLog(PojoTxnsCmbcInstPayLog cmbcInstPayLog) {
		// TODO Auto-generated method stub
		saveEntity(cmbcInstPayLog);
	}

	/**
	 *
	 * @param tranId
	 * @return
	 */
	@Override
	@Transactional(readOnly=true)
	public PojoTxnsCmbcInstPayLog queryByTranId(String tranId) {
		Criteria criteria = getSession().createCriteria(PojoTxnsCmbcInstPayLog.class);
		criteria.add(Restrictions.eq("tranId", tranId));
		return (PojoTxnsCmbcInstPayLog) criteria.uniqueResult();
	}

	/**
	 *
	 * @param payLog
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updatePayLog(PojoTxnsCmbcInstPayLog payLog) {
		// TODO Auto-generated method stub
		
	}

	/**
	 *
	 * @param realTimePayResultBean
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateInsteadPayResult(CMBCRealTimeInsteadPayResultBean realTimePayResultBean) {
		
		String hql = "update PojoTxnsCmbcInstPayLog set respType = ?,respCode = ?,respMsg = ?,bankTranId = ?,bankTranDate = ? where tranId = ?";
		Query query = getSession().createQuery(hql);
		query.setParameter(0, realTimePayResultBean.getResp_type());
		query.setParameter(1, realTimePayResultBean.getResp_code());
		query.setParameter(2, realTimePayResultBean.getResp_msg());
		query.setParameter(3, realTimePayResultBean.getBank_tran_id());
		query.setParameter(4, realTimePayResultBean.getBank_tran_date());
		query.setParameter(5, realTimePayResultBean.getTran_id());
		int rows = query.executeUpdate();
		//PojoRspmsg rspmsg = rspmsgDAO.getRspmsgByChnlCode(ChnlTypeEnum.CMBCWITHHOLDING, realTimePayResultBean.getResp_code());
		log.info("updateInsteadPayResult() effect rows:"+rows);
	}
	

}