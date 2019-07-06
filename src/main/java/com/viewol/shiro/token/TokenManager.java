package com.viewol.shiro.token;

import com.viewol.sys.pojo.SysUser;
import org.apache.shiro.SecurityUtils;

/**
 * Created by leo on 2017/11/27.
 */
public class TokenManager {

    public static SysUser getToken() {
        SysUser token = (SysUser) SecurityUtils.getSubject().getPrincipal();
        return token;
    }

    public static String getUserName() {
        return getToken() == null ? null : getToken().getUserName();
    }

    public static String getRealName() {
        return getToken() == null ? null : getToken().getRealName();
    }

    public static Integer getExpoId() {
        if (getToken() == null) {
            return -1;
        }

        if (0 == getToken().getExpoId()) {
            return -1;
        }
        return getToken().getExpoId();
    }

    public static Integer getUserId() {
        return getToken() == null ? null : getToken().getId();
    }

    public static Integer getCompanyId() {
        if (getToken() == null) {
            return -1;
        }

        if (0 == getToken().getCompanyId()) {
            return -1;
        }
        return getToken().getCompanyId();
    }

    public static Integer getRoleId() {
        return getToken() == null ? null : getToken().getRoleId();
    }


    public static SysUser login(SysUser user, Boolean rememberMe) {
        ShiroToken token = new ShiroToken(user.getUserName(), user.getPswd());
        token.setRememberMe(rememberMe);
        SecurityUtils.getSubject().login(token);
        return getToken();
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }
}
