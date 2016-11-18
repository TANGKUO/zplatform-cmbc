package com.zlebank.zplatform.cmbc.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealNameResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.common.bean.CMBCWithholdingQueryResultBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.dao.TxnsWithholdingDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
@Repository("txnsWithholdingDAO")
public class TxnsWithholdingDAOImpl extends HibernateBaseDAOImpl<PojoTxnsWithholding> implements TxnsWithholdingDAO{

	private static final Logger log = LoggerFactory.getLogger(TxnsWithholdingDAOImpl.class);
	/**
	 *
	 * @param serialno
	 * @return
	 */
	@Override
	@Transactional(readOnly=true)
	public PojoTxnsWithholding getWithholdingBySerialNo(String serialno) {
		Criteria criteria = getSession().createCriteria(PojoTxnsWithholding.class);
		criteria.add(Restrictions.eq("serialno", serialno));
		return (PojoTxnsWithholding) criteria.uniqueResult();
	}

	/**
	 *
	 * @param withholding
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateRealNameResult(PojoTxnsWithholding withholding) {
		// TODO Auto-generated method stub
		StringBuffer updateHQL = new StringBuffer("update PojoTxnsWithholding set ");
        updateHQL.append("exectype = ?,");
        updateHQL.append("execcode = ?,");
        updateHQL.append("execmsg = ?,");
        updateHQL.append("validatestatus = ?,");
        updateHQL.append("payserialno = ? ");
        updateHQL.append("where serialno = ? ");
        Query query = getSession().createQuery(updateHQL.toString());
        Object[] paramaters = new Object[]{withholding.getExectype(),withholding.getExeccode(),withholding.getExecmsg(),withholding.getValidatestatus(),withholding.getPayserialno(),withholding.getSerialno()};
        for(int i=0;i<paramaters.length;i++){
        	query.setParameter(i, paramaters[i]);
        }
        int rows = query.executeUpdate();
        log.info("updateRealNameResult() effect rows:"+rows); 
	}

	/**
	 *
	 * @param withholding
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateWithholdingLogError(PojoTxnsWithholding withholding) {
		// TODO Auto-generated method stub
		StringBuffer updateHQL = new StringBuffer("update PojoTxnsWithholding set ");
        updateHQL.append("execmsg = ?,");
        updateHQL.append("validatestatus = ? ");
        updateHQL.append("where serialno = ? ");
        Query query = getSession().createQuery(updateHQL.toString());
        Object[] paramaters = new Object[]{withholding.getExecmsg(),"11",withholding.getSerialno()};
        for(int i=0;i<paramaters.length;i++){
        	query.setParameter(i, paramaters[i]);
        }
        int rows = query.executeUpdate();
        log.info("updateWithholdingLogError() effect rows:"+rows); 
	}

	/**
	 *
	 * @param serialno
	 * @param realTimeWithholdingResultBean
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateWithholdingResult(CMBCRealTimeWithholdingResultBean realTimeWithholdingResultBean) {
		// TODO Auto-generated method stub
		StringBuffer updateHQL = new StringBuffer("update PojoTxnsWithholding set ");
        updateHQL.append("exectype = ?,");
        updateHQL.append("execcode = ?,");
        updateHQL.append("execmsg = ?,");
        updateHQL.append("settdate = ?,");
        updateHQL.append("banktrandate = ?,");
        updateHQL.append("banktrantime = ?,");
        updateHQL.append("payserialno = ? ");
        updateHQL.append("where serialno = ? ");
        Query query = getSession().createQuery(updateHQL.toString());
        Object[] paramaters = new Object[]{realTimeWithholdingResultBean.getExectype(),realTimeWithholdingResultBean.getExeccode(),realTimeWithholdingResultBean.getExecmsg(),realTimeWithholdingResultBean.getSettdate(),realTimeWithholdingResultBean.getSettdate(),realTimeWithholdingResultBean.getTranstime(),realTimeWithholdingResultBean.getPayserialno(),realTimeWithholdingResultBean.getReqserialno()};
        for(int i=0;i<paramaters.length;i++){
        	query.setParameter(i, paramaters[i]);
        }
        int rows = query.executeUpdate();
        log.info("updateWithholdingResult() effect rows:"+rows);
	}

	/**
	 *
	 * @param realNameResultBean
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateRealNameResult(CMBCRealNameResultBean realNameResultBean) {
		// TODO Auto-generated method stub
		StringBuffer updateHQL = new StringBuffer("update PojoTxnsWithholding set ");
        updateHQL.append("exectype = ?,");
        updateHQL.append("execcode = ?,");
        updateHQL.append("execmsg = ?,");
        updateHQL.append("settdate = ?,");
        updateHQL.append("banktrandate = ?,");
        updateHQL.append("banktrantime = ?,");
        updateHQL.append("payserialno = ?,");
        updateHQL.append("validatestatus = ? ");
        updateHQL.append("where serialno = ? ");
        Query query = getSession().createQuery(updateHQL.toString());
        Object[] paramaters = new Object[]{realNameResultBean.getExectype(),realNameResultBean.getExeccode(),realNameResultBean.getExecmsg(),realNameResultBean.getSettdate(),realNameResultBean.getSettdate(),realNameResultBean.getTranstime(),realNameResultBean.getPayserialno(),realNameResultBean.getValidatestatus(),realNameResultBean.getReqserialno()};
        for(int i=0;i<paramaters.length;i++){
        	query.setParameter(i, paramaters[i]);
        }
        int rows = query.executeUpdate();
        log.info("updateRealNameResult() effect rows:"+rows);
	}

	/**
	 *
	 * @param withholdingQueryResultBean
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateWithholdingQueryResult(
			CMBCWithholdingQueryResultBean withholdingQueryResultBean) {
		// TODO Auto-generated method stub
		StringBuffer updateHQL = new StringBuffer("update PojoTxnsWithholding set ");
        updateHQL.append("exectype = ?,");
        updateHQL.append("execcode = ?,");
        updateHQL.append("execmsg = ?,");
        updateHQL.append("settdate = ?,");
        updateHQL.append("oriexectype = ?,");
        updateHQL.append("oriexeccode = ?,");
        updateHQL.append("oriexecmsg = ? ");
        updateHQL.append("where serialno = ? ");
        Query query = getSession().createQuery(updateHQL.toString());
        
        Object[] paramaters = new Object[]{withholdingQueryResultBean.getExectype(),
        		withholdingQueryResultBean.getExeccode(),
        		withholdingQueryResultBean.getExecmsg(),
        		withholdingQueryResultBean.getSettdate(),
        		withholdingQueryResultBean.getOriexectype(),
        		withholdingQueryResultBean.getOriexeccode(),
        		withholdingQueryResultBean.getOriexecmsg(),
        		withholdingQueryResultBean.getReqserialno()};
        for(int i=0;i<paramaters.length;i++){
        	query.setParameter(i, paramaters[i]);
        }
        int rows = query.executeUpdate();
	}

    
}
