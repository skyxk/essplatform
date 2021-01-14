package com.chen.service.impl;

import com.chen.dao.BusinessSysMapper;
import com.chen.dao.PersonMapper;
import com.chen.entity.Person;
import com.chen.entity.SealImg;
import com.chen.service.IPersonService;
import com.chen.service.ISystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：chen
 * @date ：Created in 2019/10/17 14:59
 */
@Service
public class PersonServiceImpl implements IPersonService {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    public ISystemService systemService;
    @Override
    public Person findPersonById(String id) {
        return decryptPerson(personMapper.findPersonById(id));
    }

    @Override
    public Person findPersonBySYS(String sys_id, String sys_per_id) {
        return decryptPerson(personMapper.findPersonBySYS(sys_id,sys_per_id));
    }


    @Override
    public String findPersonIdBySYS(String sys_id, String sys_per_id) {
        return personMapper.findPersonIdBySYS(sys_id,sys_per_id);
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
