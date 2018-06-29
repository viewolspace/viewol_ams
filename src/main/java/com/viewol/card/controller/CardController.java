package com.viewol.card.controller;

import com.viewol.card.vo.CompanyCardVO;
import com.viewol.common.GridBaseResponse;
import com.viewol.shiro.token.TokenManager;
import com.youguu.core.util.PageHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 名片夹管理
 */
@Controller
@RequestMapping("card")
public class CardController {

    @RequestMapping(value = "/cardList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse cardList(@RequestParam(value = "userName", defaultValue = "") String userName,
                                     @RequestParam(value = "userCompany", defaultValue = "") String userCompany,
                                     @RequestParam(value = "userPhone", defaultValue = "") String userPhone,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        PageHolder<CompanyCardVO> pageHolder = new PageHolder<>();

        CompanyCardVO vo = new CompanyCardVO();
        vo.setId(1);
        vo.setbUserId(1);
        vo.setCompanyId(1);
        vo.setcTime(new Date());
        vo.setUserCompany("中科院");
        vo.setUserId(123);
        vo.setUserName("张三");
        vo.setUserPhone("15611118888");
        vo.setUserPosition("采购员");
        pageHolder.add(vo);
        pageHolder.setTotalCount(1);

        if (null != pageHolder) {
            rs.setData(pageHolder.getList());
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }
}
