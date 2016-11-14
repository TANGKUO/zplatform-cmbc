package com.zlebank.zplatform.cmbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.dao.TxnsWhiteListDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWhiteList;
import com.zlebank.zplatform.cmbc.service.TxnsWhiteListService;
/**
 * 
 * 白名单t_txns_white_list service类
 *
 * @author guojia
 * @version
 * @date 2015年12月11日 上午11:12:53
 * @since
 */
@Service("txnsWhiteListService")
public class TxnsWhiteListServiceImpl implements TxnsWhiteListService{

    @Autowired
    private TxnsWhiteListDAO txnsWhiteListDAO;
   
    /**
     * 保存白名单数据
     * @param whiteList
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void saveWhiteList(PojoTxnsWhiteList whiteList){
    	txnsWhiteListDAO.saveWhiteList(whiteList);
        
    }
    
    /**
     * 通过卡信息获取白名单数据
     * @param whiteList
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public PojoTxnsWhiteList getByCardInfo(PojoTxnsWhiteList whiteList){
        
        return txnsWhiteListDAO.getByCardInfo(whiteList);
    }
}
