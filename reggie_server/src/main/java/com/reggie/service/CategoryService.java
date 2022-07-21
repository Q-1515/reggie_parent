package com.reggie.service;


import com.reggie.dto.CategoryDTO;
import com.reggie.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;

public interface CategoryService {

    void save(CategoryDTO categoryDTO);
}
