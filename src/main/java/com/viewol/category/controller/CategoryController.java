package com.viewol.category.controller;

import com.viewol.category.response.CategoryTreeResponse;
import com.viewol.category.vo.CategoryTreeVO;
import com.viewol.pojo.Category;
import com.viewol.service.ICategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 展商，展品分类
 */
@Controller
@RequestMapping("category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    /**
     * 查询类别
     * @param type 1-展商；2-产品
     * @return
     */
    @RequestMapping(value = "/queryAllCategory")
    @ResponseBody
    public CategoryTreeResponse queryAllCategory(@RequestParam(value = "type", defaultValue = "0") int type) {
        CategoryTreeResponse rs = new CategoryTreeResponse();
        rs.setStatus(true);
        rs.setMsg("ok");

        List<Category> list = null;
        if(type==1){
            list = categoryService.listAll("0001");
        } else if(type == 2) {
            list = categoryService.listAll("0002");
        }

        if(list!=null && list.size()>0){
            List<CategoryTreeVO> volist = new ArrayList<>();
            for(Category category : list){
                CategoryTreeVO vo = new CategoryTreeVO();
                vo.setChecked(false);
                vo.setId(category.getId());
                vo.setMenuName(category.getName());
                vo.setParentId(category.getParentId());
                vo.setType(category.getType());

                List<Category> subList = categoryService.listByParent(category.getId());
                if(subList != null && subList.size()>0){
                    vo.setOpen(true);
                    vo.setNocheck(true);
                }

                volist.add(vo);
            }
            rs.setData(volist);
        }
        return rs;
    }
}
