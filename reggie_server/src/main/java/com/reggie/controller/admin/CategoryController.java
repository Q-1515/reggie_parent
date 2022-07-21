package com.reggie.controller.admin;


import com.reggie.dto.CategoryDTO;
import com.reggie.dto.CategoryPageQueryDTO;
import com.reggie.result.PageResult;
import com.reggie.result.R;
import com.reggie.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 分类信息分页查询
     *
     * @param categoryPageQueryDTO 查询页数，记录数，分类类型，分类的名称
     * @return PageResult 分类集合，总记录数
     */
    @GetMapping("/page")
    @ApiOperation("分类信息分页查询")
    public R<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类信息分页查询:{}", categoryPageQueryDTO);
        PageResult page = categoryService.pageQuery(categoryPageQueryDTO);
        return R.success(page);
    }


}
