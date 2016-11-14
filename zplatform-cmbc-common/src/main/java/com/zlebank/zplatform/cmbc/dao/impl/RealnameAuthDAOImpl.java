/* 
 * RealnameAuthDAOImpl.java  
 * 
 * version TODO
 *
 * 2015年11月24日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.RealnameAuthDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoRealnameAuth;

/**
 * Class Description
 *
 * @author Luxiaoshuai
 * @version
 * @date 2015年11月24日 下午12:34:32
 * @since
 */
@Repository("realnameAuthDAO")
public class RealnameAuthDAOImpl extends HibernateBaseDAOImpl<PojoRealnameAuth>
		implements RealnameAuthDAO {

	/**
	 * 保存实名认证数据
	 * 
	 * @param realnameAuth
	 * @throws TradeException
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveRealNameAuth(PojoRealnameAuth realnameAuth) {
		PojoRealnameAuth cardInfo = getByCardInfo(realnameAuth);
		if (cardInfo == null) {
			saveEntity(realnameAuth);
		}

	}

	/**
	 * 根据卡号和持卡人姓名得到
	 * 
	 * @param cardNo
	 * @param accName
	 * @return
	 */
	@Override
	public PojoRealnameAuth getByCardNoAndName(String cardNo, String accName,
			String certifId, String phoneNo) {
		Criteria crite = this.getSession().createCriteria(
				PojoRealnameAuth.class);
		crite.add(Restrictions.eq("cardNo", cardNo));
		crite.add(Restrictions.eq("customerNm", accName));
		crite.add(Restrictions.eq("certifId", certifId));
		crite.add(Restrictions.eq("phoneNo",
				Long.parseLong(phoneNo == null ? "0" : phoneNo)));
		crite.add(Restrictions.eq("status", "00"));
		@SuppressWarnings("unchecked")
		List<PojoRealnameAuth> pojos = crite.list();
		return pojos.size() > 0 ? (PojoRealnameAuth) pojos.get(0) : null;
	}

	/**
	 * 通过卡信息获取实名认证数据
	 * 
	 * @param realnameAuth
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public PojoRealnameAuth getByCardInfo(PojoRealnameAuth realnameAuth) {
		Criteria crite = this.getSession().createCriteria(
				PojoRealnameAuth.class);
		crite.add(Restrictions.eq("cardNo", realnameAuth.getCardNo()));
		crite.add(Restrictions.eq("customerNm", realnameAuth.getCustomerNm()));
		crite.add(Restrictions.eq("certifId", realnameAuth.getCertifId()));
		crite.add(Restrictions.eq("phoneNo", realnameAuth.getPhoneNo()));
		Object uniqueResult = crite.uniqueResult();
		if (uniqueResult != null) {
			return (PojoRealnameAuth) uniqueResult;
		}
		return null;
	}

}
