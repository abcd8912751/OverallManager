package com.furja.common;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料信息用于保存物料代码,物料内码、名称及规格等信息
 */

public class MaterialInfo {
    private String materialId;
    private String materialName;
    private String norm;
    private String materialISN;//物料内码
    private List<String> urls;
    private String produceNo;
    public MaterialInfo() {

    }

    public MaterialInfo(MaterialJson json) {
        formatJsonBean(json);
    }

    private void formatJsonBean(MaterialJson json) {
        MaterialJson.ErrDataBean dataBean=json.getErrData();
        if(dataBean==null)
            throw new NullPointerException();
        else {
            this.urls = new ArrayList<String>();
            setMaterialId(dataBean.getFShortNumber());
            setMaterialName(dataBean.getFName());
            setNorm(dataBean.getFModel());
            setMaterialISN(dataBean.getFItemID());
            setProduceNo(dataBean.getFSCDNO());
            List<MaterialJson.ErrDataBean.FUrlBean> urlBeans=dataBean.getFUrl();
            if(urlBeans!=null){
                for (MaterialJson.ErrDataBean.FUrlBean urlBean:urlBeans) {
                    String url = urlBean.getUrl();
                    if(!TextUtils.isEmpty(url))
                        urls.add(urlBean.getUrl());
                }
            }
        }
    }

    /**
     * 将取得的json格式化为该类
     * @param json
     */
    public void formatJson(String json) throws Exception {
        MaterialJson materialJson= JSON.parseObject(json,MaterialJson.class);
        formatJsonBean(materialJson);
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    public String getProduceNo() {
        return produceNo;
    }

    public void setProduceNo(String produceNo) {
        this.produceNo = produceNo;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }
}

