package com.chen.service.impl;

import com.chen.dao.PersonMapper;
import com.chen.dao.UserMapper;
import com.chen.entity.Person;
import com.chen.entity.Seal;
import com.chen.entity.User;
import com.chen.service.IPersonService;
import com.chen.service.ISystemService;
import com.chen.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    public UserMapper userMapper;
    @Autowired
    public PersonMapper personMapper;
    @Autowired
    public IPersonService personService;
    @Autowired
    public ISystemService systemService;
    @Override
    public List<User> findUserList(User user) {
        return null;
    }

    @Override
    public List<User> findAdminUserList(User user) {
        return userMapper.findAdminUserList(user);
    }

    @Override
    public User findUserByPersonId(String personId) {
        User user = userMapper.findUserByPersonId(personId);
        if (user==null){
            return null;
        }
        if (user.getPerson()!=null){
            user.setPerson(personService.findPersonById(user.getPerson_id()));
        }
        return user;
    }

    @Override
    public List<Person> findPersonListByKeyword(String keyword) {
        keyword = systemService.encryptString(keyword);
        List<Person> personList = personMapper.findPersonListByKeyword(keyword);
        for (Person person :personList){
            person = decryptPerson(person);
        }
        return personList;
    }
    public Person encryptPerson(Person person){
        if (person==null){
            return null;
        }
        if (person.getPerson_name()!=null){
            person.setPerson_name(systemService.encryptString(person.getPerson_name()));
        }
        if (person.getName_ap()!=null){
            person.setName_ap(systemService.encryptString(person.getName_ap()));
        }
        if (person.getId_num()!=null){
            person.setId_num(systemService.encryptString(person.getId_num()));
        }
        return person;
    }
    public Person decryptPerson(Person person){
        if (person==null){
            return null;
        }
        if (person.getPerson_name()!=null){
            person.setPerson_name(systemService.decryptString(person.getPerson_name()));
        }
        if (person.getName_ap()!=null){
            person.setName_ap(systemService.decryptString(person.getName_ap()));
        }
        if (person.getId_num()!=null){
            person.setId_num(systemService.decryptString(person.getId_num()));
        }
        return person;
    }

}
