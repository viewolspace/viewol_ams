package com.viewol.exhibitor.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.viewol.common.BaseResponse;
import com.viewol.common.LayeditResponse;
import com.viewol.common.UploadResponse;
import com.viewol.exhibitor.response.CompanyResponse;
import com.viewol.exhibitor.response.CompanyShowResponse;
import com.viewol.exhibitor.response.ErcodeResponse;
import com.viewol.exhibitor.response.ImgResult;
import com.viewol.exhibitor.vo.CompanyShowVO;
import com.viewol.exhibitor.vo.ExhibitorVO;
import com.viewol.pojo.Category;
import com.viewol.pojo.Company;
import com.viewol.pojo.CompanyProgress;
import com.viewol.pojo.CompanyShow;
import com.viewol.service.ICompanyService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.shiro.utils.StringUtils;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.sys.log.annotation.MethodLog;
import com.viewol.sys.utils.Constants;
import com.viewol.sys.utils.HtmlUtil;
import com.viewol.util.FilterHtmlUtil;
import com.viewol.util.WordsStatUtil;
import com.youguu.core.pojo.Response;
import com.youguu.core.util.HttpUtil;
import com.youguu.core.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * 展商信息管理
 */
@Controller
@RequestMapping("exhibitor")
public class ExhibitorController {

    private static final Logger logger = LoggerFactory.getLogger(ExhibitorController.class);
    @Resource
    private ICompanyService companyService;

    @RequestMapping(value = "/getExhibitor", method = RequestMethod.POST)
    @ResponseBody
    public CompanyResponse getExhibitor() {

        CompanyResponse rs = new CompanyResponse();

        Company company = companyService.getCompany(TokenManager.getCompanyId());
        if (null != company) {
            List<Category> categoryList = companyService.getCompanyCategory(company.getId());
            rs.setStatus(true);
            rs.setMsg("ok");

            ExhibitorVO vo = new ExhibitorVO();
            vo.setId(company.getId());
            vo.setName(company.getName());
            vo.setShortName(company.getShortName());
            vo.setContent(company.getContentView());
            vo.setLogo(company.getLogoView());
            vo.setBanner(company.getBannerView());
            vo.setImage(company.getImageView());
            vo.setPlace(company.getPlace());
            vo.setPlaceSvg(company.getPlaceSvg());
            vo.setProductNum(company.getProductNum());
            vo.setCanApply(company.getCanApply());
            vo.setIsRecommend(company.getIsRecommend());
            vo.setRecommendNum(company.getRecommendNum());
            vo.setcTime(company.getcTime());
            vo.setmTime(company.getmTime());

            Properties properties = null;
            try {
                properties = PropertiesUtil.getProperties("properties/config.properties");
                String inviteUrl = properties.getProperty("invite.url");
                inviteUrl = String.format(inviteUrl, company.getId());
                vo.setInviteErUrl(inviteUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }


            StringBuffer categoryNameBuffer = new StringBuffer();
            List<String> idsList = new ArrayList<>();
            if (null != categoryList && categoryList.size() > 0) {
                for (Category category : categoryList) {
                    idsList.add(category.getId());
                    categoryNameBuffer.append(category.getName()).append(",");
                }
                vo.setCategoryIds(idsList.toArray(new String[idsList.size()]));
                vo.setCategoryName(categoryNameBuffer.toString().substring(0, categoryNameBuffer.toString().length() - 1));
            } else {
                vo.setCategoryName("暂无分类");
            }
            rs.setData(vo);
        } else {
            rs.setStatus(false);
            rs.setMsg("展商不存在");
        }


        return rs;
    }

    /**
     * 修改展商分类
     *
     * @return
     */
    @RequestMapping(value = "/uploadCategory", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.EXHIBITOR, desc = "修改展商分类")
    @Repeat
    public BaseResponse uploadCategory(@RequestParam(value = "id", defaultValue = "-1") int id,
                                       @RequestParam(value = "categoryIds[]") String[] categoryIds) throws IOException {

        BaseResponse rs = new BaseResponse();

        //修改数据库图片地址
        Company company = companyService.getCompany(id);

        List<String> categoryIdList = Arrays.asList(categoryIds);
        int result = companyService.updateCompany(company, categoryIdList);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("修改成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("修改失败");
        }

        return rs;
    }

    /**
     * 上传展商Logo
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadLogo", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITOR, desc = "上传展商Logo")
    @Repeat
    public UploadResponse uploadLogo(@RequestParam(value = "id", defaultValue = "-1") int id,
                                     @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UploadResponse rs = new UploadResponse();

        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = dft.format(new Date()) + Integer.toHexString(new Random().nextInt()) + "." + myFileName.substring(myFileName.lastIndexOf(".") + 1);

            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("img.path");
            String imageUrl = properties.getProperty("imageUrl");

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            String midPath = yyyyMMdd.format(new Date());
            File fileDir = new File(path + midPath);
            if (!fileDir.exists()) { //如果不存在 则创建
                fileDir.mkdirs();
            }
            path = path + midPath + File.separator + fileName;
            File localFile = new File(path);
            try {
                file.transferTo(localFile);

                rs.setStatus(true);
                rs.setMsg("上传成功");
                String httpUrl = imageUrl + File.separator + midPath + File.separator + fileName;
                rs.setImageUrl(httpUrl);

                //修改数据库图片地址
                Company company = companyService.getCompany(id);
                company.setLogoView(httpUrl);
                company.setmTime(new Date());
                int result = companyService.updateCompany(company, null);

            } catch (IllegalStateException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
                logger.error("uploadLogo", e);
            } catch (IOException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
                logger.error("uploadLogo", e);
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("文件为空");
        }

        return rs;
    }

    /**
     * 上传展商形象图
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadBanner", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITOR, desc = "上传展商形象图")
    @Repeat
    public UploadResponse uploadBanner(@RequestParam(value = "id", defaultValue = "-1") int id,
                                       @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UploadResponse rs = new UploadResponse();

        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = dft.format(new Date()) + Integer.toHexString(new Random().nextInt()) + "." + myFileName.substring(myFileName.lastIndexOf(".") + 1);

            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("img.path");
            String imageUrl = properties.getProperty("imageUrl");

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            String midPath = yyyyMMdd.format(new Date());
            File fileDir = new File(path + midPath);
            if (!fileDir.exists()) { //如果不存在 则创建
                fileDir.mkdirs();
            }
            path = path + midPath + File.separator + fileName;
            File localFile = new File(path);
            try {
                file.transferTo(localFile);

                rs.setStatus(true);
                rs.setMsg("上传成功");
                String httpUrl = imageUrl + File.separator + midPath + File.separator + fileName;
                rs.setImageUrl(httpUrl);

                //修改数据库图片地址
                Company company = companyService.getCompany(id);
                company.setBannerView(httpUrl);
                company.setmTime(new Date());
                int result = companyService.updateCompany(company, null);

            } catch (IllegalStateException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
                logger.error("uploadBanner", e);
            } catch (IOException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
                logger.error("uploadBanner", e);
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("文件为空");
        }

        return rs;
    }


    /**
     * 上传展商图片
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITOR, desc = "上传展商图片")
    @Repeat
    public UploadResponse uploadImage(@RequestParam(value = "id", defaultValue = "-1") int id,
                                      @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UploadResponse rs = new UploadResponse();

        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = dft.format(new Date()) + Integer.toHexString(new Random().nextInt()) + "." + myFileName.substring(myFileName.lastIndexOf(".") + 1);

            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("img.path");
            String imageUrl = properties.getProperty("imageUrl");

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            String midPath = yyyyMMdd.format(new Date());
            File fileDir = new File(path + midPath);
            if (!fileDir.exists()) { //如果不存在 则创建
                fileDir.mkdirs();
            }
            path = path + midPath + File.separator + fileName;
            File localFile = new File(path);
            try {
                file.transferTo(localFile);

                rs.setStatus(true);
                rs.setMsg("上传成功");
                String httpUrl = imageUrl + File.separator + midPath + File.separator + fileName;
                rs.setImageUrl(httpUrl);

                //修改数据库图片地址
                Company company = companyService.getCompany(id);
                company.setImageView(httpUrl);
                company.setmTime(new Date());
                int result = companyService.updateCompany(company, null);

            } catch (IllegalStateException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
            } catch (IOException e) {
                rs.setStatus(false);
                rs.setMsg("服务器异常");
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("文件为空");
        }

        return rs;
    }

    /**
     * 修改展商介绍
     *
     * @param id
     * @param content
     * @return
     */
    @RequestMapping(value = "/updateContent", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITOR, desc = "修改展商介绍")
    @Repeat
    public BaseResponse updateContent(@RequestParam(value = "id", defaultValue = "-1") int id,
                                      @RequestParam(value = "content", defaultValue = "") String content) {

        BaseResponse rs = new BaseResponse();
        int count = WordsStatUtil.wordCount(FilterHtmlUtil.html2Text(content));
        if(count>80){
            rs.setStatus(false);
            rs.setMsg("展商介绍不能多余80字");
            return rs;
        }
        Company company = companyService.getCompany(id);

        company.setContentView(HtmlUtil.stringFilter(content));
        company.setmTime(new Date());

        int result = companyService.updateCompany(company, null);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("修改成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("修改失败");
        }
        return rs;
    }


    /**
     * 展商富文本上传图片
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadContentImage", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITOR, desc = "展商富文本上传图片")
    @Repeat
    public LayeditResponse uploadContentImage(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        LayeditResponse rs = new LayeditResponse();

        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = dft.format(new Date()) + Integer.toHexString(new Random().nextInt()) + "." + myFileName.substring(myFileName.lastIndexOf(".") + 1);

            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("img.path");
            String imageUrl = properties.getProperty("imageUrl");

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            String midPath = yyyyMMdd.format(new Date());
            File fileDir = new File(path + midPath);
            if (!fileDir.exists()) { //如果不存在 则创建
                fileDir.mkdirs();
            }
            path = path + midPath + File.separator + fileName;
            File localFile = new File(path);
            try {
                file.transferTo(localFile);

                rs.setCode(0);
                rs.setMsg("上传成功");
                String httpUrl = imageUrl + File.separator + midPath + File.separator + fileName;
                Map<String, String> map = new HashMap<>();
                map.put("src", httpUrl);


                rs.setData(map);

            } catch (IllegalStateException e) {
                rs.setCode(1);
                rs.setMsg("服务器异常");
            } catch (IOException e) {
                rs.setCode(1);
                rs.setMsg("服务器异常");
            }
        } else {
            rs.setCode(1);
            rs.setMsg("文件为空");
        }

        return rs;
    }

    /**
     * 获取展商小程序码
     *
     * @return
     */
    @RequestMapping(value = "/getCompanyMaErCode", method = RequestMethod.GET)
    @ResponseBody
    public ErcodeResponse getCompanyMaErCode(@RequestParam(value = "width", defaultValue = "430") int width) {
        ErcodeResponse rs = new ErcodeResponse();

        Properties properties = null;
        String url = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
            url = properties.getProperty("company.ercode.url");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (url == null || "".equals(url)) {
            rs.setStatus(false);
            rs.setMsg("小程序码URL未配置");
            return rs;
        }

        Map<String, String> params = new HashMap<>();
        //小程序 1 观展通  3 观展讯
        params.put("maNum", "3");
        params.put("type", "2");
        params.put("companyId", String.valueOf(TokenManager.getCompanyId()));
        params.put("bUserId", "0");
        params.put("width", String.valueOf(width));

        Response<String> response = HttpUtil.sendPost(url, params, "UTF-8");

        if ("0000".equals(response.getCode())) {
            String result = response.getT();
            JSONObject object = JSONObject.parseObject(result);
            if ("0000".equals(object.getString("status"))) {
                String ercode = object.getString("ercode");

                rs.setStatus(true);
                rs.setMsg("ok");
                rs.setErcode(ercode);
            } else {
                rs.setStatus(false);
                rs.setMsg("获取小程序码失败");
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("获取小程序码失败");
        }

        return rs;
    }

    /**
     * 获取展商邀请函小程序码
     *
     * @return
     */
    @RequestMapping(value = "/getInviteMaErCode", method = RequestMethod.GET)
    @ResponseBody
    public ErcodeResponse getInviteMaErCode(@RequestParam(value = "width", defaultValue = "430") int width) {
        ErcodeResponse rs = new ErcodeResponse();

        Properties properties = null;
        String url = null;
        try {
            properties = PropertiesUtil.getProperties("properties/config.properties");
            url = properties.getProperty("company.ercode.url");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (url == null || "".equals(url)) {
            rs.setStatus(false);
            rs.setMsg("小程序码URL未配置");
            return rs;
        }

        Map<String, String> params = new HashMap<>();
        //小程序 1 观展通  3 观展讯
        params.put("maNum", "3");
        params.put("type", "2");
        params.put("companyId", String.valueOf(TokenManager.getCompanyId()));
        params.put("bUserId", "0");
        params.put("width", String.valueOf(width));

        Response<String> response = HttpUtil.sendPost(url, params, "UTF-8");

        if ("0000".equals(response.getCode())) {
            String result = response.getT();
            JSONObject object = JSONObject.parseObject(result);
            if ("0000".equals(object.getString("status"))) {
                String ercode = object.getString("ercode");

                rs.setStatus(true);
                rs.setMsg("ok");
                rs.setErcode(ercode);
            } else {
                rs.setStatus(false);
                rs.setMsg("获取小程序码失败");
            }
        } else {
            rs.setStatus(false);
            rs.setMsg("获取小程序码失败");
        }

        return rs;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ImgResult uplpad(MultipartFile file, HttpServletRequest request) {
        String desFilePath = "";
        String oriName = "";
        ImgResult result = new ImgResult();
        Map<String, String> dataMap;
        ImgResult imgResult = new ImgResult();
        try {
            // 1.获取原文件名
            oriName = file.getOriginalFilename();
            // 2.获取原文件图片后缀，以最后的.作为截取(.jpg)
            String extName = oriName.substring(oriName.lastIndexOf("."));
            // 3.生成自定义的新文件名，这里以UUID.jpg|png|xxx作为格式（可选操作，也可以不自定义新文件名）
            String uuid = UUID.randomUUID().toString();
            String newName = uuid + extName;
            // 4.获取要保存的路径文件夹
            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("img.path");
            String imageUrl = properties.getProperty("imageUrl");
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            String midPath = yyyyMMdd.format(new Date());
            File fileDir = new File(path + midPath);
            if (!fileDir.exists()) { //如果不存在 则创建
                fileDir.mkdirs();
            }
            // 5.保存图片
            desFilePath = path + midPath + File.separator + newName;
            File desFile = new File(desFilePath);
            file.transferTo(desFile);

            System.out.println(desFilePath);
            // 6.返回保存结果信息
            result.setCode(0);
            dataMap = new HashMap<>();

            String httpUrl = imageUrl + File.separator + midPath + File.separator + newName;

            dataMap.put("src", httpUrl);
            result.setData(dataMap);
            result.setMsg(oriName + "上传成功！");
            return result;
        } catch (IllegalStateException e) {
            imgResult.setCode(1);
            System.out.println(desFilePath + "图片保存失败");
            return imgResult;
        } catch (IOException e) {
            imgResult.setCode(1);
            System.out.println(desFilePath + "图片保存失败--IO异常");
            return imgResult;
        }
    }

    @RequestMapping(value = "/updateShow", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse updateShow(@RequestParam(value = "publicityImgUrls", defaultValue = "") String publicityImgUrls,
                                   @RequestParam(value = "productImgUrls", defaultValue = "") String productImgUrls,
                                   @RequestParam(value = "progress", defaultValue = "") String progress,
                                   @RequestParam(value = "showFlag", defaultValue = "") String showFlag) {

        BaseResponse rs = new BaseResponse();
        List<String> imgUrl = new ArrayList<>(); //宣传图
        List<CompanyProgress> progresses = JSONArray.parseArray(progress, CompanyProgress.class);   //历程
        List<String> productUrl = new ArrayList<>();    //产品图片

        if (!StringUtils.isEmpty(publicityImgUrls)) {
            String[] imgUrlArray = publicityImgUrls.split(",");
            for (int i = 0; i < imgUrlArray.length; i++) {
                if (!StringUtils.isEmpty(imgUrlArray[i]) && imgUrlArray[i].startsWith("http")) {
                    imgUrl.add(imgUrlArray[i]);
                }
            }
        }

        if (!StringUtils.isEmpty(productImgUrls)) {
            String[] productUrlArray = productImgUrls.split(",");
            for (int i = 0; i < productUrlArray.length; i++) {
                if (!StringUtils.isEmpty(productUrlArray[i]) && productUrlArray[i].startsWith("http")) {
                    productUrl.add(productUrlArray[i]);
                }
            }
        }

        CompanyShow companyShow = new CompanyShow();
        companyShow.setImgUrl(imgUrl);
        companyShow.setProgresses(progresses);
        companyShow.setProductUrl(productUrl);
        companyShow.setShowFlag(showFlag);

        int companyId = TokenManager.getCompanyId();
        String json = JSONObject.toJSONString(companyShow);
        logger.info("修改展商秀：入参={}", json);
        int result = companyService.updateShow(companyId, json);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("修改成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("修改失败");
        }
        return rs;
    }

    @RequestMapping(value = "/getShow", method = RequestMethod.POST)
    @ResponseBody
    public CompanyShowResponse getShow() {

        CompanyShowResponse rs = new CompanyShowResponse();

        Company company = companyService.getCompany(TokenManager.getCompanyId());
        if (null != company) {
            String showInfo = company.getShowInfo();
            CompanyShowVO showVO = JSONObject.parseObject(showInfo, CompanyShowVO.class);
            rs.setStatus(true);
            rs.setMsg("ok");
            rs.setData(showVO);

        } else {
            rs.setStatus(false);
            rs.setMsg("展商不存在");
        }

        return rs;
    }
}
