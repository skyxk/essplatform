package com.chen.dao;

import com.chen.entity.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonMapper {
    Person findPersonById(String personId);

    List<Person> findPersonListByKeyword(String keyword);
}
