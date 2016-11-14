package com.zlebank.zplatform.cmbc.service;

import com.zlebank.zplatform.cmbc.pojo.PojoTxnsWithholding;

public interface TxnsWithholdingService{

    /**
     * 保存代扣业务流水
     * @param withholding 代扣交易实体类
     * @throws TradeException
     */
    public void saveWithholdingLog(PojoTxnsWithholding withholding);
    
    /**
     * 更新实名认证结果
     * @param withholding
     * @throws TradeException
     */
    public void updateRealNameResult(PojoTxnsWithholding withholding);
    
    /**
     * 通过流水号获取代扣实体类
     * @param serialno
     * @return
     */
    public PojoTxnsWithholding getWithholdingBySerialNo(String serialno);
    
    /**
     * 更新代扣交易异常信息
     * @param withholding
     */
    public void updateWithholdingLogError(PojoTxnsWithholding withholding);
    
    /**
     * 更新代付交易结果
     * @param withholding
     */
    public void updateWhithholding(PojoTxnsWithholding withholding);
}
