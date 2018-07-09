package com.viewol.exhibition.controller;

import com.viewol.common.*;
import com.viewol.exhibition.response.ExhibitionCategoryResponse;
import com.viewol.exhibition.response.ExhibitionResponse;
import com.viewol.exhibition.vo.ExhibitionCategoryVO;
import com.viewol.exhibition.vo.ExhibitionVO;
import com.viewol.exhibitor.response.CompanyResponse;
import com.viewol.pojo.Category;
import com.viewol.pojo.Product;
import com.viewol.pojo.query.ProductQuery;
import com.viewol.service.ICategoryService;
import com.viewol.service.IProductService;
import com.viewol.shiro.token.TokenManager;
import com.viewol.sys.interceptor.Repeat;
import com.viewol.sys.log.annotation.MethodLog;
import com.viewol.sys.utils.Constants;
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
import java.util.*;

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

        PageHolder<Product> pageHolder = productService.queryProduct(productQuery);
        List<ExhibitionVO> voList = new ArrayList<>();

        if (null != pageHolder && null != pageHolder.getList() && pageHolder.getList().size()>0) {
            for(Product product : pageHolder.getList()){
                ExhibitionVO vo = new ExhibitionVO();
                vo.setId(product.getId());
                vo.setName(product.getName());
                vo.setCompanyId(product.getCompanyId());
                vo.setCategoryId(product.getCategoryId());
                vo.setStatus(product.getStatus());
                vo.setPdfName(product.getPdfName());
                vo.setPdfUrl(product.getPdfUrl());
                vo.setImage(product.getImage());
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
     * @param id
     * @return
     */
    @RequestMapping(value = "/getExhibition", method = RequestMethod.POST)
    @ResponseBody
    public ExhibitionResponse getExhibition(@RequestParam(value = "id", defaultValue = "") int id) {
        ExhibitionResponse rs = new ExhibitionResponse();
        Product product = productService.getProduct(id);

        if(null != product){
            ExhibitionVO vo = new ExhibitionVO();
            vo.setId(product.getId());
            vo.setName(product.getName());
            vo.setCompanyId(product.getCompanyId());
            vo.setCategoryId(product.getCategoryId());
            vo.setStatus(product.getStatus());
            vo.setPdfName(product.getPdfName());
            vo.setPdfUrl(product.getPdfUrl());
            vo.setImage(product.getImage());
            vo.setContent(product.getContent());
            vo.setcTime(product.getcTime());
            vo.setmTime(product.getmTime());

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
     * @param name
     * @param ids
     * @param imageAvatar
     * @param content
     * @param pdfName
     * @param pdfUrl
     * @return
     */
    @RequestMapping(value = "/addExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "添加展品")
    @Repeat
    public BaseResponse addExhibition(@RequestParam(value = "name", defaultValue = "") String name,
                                     @RequestParam(value = "ids[]") String[] ids,
                                     @RequestParam(value = "imageAvatar", defaultValue = "") String imageAvatar,
                                     @RequestParam(value = "content", defaultValue = "1") String content,
                                     @RequestParam(value = "pdfName", defaultValue = "") String pdfName,
                                     @RequestParam(value = "pdfUrl", defaultValue = "") String pdfUrl) {

        BaseResponse rs = new BaseResponse();
        Product product = new Product();
        product.setCompanyId(TokenManager.getCompanyId());
        product.setCategoryId(ids[0]);
        product.setStatus(Product.STATUS_ON);//默认上架
        product.setName(name);
        product.setImage(imageAvatar);
        product.setContent(content);
        product.setPdfUrl(pdfUrl);
        product.setPdfName(pdfName);
        product.setmTime(new Date());
        product.setcTime(new Date());
        int result = productService.addProduct(product);

        if(result>0){
            rs.setStatus(true);
            rs.setMsg("保存成功");
        } else {
            rs.setStatus(false);
            rs.setMsg("保存失败");
        }

        return rs;
    }


    @RequestMapping(value = "/updateExhibition", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "修改展品")
    @Repeat
    public BaseResponse updateExhibition(@RequestParam(value = "id", defaultValue = "") int id,
                                         @RequestParam(value = "name", defaultValue = "") String name,
                                         @RequestParam(value = "ids[]") String[] ids,
                                         @RequestParam(value = "imageAvatar", defaultValue = "") String imageAvatar,
                                         @RequestParam(value = "content", defaultValue = "1") String content,
                                         @RequestParam(value = "pdfName", defaultValue = "") String pdfName,
                                         @RequestParam(value = "pdfUrl", defaultValue = "") String pdfUrl) {

        BaseResponse rs = new BaseResponse();
        Product product = productService.getProduct(id);
        product.setCategoryId(ids[0]);
        product.setName(name);
        product.setImage(imageAvatar);
        product.setContent(content);
        product.setPdfUrl(pdfUrl);
        product.setPdfName(pdfName);
        product.setmTime(new Date());
        int result = productService.updateProduct(product);

        if(result>0){
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
    @MethodLog(module = Constants.AD, desc = "删除展品")
    @Repeat
    public BaseResponse deleteExhibition(@RequestParam(value = "id", defaultValue = "") int id) {
        BaseResponse rs = new BaseResponse();

        int result = productService.delProduct(id);
        if(result>0){
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
    @MethodLog(module = Constants.AD, desc = "上传产品图片")
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
                String httpUrl = imageUrl + path;
                rs.setImageUrl(httpUrl);

                //检查图片是否同步完，同步完成再回显
//                for (int i = 0; i < 5; i++) {
//                    Response<String> response = HttpUtil.sendGet(httpUrl, null, "UTF-8");
//                    if ("0000".equals(response.getCode())) {
//                        break;
//                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//
//                    }
//
//                }
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
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadContentImage", method = RequestMethod.POST)
    @ResponseBody
    @MethodLog(module = Constants.AD, desc = "展商富文本上传图片")
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
                String httpUrl = imageUrl + path;
                Map<String, String> map = new HashMap<>();
//                map.put("src", httpUrl);
                map.put("src", "http://test.youguu.com/mncg/images/code_080.jpg");


                rs.setData(map);

                //检查图片是否同步完，同步完成再回显
//                for (int i = 0; i < 6; i++) {
//                    Response<String> response = HttpUtil.sendGet(httpUrl, null, "UTF-8");
//                    if ("0000".equals(response.getCode())) {
//                        break;
//                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//
//                    }
//
//                }
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
    @MethodLog(module = Constants.AD, desc = "上传产品PDF")
    @Repeat
    public UploadPdfResponse uploadPdf(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UploadPdfResponse rs = new UploadPdfResponse();

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
                String httpUrl = imageUrl + path;
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
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "/getExhibitionCategory", method = RequestMethod.GET)
    @ResponseBody
    public ExhibitionCategoryResponse getExhibitionCategory(@RequestParam(value = "categoryId", defaultValue = "") String categoryId) {
        ExhibitionCategoryResponse rs = new ExhibitionCategoryResponse();
        Category category = categoryService.getCategory(categoryId);
        if(null != category){
            List<String> idsList = new ArrayList<>();
            List<String> namesList = new ArrayList<>();

            idsList.add(category.getId());
            namesList.add(category.getName());

            ExhibitionCategoryVO vo = new ExhibitionCategoryVO();
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

}
