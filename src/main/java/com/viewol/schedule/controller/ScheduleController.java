package com.viewol.schedule.controller;

import com.viewol.common.BaseResponse;
import com.viewol.common.GridBaseResponse;
import com.viewol.pojo.Schedule;
import com.viewol.pojo.ScheduleUser;
import com.viewol.pojo.query.ScheduleQuery;
import com.viewol.schedule.response.ScheduleResponse;
import com.viewol.schedule.vo.ScheduleUserVO;
import com.viewol.schedule.vo.ScheduleVO;
import com.viewol.service.IScheduleService;
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

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 日程(活动)管理
 */
@Controller
@RequestMapping("schedule")
public class ScheduleController {

    @Resource
    private IScheduleService scheduleService;

    /**
     * 日程查询
     *
     * @return
     */
    @RequestMapping(value = "/scheduleList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse scheduleList(@RequestParam(value = "time", defaultValue = "") String time,
                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");
        ScheduleQuery scheduleQuery = new ScheduleQuery();
        if (null != time && !"".equals(time)) {
            scheduleQuery.setTime(time);
        }
        scheduleQuery.setCompanyId(TokenManager.getCompanyId());
        scheduleQuery.setType(Schedule.TYPE_COM);
        scheduleQuery.setPageIndex(page);
        scheduleQuery.setPageSize(limit);

        PageHolder<Schedule> pageHolder = scheduleService.querySchedule(scheduleQuery);

        List<ScheduleVO> voList = new ArrayList<>();
        if (null != pageHolder && null != pageHolder.getList() && pageHolder.getList().size() > 0) {
            for (Schedule schedule : pageHolder.getList()) {
                ScheduleVO vo = new ScheduleVO();
                vo.setId(schedule.getId());
                vo.setCompanyId(schedule.getCompanyId());
                vo.setType(schedule.getType());
                vo.setCompanyName(schedule.getCompanyName());
                vo.setTitle(schedule.getTitle());
                vo.setStatus(schedule.getStatus());
                vo.setContent(schedule.getContentView());
                vo.setPlace(schedule.getPlace());
                vo.setsTime(schedule.getsTime());
                vo.seteTime(schedule.geteTime());
                vo.setcTime(schedule.getcTime());

                voList.add(vo);
            }

            rs.setData(voList);
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }

    @RequestMapping(value = "/addSchedule", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.SCHEDULE, desc = "添加日程")
    @Repeat
    public BaseResponse addSchedule(@RequestParam(value = "title", defaultValue = "") String title,
                                    @RequestParam(value = "sTime", defaultValue = "") String sTime,
                                    @RequestParam(value = "eTime", defaultValue = "") String eTime,
                                    @RequestParam(value = "content", defaultValue = "") String content,
                                    @RequestParam(value = "place", defaultValue = "") String place) {

        BaseResponse rs = new BaseResponse();
        int companyId = TokenManager.getCompanyId();
        int result = scheduleService.applySchedule(companyId, title, place, content, sTime, eTime);
        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("添加成功");
        }  else if (result == -98) {
            rs.setStatus(false);
            rs.setMsg("暂未开通发布活动权限");
        } else if (result == -99) {
            rs.setStatus(false);
            rs.setMsg("有审核中的活动，暂时无法发布新活动");
        } else {
            rs.setStatus(false);
            rs.setMsg("添加失败");
        }

        return rs;
    }

    @RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.SCHEDULE, desc = "修改日程")
    @Repeat
    public BaseResponse updateSchedule(@RequestParam(value = "id", defaultValue = "-1") int id,
                                       @RequestParam(value = "title", defaultValue = "") String title,
                                       @RequestParam(value = "sTime", defaultValue = "") String sTime,
                                       @RequestParam(value = "eTime", defaultValue = "") String eTime,
                                       @RequestParam(value = "content", defaultValue = "") String content,
                                       @RequestParam(value = "place", defaultValue = "") String place) {

        BaseResponse rs = new BaseResponse();

        Schedule schedule = scheduleService.getSchedule(id);
        schedule.setTitle(title);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            schedule.setsTime(dft.parse(sTime));
            schedule.seteTime(dft.parse(eTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        schedule.setContentView(content);
        schedule.setPlace(place);
        int result = scheduleService.updateSchedule(schedule);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("修改成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("修改失败");
        }


        return rs;
    }

    @RequestMapping(value = "/deleteSchedule")
    @ResponseBody
    @MethodLog(module = Constants.SCHEDULE, desc = "删除日程")
    @Repeat
    public BaseResponse deleteSchedule(int id) {
        BaseResponse rs = new BaseResponse();
        int result = scheduleService.delSchedule(id);
        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("删除成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("删除失败");
        }
        return rs;
    }

    /**
     * 日程报名查询
     *
     * @return
     */
    @RequestMapping(value = "/scheduleUserList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse scheduleUserList(@RequestParam(value = "scheduleId", defaultValue = "-1") int scheduleId,
                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        PageHolder<ScheduleUser> pageHolder = scheduleService.queryScheduleUser(scheduleId, page, limit);

        List<ScheduleUserVO> voList = new ArrayList<>();
        if (null != pageHolder && null != pageHolder.getList() && pageHolder.getList().size() > 0) {
            for (ScheduleUser scheduleUser : pageHolder.getList()) {
                ScheduleUserVO vo = new ScheduleUserVO();
                vo.setUserId(scheduleUser.getUserId());
                vo.setUserName(scheduleUser.getUserName());
                vo.setPhone(scheduleUser.getPhone());
                vo.setCompany(scheduleUser.getCompany());
                vo.setPosition(scheduleUser.getPosition());
                vo.setEmail(scheduleUser.getEmail());
                vo.setAge(scheduleUser.getAge());
                vo.setReminderTime(scheduleUser.getReminderTime());
                vo.setcTime(scheduleUser.getcTime());
                vo.setReminderFlag(scheduleUser.getReminderFlag());
                voList.add(vo);
            }

            rs.setData(voList);
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }

    @RequestMapping(value = "/getSchedule")
    @ResponseBody
    public ScheduleResponse getSchedule(@RequestParam(value = "id", defaultValue = "0") int id) {

        ScheduleResponse rs = new ScheduleResponse();
        Schedule schedule = scheduleService.getSchedule(id);
        if (schedule!=null) {
            rs.setStatus(true);
            rs.setMsg("查询成功");

            ScheduleVO vo = new ScheduleVO();
            vo.setId(schedule.getId());
            vo.setCompanyId(schedule.getCompanyId());
            vo.setType(schedule.getType());
            vo.setCompanyName(schedule.getCompanyName());
            vo.setTitle(schedule.getTitle());
            vo.setStatus(schedule.getStatus());
            vo.setContent(schedule.getContentView());
            vo.setPlace(schedule.getPlace());
            vo.setsTime(schedule.getsTime());
            vo.seteTime(schedule.geteTime());
            vo.setcTime(schedule.getcTime());

            rs.setData(vo);
        } else {
            rs.setStatus(false);
            rs.setMsg("查询失败");
        }

        return rs;
    }
}
