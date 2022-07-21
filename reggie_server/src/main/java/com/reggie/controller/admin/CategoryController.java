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
        return R.success("分类添加成功");
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

    /**
     * 根据id删除分类
     *
     * @param id 要删除分类的id
     * @return success
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public R<String> deleteById(Long id) {
        log.info("删除分类:{}", id);
        categoryService.deleteById(id);
        return R.success("分类删除成功");
    }

    /**
     * 修改分类
     * @param categoryDTO 修改id 修改name 修改sort
     * @return success
     */
    @PutMapping
    @ApiOperation("修改分类")
    public R<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类:{}", categoryDTO);
        categoryService.update(categoryDTO);
        return R.success("分类修改成功");
    }


}
