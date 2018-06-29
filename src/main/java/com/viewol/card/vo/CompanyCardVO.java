package com.viewol.card.vo;

import java.util.Date;

/**
 * 公司名片夹
 */
public class CompanyCardVO {
    private int id;
    private int bUserId;//业务员id
    private int companyId;///展商id
    private int userId;//客户id
    private String userName;//客户姓名
    private String userCompany;//客户公司
    private String userPhone;//客户电话
    private String userPosition;//客户职位
    private Date cTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getbUserId() {
        return bUserId;
    }

    public void setbUserId(int bUserId) {
        this.bUserId = bUserId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }

    public Date getcTime() {
        return cTime;
    }

    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
}
