package com.viewol.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.viewol.common.BaseResponse;
import com.viewol.pojo.CfpaCompany;
import com.viewol.pojo.CfpaProduct;
import com.viewol.pojo.Company;
import com.viewol.pojo.Product;
import com.viewol.service.CfpaService;
import com.viewol.service.ICompanyService;
import com.viewol.service.IProductService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.pojo.SysUser;
import com.viewol.sys.pojo.SysUserRole;
import com.viewol.sys.response.LoginResponse;
import com.viewol.sys.service.SysPermissionService;
import com.viewol.sys.service.SysUserRoleService;
import com.viewol.sys.service.SysUserService;
import com.viewol.sys.utils.SecurityCode;
import com.viewol.sys.utils.SecurityImage;
import com.youguu.core.util.MD5;
import com.youguu.core.util.PropertiesUtil;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.DisabledAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Resource
    private SysPermissionService sysPermissionService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private CfpaService cfpaService;
    @Resource
    private ICompanyService companyService;
    @Resource
    private IProductService productService;

    /**
     * 跳转到登录页面
     *
     * @return
     */
    @RequestMapping(value = "/toLogin")
    public String toLogin() {
        // 跳转到/page/login.jsp页面
        return "login";
    }

    /**
     * 实现用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "/submitLogin", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse Login(HttpServletRequest request, String userName, String password, String rememberMe, String checkcode) {
        LoginResponse rs = new LoginResponse();
        rs.setStatus(true);
        rs.setMsg("登录成功");

        String securityCode = (String) request.getSession().getAttribute("securityCode");
        if (null == securityCode || !securityCode.equals(checkcode)) {
            rs.setStatus(false);
            rs.setMsg("验证码输入错误");
            return rs;
        }

        password = new MD5().getMD5ofStr(password).toLowerCase();
        SysUser user = sysUserService.findSysUserByUserName(userName);
        if (null == user) {
            rs.setStatus(false);
            rs.setMsg("登录账号错误");
            logger.error("登录账号错误， userName=" + userName);
            return rs;
        }
        user.setUserName(userName);
        user.setPswd(password);

        boolean remember = false;
        if ("on".equals(rememberMe)) {
            remember = true;
        }
        try {
            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String expoIdStr = properties.getProperty("expoId");
            Integer expoId = Integer.parseInt(expoIdStr);
            user.setExpoId(expoId);
            TokenManager.login(user, true);
        } catch (DisabledAccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        } catch (AccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        } catch (IOException e) {
            rs.setStatus(false);
            rs.setMsg("展会ID配置异常");
        }

        if (rs.isStatus()) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", user.getId());
            userJson.put("name", user.getRealName());
            userJson.put("userName", userName);
            userJson.put("roleCode", user.getRoleCode());

            rs.setData(userJson);
        }
        return rs;
    }


    /**
     * 单点登录，统一信用代码登录
     *
     * @param userNum
     * @return
     */
    @RequestMapping(value = "/ssoLogin", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse ssoLogin(String userNum) {
        LoginResponse rs = new LoginResponse();
        rs.setStatus(true);
        rs.setMsg("登录成功");

        try {
            Company company = companyService.getCompanyByUserNum(userNum);
            if (null == company) {
                /**
                 * 1、实时同步展商信息
                 */
                CfpaCompany cfpaCompany = cfpaService.getCfpaCompany(userNum);

                Company cp = new Company();
                cp.setName(cfpaCompany.getZwgsmc());
                cp.setShortName(cfpaCompany.getZwgsmc());
                cp.setLogo("/" + cfpaCompany.getQyjsSrc());
                cp.setContent(cfpaCompany.getQyjj());
                cp.setProductNum(100);
                cp.setCanApply(1);
                cp.setPlace(cfpaCompany.getZwh());
                cp.setPlaceSvg(cfpaCompany.getZwh());
                cp.setcTime(new Date());
                cp.setUserNum(cfpaCompany.getTyshxydm());

                List<String> cList = new ArrayList<>();
                cList.add("00010009");
                int companyId = companyService.addCompany(2, cp, cList);

                cfpaService.downloadImg(cfpaCompany.getQyjsSrc());
                /**
                 * 2、给展商自动注册用户
                 */
                company = companyService.getCompanyByUserNum(userNum);
                SysUser sysUser = new SysUser();
                sysUser.setPswd(new MD5().getMD5ofStr("123456").toLowerCase());
                sysUser.setUserName(company.getName());
                sysUser.setRealName(company.getName());
                sysUser.setCompanyId(companyId);
                sysUser.setUserStatus(1);
                sysUser.setCreateTime(new Date());
                sysUser.setEmail("");
                sysUser.setPhone("");
                sysUserService.saveSysUser(sysUser);

                /**
                 * 3、给用户分配权限
                 */
                SysUserRole sysUserRole = new SysUserRole();
                //rid=8 展商管理员
                sysUserRole.setRid(8);
                sysUserRole.setUid(sysUser.getId());
                sysUserRole.setCreateTime(new Date());
                sysUserRoleService.saveSysUserRole(sysUserRole);

                /**
                 * 4、同步展商下的产品列表
                 */
                //统一信用代码（唯一标识）
                String tyshxydm = cfpaCompany.getTyshxydm();
                List<CfpaProduct> cfpaProductList = cfpaService.getCfpaProduct(tyshxydm);
                for (CfpaProduct cfpaProduct : cfpaProductList) {
                    Product product = new Product();
                    product.setName(cfpaProduct.getCplxmc());
                    product.setCompanyId(companyId);
                    product.setContent(cfpaProduct.getCpjj());
                    product.setImage("/" + cfpaProduct.getSrc());
                    product.setCategoryId("00020009");
                    //0上架
                    product.setStatus(0);
                    product.setcTime(new Date());
                    product.setUuid(cfpaProduct.getUuid());

                    productService.addProduct(2, product);
                    cfpaService.downloadImg(cfpaProduct.getSrc());
                }
            }
        } catch (Exception e) {
            rs.setStatus(false);
            rs.setMsg("登录失败原因：" + e.getMessage());
            logger.error("ssoLogin error", e);
            return rs;
        }

        /**
         * 5、同步远程数据后，再查查询展商表是否有“统一信用代码”对应的展商，有则放行；无则提示统一信用代码不存在。
         */
        Company company = companyService.getCompanyByUserNum(userNum);
        if (null == company) {
            rs.setStatus(false);
            rs.setMsg("统一信用代码错误");
            logger.error("统一信用代码错误， userNum=" + userNum);
            return rs;
        }

        /**
         * 6、查询到展商后，用户展商名称模拟登录过程。user为空，修改sys_user表数据
         */
        SysUser user = sysUserService.findSysUserByCompanyId(company.getId());
        if (null == user) {
            /**
             * 2、给展商自动注册用户
             */
            company = companyService.getCompanyByUserNum(userNum);
            SysUser sysUser = new SysUser();
            sysUser.setPswd(new MD5().getMD5ofStr("123456").toLowerCase());
            sysUser.setUserName(company.getName());
            sysUser.setRealName(company.getName());
            sysUser.setCompanyId(company.getId());
            sysUser.setUserStatus(1);
            sysUser.setCreateTime(new Date());
            sysUser.setEmail("");
            sysUser.setPhone("");
            sysUserService.saveSysUser(sysUser);

            /**
             * 3、给用户分配权限
             */
            SysUserRole sysUserRole = new SysUserRole();
            //rid=8 展商管理员
            sysUserRole.setRid(8);
            sysUserRole.setUid(sysUser.getId());
            sysUserRole.setCreateTime(new Date());
            sysUserRoleService.saveSysUserRole(sysUserRole);

            try {
                CfpaCompany fCompany = cfpaService.getCfpaCompany(userNum);
                company.setName(fCompany.getZwgsmc());
                company.setShortName(fCompany.getZwgsmc());
                company.setLogo("/" + fCompany.getQyjsSrc());
                if(StringUtils.isEmpty(company.getContent())){
                    company.setContent(fCompany.getQyjj());
                }
                company.setPlace(fCompany.getZwh());
                company.setPlaceSvg(fCompany.getZwh());
                logger.info("更新展商：" + JSON.toJSONString(company));
                companyService.updateByUserNum(company);
            } catch (Exception e) {
                logger.error("登录修改展商数据失败", e);
            }


            /**
             * 4、同步展商下的产品列表
             */
            //统一信用代码（唯一标识）
            List<CfpaProduct> cfpaProductList = cfpaService.getCfpaProduct(company.getUserNum());
            for (CfpaProduct cfpaProduct : cfpaProductList) {
                Product product = new Product();
                product.setName(cfpaProduct.getCplxmc());
                product.setCompanyId(company.getId());
                product.setContent(cfpaProduct.getCpjj());
                product.setImage("/" + cfpaProduct.getSrc());
                product.setCategoryId("00020009");
                //0上架
                product.setStatus(0);
                product.setcTime(new Date());
                product.setUuid(cfpaProduct.getUuid());

                productService.addProduct(2, product);
                cfpaService.downloadImg(cfpaProduct.getSrc());
            }
        }

        user = sysUserService.findSysUserByCompanyId(company.getId());

        if (null == user) {
            rs.setStatus(false);
            rs.setMsg("统一信用代码登录错误");
            logger.error("统一信用代码错误， userNum=" + company.getUserNum());
            return rs;
        }

        try {
            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String expoIdStr = properties.getProperty("expoId");
            //expoId展会ID，1-安防展；2-消防展
            Integer expoId = Integer.parseInt(expoIdStr);
            user.setExpoId(expoId);
            TokenManager.login(user, true);
        } catch (DisabledAccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        } catch (AccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        } catch (IOException e) {
            rs.setStatus(false);
            rs.setMsg("展会ID配置异常");
        }

        if (rs.isStatus()) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", user.getId());
            userJson.put("name", user.getRealName());
            userJson.put("userName", user.getRealName());
            userJson.put("roleCode", user.getRoleCode());

            rs.setData(userJson);
        }
        return rs;
    }

    /**
     * 解锁校验用户名，密码
     *
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse unlock(HttpServletRequest request, String userName, String password) {
        LoginResponse rs = new LoginResponse();
        rs.setStatus(true);
        rs.setMsg("解锁成功");

        password = new MD5().getMD5ofStr(password).toLowerCase();
        SysUser user = sysUserService.findSysUserByUserName(userName);
        if (null == user) {
            rs.setStatus(false);
            rs.setMsg("Session失效，请重新登录");
            return rs;
        }

        if (!password.equals(user.getPswd())) {
            rs.setStatus(false);
            rs.setMsg("密码不正确，请重新输入");
        }

        return rs;
    }

    /**
     * 登录时获取图形验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getValidImg")
    public synchronized void getValidImg(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("repeatData");
        String securityCode = SecurityCode.getSecurityCode();

        request.getSession().setAttribute("securityCode", securityCode);

        byte[] image = SecurityImage.getImageAsInputStream(securityCode.replaceAll("", " "));

        try {
            OutputStream os = response.getOutputStream();
            os.write(image);
            os.flush();
            os.close();
        } catch (IOException e) {
            //TODO
        }
    }

    /**
     * 退出
     *
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse logout(HttpServletRequest request) {
        BaseResponse rs = new BaseResponse();
        try {
            TokenManager.logout();

            rs.setStatus(true);
            rs.setMsg("OK");
        } catch (Exception e) {
            rs.setStatus(false);
            rs.setMsg("退出异常");
        }
        return rs;
    }

    /**
     * 注册并自动登录
     *
     * @param request
     * @param userName
     * @param password
     * @param repassword
     * @param email
     * @param phone
     * @param realName
     * @param checkcode
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse register(HttpServletRequest request, String userName, String password, String repassword,
                                  String email, String phone, String realName, String checkcode) {
        LoginResponse rs = new LoginResponse();
        rs.setStatus(true);
        rs.setMsg("注册成功");

        String registerCode = (String) request.getSession().getAttribute("registerCode");
        if (null == registerCode || !registerCode.equals(checkcode)) {
            rs.setStatus(false);
            rs.setMsg("验证码输入错误");
            return rs;
        }

        //注册用户
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        sysUser.setRealName(realName);
        sysUser.setEmail(email);
        sysUser.setPhone(phone);
        sysUser.setPswd(new MD5().getMD5ofStr(password).toLowerCase());
        sysUser.setUserStatus(1);
        sysUser.setCreateTime(new Date());
        int result = sysUserService.saveSysUser(sysUser);

        if (result > 0) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUid(result);
            userRole.setRid(2);//只注册了，没有开发权限
            userRole.setCreateTime(new Date());
            sysUserRoleService.saveSysUserRole(userRole);

            rs.setStatus(true);
            rs.setMsg("注册成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("注册失败");
        }


        if (!password.equals(repassword)) {
            rs.setStatus(false);
            rs.setMsg("两次输入密码不一致");
            return rs;
        }

        try {
            TokenManager.login(sysUser, true);
        } catch (DisabledAccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        } catch (AccountException e) {
            rs.setStatus(false);
            rs.setMsg(e.getMessage());
        }

        if (rs.isStatus()) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", sysUser.getId());
            userJson.put("name", sysUser.getRealName());
            userJson.put("userName", userName);
            userJson.put("roleCode", sysUser.getRoleCode());
            rs.setData(userJson);
        }
        return rs;
    }
}