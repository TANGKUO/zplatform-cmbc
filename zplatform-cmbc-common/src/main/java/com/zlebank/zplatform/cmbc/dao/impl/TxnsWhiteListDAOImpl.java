package com.zlebank.zplatform.cmbc.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.TxnsWhiteListDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWhiteList;
@Repository("txnsWhiteListDAO")
public class TxnsWhiteListDAOImpl extends HibernateBaseDAOImpl<PojoTxnsWhiteList> implements TxnsWhiteListDAO{

	/**
	 *
	 * @param whiteList
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveWhiteList(PojoTxnsWhiteList whiteList) {
		PojoTxnsWhiteList whiteList_exist = getByCardInfo(whiteList);
		if(whiteList_exist==null){
			saveEntity(whiteList);
		}
	}

	/**
	 *
	 * @param whiteList
	 * @return
	 */
	@Override
	@Transactional(readOnly=true)
	public PojoTxnsWhiteList getByCardInfo(PojoTxnsWhiteList whiteList) {
		Criteria criteria = getSession().createCriteria(PojoTxnsWhiteList.class);
		criteria.add(Restrictions.eq("bankaccno", whiteList.getBankaccno()));
		criteria.add(Restrictions.eq("bankaccname", whiteList.getBankaccname()));
		criteria.add(Restrictions.eq("certno", whiteList.getCertno()));
		criteria.add(Restrictions.eq("mobile", whiteList.getMobile()));
		return (PojoTxnsWhiteList) criteria.uniqueResult();
	}

    
}
