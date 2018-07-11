package com.viewol.card.controller;

import com.viewol.card.vo.CompanyCardVO;
import com.viewol.common.GridBaseResponse;
import com.viewol.pojo.UserCardVO;
import com.viewol.pojo.query.UserCardQuery;
import com.viewol.service.IUserCardService;
import com.viewol.shiro.token.TokenManager;
import com.youguu.core.util.PageHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 名片夹管理
 */
@Controller
@RequestMapping("card")
public class CardController {

    @Resource
    private IUserCardService userCardService;

    /**
     *
     * @param fUserId 客户ID
     * @param bUserId 业务员ID
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/cardList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse cardList(@RequestParam(value = "fUserId", defaultValue = "") Integer fUserId,
                                     @RequestParam(value = "bUserId", defaultValue = "") Integer bUserId,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        UserCardQuery cardQuery = new UserCardQuery();
        cardQuery.setfUserId(fUserId);
        cardQuery.setCompanyId(TokenManager.getCompanyId());
        cardQuery.setbUserId(bUserId);
        cardQuery.setPageIndex(page);
        cardQuery.setPageSize(limit);

        PageHolder<UserCardVO> pageHolder = userCardService.queryUserCard(cardQuery);

        rs.setData(pageHolder.getList());
        rs.setCount(pageHolder.getTotalCount());
        return rs;
    }
}
