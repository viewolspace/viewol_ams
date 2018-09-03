package com.viewol.exhibition.vo;

import java.util.Date;

public class ExhibitionVO {
    private int id;
    private int companyId;//展商id
    private String categoryId;//分类id
    private int status;//0 上架  1 下架
    private String name;//产品名称
    private String image;//产品图片-用于列表展示
    private String regImage;//产品图片-用于首页推荐
    private String content;//产品介绍
    private String pdfUrl;//产品说明书
    private String pdfName;//说明书的名字
    private Date cTime;
    private Date mTime;
    private String ercode;//产品小程序码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRegImage() {
        return regImage;
    }

    public void setRegImage(String regImage) {
        this.regImage = regImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }

    public Date getmTime() {
        return mTime;
    }

    public void setmTime(Date mTime) {
        this.mTime = mTime;
    }

    public String getErcode() {
        return ercode;
    }

    public void setErcode(String ercode) {
        this.ercode = ercode;
    }
}
