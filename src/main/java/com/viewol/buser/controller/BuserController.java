package com.viewol.buser.controller;

import com.viewol.common.BaseResponse;
import com.viewol.common.GridBaseResponse;
import com.viewol.exhibitor.response.CompanyResponse;
import com.viewol.exhibitor.response.ErcodeResponse;
import com.viewol.pojo.BUser;
import com.viewol.service.IBUserService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.sys.log.annotation.MethodLog;
import com.viewol.sys.utils.Constants;
import com.youguu.core.util.PropertiesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

/**
 * 授权管理
 */
@Controller
@RequestMapping("buser")
public class BuserController {

    @Resource
    private IBUserService bUserService;

    @RequestMapping(value = "/buserList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse buserList() {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        List<BUser> list = bUserService.listByCom(TokenManager.getCompanyId());

        if (null != list && list.size() > 0) {
            rs.setData(list);
            rs.setCount(list.size());
        } else {
            rs.setCode(-1);
            rs.setMsg("暂无数据");
        }

        return rs;
    }

    @RequestMapping(value = "/review")
    @ResponseBody
    @MethodLog(module = Constants.BUSER, desc = "授权审核")
    @Repeat
    public BaseResponse review(@RequestParam(value = "userId", defaultValue = "-1") int userId,
                               @RequestParam(value = "status", defaultValue = "-1") int status) {
        BaseResponse rs = new BaseResponse();

        int result = bUserService.setStatus(userId, status);
        if (result > 0) {
            rs.setStatus(true);
            if (status == BUser.STATUS_TRIAL) {
                rs.setMsg("审核中");
            } else if (status == BUser.STATUS_OK) {
                rs.setMsg("通过");
            } else if (status == BUser.STATUS_BACK) {
                rs.setMsg("打回");
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("数据修改失败");
        }

        return rs;
    }

    /**
     * 获取业务员授权二维码
     * @return
     */
    @RequestMapping(value = "/loadErCode", method = RequestMethod.POST)
    @ResponseBody
    public ErcodeResponse loadErCode() {
        ErcodeResponse rs = new ErcodeResponse();
        Properties properties = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
            String buserUrl = properties.getProperty("buser.url");
            buserUrl = MessageFormat.format(buserUrl, TokenManager.getCompanyId());
            rs.setStatus(true);
            rs.setMsg("ok");
            rs.setErcode(buserUrl);
            return rs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        rs.setStatus(false);
        rs.setMsg("获取授权二维码失败");
        return rs;
    }
}
