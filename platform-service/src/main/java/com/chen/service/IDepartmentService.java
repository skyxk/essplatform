package com.chen.service;

import com.chen.entity.Department;

import java.util.List;

public interface IDepartmentService {

    List<Department> findDepartmentList(Department department);
}
