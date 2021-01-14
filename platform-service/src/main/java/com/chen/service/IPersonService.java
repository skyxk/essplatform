package com.chen.service;

import com.chen.entity.Person;

public interface IPersonService {


    Person findPersonById(String id);


    Person findPersonBySYS(String sys_id,String sys_per_id);

    String findPersonIdBySYS(String sys_id,String sys_per_id);


}
