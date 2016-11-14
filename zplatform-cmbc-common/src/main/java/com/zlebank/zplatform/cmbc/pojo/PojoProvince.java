
/* 
 * PojoProvince.java  
 * 
 * version TODO
 *
 * 2015年9月13日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.cmbc.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Class Description
 *
 * @author yangpeng
 * @version
 * @date 2015年9月13日 下午4:40:59
 * @since 
 */
/* 
 * PojoProvince.java  
 * 
 * version TODO
 *
 * 2015年9月13日 
 * 
 * Copyright (c) 2015,zlebank.All rights reserved.
 * 
 */
/**
 * Class Description
 *
 * @author yangpeng
 * @version
 * @date 2015年9月13日 下午4:40:59
 * @since 
 */
@Entity
@Table(name="T_PROVINCE")
public class PojoProvince implements Serializable{
    
    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4534621356920666598L;

	private long provinceId;
    
    private String provinceName;
    
    private String provinceCode;
    
    private String xzCode;
    
    @Id
    @Column(name="P_ID")
    public long getProvinceId() {
        return provinceId;
    }
    
    @Column(name="P_NAME")
    public String getProvinceName() {
        return provinceName;
    }
    @Column(name="P_CODE")
    public String getProvinceCode() {
        return provinceCode;
    }
    @Column(name="XZ_CODE")
    public String getXzCode() {
        return xzCode;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setXzCode(String xzCode) {
        this.xzCode = xzCode;
    }
    
    
    
}
