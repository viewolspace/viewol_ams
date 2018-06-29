package com.viewol.exhibition.controller;

import com.viewol.common.BaseResponse;
import com.viewol.common.GridBaseResponse;
import com.viewol.exhibition.vo.ExhibitionVO;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.sys.log.annotation.MethodLog;
import com.viewol.sys.utils.Constants;
import com.youguu.core.util.PageHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 展品(产品)管理，最多上传五个展品
 */
@Controller
@RequestMapping("exhibition")
public class ExhibitionController {

    @RequestMapping(value = "/exhibitionList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse exhibitionList(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");


        PageHolder<ExhibitionVO> pageHolder = new PageHolder<>();
        ExhibitionVO vo = new ExhibitionVO();
        vo.setId(1);
        vo.setName("ABC");
        vo.setcTime(new Date());
        vo.setmTime(new Date());

        pageHolder.add(vo);
        pageHolder.setTotalCount(1);

        if (null != pageHolder) {
            rs.setData(pageHolder.getList());
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }

    @RequestMapping(value = "/addExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "添加展品")
    @Repeat
    public BaseResponse addExhibitor(@RequestParam(value = "title", defaultValue = "") String title,
                                     @RequestParam(value = "beginDate", defaultValue = "") String beginDate,
                                     @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                     @RequestParam(value = "rank", defaultValue = "1") int rank,
                                     @RequestParam(value = "forwardUrl", defaultValue = "") String forwardUrl,
                                     @RequestParam(value = "avatar", defaultValue = "") String adImage) {

        BaseResponse rs = new BaseResponse();


        return rs;
    }


    @RequestMapping(value = "/updateExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "修改展品")
    @Repeat
    public BaseResponse updateExhibition(@RequestParam(value = "title", defaultValue = "") String title,
                                     @RequestParam(value = "beginDate", defaultValue = "") String beginDate,
                                     @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                     @RequestParam(value = "rank", defaultValue = "1") int rank,
                                     @RequestParam(value = "forwardUrl", defaultValue = "") String forwardUrl,
                                     @RequestParam(value = "avatar", defaultValue = "") String adImage) {

        BaseResponse rs = new BaseResponse();


        return rs;
    }

    @RequestMapping(value = "/deleteExhibition")
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "删除展品")
    @Repeat
    public BaseResponse deleteExhibition(int id) {
        BaseResponse rs = new BaseResponse();
        rs.setStatus(true);
        rs.setMsg("删除成功");

        return rs;
    }
}
