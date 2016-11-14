package com.zlebank.zplatform.cmbc.dao;

import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWhiteList;

public interface TxnsWhiteListDAO extends BaseDAO<PojoTxnsWhiteList>{
    
	/**
     * 保存白名单数据
     * @param whiteList
     */
    public void saveWhiteList(PojoTxnsWhiteList whiteList);
    
    /**
     * 通过卡信息获取白名单数据
     * @param whiteList
     * @return
     */
    public PojoTxnsWhiteList getByCardInfo(PojoTxnsWhiteList whiteList);
}
