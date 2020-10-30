package com.chen.dao;

import com.chen.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentMapper {


    List<Department> findDepartmentList(Department department);

}
