package com.viewol.exhibitor.vo;

import com.viewol.pojo.CompanyProgress;

import java.util.List;

/**
 * describe:
 *
 * @author: shi_lei@suixingpay.com
 * @date: 2019/07/14 21:52:21:52
 * @version: V1.0
 * @review:
 */
public class CompanyShowVO {
    private List<String> imgUrl;

    /**
     * 历程
     */
    private List<CompanyProgress> progresses;

    /**
     * 产品图片
     */
    private List<String> productUrl;

    private String showFlag;

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<CompanyProgress> getProgresses() {
        return progresses;
    }

    public void setProgresses(List<CompanyProgress> progresses) {
        this.progresses = progresses;
    }

    public List<String> getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(List<String> productUrl) {
        this.productUrl = productUrl;
    }

    public String getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(String showFlag) {
        this.showFlag = showFlag;
    }
}
