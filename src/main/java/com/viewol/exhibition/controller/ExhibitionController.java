package com.viewol.exhibition.controller;

import com.alibaba.fastjson.JSONObject;
import com.viewol.common.BaseResponse;
import com.viewol.common.GridBaseResponse;
import com.viewol.common.LayeditResponse;
import com.viewol.common.UploadPdfResponse;
import com.viewol.common.UploadResponse;
import com.viewol.exhibition.response.ExhibitionCategoryResponse;
import com.viewol.exhibition.response.ExhibitionResponse;
import com.viewol.exhibition.response.ProductIdeaResponse;
import com.viewol.exhibition.vo.ExhibitionCategoryVO;
import com.viewol.exhibition.vo.ExhibitionVO;
import com.viewol.pojo.Category;
import com.viewol.pojo.Company;
import com.viewol.pojo.Product;
import com.viewol.pojo.ProductIdea;
import com.viewol.pojo.query.ProductQuery;
import com.viewol.service.ICategoryService;
import com.viewol.service.ICompanyService;
import com.viewol.service.IProductIdeaService;
import com.viewol.service.IProductService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.sys.log.annotation.MethodLog;
import com.viewol.sys.utils.Constants;
import com.viewol.sys.utils.HtmlUtil;
import com.youguu.core.pojo.Response;
import com.youguu.core.util.HttpUtil;
import com.youguu.core.util.PageHolder;
import com.youguu.core.util.PropertiesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * 展品(产品)管理，最多上传五个展品
 */
@Controller
@RequestMapping("exhibition")
public class ExhibitionController {
    @Resource
    private IProductService productService;
    @Resource
    private ICategoryService categoryService;
    @Resource
    private IProductIdeaService productIdeaService;
    @Resource
    private ICompanyService companyService;

    @RequestMapping(value = "/exhibitionList", method = RequestMethod.POST)
    @ResponseBody
    public GridBaseResponse exhibitionList(@RequestParam(value = "page", defaultValue = "1") int page,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        GridBaseResponse rs = new GridBaseResponse();
        rs.setCode(0);
        rs.setMsg("ok");

        ProductQuery productQuery = new ProductQuery();
        productQuery.setCategoryId(null);
        productQuery.setCompanyId(TokenManager.getCompanyId());
        productQuery.setName(null);
        productQuery.setStatus(null);
        productQuery.setPageIndex(page);
        productQuery.setPageSize(limit);
        productQuery.setExpoId(TokenManager.getExpoId());

        PageHolder<Product> pageHolder = productService.queryProduct(productQuery);
        List<ExhibitionVO> voList = new ArrayList<>();

        if (null != pageHolder && null != pageHolder.getList() && pageHolder.getList().size() > 0) {
            for (Product product : pageHolder.getList()) {
                ExhibitionVO vo = new ExhibitionVO();
                vo.setId(product.getId());
                vo.setName(product.getName());
                vo.setCompanyId(product.getCompanyId());
                vo.setCategoryId(product.getCategoryId());
                vo.setStatus(product.getStatus());
                vo.setPdfName(product.getPdfName());
                vo.setPdfUrl(product.getPdfUrlView());
                vo.setImage(product.getImageView());
                vo.setRegImage(product.getReImgView());
                vo.setcTime(product.getcTime());
                vo.setmTime(product.getmTime());

                voList.add(vo);
            }

            rs.setData(voList);
            rs.setCount(pageHolder.getTotalCount());
        }

        return rs;
    }

    /**
     * 根据产品ID查询产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getExhibition", method = RequestMethod.POST)
    @ResponseBody
    public ExhibitionResponse getExhibition(@RequestParam(value = "id", defaultValue = "") int id) {
        ExhibitionResponse rs = new ExhibitionResponse();
        Product product = productService.getProduct(id);

        if (null != product) {
            ExhibitionVO vo = new ExhibitionVO();
            vo.setId(product.getId());
            vo.setName(product.getName());
            vo.setCompanyId(product.getCompanyId());
            vo.setCategoryId(product.getCategoryId());
            vo.setStatus(product.getStatus());
            vo.setPdfName(product.getPdfName());
            vo.setPdfUrl(product.getPdfUrl());
            vo.setImage(product.getImageView());
            vo.setRegImage(product.getReImgView());
            vo.setContent(product.getContentView());
            vo.setcTime(product.getcTime());
            vo.setmTime(product.getmTime());

            //查询产品小程序码
            Properties properties = null;
            String url = null;
            try {
                properties = PropertiesUtil.getProperties("properties/config.properties");
                url = properties.getProperty("product.ercode.url");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (url == null || "".equals(url)) {
                rs.setStatus(false);
                rs.setMsg("小程序码URL未配置");
                return rs;
            }

            Map<String, String> params = new HashMap<>();
            params.put("type", "1");
            params.put("companyId", String.valueOf(TokenManager.getCompanyId()));
            params.put("productId", String.valueOf(vo.getId()));
            params.put("width", "150");

            Response<String> response = HttpUtil.sendPost(url, params, "UTF-8");

            if ("0000".equals(response.getCode())) {
                String result = response.getT();
                JSONObject object = JSONObject.parseObject(result);
                if ("0000".equals(object.getString("status"))) {
                    String ercode = object.getString("ercode");
                    vo.setErcode(ercode);
                }
            }
            rs.setStatus(true);
            rs.setMsg("ok");
            rs.setData(vo);
        } else {
            rs.setStatus(false);
            rs.setMsg("无此产品");
        }
        return rs;
    }

    /**
     * 添加产品
     *
     * @param name
     * @param ids
     * @param imageAvatar    产品列表图片
     * @param regImageAvatar 产品首页推荐图片地址
     * @param content
     * @param pdfName
     * @param pdfUrl
     * @return
     */
    @RequestMapping(value = "/addExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.EXHIBITION, desc = "添加展品")
    @Repeat
    public BaseResponse addExhibition(@RequestParam(value = "name", defaultValue = "") String name,
                                      @RequestParam(value = "ids[]") String[] ids,
                                      @RequestParam(value = "imageAvatar", defaultValue = "") String imageAvatar,
                                      @RequestParam(value = "regImageAvatar", defaultValue = "") String regImageAvatar,
                                      @RequestParam(value = "content", defaultValue = "1") String content,
                                      @RequestParam(value = "pdfName", defaultValue = "") String pdfName,
                                      @RequestParam(value = "pdfUrl", defaultValue = "") String pdfUrl) {

        BaseResponse rs = new BaseResponse();
        Product product = new Product();
        product.setCompanyId(TokenManager.getCompanyId());
        product.setCategoryId(ids[0]);
        product.setStatus(Product.STATUS_ON);//默认上架
        product.setName(name);
        product.setImageView(imageAvatar);

        product.setContentView(HtmlUtil.stringFilter(content));
        product.setPdfUrlView(pdfUrl);
        product.setPdfName(pdfName);
        product.setmTime(new Date());
        product.setcTime(new Date());
        product.setReImgView(regImageAvatar);

        product.setIsRecommend(0);//默认非推荐
        product.setRecommendNum(0);//推荐顺序默认0
        int expoId = TokenManager.getExpoId();//展会ID
        int result = productService.addProduct(expoId, product);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("保存成功");
        } else if (result == -99) {
            rs.setStatus(false);
            rs.setMsg("超过允许添加产品的上限");
        } else if (result == -98) {
            rs.setStatus(false);
            rs.setMsg("展商不存在");
        } else {
            rs.setStatus(false);
            rs.setMsg("保存失败");
        }

        return rs;
    }


    @RequestMapping(value = "/updateExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.EXHIBITION, desc = "修改展品")
    @Repeat
    public BaseResponse updateExhibition(@RequestParam(value = "id", defaultValue = "") int id,
                                         @RequestParam(value = "name", defaultValue = "") String name,
                                         @RequestParam(value = "ids[]") String[] ids,
                                         @RequestParam(value = "imageAvatar", defaultValue = "") String imageAvatar,
                                         @RequestParam(value = "regImageAvatar", defaultValue = "") String regImageAvatar,
                                         @RequestParam(value = "content", defaultValue = "1") String content,
                                         @RequestParam(value = "pdfName", defaultValue = "") String pdfName,
                                         @RequestParam(value = "pdfUrl", defaultValue = "") String pdfUrl) {

        BaseResponse rs = new BaseResponse();
        Product product = productService.getProduct(id);
        product.setCategoryId(ids[0]);
        product.setName(name);
        product.setImageView(imageAvatar);
        product.setContentView(HtmlUtil.stringFilter(content));
        product.setPdfUrlView(pdfUrl);
        product.setPdfName(pdfName);
        product.setmTime(new Date());
        product.setReImgView(regImageAvatar);
        int result = productService.updateProduct(product);

        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("修改成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("修改失败");
        }

        return rs;
    }

    @RequestMapping(value = "/deleteExhibition")
    @ResponseBody
    @MethodLog(module = Constants.EXHIBITION, desc = "删除展品")
    @Repeat
    public BaseResponse deleteExhibition(@RequestParam(value = "id", defaultValue = "") int id) {
        BaseResponse rs = new BaseResponse();

        int result = productService.delProduct(id);
        if (result > 0) {
            rs.setStatus(true);
            rs.setMsg("删除成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("删除失败");
        }
        return rs;
    }

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITION, desc = "上传产品图片")
    @Repeat
    public UploadResponse uploadImg(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

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
     * 展品富文本上传图片
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadContentImage", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITION, desc = "展商富文本上传图片")
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


    @RequestMapping(value = "/uploadPdf", method = RequestMethod.POST)
    @ResponseBody
//    @MethodLog(module = Constants.EXHIBITION, desc = "上传产品PDF")
    @Repeat
    public UploadPdfResponse uploadPdf(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UploadPdfResponse rs = new UploadPdfResponse();

        if (null != file) {
            String myFileName = file.getOriginalFilename();// 文件原名称
            SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = dft.format(new Date()) + Integer.toHexString(new Random().nextInt()) + "." + myFileName.substring(myFileName.lastIndexOf(".") + 1);

            Properties properties = PropertiesUtil.getProperties("properties/config.properties");
            String path = properties.getProperty("pdf.path");
            String pdfUrl = properties.getProperty("pdfUrl");

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
                String httpUrl = pdfUrl + File.separator + midPath + File.separator + fileName;
                rs.setPdfUrl(httpUrl);

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
     * 查询产品所属分类
     *
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/getExhibitionCategory", method = RequestMethod.GET)
    @ResponseBody
    public ExhibitionCategoryResponse getExhibitionCategory(@RequestParam(value = "categoryId", defaultValue = "") String categoryId) {
        ExhibitionCategoryResponse rs = new ExhibitionCategoryResponse();
        Category category = categoryService.getCategory(categoryId);
        if (null != category) {
            List<String> idsList = new ArrayList<>();
            List<String> namesList = new ArrayList<>();

            idsList.add(category.getId());
            namesList.add(category.getName());

            ExhibitionCategoryVO vo = new ExhibitionCategoryVO();
            vo.setIds(null);
            vo.setCategoryNames(null);
            vo.setIds(idsList.toArray(new String[idsList.size()]));
            vo.setCategoryNames((namesList.toArray(new String[namesList.size()])));

            rs.setStatus(true);
            rs.setMsg("ok");
            rs.setData(vo);
        } else {
            rs.setStatus(false);
            rs.setMsg("无数据");
        }

        return rs;
    }

    @RequestMapping(value = "/addProductIdea", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.EXHIBITION, desc = "申请创新产品")
    @Repeat
    public BaseResponse addProductIdea(@RequestParam(value = "productId", defaultValue = "") int productId,
                                       @RequestParam(value = "productName", defaultValue = "") String productName,
                                       @RequestParam(value = "companyId", defaultValue = "") int companyId,
                                       @RequestParam(value = "companyName", defaultValue = "") String companyName,
                                       @RequestParam(value = "companyPlace", defaultValue = "") String companyPlace,
                                       @RequestParam(value = "liaisonMan", defaultValue = "") String liaisonMan,
                                       @RequestParam(value = "phone", defaultValue = "") String phone,
                                       @RequestParam(value = "landLine", defaultValue = "") String landLine,
                                       @RequestParam(value = "website", defaultValue = "") String website,
                                       @RequestParam(value = "email", defaultValue = "") String email,
                                       @RequestParam(value = "categoryId", defaultValue = "") String categoryId,
                                       @RequestParam(value = "logo", defaultValue = "") String logo,
                                       @RequestParam(value = "des", defaultValue = "") String des,
                                       @RequestParam(value = "quota", defaultValue = "") String quota,
                                       @RequestParam(value = "ideaPoint", defaultValue = "") String ideaPoint,
                                       @RequestParam(value = "extend", defaultValue = "") String extend,
                                       @RequestParam(value = "productPic", defaultValue = "") String productPic,
                                       @RequestParam(value = "comLogo", defaultValue = "") String comLogo,
                                       @RequestParam(value = "ext", defaultValue = "") String ext,
                                       @RequestParam(value = "model", defaultValue = "") String model,
                                       @RequestParam(value = "status", defaultValue = "") int status) {

        BaseResponse rs = new BaseResponse();
        ProductIdea productIdea = productIdeaService.getProductIdea(productId);
        boolean addFlag = true;
        if (null == productIdea) {
            productIdea = new ProductIdea();
            productIdea.setcTime(new Date());
        } else {
            if (1 == productIdea.getStatus()) {
                rs.setStatus(false);
                rs.setMsg("评审通过，禁止修改");
                return rs;
            }
            productIdea.setmTime(new Date());
            addFlag = false;
        }

        productIdea.setProductId(productId);
        productIdea.setProductName(productName);
        productIdea.setCompanyId(companyId);
        productIdea.setCompanyName(companyName);
        productIdea.setCompanyPlace(companyPlace);
        productIdea.setLiaisonMan(liaisonMan);
        productIdea.setPhone(phone);
        productIdea.setLandLine(landLine);
        productIdea.setWebsite(website);
        productIdea.setEmail(email);
        productIdea.setCategoryId(categoryId);
        productIdea.setLogo(logo);
        productIdea.setDes(des);
        productIdea.setQuota(quota);
        productIdea.setIdeaPoint(ideaPoint);
        productIdea.setExtend(extend);
        productIdea.setProductPic(productPic);
        productIdea.setComLogo(comLogo);
        productIdea.setExt(ext);
        productIdea.setModel(model);
        productIdea.setStatus(status);

        int count = productIdeaService.countByCompanyId(companyId);
        if(count > 4){
            rs.setStatus(false);
            rs.setMsg("最多申请4个创新产品");
            return rs;
        }
        int result = 0;
        if (addFlag) {
            result = productIdeaService.addProductIdea(productIdea);
        } else {
            result = productIdeaService.updateProductIdea(productIdea);
        }

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
     * 查询创新产品
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getProductIdea", method = RequestMethod.POST)
    @ResponseBody
    public ProductIdeaResponse getProductIdea(@RequestParam(value = "id", defaultValue = "") int id) {
        ProductIdeaResponse rs = new ProductIdeaResponse();
        try {
            ProductIdea productIdea = productIdeaService.getProductIdea(id);

            if (null == productIdea) {
                productIdea = new ProductIdea();
                Product product = productService.getProduct(id);
                if (null == product) {
                    rs.setStatus(false);
                    rs.setMsg("无此产品");
                    return rs;
                }
                Company company = companyService.getCompany(product.getCompanyId());
                if (null == company) {
                    rs.setStatus(false);
                    rs.setMsg("展商不存在");
                    return rs;
                }
                productIdea.setProductId(product.getId());
                productIdea.setProductName(product.getName());
                productIdea.setCompanyId(product.getCompanyId());
                productIdea.setCompanyName(company.getName());
                productIdea.setCompanyPlace(company.getPlace());
            }

            rs.setStatus(true);
            rs.setMsg("ok");
            rs.setData(productIdea);
        } catch (Exception e) {
            rs.setStatus(false);
            rs.setMsg("无此产品");
            e.printStackTrace();
        }

        return rs;
    }
}
