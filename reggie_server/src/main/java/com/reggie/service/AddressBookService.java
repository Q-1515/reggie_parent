package com.reggie.service;

import com.reggie.entity.AddressBook;
import java.util.List;

public interface AddressBookService {

    public List<AddressBook> list(AddressBook addressBook);

    public void save(AddressBook addressBook);

    public AddressBook getById(Long id);

    public void update(AddressBook addressBook);

    public void setDefault(AddressBook addressBook);

    public void deleteById(Long id);

}
