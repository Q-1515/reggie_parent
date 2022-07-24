package com.reggie.mapper;

import com.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    public List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     *
     * @param addressBook
     */
    public void insert(AddressBook addressBook);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public AddressBook getById(Long id);

    /**
     * 根据id修改
     *
     * @param addressBook
     */
    public void update(AddressBook addressBook);

    /**
     * 根据 用户id修改 是否默认地址
     *
     * @param addressBook
     */
    public void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id
     */
    public void deleteById(Long id);

}
