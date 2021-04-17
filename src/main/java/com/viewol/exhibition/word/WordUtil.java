package com.viewol.exhibition.word;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.deepoove.poi.util.BytePictureUtils;
import com.viewol.exhibitor.controller.ExhibitorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WordUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExhibitorController.class);

    private static Random random = new Random();


    public static void main(String[] args) {
        //模板、文件、图片路径
        final String workPath = System.getProperty("user.dir") + "/viewol_ams/src/main/resources/word/";
        String templateName = "template.docx";
        //模板替换数据封装
        Map<String, Object> datas = new HashMap<String, Object>() {
            {
                //文本
                put("name", "xiaoguo");
                put("sex", "男");
                put("year", "20200105");
                put("hello", "xiaoguo写于2020年一月");

                //自定义表格
                RowRenderData header = RowRenderData.build(new TextRenderData("1C86EE", "姓名"), new TextRenderData("1C86EE", "学历"));
                RowRenderData row0 = RowRenderData.build("张三", "研究生");
                RowRenderData row1 = RowRenderData.build("李四", "博士");
                RowRenderData row2 = RowRenderData.build("王五", "博士后");
                put("tables", new MiniTableRenderData(header, Arrays.asList(row0, row1, row2)));

                //自定义有序列表
                put("testText", new NumbericRenderData(NumbericRenderData.FMT_DECIMAL, new ArrayList<TextRenderData>() {
                    {
                        add(new TextRenderData("Plug-in grammar"));
                        add(new TextRenderData("Supports word text, header..."));
                        add(new TextRenderData("Not just templates, but also style templates"));
                    }
                }));

                //网落图片
                put("picture", new PictureRenderData(200, 150, ".jpg", BytePictureUtils.getUrlBufferedImage("https://gss3.bdstatic.com/7Po3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike116%2C5%2C5%2C116%2C38/sign=61c551093f6d55fbd1cb7e740c4b242f/d8f9d72a6059252d937820d3369b033b5ab5b9fd.jpg")));
                //本地图片
                put("picture2", new PictureRenderData(200, 150, ".jpg", BytePictureUtils.getLocalByteArray(new File(workPath + "c1.jpg"))));

            }
        };
        //生成word文档 todo 返回生成的word文件引用，可以根据需要是否要删除文件
        File file = generateWord(datas, workPath + templateName, workPath);
    }


    /**
     * 通过word模板并生成word文档
     *
     * @param paramData    参数数据
     * @param templatePath word模板地址加模板文件名字
     * @param outFilePath  输出文件地址（不带文件名字）
     * @return 生成的word文件
     */
    public static File generateWord(Map<String, Object> paramData, String templatePath, String outFilePath) {
        String outFileName = "2021年创新消防技术产品评选活动.docx";
        return generateWord(paramData, templatePath, outFilePath, outFileName);
    }


    /**
     * 通过word模板并生成word文档
     *
     * @param paramData    参数数据
     * @param templatePath word模板地址加模板文件名字
     * @param outFilePath  输出文件地址（不带文件名字）
     * @param outFileName  输出文件名字
     * @return 生成的word文件
     */
    public static File generateWord(Map<String, Object> paramData, String templatePath, String outFilePath, String outFileName) {
        //判断输出文件路径和文件名是否含有指定后缀
        outFilePath = CommonUtil.addIfNoSuffix(outFilePath, "/", "\\");
        outFileName = CommonUtil.addIfNoSuffix(outFileName, ".doc", ".docx");

        File fileDir = new File(outFilePath);
        if (!fileDir.exists()) { //如果不存在 则创建
            fileDir.mkdirs();
        }

        //解析word模板
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath);
        XWPFTemplate template = XWPFTemplate.compile(is).render(paramData);
        //输出文件
        FileOutputStream out = null;
        File outFile = new File(outFilePath + outFileName);
        try {
            out = new FileOutputStream(outFile);
            template.write(out);
            out.flush();
        } catch (IOException e) {
            logger.error("生成word写入文件失败", e);
        } finally {
            if (template != null) {
                try {
                    template.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outFile;
    }


}
