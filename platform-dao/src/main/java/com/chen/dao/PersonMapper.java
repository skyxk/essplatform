package com.chen.dao;

import com.chen.entity.Person;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonMapper {
    Person findPersonById(String personId);

    List<Person> findPersonListByKeyword(String keyword);

    Person findPersonBySYS(@Param("sys_id") String sys_id, @Param("sys_per_id")String sys_per_id);

    String findPersonIdBySYS(@Param("sys_id") String sys_id, @Param("sys_per_id")String sys_per_id);
}
