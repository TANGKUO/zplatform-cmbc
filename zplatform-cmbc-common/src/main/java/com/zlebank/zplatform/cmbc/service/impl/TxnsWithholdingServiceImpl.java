package com.zlebank.zplatform.cmbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zlebank.zplatform.cmbc.common.bean.CMBCRealTimeWithholdingResultBean;
import com.zlebank.zplatform.cmbc.common.bean.ResultBean;
import com.zlebank.zplatform.cmbc.common.bean.TradeBean;
import com.zlebank.zplatform.cmbc.dao.TxnsWithholdingDAO;
import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;
import com.zlebank.zplatform.cmbc.service.TxnsWithholdingService;
@Service("txnsWithholdingService")
public class TxnsWithholdingServiceImpl implements TxnsWithholdingService{

    @Autowired
    private TxnsWithholdingDAO txnsWithholdingDAO;
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void saveWithholdingLog(PojoTxnsWithholding withholding) {
            txnsWithholdingDAO.saveEntity(withholding);
    }
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateRealNameResult(PojoTxnsWithholding withholding) {
    	txnsWithholdingDAO.updateRealNameResult(withholding);
    }
    
    
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateWhithholding(PojoTxnsWithholding withholding){
        txnsWithholdingDAO.update(withholding);
    }
    
    @Transactional(readOnly=true)
    public PojoTxnsWithholding getWithholdingBySerialNo(String serialno){
        return txnsWithholdingDAO.getWithholdingBySerialNo(serialno);
    }
    @Override
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void updateWithholdingLogError(PojoTxnsWithholding withholding) {
    	txnsWithholdingDAO.updateWithholdingLogError(withholding);
    }
    
    
}
