package com.viewol.billboard.controller;

import com.viewol.billboard.response.BillBoardResponse;
import com.viewol.billboard.vo.AdExcelModel;
import com.viewol.common.BaseResponse;
import com.viewol.exhibitor.controller.ExhibitorController;
import com.viewol.pojo.AdMedia;
import com.viewol.service.IAdMediaService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.util.ExcelUtil;
import jdk.nashorn.internal.runtime.ECMAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("billBoard")
public class BillBoardController {
    private static final Logger logger = LoggerFactory.getLogger(BillBoardController.class);

    @Resource
    private IAdMediaService adMediaService;


    @RequestMapping(value = "/addBillBoard", method = RequestMethod.POST)
    @ResponseBody
    @Repeat
    public BaseResponse addBillBoard(
            @RequestParam(value = "dataHtml", defaultValue = "") String dataHtml,
            @RequestParam(value = "selectHtml", defaultValue = "") String selectHtml,
            @RequestParam(value = "userName", defaultValue = "") String userName,
            @RequestParam(value = "phone", defaultValue = "") String phone) {

        /*dataHtml = deleteAllCRLF(dataHtml);
        String[] dataHtmlArray = dataHtml.split("\\|");
        List<AdMedia> adMediaList = new ArrayList<>();
        for (String s : dataHtmlArray) {
            String[] row = s.split("@");
            if(row.length == 5){
                AdMedia adMedia = new AdMedia();
                adMedia.setShowRoom(row[0]); //展厅
                adMedia.setItemName(row[1]);  //项目名称
                adMedia.setNum(row[2]);   //序号
                adMedia.setSize(row[3]);  //尺寸
                adMedia.setPrice(row[4]); //价格
                adMedia.setPhone(phone); //联系电话
                adMedia.setUserName(name);  //联系人
                adMedia.setcTime(new Date()); //申请时间
            } else {

            }
        }*/
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/广告位.xlsx");
        List<AdExcelModel> excelList = (List<AdExcelModel>)ExcelUtil.readExcel(inputStream, AdExcelModel.class, 1, 1);

        String[] selectHtmlArr = selectHtml.split("\\|");
        List<AdMedia> adMediaList = new ArrayList<>();

        for (String s : selectHtmlArr) {
            String[] row = s.split("=");
            AdMedia adMedia = new AdMedia();
            adMedia.setItemName(row[0]);  //项目名称
            adMedia.setNum(row[1]);   //序号
            adMedia.setPhone(phone); //联系电话
            adMedia.setUserName(userName);  //联系人
            adMedia.setcTime(new Date()); //申请时间
            adMedia.setCompanyId(TokenManager.getCompanyId());
            adMedia.setCompanyName(TokenManager.getRealName());

            try {
                for (AdExcelModel adExcelModel : excelList) {
                    if(adMedia.getItemName().equals(adExcelModel.getItemName())){
                        List<String> numList = Arrays.asList(adExcelModel.getNum().split(","));
                        if(numList.contains(adMedia.getNum())){
                            adMedia.setShowRoom(adExcelModel.getShowRoom());
                            adMedia.setPrice(adExcelModel.getPrice());
                            adMedia.setSize(adExcelModel.getSize());
                        }
                    }
                }
            } catch (Exception e){
                logger.error("关联展厅，报价异常", e);
            }

            adMediaList.add(adMedia);
        }

        BaseResponse rs = new BaseResponse();

        if (adMediaList.size() > 0) {
            adMediaService.delAdmedia(TokenManager.getCompanyId());
            int result = adMediaService.addAdMedias(adMediaList);
            if (result > 0) {
                rs.setStatus(true);
                rs.setMsg("数据提交成功");
            } else {
                rs.setStatus(false);
                rs.setMsg("数据提交失败");
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("数据无更新");
        }

        return rs;
    }


    @RequestMapping(value = "/billboardList", method = RequestMethod.POST)
    @ResponseBody
    public BillBoardResponse billboardList() {

        BillBoardResponse rs = new BillBoardResponse();
        rs.setStatus(true);
        rs.setMsg("成功");

        List<AdMedia> list = adMediaService.listByCompanyId(TokenManager.getCompanyId());
        Map<String, List<String>> resultMap = new HashMap<>();
        for (AdMedia adMedia : list) {
            if(resultMap.containsKey(adMedia.getItemName())){
                resultMap.get(adMedia.getItemName()).add(adMedia.getNum());
            } else {
                List<String> values = new ArrayList<>();
                values.add(adMedia.getNum());
                resultMap.put(adMedia.getItemName(), values);
            }
            if(!StringUtils.isEmpty(adMedia.getPhone())){
                rs.setAdMedia(adMedia);
            }
        }
        rs.setData(resultMap);

        return rs;
    }

    /***
     * Delete all spaces
     *
     * @param input
     * @return
     */
    public static String deleteAllCRLF(String input) {
        return input.replaceAll("((\r\n)|\n)[\\s\t ]*", " ").replaceAll("^((\r\n)|\n)", "");

    }
}
