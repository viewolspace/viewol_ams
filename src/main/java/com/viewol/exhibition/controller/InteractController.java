package com.viewol.exhibition.controller;

import com.viewol.common.BaseResponse;
import com.viewol.common.GridBaseResponse;
import com.viewol.pojo.UserInteract;
import com.viewol.pojo.query.UserInteractQuery;
import com.viewol.service.IUserInteractService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.youguu.core.util.PageHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("interact")
public class InteractController {
    @Resource
    private IUserInteractService userInteractService;

    /**
     * @param classify 1 展商 2 产品
     * @param thirdId  展商ID 或者 产品ID
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/interactList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse interactList(@RequestParam(value = "classify", defaultValue = "-1") int classify,
                                         @RequestParam(value = "thirdId", defaultValue = "-1") int thirdId,
                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        UserInteractQuery userInteractQuery = new UserInteractQuery();
        userInteractQuery.setCompanyId(TokenManager.getCompanyId());
        if(classify!=99){
            userInteractQuery.setClassify(classify);
        }
        //1 围观 2 点赞 3 评论
        userInteractQuery.setType(3);
        userInteractQuery.setThirdId(thirdId);
        userInteractQuery.setPageIndex(page);
        userInteractQuery.setPageSize(limit);

        PageHolder<UserInteract> pageHolder = userInteractService.queryUserInteract(userInteractQuery);
        if (null != pageHolder && null != pageHolder.getList() && pageHolder.getList().size() > 0) {

            rs.setData(pageHolder.getList());
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }

    @RequestMapping(value = "/addInteract", method = RequestMethod.POST)
    @ResponseBody
    @Repeat
    public BaseResponse addInteract(@RequestParam(value = "id", defaultValue = "-1") int id,
                                    @RequestParam(value = "reply", defaultValue = "") String reply) {

        BaseResponse rs = new BaseResponse();

        int result = userInteractService.reply(id, reply);
        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("数据提交成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("数据提交失败");
        }

        return rs;
    }
}
