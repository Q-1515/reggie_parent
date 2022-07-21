package com.reggie.controller.admin;


import com.reggie.dto.CategoryDTO;
import com.reggie.result.R;
import com.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类接口
     *
     * @param categoryDTO 新增分类(菜品、套餐)
     * @return success
     */
    @PostMapping
    @ApiOperation("新增分类接口")
    public R<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}", categoryDTO);
        categoryService.save(categoryDTO);
        return R.success("添加成功");
    }

}
